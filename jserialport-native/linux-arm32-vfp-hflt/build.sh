#!/usr/bin/env bash

_OS_NAME="";
_OS_ARCH="$2";

shopt -s nocasematch
case $1 in
'unix'*)
    _OS_NAME="linux";
;;
'linux'*)
    _OS_NAME="linux";
;;
'sparc'*)
    _OS_NAME="linux";
;;
*)
    _OS_NAME="linux";
;;
esac

case $2 in
'i386'*)
    _OS_ARCH="i386";
;;
'amd64'*)
    _OS_ARCH="amd64";
;;
'arm32'*)
    _OS_ARCH="arm32";
;;
'arm'*)
    _OS_ARCH="arm32";
;;
*)
    _OS_NAME="amd64";
;;
esac

_TOOL_CHAIN="-DCMAKE_TOOLCHAIN_FILE=../toolchain-${_OS_NAME}-${_OS_ARCH}.cmake.txt"

CURPATH=$(pwd)
TARGET_CLASSES_PATH="target/classes/linux-arm32-vfp-hflt"
TARGET_PATH="target"

exitWithError() {
  cd ${CURPATH}
  echo "*** An error occured. Please check log messages. ***"
  exit $1
}

mkdir -p "$TARGET_CLASSES_PATH"

cd "$TARGET_PATH"
cmake $_TOOL_CHAIN ../../../ || exitWithError $?
make || exitWithError $?

rm -f "$CURPATH/${TARGET_CLASSES_PATH}/libjserialport.so"
cp -v "./libjserialport.so" "$CURPATH/${TARGET_CLASSES_PATH}" || exitWithError $?

cd ${CURPATH}
