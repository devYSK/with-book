//ch13/ex13.15/ex13.15.go
package main

import "fmt"

func main() {
	var str string = "Hello World"
	var slice []byte = []byte(str) // ❶ 슬라이스로 타입 변환

	slice[2] = 'a' // ❷ 3번째 문자 변경

	fmt.Println(str)
	fmt.Printf("%s\n", slice)
}
