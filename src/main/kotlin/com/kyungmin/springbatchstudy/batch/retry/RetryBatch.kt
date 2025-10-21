package com.kyungmin.springbatchstudy.batch.retry

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

@Configuration
class RetryBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {

  private val log = KotlinLogging.logger {}

  @Bean
  fun retryJob(): Job = JobBuilder("retryJob", jobRepo)
    .start(retryStep())
    .build()

  @Bean
  fun retryStep(): Step = StepBuilder("retryStep", jobRepo)
    .chunk<String, String>(5, transactionManager)
    .reader(retryReader())
    .processor(retryProcessor())
    .writer(retryWriter())
    .faultTolerant() // 내결함성 기능 활성화
    .retry(SQLException::class.java) // retry할 예외
    .retryLimit(2) // retry 허용 횟수
    .noSkip(NullPointerException::class.java)
    .build()

  @Bean
  fun retryReader(): ItemReader<String> {
    var number = 0

    return ItemReader {
      number++

      val response = if (number > 20) { null } else { number.toString() }

      log.info { "ItemReader >> $number" }
      response
    }
  }

  @Bean
  fun retryProcessor(): ItemProcessor<String, String> {
    return ItemProcessor {

      if (it == "4") {
        log.error { "ItemProcessor ==> SQLException 발생" }
        throw SQLException("에러 발생")
      }

      log.info { "ItemProcessor >> $it" }
      it
    }
  }

  @Bean
  fun retryWriter(): ItemWriter<String?> {
    return ItemWriter {
      it.items.forEach { item -> println(item) }
    }
  }
}