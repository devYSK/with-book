//ch13/ex13.13/ex13.13.go
package main

import "fmt"

func main() {
	str1 := "안녕하세요. 한글 문자열입니다."
	str2 := str1

	fmt.Printf(str1) // ❷
	fmt.Printf("\n")
	fmt.Printf(str2) // ❷
}
