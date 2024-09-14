//ch10/ex10.2/ex10.2.go
package main

const Y int = 3 // ❶ 상수

func main() {
	x := 5                     // ❷ 변수
	a := [x]int{1, 2, 3, 4, 5} // ❸

	b := [Y]int{1, 2, 3} // ➍

	var c [10]int // ➎
}
