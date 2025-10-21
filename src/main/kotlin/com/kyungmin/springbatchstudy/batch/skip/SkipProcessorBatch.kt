package com.kyungmin.springbatchstudy.batch.skip

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.sql.SQLException

/**
 * Processor Skip Batch Config Class
 */

@Configuration
class SkipProcessorBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {
  private val log = KotlinLogging.logger {}

  @Bean
  fun skipProcessorJob(): Job = JobBuilder("skipProcessorJob", jobRepo)
    .start(skipProcessorStep())
    .build()

  @Bean
  fun skipProcessorStep(): Step = StepBuilder("skipProcessorStep", jobRepo)
    .chunk<String, String>(5, transactionManager)
    .reader(skipProcessorReader())
    .processor(skipProcessor())
    .writer(skipProcessorWriter())
    .faultTolerant() // 내결함성 기능 활성화
    .skipLimit(2) // 몇번의 skip을 허용할 건지 (skip 허용 횟수)
    .skip(IllegalArgumentException::class.java) // skip할 예외
    .skip(SQLException::class.java)
    .noSkip(NullPointerException::class.java) // 이건 skip 안돼!라고 할 예외 (어떤 예외에 대해서는 skip 하지 않음)
    .build()

  @Bean
  fun skipProcessorReader(): ItemReader<String> {
    var number = 0

    return ItemReader {
      number++

      if (number == 2) {
        log.error { "ItemReader ==> IllegalArgumentException 발생" }
        throw IllegalArgumentException("에러 발생")
      }

      log.info { "ItemReader >> $number" }

      val response = if (number > 20) {
        null
      } else {
        number.toString()
      }

      response
    }
  }

  @Bean
  fun skipProcessor(): ItemProcessor<String, String> {
    log.info { "SkipProcessor 실행" }
    return ItemProcessor {

      if (it.equals("4")) {
        log.error { "ItemProcessor ==> SQLException 발생" }
        throw SQLException("에러 발생")
      }

      it
    }
  }

  @Bean
  fun skipProcessorWriter(): ItemWriter<String?> {
    return ItemWriter {
      it.items.forEach { item ->
        log.info { "ItemWriter ==> $item" }
      }
    }
  }
}

/**
 * 실행결과
 *
 * : SkipProcessor 실행
 * : Tomcat started on port 8080 (http) with context path '/'
 * : Started SpringBatchStudyApplicationKt in 2.653 seconds (process running for 2.954)
 * : Initializing Spring DispatcherServlet 'dispatcherServlet'
 * : Initializing Servlet 'dispatcherServlet'
 * : Completed initialization in 10 ms
 * : Job: [SimpleJob: [name=skipProcessorJob]] launched with the following parameters: [{'value':'{value=7, type=class java.lang.String, identifying=true}'}]
 * : Executing step: [skipProcessorStep]
 * : ItemReader >> 1
 * : ItemReader ==> IllegalArgumentException 발생
 * : ItemReader >> 3
 * : ItemReader >> 4
 * : ItemReader >> 5
 * : ItemReader >> 6
 * : ItemProcessor ==> SQLException 발생
 * : ItemWriter ==> 1
 * : ItemWriter ==> 3
 * : ItemWriter ==> 5
 * : ItemWriter ==> 6
 * : ItemReader >> 7
 * : ItemReader >> 8
 * : ItemReader >> 9
 * : ItemReader >> 10
 * : ItemReader >> 11
 * : ItemWriter ==> 7
 * : ItemWriter ==> 8
 * : ItemWriter ==> 9
 * : ItemWriter ==> 10
 * : ItemWriter ==> 11
 */