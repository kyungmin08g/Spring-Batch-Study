package com.kyungmin.springbatchstudy.batch.skip

import io.github.oshai.kotlinlogging.KotlinLogging
import lombok.extern.slf4j.Slf4j
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.sql.SQLException

@Slf4j
@Configuration
class SkipReaderBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {
  val log = KotlinLogging.logger {}

  @Bean
  fun skipJob(): Job = JobBuilder("skipJob", jobRepo)
    .start(skipStep())
    .build()

  @Bean
  @JobScope
  fun skipStep(): Step = StepBuilder("skipStep", jobRepo)
    .chunk<String, String>(10, transactionManager)
    .reader(skipReader())
    .faultTolerant()
    .skipLimit(2)
    .skip(IllegalArgumentException::class.java)
    .noSkip(SQLException::class.java)
    .build()

  @Bean
  @StepScope
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
}