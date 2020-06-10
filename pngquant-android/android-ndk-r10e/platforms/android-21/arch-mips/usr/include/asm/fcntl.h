/****************************************************************************
 ****************************************************************************
 ***
 ***   This header was automatically generated from a Linux kernel header
 ***   of the same name, to make information necessary for userspace to
 ***   call into the kernel available to libc.  It contains only constants,
 ***   structures, and macros generated from the original header, and thus,
 ***   contains no copyrightable information.
 ***
 ***   To edit the content of this header, modify the corresponding
 ***   source file (e.g. under external/kernel-headers/original/) then
 ***   run bionic/libc/kernel/tools/update_all.py
 ***
 ***   Any manual change here will be lost the next time this script will
 ***   be run. You've been warned!
 ***
 ****************************************************************************
 ****************************************************************************/
#ifndef _UAPI_ASM_FCNTL_H
#define _UAPI_ASM_FCNTL_H
#include <asm/sgidefs.h>
#define O_APPEND 0x0008
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#define O_DSYNC 0x0010
#define O_NONBLOCK 0x0080
#define O_CREAT 0x0100
#define O_TRUNC 0x0200
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#define O_EXCL 0x0400
#define O_NOCTTY 0x0800
#define FASYNC 0x1000
#define O_LARGEFILE 0x2000
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#define __O_SYNC 0x4000
#define O_SYNC (__O_SYNC|O_DSYNC)
#define O_DIRECT 0x8000
#define F_GETLK 14
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#define F_SETLK 6
#define F_SETLKW 7
#define F_SETOWN 24
#define F_GETOWN 23
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#ifndef __mips64
#define F_GETLK64 33
#define F_SETLK64 34
#define F_SETLKW64 35
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#endif
#if _MIPS_SIM != _MIPS_SIM_ABI64
#include <linux/types.h>
struct flock {
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
 short l_type;
 short l_whence;
 __kernel_off_t l_start;
 __kernel_off_t l_len;
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
 long l_sysid;
 __kernel_pid_t l_pid;
 long pad[4];
};
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
#define HAVE_ARCH_STRUCT_FLOCK
#endif
#include <asm-generic/fcntl.h>
#endif
/* WARNING: DO NOT EDIT, AUTO-GENERATED CODE - SEE TOP FOR INSTRUCTIONS */
