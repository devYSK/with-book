//ch18/ex18.8/ex18.8.go
package main

type Stringer interface {
	String() string
}

type Student struct {
}

func main() {
	var stringer Stringer
	stringer.(*Student) // ❶ 빌드 타임 에러 발생
}
