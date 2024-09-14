package main

import (
	"fmt"
	"maps"
	"strconv"
)

func main() {
	m1 := map[int]string{
		1:    "1",
		10:   "10",
		1000: "1000",
	}
	m2 := map[int]int{
		1:    1,
		10:   10,
		1000: 1000,
	}
	eq := maps.EqualFunc(m1, m2, func(v1 string, v2 int) bool { // 1
		i1, err := strconv.Atoi(v1)
		if err != nil {
			return false
		}
		return i1 == v2
	})
	fmt.Println(eq)
}
