//ch4/ex4.10/ex4.10.go
package main

import "fmt"

func main() {
	var a int = 10
	var b int = 20

	a, b = b, a // ❶ a와 b값을 서로 바꿉니다.

	fmt.Println(a, b)

	c := 30
	d := 40
	e := 50

	c, d, e = e, d, c

	fmt.Println(c, d, e)
}
