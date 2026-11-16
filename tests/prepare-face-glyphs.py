"""Code-native control plates using outlines from the bundled original game font."""
from pathlib import Path
from fontTools.ttLib import TTFont
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.boundsPen import BoundsPen
font=TTFont('assets/zzz/zzz-us.woff2');glyphs=font.getGlyphSet();cmap=font.getBestCmap()
for key,color in [('A','#00b977'),('B','#ef4c51'),('X','#36a8dc'),('Y','#e5ba47')]:
 glyph=glyphs[cmap[ord(key)]];pen=SVGPathPen(glyphs);glyph.draw(pen);bounds=BoundsPen(glyphs);glyph.draw(bounds);l,b,r,t=bounds.bounds;scale=15/max(r-l,t-b);tx=16-(l+r)*scale/2;ty=16+(b+t)*scale/2
 svg=f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32"><circle cx="16" cy="16" r="14" fill="#080808" stroke="#65717b" stroke-width="2"/><circle cx="16" cy="16" r="11.7" fill="none" stroke="#20272d"/><path fill="{color}" transform="translate({tx:.6f} {ty:.6f}) scale({scale:.8f} {-scale:.8f})" d="{pen.getCommands()}"/></svg>'
 Path('assets/controls/'+key+'.svg').write_text(svg)
print('4 game-font control glyphs centered by their actual outline bounds')
