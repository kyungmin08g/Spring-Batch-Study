package com.kyungmin.springbatchstudy.batch.skip

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.sql.SQLException

/**
 * Reader Skip Batch Config Class
 */

@Configuration
class SkipReaderBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {
  private val log = KotlinLogging.logger {} // Kotlin용 Logger

  @Bean
  fun skipReaderJob(): Job = JobBuilder("skipReaderJob", jobRepo)
    .start(skipReaderStep())
    .build()

  @Bean
  fun skipReaderStep(): Step = StepBuilder("skipReaderStep", jobRepo)
    .chunk<String, String>(2, transactionManager)
    .reader(skipReader())
    .writer(skipWriter())
    .faultTolerant() // 내결함성 기능 활성화
    .skipLimit(2) // 몇번의 skip을 허용할 건지 (skip 허용 횟수)
    .skip(IllegalArgumentException::class.java) // skip할 예외
    .noSkip(SQLException::class.java) // 이건 skip 안돼!라고 할 예외 (어떤 예외에 대해서는 skip 하지 않음)
    .build()

  @Bean
  fun skipReader(): ItemReader<String> {
    var number = 0

    return ItemReader {
      number++

      if (number == 2) {
        log.error { "ItemReader ==> IllegalArgumentException 발생" }
        throw IllegalArgumentException("에러 발생")
      }

      if (number == 5) {
        log.error { "ItemReader ==> SQLException 발생" }
        throw SQLException("에러 발생")
      }

      log.info { "ItemReader >> $number" }
      number.toString()
    }
  }

  @Bean
  fun skipWriter(): ItemWriter<String?> {
    return ItemWriter {
      it.items.forEach { item ->
        println(item)
      }
    }
  }
}

/**
 * 실행결과
 *
 * 2025-10-19T15:15:21.616+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [SimpleJob: [name=skipReaderJob]] launched with the following parameters: [{'value':'{value=b, type=class java.lang.String, identifying=true}'}]
 * 2025-10-19T15:15:21.768+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [skipReaderStep]
 * 2025-10-19T15:15:21.800+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.batch.skip.SkipReaderBatch         : ItemReader >> 1
 * 2025-10-19T15:15:21.804+09:00 ERROR 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.batch.skip.SkipReaderBatch         : ItemReader ==> IllegalArgumentException 발생
 * 2025-10-19T15:15:21.804+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.batch.skip.SkipReaderBatch         : ItemReader >> 3
 * 1
 * 3
 * 2025-10-19T15:15:21.817+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.batch.skip.SkipReaderBatch         : ItemReader >> 4
 * 2025-10-19T15:15:21.817+09:00 ERROR 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.batch.skip.SkipReaderBatch         : ItemReader ==> SQLException 발생
 * 2025-10-19T15:15:21.828+09:00 ERROR 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.step.AbstractStep         : Encountered an error executing step skipReaderStep in job skipReaderJob
 *
 * org.springframework.batch.core.step.skip.NonSkippableReadException: Non-skippable exception during read
 * 	at org.springframework.batch.core.step.item.FaultTolerantChunkProvider.read(FaultTolerantChunkProvider.java:104) ~[spring-batch-core-5.2.3.jar:5.2.3]
 * 	at org.springframework.batch.core.step.item.SimpleChunkProvider.lambda$provide$0(SimpleChunkProvider.java:132) ~[spring-batch-core-5.2.3.jar:5.2.3]
 * 	중략..
 * Caused by: java.sql.SQLException: 에러 발생
 * 	at com.kyungmin.springbatchstudy.batch.skip.SkipReaderBatch.skipReader$lambda$0(SkipReaderBatch.kt:55) ~[main/:na]
 * 	at org.springframework.batch.core.step.item.SimpleChunkProvider.doRead(SimpleChunkProvider.java:108) ~[spring-batch-core-5.2.3.jar:5.2.3]
 * 	at org.springframework.batch.core.step.item.FaultTolerantChunkProvider.read(FaultTolerantChunkProvider.java:86) ~[spring-batch-core-5.2.3.jar:5.2.3]
 * 	... 77 common frames omitted
 *
 * 2025-10-19T15:15:21.839+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.step.AbstractStep         : Step: [skipReaderStep] executed in 70ms
 * 2025-10-19T15:15:21.867+09:00  INFO 28231 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [SimpleJob: [name=skipReaderJob]] completed with the following parameters: [{'value':'{value=b, type=class java.lang.String, identifying=true}'}] and the following status: [FAILED] in 218ms
 */