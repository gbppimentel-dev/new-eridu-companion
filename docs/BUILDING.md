# building & releases

[architecture](ARCHITECTURE.md) · [contributing](../CONTRIBUTING.md) · [README](../README.md)

## toolchain

Use Python 3.10+, Node.js 22+ (CI uses 24), JDK 21 and Android platform 36/build-tools 36.0.0. Set `JAVA_HOME` and `ANDROID_HOME` (or `ANDROID_SDK_ROOT`). Normal builds need no original game installation, account data or private screen captures.

```sh
python3 build.py
python3 build.py --paired-home
```

The outputs are unsigned APKs in `dist/`. `prepare-deps.py` downloads the OCR archive/model listed in `ocr-dependencies.json` and verifies their SHA-256 hashes before use. Native libraries and the observer DEX are staged during the build. Dependencies and generated binaries are ignored by Git.

## signing

Keep the keystore outside the checkout. Supply secrets through environment variables:

```sh
export NEWERIDU_KEYSTORE=/path/outside/checkout/release.jks
export NEWERIDU_KEY_ALIAS=your-alias
read -rs NEWERIDU_KEYSTORE_PASSWORD
export NEWERIDU_KEYSTORE_PASSWORD
python3 build.py
python3 build.py --paired-home
```

Set `NEWERIDU_KEY_PASSWORD` if separate. The builder passes environment references to `apksigner`, then verifies the signed APK. A custom certificate cannot update an official build in place. The official key is not stored in GitHub or CI.

## checks

```sh
node tests/ui-state.cjs
python3 tests/native-checks.py
python3 tests/publication-audit.py
```

Private calibration tests are retained as optional local tools. They need locally supplied `private/calibration/` images and are not advertised as reproducible public CI. Public fixtures in `tests/fixtures/` are synthetic and contain no player's account state.

## visual review

Start an isolated Chrome instance with remote debugging on port 9230, then:

```sh
ZZZ_REVIEW_DIR=build/review node tests/terminal-review.mjs
```

The public fixture is synthetic. Optional raster/font asset tools need `requirements-art.txt`; they are not needed for a normal build.

## packaging

Run checks and build/sign the candidate. Then package that exact APK:

```sh
python3 packaging/package_release.py --apk dist/NewEriduCompanion-v2.6.4.apk --paired-apk dist/PairedHome-v1.2.apk
```

The package includes the APK, optional helper, quick start, notices, compatibility/validation JSON, source commit and checksums. It verifies package/version and excludes local state. A package/build check is not a substitute for hardware validation; update the validation record only for checks actually performed.
