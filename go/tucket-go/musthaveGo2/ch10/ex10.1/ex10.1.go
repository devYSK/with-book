// ch10/ex10.1/ex10.1.go
package main

import "fmt"

func main() {
	var t = [5]float64{24.0, 25.9, 27.8, 26.9, 26.2} // ❶

	for i := 0; i < 5; i++ { // ❷
		fmt.Println(t[i]) // ❸
	}

	t[0] = 10.0

	fmt.Println(t)
	fmt.Println(t[0])

	c := t

	fmt.Println(c)

	c[1] = 9999.0
	fmt.Println(c)
	fmt.Println(t)
}
