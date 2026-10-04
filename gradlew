#!/usr/bin/env sh
set -eu
GRADLE_VERSION=8.9
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin"
GRADLE="$DIST/gradle-$GRADLE_VERSION/bin/gradle"
if [ ! -x "$GRADLE" ]; then
  mkdir -p "$DIST"
  ZIP="$DIST/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then curl -fL "$URL" -o "$ZIP"; else wget -O "$ZIP" "$URL"; fi
  unzip -q -o "$ZIP" -d "$DIST"
  rm -f "$ZIP"
fi
exec "$GRADLE" "$@"
