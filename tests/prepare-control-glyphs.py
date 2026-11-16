from pathlib import Path
from PIL import Image,ImageDraw
root=Path('assets/controls');root.mkdir(exist_ok=True)
import subprocess,sys
subprocess.run([sys.executable,'tests/prepare-face-glyphs.py'],check=True)
# Remaining glyphs follow ZZZ's black gamepad plates, using Thor's labels.
for key,label in [('L1','L1'),('R1','R1'),('L2','L2'),('R2','R2'),('SELECT','−'),('START','+'),('L3','L3'),('R3','R3')]:
 pill=key not in ['L3','R3','SELECT','START'];shape='<rect x="1" y="4" width="30" height="24" rx="4"/>' if pill else '<circle cx="16" cy="16" r="14"/>'
 svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"><g fill="#080808" stroke="#888" stroke-width="1.5">{shape}</g><text x="16" y="20" text-anchor="middle" fill="#e6e6e6" font-family="Arial,sans-serif" font-weight="bold" font-size="{12 if pill or key in ["L3","R3"] else 18}">{label}</text></svg>'
 if key in ['SELECT','START']:
  paths='M10 16h12'+('M16 10v12' if key=='START' else '')
  svg=f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32"><circle cx="16" cy="16" r="14" fill="#080808" stroke="#888" stroke-width="1.5"/><path d="{paths}" fill="none" stroke="#e6e6e6" stroke-width="2.5" stroke-linecap="round"/></svg>'
 (root/(key+'.svg')).write_text(svg)
for key,rotation in [('UP',0),('RIGHT',90),('DOWN',180),('LEFT',270)]:
 (root/(key+'.svg')).write_text(f'<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"><g transform="rotate({rotation} 16 16)" fill="#111" stroke="#858585" stroke-width="1.4"><path d="M11 2h10v9h9v10h-9v9H11v-9H2V11h9z"/><path d="M11 11l5-6 5 6z" fill="#eee" stroke="none"/></g></svg>')
print('16 ZZZ/Thor control glyphs prepared')
