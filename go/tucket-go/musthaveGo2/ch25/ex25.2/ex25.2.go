// ch25/ex25.2/ex25.2.go
package main

import (
	"bufio"
	"fmt"
	"os"
)

func main() {
	// 현재 작업 디렉토리를 출력합니다.
	dir, err := os.Getwd()

	if err != nil {
		fmt.Println("현재 작업 디렉토리 가져오기 오류:", err)
		return
	}
	fmt.Println("현재 작업 디렉토리:", dir)

	PrintFile("hamlet.txt")
}

func PrintFile(filename string) {
	file, err := os.Open(filename) // ❶ 파일 열기
	if err != nil {
		fmt.Println("파일을 찾을 수 없습니다. ", filename, err)
		return
	}
	defer file.Close() // ❷ 함수 종료 전에 파일 닫기

	scanner := bufio.NewScanner(file) // ❸ 스캐너를 생성해서 한 줄씩 읽기

	for scanner.Scan() {
		fmt.Println(scanner.Text())
	}

}
