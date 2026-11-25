"""Package signed preview artifacts and their exact source/validation records."""
from pathlib import Path
import argparse,hashlib,json,shutil,subprocess,zipfile,xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',required=True,type=Path);p.add_argument('--paired-apk',type=Path);a=p.parse_args()
version=ET.parse(ROOT/'AndroidManifest.xml').getroot().attrib['{http://schemas.android.com/apk/res/android}versionName'];out=ROOT/'dist'/('release-v'+version);out.mkdir(parents=True,exist_ok=True)
with zipfile.ZipFile(a.apk) as z:
 assert z.testzip() is None
 for n in z.namelist():assert not any(v in n.lower() for v in ['.keystore','device-state.json','snapshot.sav','dashboard.xml']),n
apk=out/f'NewEriduCompanion-v{version}.apk';shutil.copy2(a.apk,apk);assets=[apk]
if a.paired_apk:
 helper=out/a.paired_apk.name;shutil.copy2(a.paired_apk,helper);assets.append(helper)
compat=out/f'compatibility-v{version}.json';shutil.copy2(ROOT/'compatibility.json',compat);assets.append(compat)
validation=json.loads((ROOT/f'docs/validation-v{version}.json').read_text())
commit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip()
manifest=out/f'release-manifest-v{version}.json';manifest.write_text(json.dumps({'sourceCommit':commit,'release':'v'+version,'developmentPreview':True,'publicBuildDeviceValidated':validation.get('publicBuildDeviceValidated',False),'apkSha256':sha(apk),'artifacts':{f.name:sha(f) for f in assets},'validation':validation},indent=2));assets.append(manifest)
archive=out/f'NewEriduCompanion-v{version}.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
 for f in assets:z.write(f,f.name)
 for f in [ROOT/'packaging/QUICK-START.md',ROOT/'LICENSE',ROOT/'THIRD_PARTY_NOTICES.md',ROOT/f'docs/validation-v{version}.json']:z.write(f,f.name)
assets.append(archive);sums=out/f'SHA256SUMS-v{version}.txt';sums.write_text(''.join(f'{sha(f)}  {f.name}\n' for f in assets))
print('Packaged',len(assets)+1,'artifacts in',out)
