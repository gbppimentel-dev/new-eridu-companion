from PIL import Image
from pathlib import Path
import json,urllib.request,concurrent.futures,subprocess
avatars=json.load(open('assets/avatars.json'));paths=set()
for a in avatars.values():
 paths.add(a['Image'])
 for skin in a.get('Skins',{}).values():paths.add(skin['Image'])
root=Path('reference/original-cutouts');root.mkdir(exist_ok=True)
def prepare(path):
 try:
  raw=root/Path(path).name
  if not raw.exists():subprocess.run(['curl','-L','--fail','--silent','--max-time','30','https://enka.network'+path,'-o',str(raw)],check=True,capture_output=True)
  im=Image.open(raw).convert('RGBA');l,t,r,b=im.getbbox();height=b-t
  # Keep the head and upper torso, including the original silhouette and transparency.
  top=im.crop((l,t,r,min(b,t+int(height*.49))))
  framing=json.load(open('tests/cutout-framing.json'))
  agent=next((id for id,a in avatars.items() if a['Image']==path),None)
  if agent in framing:top=im.crop(framing[agent])
  top.thumbnail((530,530),Image.Resampling.LANCZOS);top.save('assets/cutouts/'+raw.stem+'.webp','WEBP',quality=92,method=5)
  return True
 except Exception:return False
with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:result=list(pool.map(prepare,sorted(paths)))
print(sum(result),'of',len(result),'high-resolution game cutouts prepared')
p=Path('assets/asset-sources.json');s=json.load(open(p));s['portraits']='Original game cutouts from https://enka.network/ui/zzz/, cropped to upper bodies from original PNGs for the Thor. CircleIcon assets retained for recognition/reference.';s['official_art']='Original public HoYoLAB game-record art, cached through official-map.js for Bangboo, banners, enemies and exploration.';s['ocr']='Tesseract4Android 4.9.0 (Apache 2.0), Tesseract 5.5.1, tessdata_fast English model (Apache 2.0). In-memory game-only OCR.';p.write_text(json.dumps(s,indent=2))
