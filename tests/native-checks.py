"""Run public, account-free JVM checks. Private capture suites are optional local tools."""
from pathlib import Path
import hashlib,json,os,shutil,subprocess,urllib.request
ROOT=Path(__file__).resolve().parents[1];os.chdir(ROOT)
for d in json.loads((ROOT/'test-dependencies.json').read_text()).values():
 p=ROOT/d['path'];p.parent.mkdir(exist_ok=True)
 if not p.exists():
  with urllib.request.urlopen(d['url']) as r:p.write_bytes(r.read())
 assert hashlib.sha256(p.read_bytes()).hexdigest()==d['sha256'],'Test dependency checksum mismatch'
java=Path(os.environ['JAVA_HOME'])/'bin' if os.environ.get('JAVA_HOME') else Path(shutil.which('javac') or '').parent
if not (java/'javac').exists():raise SystemExit('Set JAVA_HOME to JDK 21')
classes=['LoginPolicy','SessionCookies','AuthRecovery','SyncPolicy','ContextParser','HudClassifier','FrameRate','CompanionLifecycle','SquadTracker'];tests=['AuthPolicyTest','AuthRecoveryTest','SyncPolicyTest','ContextParserTest','HudClassifierTest','FrameRateTest','CompanionLifecycleTest','SquadTrackerTest'];out=ROOT/'build/native-tests';out.mkdir(parents=True,exist_ok=True);cp=str(ROOT/'reference/json-test.jar')
subprocess.run([str(java/'javac'),'-cp',cp,'-d',str(out),*['src/dev/gbppimentel/zzz/'+n+'.java' for n in classes],*['tests/'+n+'.java' for n in tests]],check=True)
for n in tests:subprocess.run([str(java/'java'),'-Djava.awt.headless=true','-cp',str(out)+os.pathsep+cp,'dev.gbppimentel.zzz.'+n],check=True)
subprocess.run([str(java/'javac'),'-d',str(out),'extras/paired-home/src/dev/gbppimentel/sessionhome/SessionPolicy.java','extras/paired-home/tests/SessionPolicyTest.java'],check=True)
subprocess.run([str(java/'java'),'-cp',str(out),'dev.gbppimentel.sessionhome.SessionPolicyTest'],check=True)
