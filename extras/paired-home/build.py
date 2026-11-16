"""Build Paired Home using the repository's shared portable Android builder."""
from pathlib import Path
import subprocess,sys
subprocess.run([sys.executable,str(Path(__file__).resolve().parents[2]/'build.py'),'--paired-home',*sys.argv[1:]],check=True)
