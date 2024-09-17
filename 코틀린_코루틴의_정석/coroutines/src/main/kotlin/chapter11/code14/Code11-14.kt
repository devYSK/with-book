package chapter11.code14

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
  val job = launch(start = CoroutineStart.ATOMIC) {
    println("작업1")
  }
  job.cancel() // 실행 대기 상태의 코루틴에 취소 요청
  println("작업2")
}
/*
// 결과:
작업2
작업1
*/