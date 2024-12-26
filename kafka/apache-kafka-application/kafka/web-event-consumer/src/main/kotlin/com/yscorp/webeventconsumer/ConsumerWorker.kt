package com.yscorp.webeventconsumer

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FSDataOutputStream
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ConsumerWorker(prop: Properties, topic: String, number: Int) : Runnable {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerWorker::class.java)
    private val prop: Properties
    private val topic: String
    private val threadName: String
    private var consumer: KafkaConsumer<String, String>? = null

    init {
        logger.info("Generate ConsumerWorker")
        this.prop = prop
        this.topic = topic
        this.threadName = "consumer-thread-$number"
    }

    override fun run() {
        Thread.currentThread().name = threadName
        consumer = KafkaConsumer(prop)
        consumer!!.subscribe(Arrays.asList(topic))
        try {
            while (true) {
                val records = consumer!!.poll(Duration.ofSeconds(1))

                for (record in records) {
                    addHdfsFileBuffer(record)
                }
                saveBufferToHdfsFile(consumer!!.assignment())
            }
        } catch (e: WakeupException) {
            logger.warn("Wakeup consumer")
        } catch (e: Exception) {
            logger.error(e.message, e)
        } finally {
            consumer!!.close()
        }
    }

    private fun addHdfsFileBuffer(record: ConsumerRecord<String, String>) {
        val buffer = bufferString.getOrDefault(record.partition(), ArrayList())
        buffer!!.add(record.value())
        bufferString[record.partition()] = buffer

        if (buffer.size == 1) currentFileOffset[record.partition()] = record.offset()
    }

    private fun saveBufferToHdfsFile(partitions: Set<TopicPartition>) {
        partitions.forEach { p: TopicPartition ->
            checkFlushCount(
                p.partition(),
            )
        }
    }

    private fun checkFlushCount(partitionNo: Int) {
        if (bufferString[partitionNo] != null) {
            if (bufferString[partitionNo]!!.size > FLUSH_RECORD_COUNT - 1) {
                save(partitionNo)
            }
        }
    }

    private fun save(partitionNo: Int) {
        if (bufferString[partitionNo]!!.size > 0) try {
            val fileName = "/data/color-" + partitionNo + "-" + currentFileOffset[partitionNo] + ".log"
            val configuration: Configuration = Configuration()
            configuration.set("fs.defaultFS", "hdfs://localhost:9000")
            val hdfsFileSystem: FileSystem = FileSystem.get(configuration)
            val fileOutputStream: FSDataOutputStream = hdfsFileSystem.create(Path(fileName))
            // FileOutputStream 사용
            fileOutputStream.use { fos ->
                val dataToWrite = bufferString[partitionNo]?.joinToString("\n")
                fos.write(dataToWrite?.toByteArray())
            }

            bufferString[partitionNo] = ArrayList()
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }

    private fun saveRemainBufferToHdfsFile() {
        bufferString.forEach { (partitionNo: Int, v: List<String>?) ->
            this.save(
                partitionNo
            )
        }
    }

    fun stopAndWakeup() {
        logger.info("stopAndWakeup")
        consumer!!.wakeup()
        saveRemainBufferToHdfsFile()
    }

    companion object {
        private val bufferString: MutableMap<Int, MutableList<String>?> = ConcurrentHashMap()
        private val currentFileOffset: MutableMap<Int, Long> = ConcurrentHashMap()

        private const val FLUSH_RECORD_COUNT = 10
    }
}