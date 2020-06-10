#!/bin/sh
#
# Copyright (C) 2011 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# This script is used to rebuild the host 'ndk-stack' tool from the
# sources under sources/host-tools/ndk-stack.
#
# Note: The tool is installed under prebuilt/$HOST_TAG/bin/ndk-stack
#       by default.
#
PROGDIR=$(dirname $0)
. $PROGDIR/prebuilt-common.sh

PROGRAM_PARAMETERS=""
PROGRAM_DESCRIPTION=\
"This script is used to rebuild the host ndk-stack binary program."

register_jobs_option

OPTION_BUILD_DIR=
BUILD_DIR=
register_var_option "--build-dir=<path>" BUILD_DIR "Specify build directory"

NDK_DIR=$ANDROID_NDK_ROOT
register_var_option "--ndk-dir=<path>" NDK_DIR "Place binary in NDK installation path"

GNUMAKE=
register_var_option "--make=<path>" GNUMAKE "Specify GNU Make program"

DEBUG=
register_var_option "--debug" DEBUG "Build debug version"

WITH_LIBBFD=
register_var_option "--with-libbfd" WITH_LIBBFD "Link with libbfd.a instead of elff/. Need to set --src-dir= too."

SRC_DIR=
register_var_option "--src-dir=<path>" SRC_DIR "Specify binutils source dir.  Must be set for --with-libbfd"

PROGNAME=ndk-stack
register_var_option "--program-name=<name>" PROGNAME "Alternate NDK tool program name"

PACKAGE_DIR=
register_var_option "--package-dir=<path>" PACKAGE_DIR "Archive binary into specific directory"

register_canadian_option
register_try64_option

extract_parameters "$@"

if [ "$WITH_LIBBFD" ]; then
    if [ -z "$SRC_DIR" ]; then
        echo "ERROR: Missing source directory parameter. See --help for details."
        exit 1
    fi
fi

prepare_abi_configure_build
prepare_host_build

# Choose a build directory if not specified by --build-dir
if [ -z "$BUILD_DIR" ]; then
    BUILD_DIR=$NDK_TMPDIR/build-$PROGNAME
    log "Auto-config: --build-dir=$BUILD_DIR"
else
    OPTION_BUILD_DIR="yes"
fi

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR

prepare_canadian_toolchain $BUILD_DIR

CFLAGS=$HOST_CFLAGS" -O2 -s -ffunction-sections -fdata-sections"
LDFLAGS=$HOST_LDFLAGS
EXTRA_CONFIG=

if [ "$HOST_OS" != "darwin" -a "$DARWIN" != "yes" ]; then
    LDFLAGS=$LDFLAGS" -Wl,-gc-sections"
else
    # In darwin libbfd has to be built with some *linux* target or it won't understand ELF
    EXTRA_CONFIG="-target=arm-linux-androideabi"
fi

BINUTILS_BUILD_DIR=$BUILD_DIR/binutils
BINUTILS_SRC_DIR=$SRC_DIR/binutils/binutils-$RECENT_BINUTILS_VERSION
if [ "$WITH_LIBBFD" ]; then
    # build binutils first
    if [ -z "$ABI_CONFIGURE_HOST" ]; then
        ABI_CONFIGURE_HOST=$ABI_CONFIGURE_BUILD
    fi
    run mkdir -p $BINUTILS_BUILD_DIR
    run export CC CFLAGS LDFLAGS
    run cd $BINUTILS_BUILD_DIR && \
    run $BINUTILS_SRC_DIR/configure \
        --host=$ABI_CONFIGURE_HOST \
        --build=$ABI_CONFIGURE_BUILD \
        --disable-nls \
        --with-bug-report-url=$DEFAULT_ISSUE_TRACKER_URL \
	$EXTRA_CONFIG
    fail_panic "Can't configure $BINUTILS_SRC_DIR"
    run make -j$NUM_JOBS
    fail_panic "Can't build $BINUTILS_SRC_DIR"

fi

OUT=$NDK_DIR/$(get_host_exec_name $PROGNAME)

# GNU Make
if [ -z "$GNUMAKE" ]; then
    GNUMAKE=make
    log "Auto-config: --make=$GNUMAKE"
fi

if [ "$PACKAGE_DIR" ]; then
    mkdir -p "$PACKAGE_DIR"
    fail_panic "Could not create package directory: $PACKAGE_DIR"
fi

# Create output directory
mkdir -p $(dirname $OUT)
if [ $? != 0 ]; then
    echo "ERROR: Could not create output directory: $(dirname $OUT)"
    exit 1
fi

SRCDIR=$ANDROID_NDK_ROOT/sources/host-tools/$PROGNAME

# Let's roll
if [ "$WITH_LIBBFD" ]; then
    CFLAGS="$CFLAGS \
       -DWITH_LIBBFD=1 \
       -DHAVE_CONFIG_H \
       -I$BINUTILS_BUILD_DIR/binutils \
       -I$BINUTILS_SRC_DIR/binutils \
       -I$BINUTILS_BUILD_DIR/bfd \
       -I$BINUTILS_SRC_DIR/bfd \
       -I$BINUTILS_SRC_DIR/include \
       "
    LDFLAGS="$LDFLAGS \
       $BINUTILS_BUILD_DIR/binutils/bucomm.o \
       $BINUTILS_BUILD_DIR/binutils/version.o \
       $BINUTILS_BUILD_DIR/binutils/filemode.o \
       $BINUTILS_BUILD_DIR/bfd/libbfd.a \
       $BINUTILS_BUILD_DIR/libiberty/libiberty.a \
       "
    if [ "$MINGW" != "yes" ]; then
        LDFLAGS="$LDFLAGS -ldl -lz"
    fi
fi

export CFLAGS LDFLAGS
run $GNUMAKE -C $SRCDIR -f $SRCDIR/GNUmakefile \
    -B -j$NUM_JOBS \
    PROGNAME="$OUT" \
    WITH_LIBBFD=$WITH_LIBBFD \
    BUILD_DIR="$BUILD_DIR" \
    CC="$CC" CXX="$CXX" \
    STRIP="$STRIP" \
    DEBUG=$DEBUG

if [ $? != 0 ]; then
    echo "ERROR: Could not build host program!"
    exit 1
fi

if [ "$PACKAGE_DIR" ]; then
    ARCHIVE=$PROGNAME-$HOST_TAG.tar.bz2
    SUBDIR=$(get_host_exec_name $PROGNAME $HOST_TAG)
    dump "Packaging: $ARCHIVE"
    pack_archive "$PACKAGE_DIR/$ARCHIVE" "$NDK_DIR" "$SUBDIR"
    fail_panic "Could not create package: $PACKAGE_DIR/$ARCHIVE from $OUT"
fi

if [ "$OPTION_BUILD_DIR" != "yes" ]; then
    log "Cleaning up..."
    rm -rf $BUILD_DIR
else
    log "Don't forget to cleanup: $BUILD_DIR"
fi

log "Done!"
exit 0
