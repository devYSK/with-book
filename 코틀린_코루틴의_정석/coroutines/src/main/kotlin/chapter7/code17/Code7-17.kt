package chapter7.code17

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
  launch(CoroutineName("Coroutine1")) {
    launch(CoroutineName("Coroutine3")) {
      println("[${Thread.currentThread().name}] 코루틴 실행")
    }
    launch(CoroutineName("Coroutine4")) {
      println("[${Thread.currentThread().name}] 코루틴 실행")
    }
  }

  launch(CoroutineName("Coroutine2")) {
    println("[${Thread.currentThread().name}] 코루틴 실행")
  }
}