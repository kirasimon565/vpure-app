#!/data/data/com.termux/files/usr/bin/bash

APPNAME="vpure"
PROJECT_DIR="$PWD/src/main"
BUILD_DIR="$PWD/build"
TOOLS="$HOME/android-tools"
ANDROID_JAR="$TOOLS/android-13/android.jar"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"/{bin,dex,compiled-res}

echo "=== COMPILING JAVA FILES ==="
find "$PROJECT_DIR/java" -name "*.java" > sources.txt
javac -cp "$ANDROID_JAR" -d "$BUILD_DIR/bin" @sources.txt

echo "=== DEXING ==="
d8 "$BUILD_DIR/bin" --output "$BUILD_DIR/dex"

echo "=== COMPILING RESOURCES ==="
aapt2 compile -o "$BUILD_DIR/compiled-res" $(find "$PROJECT_DIR/res" -name "*.xml")

echo "=== LINKING APK ==="
aapt2 link \
  -o "$BUILD_DIR/unsigned.apk" \
  -I "$ANDROID_JAR" \
  --manifest "$PROJECT_DIR/manifests/AndroidManifest.xml" \
  $(find "$BUILD_DIR/compiled-res" -name "*.flat")

echo "=== ADDING CLASSES.DEX ==="
cd "$BUILD_DIR"
zip -j "unsigned.apk" "dex/classes.dex"

echo "=== SIGNING APK ==="
apksigner sign \
  --ks "$TOOLS/mykey.keystore" \
  --ks-pass pass:123456 \
  "unsigned.apk"

mv unsigned.apk "$APPNAME.apk"

echo "✔ DONE! Final APK → $BUILD_DIR/$APPNAME.apk"
