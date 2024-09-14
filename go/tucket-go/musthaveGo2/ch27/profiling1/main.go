package main

import (
	"fmt"
	"log"
	"os"
	"runtime/pprof"
	"time"
)

func Fib(n int) int {
	if n == 0 {
		return 0
	} else if n == 1 {
		return 1
	} else {
		return Fib(n-1) + Fib(n-2)
	}
}
func main() {
	// 1 프로파일링 결과를 저장할 파일을 만듭니다
	f, err := os.Create("cpu.prof")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()
	// 2 프로파일링을 시작합니다.
	pprof.StartCPUProfile(f)
	// 3 프로그램 종료 전에 프로파일링을 종료합니다
	defer pprof.StopCPUProfile()
	fmt.Println("연산 시작")
	fmt.Println(Fib(10))
	// 10초를 대기합니다.
	fmt.Println("10초 대기합니다.")
	time.Sleep(10 * time.Second)
}
