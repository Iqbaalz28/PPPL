#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

JAVA_BIN="${JAVA_BIN:-$HOME/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home/bin/java}"
JAVAC_BIN="${JAVAC_BIN:-$HOME/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home/bin/javac}"

if [ ! -f "$JAVAC_BIN" ]; then
    JAVAC_BIN="javac"
    JAVA_BIN="java"
fi

echo "============================================"
echo " The Last Penguin - JUnit 5 Test Runner"
echo "============================================"

mkdir -p build build/test

echo "[1/3] Compiling source code..."
"$JAVAC_BIN" -d build -cp "lib/*" $(find src -name "*.java")

echo "[2/3] Compiling test suite..."
"$JAVAC_BIN" -d build/test -cp "build:lib/*" $(find test -name "*.java")

echo "[3/3] Running JUnit 5 tests..."
"$JAVA_BIN" -jar lib/junit-platform-console-standalone-1.10.2.jar execute \
    --class-path "build:build/test" \
    --scan-class-path \
    --details=tree

