package main

import (
	"fmt"

	"golang.org/x/exp/constraints"
)

// 1 제네릭 함수 정의
func add[T constraints.Integer | constraints.Float](a, b T) T {
	return a + b
}
func main() {
	var a int = 1
	var b int = 2
	fmt.Println(add(a, b))
	var f1 float64 = 3.14
	var f2 float64 = 1.43
	fmt.Println(add(f1, f2))
}
