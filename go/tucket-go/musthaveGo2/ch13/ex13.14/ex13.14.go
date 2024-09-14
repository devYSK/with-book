// ch13/ex13.14/ex13.14.go
package main

import (
	"fmt"
	"unsafe"
)

type StringHeader struct {
	Data uintptr
	Len  int
}

func main() {
	str1 := "Hello World!"
	str2 := str1 // ❶ str1 변수값을 str2에 복사

	stringHeader1 := (*StringHeader)(unsafe.Pointer(&str1)) // ❷ Data값 추출
	stringHeader2 := (*StringHeader)(unsafe.Pointer(&str2)) // ❸  Data값 추출

	fmt.Println(stringHeader1) // ❹ 각 필드 값을 출력합니다.
	fmt.Println(stringHeader2)
}
