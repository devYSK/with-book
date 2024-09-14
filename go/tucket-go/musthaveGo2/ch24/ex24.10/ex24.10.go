package main

import (
	"cmp"
	"fmt"
	"slices"
)

func main() {
	type Person struct { // 1
		Name string
		Age  int
	}
	people := []Person{
		{"Alice", 55},
		{"Bob", 24},
		{"Gopher", 13},
	}
	n, found := slices.BinarySearchFunc(people, "Bob", func(a Person, b string) int {
		return cmp.Compare(a.Name, b) // 2
	})
	fmt.Println("Bob:", n, found)
}
