"""Fetch and verify pinned OCR runtime inputs when building a fresh checkout."""
from pathlib import Path
import hashlib,json,subprocess,zipfile
base=Path(__file__).resolve().parent
for dep in json.loads((base/'ocr-dependencies.json').read_text()).values():
 target=base/dep['path'];target.parent.mkdir(parents=True,exist_ok=True)
 if not target.exists():subprocess.run(['curl','-L','--fail','--silent','--show-error',dep['url'],'-o',str(target)],check=True)
 if hashlib.sha256(target.read_bytes()).hexdigest()!=dep['sha256']:raise SystemExit('OCR dependency checksum mismatch: '+target.name)
with zipfile.ZipFile(base/'reference/tesseract4android.aar') as archive:
 (base/'reference/ocr-classes.jar').write_bytes(archive.read('classes.jar'))
 (base/'assets/native').mkdir(exist_ok=True)
 for name in archive.namelist():
  if name.startswith('jni/arm64-v8a/') and name.endswith('.so'):(base/'assets/native'/Path(name).name).write_bytes(archive.read(name))
