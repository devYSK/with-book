// ch13/ex13.17/ex13.17.go
package main

import (
	"fmt"
	"unsafe"
)

func main() {
	var str string = "Hello"
	addr1 := unsafe.StringData(str) // 1

	str += " World"                 // 2
	addr2 := unsafe.StringData(str) // 3

	str += " Welcome!"              // 4
	addr3 := unsafe.StringData(str) // 5

	fmt.Println(str)
	fmt.Printf("addr1:\t%p\n", addr1)
	fmt.Printf("addr2:\t%p\n", addr2)
	fmt.Printf("addr3:\t%p\n", addr3)
}
