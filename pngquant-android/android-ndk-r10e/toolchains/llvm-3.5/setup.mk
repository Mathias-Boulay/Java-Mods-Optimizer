# Copyright (C) 2014 The Android Open Source Project
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

# this file is used to prepare the NDK to build with the clang-3.5
# toolchain any number of source files
#
# its purpose is to define (or re-define) templates used to build
# various sources into target object files, libraries or executables.
#
# Note that this file may end up being parsed several times in future
# revisions of the NDK.
#

TOOLCHAIN_VERSION := 4.9

ifneq ($(filter %bcarmeabi-v7a,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi-v7a
TARGET_ARM_MODE  := arm
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/arm-linux-androideabi-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/arm-linux-androideabi-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcarmeabi-v7a-hard,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi-v7a-hard
TARGET_ARM_MODE  := arm
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/arm-linux-androideabi-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/arm-linux-androideabi-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcarmeabi,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi
TARGET_ARM_MODE  := arm
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/arm-linux-androideabi-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/arm-linux-androideabi-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcx86,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-x86
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-x86/gdbserver/gdbserver
TARGET_ARCH_ABI  := x86
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/x86-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/i686-linux-android-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcmips,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-mips
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-mips/gdbserver/gdbserver
TARGET_ARCH_ABI  := mips
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/mipsel-linux-android-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/mipsel-linux-android-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcarm64-v8a,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm64/gdbserver/gdbserver
TARGET_ARCH_ABI  := arm64-v8a
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/aarch64-linux-android-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/aarch64-linux-android-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)
UNKNOWN_ABI_64   := true

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcx86_64,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-x86_64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-x86_64/gdbserver/gdbserver
TARGET_ARCH_ABI  := x86_64
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/x86_64-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/x86_64-linux-android-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)
UNKNOWN_ABI_64   := true

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %bcmips64,$(TARGET_ARCH_ABI)),)
SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-mips64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-mips64/gdbserver/gdbserver
TARGET_ARCH_ABI  := mips64
TARGET_LDFLAGS   += -Wl,-link-native-binary
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)

TARGET_PREBUILT_ROOT = $(call host-prebuilt-tag,$(NDK_ROOT)/toolchains/mips64el-linux-android-$(TOOLCHAIN_VERSION))
cmd-strip = $(TARGET_PREBUILT_ROOT)/bin/mips64el-linux-android-strip$(HOST_EXEEXT) --strip-unneeded $(call host-path,$1)
UNKNOWN_ABI_64   := true

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

else
ifneq ($(filter %armeabi-v7a,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi-v7a
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/armeabi-v7a/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/armeabi-v7a/libportable.wrap
include $(NDK_ROOT)/toolchains/arm-linux-androideabi-clang3.5/setup.mk

else
ifneq ($(filter %armeabi-v7a-hard,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi-v7a-hard
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/armeabi-v7a-hard/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/armeabi-v7a-hard/libportable.wrap
include $(NDK_ROOT)/toolchains/arm-linux-androideabi-clang3.5/setup.mk

else
ifneq ($(filter %armeabi,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm/gdbserver/gdbserver
TARGET_ARCH_ABI  := armeabi
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/armeabi/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/armeabi/libportable.wrap
include $(NDK_ROOT)/toolchains/arm-linux-androideabi-clang3.5/setup.mk

else
ifneq ($(filter %x86,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-x86
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-x86/gdbserver/gdbserver
TARGET_ARCH_ABI  := x86
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/x86/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/x86/libportable.wrap
include $(NDK_ROOT)/toolchains/x86-clang3.5/setup.mk

else
ifneq ($(filter %mips,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-mips
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-mips/gdbserver/gdbserver
TARGET_ARCH_ABI  := mips
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/mips/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/mips/libportable.wrap
include $(NDK_ROOT)/toolchains/mipsel-linux-android-clang3.5/setup.mk

else
ifneq ($(filter %arm64-v8a,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-arm64/gdbserver/gdbserver
TARGET_ARCH_ABI  := arm64-v8a
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/arm64-v8a/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/arm64-v8a/libportable.wrap
include $(NDK_ROOT)/toolchains/aarch64-linux-android-clang3.5/setup.mk

else
ifneq ($(filter %x86_64,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-x86_64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-x86_64/gdbserver/gdbserver
TARGET_ARCH_ABI  := x86_64
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/x86_64/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/x86_64/libportable.wrap
include $(NDK_ROOT)/toolchains/x86_64-clang3.5/setup.mk

else
ifneq ($(filter %mips64,$(TARGET_ARCH_ABI)),)

SYSROOT_LINK     := $(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-mips64
TARGET_GDBSERVER := $(NDK_ROOT)/prebuilt/android-mips64/gdbserver/gdbserver
TARGET_ARCH_ABI  := mips64
NDK_APP_DST_DIR  := $(NDK_APP_PROJECT_PATH)/libs/$(TARGET_ARCH_ABI)
TARGET_LDLIBS    := $(NDK_ROOT)/sources/android/libportable/libs/mips64/libportable.a $(TARGET_LDLIBS)
TARGET_LDFLAGS   += -Wl,@$(NDK_ROOT)/sources/android/libportable/libs/mips64/libportable.wrap
include $(NDK_ROOT)/toolchains/mips64el-linux-android-clang3.5/setup.mk

else

# if unknown ABI ends with 64 suffix then use le64
ifneq ($(filter %64,$(APP_ABI)),)
UNKNOWN_ABI_64 := true
NDK_APP_DST_DIR := $(NDK_APP_LIBS_OUT)/$(TARGET_ARCH_ABI)64
endif

TARGET_OBJ_EXTENSION := .bc
TARGET_LIB_EXTENSION := .a
TARGET_SONAME_EXTENSION := .bc

include $(NDK_ROOT)/toolchains/llvm-3.5/setup-common.mk

endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
endif
