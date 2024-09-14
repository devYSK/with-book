package main

type Integer interface { // 1 타입 제한
	int8 | int16 | int32 | int64 | int
}

func add[T Integer](a, b T) T { // 2 add() 함수 정의
	return a + b
}

type MyInt int // 3 별칭 타입 정의
func main() {
	add(1, 2)
	var a MyInt = 3
	var b MyInt = 5
	add(a, b) // 4 에러 발생
}
