"""Optional development installation; normal users can install a release APK directly."""
import argparse,os,shutil,subprocess
from pathlib import Path
p=argparse.ArgumentParser(description=__doc__);p.add_argument('apk',type=Path);p.add_argument('--serial');a=p.parse_args()
sdk=os.environ.get('ANDROID_HOME') or os.environ.get('ANDROID_SDK_ROOT');adb=shutil.which('adb') or str(Path(sdk or Path.home()/'Library/Android/sdk')/'platform-tools/adb');cmd=[adb]+(['-s',a.serial] if a.serial else [])
subprocess.run(cmd+['install','--no-incremental','-r',str(a.apk)],check=True)
print('Installed. Launch through the game’s configured Cocoon entry.')
