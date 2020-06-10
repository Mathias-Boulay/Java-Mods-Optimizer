//===-------------------- test_exception_storage.cpp ----------------------===//
//
//                     The LLVM Compiler Infrastructure
//
// This file is dual licensed under the MIT and the University of Illinois Open
// Source Licenses. See LICENSE.TXT for details.
//
//===----------------------------------------------------------------------===//

#include "../src/config.h"

#include <cstdlib>
#include <algorithm>
#include <iostream>
#if !LIBCXXABI_SINGLE_THREADED
#  include <pthread.h>
#endif
#include <unistd.h>

#include "../src/cxa_exception.hpp"

typedef __cxxabiv1::__cxa_eh_globals globals_t ;

void *thread_code (void *parm) {
    size_t *result = (size_t *) parm;
    globals_t *glob1, *glob2;
    
    glob1 = __cxxabiv1::__cxa_get_globals ();
    if ( NULL == glob1 ) {
        std::cerr << "Got null result from __cxa_get_globals" << std::endl;
        return 0;
    }

    glob2 = __cxxabiv1::__cxa_get_globals_fast ();
    if ( glob1 != glob2 ) {
        std::cerr << "Got different globals!" << std::endl;
        return 0;
    }

    *result = (size_t) glob1;
#if !LIBCXXABI_SINGLE_THREADED
    sleep ( 1 );
#endif
    return parm;
    }

#if !LIBCXXABI_SINGLE_THREADED
#define NUMTHREADS  10
size_t      thread_globals [ NUMTHREADS ] = { 0 };
pthread_t   threads        [ NUMTHREADS ];
#endif

void print_sizes ( size_t *first, size_t *last ) {
    std::cout << "{ " << std::hex;
    for ( size_t *iter = first; iter != last; ++iter )
        std::cout << *iter << " ";
    std::cout << "}" << std::dec << std::endl;
    }

int main ( int argc, char *argv [] ) {
    int retVal = 0;

#if LIBCXXABI_SINGLE_THREADED
    size_t thread_globals;
    retVal = thread_code(&thread_globals) != &thread_globals;
#else
//  Make the threads, let them run, and wait for them to finish
    for ( int i = 0; i < NUMTHREADS; ++i )
        pthread_create( threads + i, NULL, thread_code, (void *) (thread_globals + i));
    for ( int i = 0; i < NUMTHREADS; ++i )
        pthread_join ( threads [ i ], NULL );

    for ( int i = 0; i < NUMTHREADS; ++i )
        if ( 0 == thread_globals [ i ] ) {
            std::cerr << "Thread #" << i << " had a zero global" << std::endl;
            retVal = 1;
            }
        
//  print_sizes ( thread_globals, thread_globals + NUMTHREADS );
    std::sort ( thread_globals, thread_globals + NUMTHREADS );
    for ( int i = 1; i < NUMTHREADS; ++i )
        if ( thread_globals [ i - 1 ] == thread_globals [ i ] ) {
            std::cerr << "Duplicate thread globals (" << i-1 << " and " << i << ")" << std::endl;
            retVal = 2;
            }
//  print_sizes ( thread_globals, thread_globals + NUMTHREADS );

#endif
    return retVal;
    }
