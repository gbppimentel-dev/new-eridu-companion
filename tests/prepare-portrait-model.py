"""Build roster-wide HUD features from public portraits, never labeled screenshots.
The two shared crop banks reflect the active and reserve HUD layouts. Screenshots
are held back exclusively for verification; none supply identity labels here.
"""
from PIL import Image
from pathlib import Path
import json
ACTIVE=[(5,0,175,56),(0,0,180,56),(10,0,170,56),(5,0,175,52),(5,2,175,58)]
RESERVE=[(25,-10,165,54),(20,-10,165,54),(30,-10,165,54),(25,-8,165,52),(25,-12,165,56)]
models=[]
for path in sorted(Path('reference/hud-public').glob('*.png')):
 im=Image.open(path).convert('RGBA')
 # Transparent corners are black in the game plate, not arbitrary hidden RGB.
 bg=Image.new('RGBA',im.size,(0,0,0,255));bg.alpha_composite(im);im=bg.convert('RGB')
 for role,crops in [('active',ACTIVE),('reserve',RESERVE)]:
  for box in crops:
   small=im.crop(box).resize((24,12),Image.Resampling.BOX)
   models.append(dict(id=path.stem,kind='combat',role=role,x=64 if role=='active' else 290,y=26 if role=='active' else 24,w=62 if role=='active' else 38,h=22 if role=='active' else 16,rgb=[v for pixel in small.getdata() for v in pixel]))
Path('assets/portraits.json').write_text(json.dumps(models,separators=(',',':')))
print(len(models),'public portrait variants for',len({m['id'] for m in models}),'agents')
