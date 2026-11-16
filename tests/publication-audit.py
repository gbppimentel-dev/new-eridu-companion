"""Audit the public source boundary without printing matched credentials."""
from pathlib import Path
import re,subprocess,json
ROOT=Path(__file__).resolve().parents[1]
ignored={'build','dist','release','reference','review','private','.git','.venv','node_modules','__pycache__'}
r=subprocess.run(['git','ls-files','--cached','--others','--exclude-standard','-z'],cwd=ROOT,text=True,capture_output=True)
files=[ROOT/x for x in r.stdout.split('\0') if x] if r.returncode==0 else []
if not files:files=[p for p in ROOT.rglob('*') if p.is_file() and not any(v in ignored for v in p.relative_to(ROOT).parts)]
forbidden={'.keystore','.jks','.p12','.pfx','.pem','.sav','.db','.sqlite','.apk','.zip','.aar','.jar','.so','.traineddata'}
secret=re.compile(r'gh[pousr]_[A-Za-z0-9]{25,}|github_pat_[A-Za-z0-9_]{25,}|-----BEGIN [A-Z ]*PRIVATE KEY-----')
errors=[]
for p in files:
 rel=p.relative_to(ROOT).as_posix()
 if p.suffix in forbidden or any(v in ignored for v in p.relative_to(ROOT).parts):errors.append('Private/generated artifact: '+rel)
 try:s=p.read_text()
 except (UnicodeDecodeError,ValueError):continue
 if secret.search(s):errors.append('Credential pattern: '+rel)
 if re.search('/'+r'Users/[^/\s]+/|[A-Z]:\\Users\\[^\\\s]+\\',s):errors.append('Machine home path: '+rel)
 if re.search(r'getString\("uid",\s*"[0-9]{8,12}"\)',s):errors.append('Preconfigured account UID: '+rel)
 if re.search('--ks-'+'pass'+r'[^\n]{0,20}'+'pass'+':',s):errors.append('Literal signing password: '+rel)
assert not errors,'\n'.join(errors)
print('PASS:',len(files),'public files; no private state, keys, credentials or developer account defaults')
