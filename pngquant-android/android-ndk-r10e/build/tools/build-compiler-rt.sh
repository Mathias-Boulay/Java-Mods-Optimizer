#!/bin/sh
#
# Copyright (C) 2013 The Android Open Source Project
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

# include common function and variable definitions
. `dirname $0`/prebuilt-common.sh
. `dirname $0`/builder-funcs.sh

PROGRAM_PARAMETERS=""

PROGRAM_DESCRIPTION=\
"Rebuild compiler-rt for the Android NDK.

This requires a temporary NDK installation containing
toolchain binaries for all target architectures.

By default, this will try with the current NDK directory, unless
you use the --ndk-dir=<path> option.

The output will be placed in appropriate sub-directories of
<ndk>/$COMPILER_RT_SUBDIR, but you can override this with the --out-dir=<path>
option.
"

PACKAGE_DIR=
register_var_option "--package-dir=<path>" PACKAGE_DIR "Put prebuilt tarballs into <path>."

NDK_DIR=
register_var_option "--ndk-dir=<path>" NDK_DIR "Specify NDK root path for the build."

SRC_DIR=
register_var_option "--src-dir=<path>" SRC_DIR "Specify compiler-rt source dir."

BUILD_DIR=
OPTION_BUILD_DIR=
register_var_option "--build-dir=<path>" OPTION_BUILD_DIR "Specify temporary build dir."

OUT_DIR=
register_var_option "--out-dir=<path>" OUT_DIR "Specify output directory directly."

ABIS="$PREBUILT_ABIS"
register_var_option "--abis=<list>" ABIS "Specify list of target ABIs."

NO_MAKEFILE=
register_var_option "--no-makefile" NO_MAKEFILE "Do not use makefile to speed-up build"

GCC_VERSION=
register_var_option "--gcc-version=<ver>" GCC_VERSION "Specify GCC version"

LLVM_VERSION=
register_var_option "--llvm-version=<ver>" LLVM_VERSION "Specify LLVM version"

register_jobs_option

extract_parameters "$@"

ABIS=$(commas_to_spaces $ABIS)

# Handle NDK_DIR
if [ -z "$NDK_DIR" ] ; then
    NDK_DIR=$ANDROID_NDK_ROOT
    log "Auto-config: --ndk-dir=$NDK_DIR"
else
    if [ ! -d "$NDK_DIR" ] ; then
        echo "ERROR: NDK directory does not exists: $NDK_DIR"
        exit 1
    fi
fi

if [ -z "$OPTION_BUILD_DIR" ]; then
    BUILD_DIR=$NDK_TMPDIR/build-compiler-rt
else
    BUILD_DIR=$OPTION_BUILD_DIR
fi
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"
fail_panic "Could not create build directory: $BUILD_DIR"

if [ -z "$SRC_DIR" -o ! -d "$SRC_DIR" ]; then
    dump "Could not found compiler-rt source directory: $SRC_DIR"
    dump "Use --src-dir=<dir> to specify source directory."
    exit 1
fi

# Compiler flags we want to use
COMPILER_RT_CFLAGS="-fPIC -O2 -DANDROID -D__ANDROID__ -ffunction-sections"
COMPILER_RT_CFLAGS=$COMPILER_RT_CFLAGS" -I$SRC_DIR/include -I$SRC_DIR/lib"
COMPILER_RT_LDFLAGS="-nostdlib"

# List of sources to compile
COMPILER_RT_GENERIC_SOURCES=$(cd $SRC_DIR && ls lib/builtins/*.c)

# filter out the sources we don't need
UNUSED_SOURCES="lib/builtins/apple_versioning.c lib/builtins/gcc_personality_v0.c"
COMPILER_RT_GENERIC_SOURCES=$(filter_out "$UNUSED_SOURCES" "$COMPILER_RT_GENERIC_SOURCES")

# ARM specific
COMPILER_RT_ARM_SOURCES="
lib/builtins/arm/aeabi_dcmp.S \
lib/builtins/arm/aeabi_fcmp.S \
lib/builtins/arm/aeabi_idivmod.S \
lib/builtins/arm/aeabi_ldivmod.S \
lib/builtins/arm/aeabi_memcmp.S \
lib/builtins/arm/aeabi_memcpy.S \
lib/builtins/arm/aeabi_memmove.S \
lib/builtins/arm/aeabi_memset.S \
lib/builtins/arm/aeabi_uidivmod.S \
lib/builtins/arm/aeabi_uldivmod.S \
lib/builtins/arm/comparesf2.S
lib/builtins/arm/divmodsi4.S
lib/builtins/arm/divsi3.S
lib/builtins/arm/modsi3.S
lib/builtins/arm/udivmodsi4.S
lib/builtins/arm/udivsi3.S
lib/builtins/arm/umodsi3.S
lib/builtins/arm/idiv0.c"

# X86 specific
COMPILER_RT_X86_SOURCES="
lib/builtins/i386/ashldi3.S \
lib/builtins/i386/ashrdi3.S \
lib/builtins/i386/divdi3.S \
lib/builtins/i386/floatdidf.S \
lib/builtins/i386/floatdisf.S \
lib/builtins/i386/floatdixf.S \
lib/builtins/i386/floatundidf.S \
lib/builtins/i386/floatundisf.S \
lib/builtins/i386/floatundixf.S \
lib/builtins/i386/lshrdi3.S \
lib/builtins/i386/moddi3.S \
lib/builtins/i386/muldi3.S \
lib/builtins/i386/udivdi3.S \
lib/builtins/i386/umoddi3.S"

# Mips specific
COMPILER_RT_MIPS_SOURCES=

# X86_64 specific
COMPILER_RT_X86_64_SOURCES="
lib/builtins/x86_64/floatdidf.c \
lib/builtins/x86_64/floatdisf.c \
lib/builtins/x86_64/floatdixf.c \
lib/builtins/x86_64/floatundidf.S \
lib/builtins/x86_64/floatundisf.S \
lib/builtins/x86_64/floatundixf.S
"

# If the --no-makefile flag is not used, we're going to put all build
# commands in a temporary Makefile that we will be able to invoke with
# -j$NUM_JOBS to build stuff in parallel.
#
if [ -z "$NO_MAKEFILE" ]; then
    MAKEFILE=$BUILD_DIR/Makefile
else
    MAKEFILE=
fi

# prepare_compiler_rt_source_for_abi
# $1: ABI
prepare_compiler_rt_source_for_abi ()
{
    local ABI=$1
    local ARCH_SOURCES GENERIC_SOURCES FOUND

    if [ $ABI = "armeabi" -o $ABI = "armeabi-v7a" -o $ABI = "armeabi-v7a-hard" ]; then
        ARCH_SOURCES="$COMPILER_RT_ARM_SOURCES"
    elif [ $ABI = "x86" ]; then
        ARCH_SOURCES="$COMPILER_RT_X86_SOURCES"
    elif [ $ABI = "mips" -o $ABI = "mips32r6" ]; then
        ARCH_SOURCES="$COMPILER_RT_MIPS_SOURCES"
    elif [ $ABI = "x86_64" ]; then
        ARCH_SOURCES="$COMPILER_RT_X86_64_SOURCES"
    fi

    for SOURCE in $COMPILER_RT_GENERIC_SOURCES; do
        FILENAME=`basename $SOURCE`
        FILENAME=$"${FILENAME/\.c/}"
        # if we have lib/$ABI/*.S, skip lib/*.c
        FOUND=$(echo $ARCH_SOURCES | grep $FILENAME)
        if [ -z "$FOUND" ]; then
            GENERIC_SOURCES="$GENERIC_SOURCES $SOURCE"
        fi
    done

    echo "$ARCH_SOURCES $GENERIC_SOURCES"
}

# build_compiler_rt_libs_for_abi
# $1: ABI
# $2: build directory
# $3: build type: "static" or "shared"
# $4: (optional) installation directory
build_compiler_rt_libs_for_abi ()
{
    local ARCH BINPREFIX
    local ABI=$1
    local BUILDDIR="$2"
    local TYPE="$3"
    local DSTDIR="$4"
    local GCCVER

    mkdir -p "$BUILDDIR"

    # If the output directory is not specified, use default location
    if [ -z "$DSTDIR" ]; then
        DSTDIR=$NDK_DIR/$COMPILER_RT_SUBDIR/libs/$ABI
    fi

    mkdir -p "$DSTDIR"

    if [ -n "$GCC_VERSION" ]; then
        GCCVER=$GCC_VERSION
    else
        ARCH=$(convert_abi_to_arch $ABI)
        GCCVER=$(get_default_gcc_version_for_arch $ARCH)
        if [ "$LLVM_VERSION" \> "3.4" ]; then
            # Turn on integrated-as for clang >= 3.5 otherwise file like
            # can't be compiled
            COMPILER_RT_CFLAGS="$COMPILER_RT_CFLAGS -fintegrated-as"
        fi
    fi

    if [ -z "$PLATFORM" ]; then
        PLATFORM="android-$FIRST_API64_LEVEL"
    fi

    builder_begin_android $ABI "$BUILDDIR" "$GCCVER" "$LLVM_VERSION" "$MAKEFILE" "$PLATFORM"
    builder_set_srcdir "$SRC_DIR"
    builder_set_dstdir "$DSTDIR"

    builder_cflags "$COMPILER_RT_CFLAGS"

    if [ $ABI = "armeabi" -o $ABI = "armeabi-v7a" -o $ABI = "armeabi-v7a-hard" ]; then
        builder_cflags "-D__ARM_EABI__"
        if [ $ABI = "armeabi-v7a-hard" ]; then
            builder_cflags "-mhard-float -D_NDK_MATH_NO_SOFTFP=1"
        fi
    fi

    builder_ldflags "$COMPILER_RT_LDFLAGS"
    if [ $ABI = "armeabi-v7a-hard" ]; then
        builder_cflags "-Wl,--no-warn-mismatch -lm_hard"
    fi

    builder_sources $(prepare_compiler_rt_source_for_abi $ABI)

    if [ "$TYPE" = "static" ]; then
        log "Building $DSTDIR/libcompiler_rt_static.a"
        builder_static_library libcompiler_rt_static
    else
        log "Building $DSTDIR/libcompiler_rt_shared.so"
        builder_ldflags "-lc"
        if [ $ABI != "armeabi-v7a-hard" ]; then
            builder_ldflags "-lm"
        fi
        builder_nostdlib_shared_library libcompiler_rt_shared
    fi
    builder_end
}

for ABI in $ABIS; do
    build_compiler_rt_libs_for_abi $ABI "$BUILD_DIR/$ABI/shared" "shared" "$OUT_DIR"
    build_compiler_rt_libs_for_abi $ABI "$BUILD_DIR/$ABI/static" "static" "$OUT_DIR"
done

# If needed, package files into tarballs
if [ -n "$PACKAGE_DIR" ] ; then
    for ABI in $ABIS; do
        FILES=""
        for LIB in libcompiler_rt_static.a libcompiler_rt_shared.so; do
            FILES="$FILES $COMPILER_RT_SUBDIR/libs/$ABI/$LIB"
        done
        PACKAGE="$PACKAGE_DIR/compiler-rt-libs-$ABI.tar.bz2"
        log "Packaging: $PACKAGE"
        pack_archive "$PACKAGE" "$NDK_DIR" "$FILES"
        fail_panic "Could not package $ABI compiler-rt binaries!"
        dump "Packaging: $PACKAGE"
    done
fi

if [ -z "$OPTION_BUILD_DIR" ]; then
    log "Cleaning up..."
    rm -rf $BUILD_DIR
else
    log "Don't forget to cleanup: $BUILD_DIR"
fi

log "Done!"
