// ch13/ex13.9/ex13.9.go
package main

import (
	"fmt"
	"strings"
)

func main() {
	str := "Hello 월드!"      // ❶ 한영 문자가 섞인 문자열
	for _, v := range str { // ❷ range를 이용한 순회
		fmt.Printf("타입:%T 값:%d 문자:%c\n", v, v, v) // ❸ 출력
	}

	isContain := strings.Contains(str, "Hello") // ❹ 문자열에 "Hello"가 포함되어 있는지 확인

	fmt.Println(isContain) // ➎ 결과 출력
}
