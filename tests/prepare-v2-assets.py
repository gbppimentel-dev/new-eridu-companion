from pathlib import Path
from PIL import Image
import json,urllib.request,concurrent.futures,hashlib
root=Path('.');names=json.load(open('private/agent-names.json'));av=json.load(open('assets/avatars.json'));state=json.load(open('private/device-state.json'))['sections']
(root/'assets/cutouts').mkdir(exist_ok=True);(root/'assets/official').mkdir(exist_ok=True)
cut={}
for p in Path('reference/unused-full-body').glob('*.webp'):
 im=Image.open(p).convert('RGBA');box=im.getbbox()
 if not box:continue
 # Upper-body art floats outside the card baseline; preserve original transparency.
 l,t,r,b=box;im=im.crop((l,t,r,min(b,t+int((b-t)*.61))));im.thumbnail((430,470),Image.Resampling.LANCZOS);im.save('assets/cutouts/'+p.name,'WEBP',quality=88);cut[p.stem]='cutouts/'+p.name
sources={};jobs={}
for id in av:jobs['https://act-webstatic.hoyoverse.com/game_record/zzzv2/role_square_avatar/role_square_avatar_'+id+'.png']='agent-'+id
bosses=[]
def walk(v):
 if isinstance(v,list):
  for x in v:walk(x)
 elif isinstance(v,dict):
  for k,val in v.items():
   if isinstance(val,str) and val.startswith('https://') and any(x in val for x in ['act-webstatic.hoyoverse.com/','upload-os-bbs.hoyolab.com/']):jobs[val]=hashlib.sha256(val.encode()).hexdigest()[:16]
   elif isinstance(val,(dict,list)):walk(val)
  if 'name' in v and 'icon' in v and ('medal' in v or 'race_icon' in v):bosses.append({'name':v['name'],'icon':v['icon']})
for key in ['account','bangboos','banners','annihilation','threshold']:walk(state.get(key,{}).get('data',{}))
def download(item):
 url,key=item;p=Path('assets/official/'+key+'.webp')
 try:
  if not p.exists():
   req=urllib.request.Request(url,headers={'User-Agent':'Mozilla/5.0'});raw=urllib.request.urlopen(req,timeout=25).read();tmp=Path('/private/tmp/eridu-asset-'+key);tmp.write_bytes(raw);im=Image.open(tmp).convert('RGBA');im.thumbnail((640,640),Image.Resampling.LANCZOS);im.save(p,'WEBP',quality=88);tmp.unlink()
  return url,str(p).removeprefix('assets/')
 except Exception:return url,None
with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
 for url,path in pool.map(download,jobs.items()):
  if path:sources[url]=path
Path('assets/official-map.js').write_text('const OFFICIAL='+json.dumps(sources,separators=(',',':'))+';\nconst CUTOUTS='+json.dumps(cut,separators=(',',':'))+';')
Path('assets/combat-catalog.json').write_text(json.dumps({'agents':[{ 'id':id,'name':a['name']} for id,a in names.items()],'enemies':bosses,'areas':[a['name'] for a in state['account']['data'].get('area_collections',[])]}))
print('Prepared',len(cut),'transparent upper-body cutouts;',len(sources),'official images')
