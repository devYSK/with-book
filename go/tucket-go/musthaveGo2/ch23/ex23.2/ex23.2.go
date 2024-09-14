//ch23/ex23.2/ex23.2.go
package main

import "fmt"

func main() {
	ch := make(chan int) // ❶ 크기 0인 채널 생성

	ch <- 9                    // ❷ main() 함수가 여기서 멈춘다
	fmt.Println("Never print") // ❸ 실행되지 않는다
}
