package main

import (
	"fmt"
	"maps"
)

func main() {
	m1 := map[string]int{
		"one": 1,
		"two": 2,
	}
	m2 := map[string]int{
		"one": 10,
	}
	maps.Copy(m2, m1) // 1 copy m1 to m2
	fmt.Println("m2 is:", m2)
	m2["one"] = 100
	fmt.Println("m1 is:", m1)
	fmt.Println("m2 is:", m2)
	m3 := map[string][]int{
		"one": {1, 2, 3},
		"two": {4, 5, 6},
	}
	m4 := map[string][]int{
		"one": {7, 8, 9},
	}
	maps.Copy(m4, m3) // 2 copy m3 to m4
	fmt.Println("m4 is:", m4)
	m4["one"][0] = 100 // 3 change m4 value
	fmt.Println("m3 is:", m3)
	fmt.Println("m4 is:", m4)
}
