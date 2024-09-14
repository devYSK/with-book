package main

import (
	"fmt"
	"slices"
)

func main() {
	names := []string{"Alice", "Bob", "Vera"}
	n, found := slices.BinarySearch(names, "Vera") // 1
	fmt.Println("Vera:", n, found)
	n, found = slices.BinarySearch(names, "Bill") // 2
	fmt.Println("Bill:", n, found)
}
