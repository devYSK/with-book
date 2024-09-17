package chapter1.code10

import com.sun.org.slf4j.internal.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


class Abc {
    companion object {
      val log = LoggerFactory.getLogger(this::class.java)
    }

}

fun main() {
  val startTime = System.currentTimeMillis()
  val executor = Executors.newFixedThreadPool(2)

  // CompletableFuture 생성 및 비동기 작업 실행
  val completableFuture = CompletableFuture.supplyAsync({
    Thread.sleep(1000L) // 1초간 대기
    return@supplyAsync "결과" // 결과 반환
  }, executor)

  // 비동기 작업 완료 후 결과 처리를 위한 체이닝 함수 등록
  completableFuture.thenAccept { result ->
    Abc.Companion.log.error("result: $result")
    println("[${getElapsedTime(startTime)}] $result 처리") // 결과 처리 출력
  }

  // 비동기 작업 실행 도중 다른 작업 실행
  println("[${getElapsedTime(startTime)}] 다른 작업 실행")
  Abc.Companion.log.error("완료 로그 ")

  executor.shutdown()
}
/*
// 결과:
[지난 시간: 11ms] 다른 작업 실행
[지난 시간: 1008ms] 결과 처리
*/

fun getElapsedTime(startTime: Long): String =
  "지난 시간: ${System.currentTimeMillis() - startTime}ms"