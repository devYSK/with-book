//ch5/ex5.1/ex5.1.go
package main

import "fmt"

func Add(a int, b int) int { // ❶
	return a + b // ❷
}

func main() {
	c := Add(3, 6) // ❸
	fmt.Println(c) // ➍
}
