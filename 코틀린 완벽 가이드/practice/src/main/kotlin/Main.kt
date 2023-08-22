import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.io.File
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class Coroutine {


}
suspend fun main() {

    val async = CoroutineScope(Dispatchers.Default).launch {
        println("start launch")
        delay(100)
        println("end launch")
    }


    val async1 = GlobalScope.launch {
        println("launch1 start")
    }

    val async2 = GlobalScope.launch {
        println("launch2 start")
    }

    async.join()
    async1.join()
    async2.join()
    println("main end")
}
