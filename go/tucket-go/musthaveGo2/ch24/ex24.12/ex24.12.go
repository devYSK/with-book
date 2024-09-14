package main

import (
	"fmt"
	"slices"
)

func main() {
	s1 := []int{1, 2, 3}
	s2 := []int{4, 5, 6, 7}
	s3 := []int{8, 9, 10}
	s := slices.Concat(s1, s2, s3) // 1
	fmt.Println(s)
}
