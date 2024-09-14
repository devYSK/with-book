package main

import "fmt"

func main() {
	var v1 int = 3
	var v2 interface{} = &v1  // boxing
	var v3 int = *(v2.(*int)) // unboxing
	fmt.Printf("v1: %x %T\n", &v1, &v1)
	fmt.Printf("v2: %x %T\n", &v2, &v2)
	fmt.Printf("v3: %x %T\n", &v3, &v3)
}
