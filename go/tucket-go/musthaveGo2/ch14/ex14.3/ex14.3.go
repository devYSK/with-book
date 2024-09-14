//ch14/ex14.3/ex14.3.go
package main

import (
	"ch14/ex14.3/ex14init" // ⓬ exinit 패키지 임포트
	"fmt"
)

func main() { // ⓭ main() 함수
	fmt.Println("main function")
	exinit.PrintD()
}
