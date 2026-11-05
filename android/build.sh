#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_SDK_ROOT:?Set ANDROID_SDK_ROOT}"
BT="$ANDROID_SDK_ROOT/build-tools/35.0.0"
PLATFORM="$ANDROID_SDK_ROOT/platforms/android-35/android.jar"
mkdir -p build/classes build/dex build/gen
"$BT/aapt2" compile --dir res -o build/resources.zip
"$BT/aapt2" link -o build/base.apk --manifest AndroidManifest.xml -I "$PLATFORM" --java build/gen --auto-add-overlay -A assets build/resources.zip
javac -encoding UTF-8 --release 8 -classpath "$PLATFORM" -d build/classes src/cn/wulian/fitness/*.java build/gen/cn/wulian/fitness/R.java
find build/classes -name '*.class' -print > build/class-list.txt
"$BT/d8" --min-api 26 --lib "$PLATFORM" --output build/dex @build/class-list.txt
cp build/base.apk build/unaligned.apk
(cd build/dex && zip -q -u ../unaligned.apk classes.dex)
"$BT/zipalign" -f 4 build/unaligned.apk build/aligned.apk
if [[ -n "${WULIAN_KEYSTORE:-}" ]]; then
 "$BT/apksigner" sign --ks "$WULIAN_KEYSTORE" --ks-key-alias wulian --ks-pass env:WULIAN_SIGN_PASSWORD --key-pass env:WULIAN_SIGN_PASSWORD --out build/wulian-1.0.0.apk build/aligned.apk
 "$BT/apksigner" verify --verbose build/wulian-1.0.0.apk
else
 echo 'Unsigned build created. Set WULIAN_KEYSTORE and WULIAN_SIGN_PASSWORD to sign.'
fi
