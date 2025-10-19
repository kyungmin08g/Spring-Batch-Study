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
 * Writer Skip Batch Config Class
 */

@Configuration
class SkipWriterBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {
  private val log = KotlinLogging.logger {}

  @Bean
  fun skipWriterJob(): Job = JobBuilder("skipWriterJob", jobRepo)
    .start(skipWriterStep())
    .build()

  @Bean
  fun skipWriterStep(): Step = StepBuilder("skipWriterStep", jobRepo)
    .chunk<String, String>(5, transactionManager)
    .reader(skipWriterReader())
    .writer(skipWriter())
    .faultTolerant() // 내결함성 기능 활성화
    .skipLimit(2) // 몇번의 skip을 허용할 건지 (skip 허용 횟수)
    .skip(IllegalArgumentException::class.java) // skip할 예외
    .skip(SQLException::class.java)
    .noSkip(NullPointerException::class.java) // 이건 skip 안돼!라고 할 예외 (어떤 예외에 대해서는 skip 하지 않음)
    .build()

  @Bean
  fun skipWriterReader(): ItemReader<String> {
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
  fun skipWriter(): ItemWriter<String?> {
    return ItemWriter {
      it.items.forEach { item ->
        if (item.equals("4")) {
          log.error { "ItemWriter ==> SQLException 발생" }
          throw SQLException("에러 발생")
        }
      }

      log.info { "ItemWriter >> $it" }
    }
  }
}

/**
 * 실행결과
 *
 * : Job: [SimpleJob: [name=skipWriterJob]] launched with the following parameters: [{'value':'{value=7, type=class java.lang.String, identifying=true}'}]
 * : Executing step: [skipWriterStep]
 * : ItemReader >> 1
 * : ItemReader ==> IllegalArgumentException 발생
 * : ItemReader >> 3
 * : ItemReader >> 4
 * : ItemReader >> 5
 * : ItemReader >> 6
 * : ItemWriter ==> SQLException 발생
 * : ItemWriter >> [items=[1], skips=[]]
 * : ItemWriter >> [items=[3], skips=[]]
 * : ItemWriter ==> SQLException 발생
 * : ItemWriter >> [items=[5], skips=[]]
 * : ItemWriter >> [items=[6], skips=[]]
 * : ItemReader >> 7
 * : ItemReader >> 8
 * : ItemReader >> 9
 * : ItemReader >> 10
 * : ItemReader >> 11
 */