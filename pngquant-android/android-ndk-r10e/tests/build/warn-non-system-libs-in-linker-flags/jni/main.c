#include <stdio.h>

extern int foo(void);

int main(void) {
  printf("foo=%d\n", foo());
  return 0;
}
