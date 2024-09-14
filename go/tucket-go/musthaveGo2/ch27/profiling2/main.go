package main

import (
	"fmt"
	"log"
	"os"
	"runtime/pprof"
	"time"
)

// 1 피보나치 결과를 저장할 맵
var fibMap [65535]int

func Fib(n int) int {
	// 2 이미 계산했다면 바로 반환합니다.
	f := fibMap[n]
	if f > 0 {
		return f
	}
	if n == 0 {
		return 0
	} else if n == 1 {
		f = 1
	} else {
		f = Fib(n-1) + Fib(n-2)
	}
	// 3 결과를 저장합니다.
	fibMap[n] = f
	return f
}
func main() {
	f, err := os.Create("cpu.prof")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()
	pprof.StartCPUProfile(f)
	defer pprof.StopCPUProfile()
	fmt.Println(Fib(50))
	// 10초를 대기합니다.
	time.Sleep(10 * time.Second)
}
