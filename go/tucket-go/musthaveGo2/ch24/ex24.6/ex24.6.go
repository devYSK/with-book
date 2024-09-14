package main

import "fmt"

type Node[T any] struct {
	val  T
	next *Node[T]
}

func NewNode[T any](v T) *Node[T] { // 1 T 타입의 val 필드값을 갖는 객체 생성
	return &Node[T]{val: v}
}

func (n *Node[T]) Push(v T) *Node[T] { // 2 Node[T] 타입의 메서드 정의
	node := NewNode(v)
	n.next = node
	return node
}
func main() {
	node1 := NewNode(1) // 3 객체 생성
	node1.Push(2).Push(3).Push(4)
	for node1 != nil {
		fmt.Print(node1.val, " - ")
		node1 = node1.next
	}
	fmt.Println()
	node2 := NewNode("Hi") // 4 객체 생성
	node2.Push("Hello").Push("How are you")
	for node2 != nil {
		fmt.Print(node2.val, " - ")
		node2 = node2.next
	}
	fmt.Println()
}
