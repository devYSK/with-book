// ch16/ex16.12/ex16.12.go
package main

import (
	"fmt"
	"sort"
)

func main() {
	s := []int{5, 2, 6, 3, 1, 4} // ❶ 정렬되지 않은 슬라이스
	sort.Ints(s)                 // ❷ 정렬

	fmt.Println(s)
	sort.Sort(sort.Reverse(sort.IntSlice(s))) // ❹ 역순 정렬

	fmt.Println(s)

}
