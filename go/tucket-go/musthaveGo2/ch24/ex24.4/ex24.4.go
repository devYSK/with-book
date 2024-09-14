package main

import (
	"fmt"
	"hash/fnv"
)

type ComparableHasher interface { // 1 타입 제한 정의
	comparable
	Hash() uint32
}
type MyString string // 2 별칭 타입 정의
func (s MyString) Hash() uint32 {
	h := fnv.New32a()
	h.Write([]byte(s))
	return h.Sum32()
}
func Equal[T ComparableHasher](a, b T) bool { // 3 제네릭 함수 정의
	if a == b {
		return true
	}
	return a.Hash() == b.Hash()
}
func main() {
	var str1 MyString = "Hello"
	var str2 MyString = "World"
	fmt.Println(Equal(str1, str2))
}
