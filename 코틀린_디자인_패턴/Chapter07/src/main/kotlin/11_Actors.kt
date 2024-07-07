import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking

@OptIn(ObsoleteCoroutinesApi::class)
fun main() {
    runBlocking {
        val actor = actor<Int> {
            channel.consumeEach {
                println(it)
            }
        }

        (1..10).forEach {
            actor.send(it)
        }
    }
}