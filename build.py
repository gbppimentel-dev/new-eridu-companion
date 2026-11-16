"""Portable Android build. Unsigned by default; signing secrets stay outside the checkout."""
from pathlib import Path
import argparse,hashlib,json,os,re,shutil,subprocess,sys,zipfile
ROOT=Path(__file__).resolve().parent

def toolchain():
 sdk=Path(os.environ.get('ANDROID_HOME') or os.environ.get('ANDROID_SDK_ROOT') or Path.home()/'Library/Android/sdk')
 java=Path(os.environ['JAVA_HOME'])/'bin' if os.environ.get('JAVA_HOME') else None
 if java is None:
  local=Path.home()/'Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home/bin'
  java=local if local.exists() else Path(shutil.which('javac') or '').parent
 for p in [sdk/'platforms/android-36/android.jar',sdk/'build-tools/36.0.0/d8',java/'javac']:
  if not p.exists():raise SystemExit('Missing toolchain component: '+str(p)+'; see docs/BUILDING.md')
 return sdk,java

def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--paired-home',action='store_true');p.add_argument('--debug',action='store_true');p.add_argument('--unsigned',action='store_true');p.add_argument('--keystore',default=os.environ.get('NEWERIDU_KEYSTORE'));a=p.parse_args()
 base=ROOT/'extras/paired-home' if a.paired_home else ROOT;sdk,java=toolchain();bt=sdk/'build-tools/36.0.0';jar=sdk/'platforms/android-36/android.jar';env=dict(os.environ);env['JAVA_HOME']=str(java.parent);env['PATH']=str(java)+os.pathsep+env.get('PATH','')
 def run(*args):subprocess.run([str(x) for x in args],check=True,env=env,cwd=ROOT)
 version=re.search(r'android:versionName="([^"]+)"',(base/'AndroidManifest.xml').read_text()).group(1);name='PairedHome' if a.paired_home else 'NewEriduCompanion';build=ROOT/'build'/name
 if build.exists():shutil.rmtree(build)
 for d in ['classes','dex','assets']:(build/d).mkdir(parents=True)
 if not a.paired_home:run(sys.executable,ROOT/'prepare-deps.py')
 sources=list((base/'src').rglob('*.java'));classpath=[]
 if not a.paired_home:
  flags=build/'BuildFlags.java';flags.write_text('package dev.gbppimentel.zzz; final class BuildFlags { static final boolean DEBUG = '+str(a.debug).lower()+'; static final String VERSION = '+json.dumps(version)+'; }');sources.append(flags);classpath=['-classpath',ROOT/'reference/ocr-classes.jar']
 run(java/'javac','-source','8','-target','8',*classpath,'-bootclasspath',str(jar)+os.pathsep+str(bt/'core-lambda-stubs.jar'),'-d',build/'classes',*sources)
 run(bt/'d8','--lib',jar,'--min-api','30','--output',build/'dex',*list((build/'classes').rglob('*.class')),*([ROOT/'reference/ocr-classes.jar'] if not a.paired_home else []))
 if (base/'assets').exists():shutil.copytree(base/'assets',build/'assets',dirs_exist_ok=True,ignore=shutil.ignore_patterns('observer.jar'))
 if not a.paired_home:
  with zipfile.ZipFile(build/'assets/observer.jar','w') as z:z.write(build/'dex/classes.dex','classes.dex')
 run(bt/'aapt','package','-f','-M',base/'AndroidManifest.xml','-S',base/'res','-A',build/'assets','-I',jar,'-F',build/'unsigned.apk')
 with zipfile.ZipFile(build/'unsigned.apk','a') as z:z.write(build/'dex/classes.dex','classes.dex')
 run(bt/'zipalign','-f','4',build/'unsigned.apk',build/'aligned.apk');out=ROOT/'dist';out.mkdir(exist_ok=True)
 if a.keystore and not a.unsigned:
  if not env.get('NEWERIDU_KEYSTORE_PASSWORD'):raise SystemExit('Set NEWERIDU_KEYSTORE_PASSWORD in the environment.')
  target=out/f'{name}-v{version}.apk';sign=['sign','--ks',a.keystore,'--ks-pass','env:NEWERIDU_KEYSTORE_PASSWORD']
  if env.get('NEWERIDU_KEY_ALIAS'):sign+=['--ks-key-alias',env['NEWERIDU_KEY_ALIAS']]
  if env.get('NEWERIDU_KEY_PASSWORD'):sign+=['--key-pass','env:NEWERIDU_KEY_PASSWORD']
  run(bt/'apksigner',*sign,'--out',target,build/'aligned.apk');run(bt/'apksigner','verify',target)
 else:target=out/f'{name}-v{version}-unsigned.apk';shutil.copy2(build/'aligned.apk',target)
 print(target.relative_to(ROOT));print('SHA256 '+hashlib.sha256(target.read_bytes()).hexdigest())
if __name__=='__main__':main()
