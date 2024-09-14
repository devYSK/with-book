package main

import "fmt"

type NodeType1 struct { // 1 빈 인터페이스를 이용해 정의
	val  interface{}
	next *NodeType1
}
type NodeType2[T any] struct { // 2 타입 파라미터를 사용해서 제네릭 타입
	val  T
	next *NodeType2[T]
}

func main() {
	node1 := &NodeType1{val: 1} // 3 val 필드값으로 int 타입 사용
	node2 := &NodeType2[int]{val: 2}
	var v1 int = node1.val // 4 에러 발생
	fmt.Println(v1)
	var v2 int = node2.val // 5 문제 없음
	fmt.Println(v2)
}
