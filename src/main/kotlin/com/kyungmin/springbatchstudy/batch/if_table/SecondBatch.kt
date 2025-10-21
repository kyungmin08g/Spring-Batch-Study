package com.kyungmin.springbatchstudy.batch.if_table

import com.kyungmin.springbatchstudy.batch.if_table.entity.WinEntity
import com.kyungmin.springbatchstudy.batch.if_table.repository.WinRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager

/**
 * 읽는 방식에 조건을 걸어 데이터 처리하는 Second Batch
 * (winReader() 함수 참고)
 */

@Configuration
class SecondBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager,
  private val winRepo: WinRepository
) {

  // Job (firstJob과 내용은 동일)
  @Bean
  fun secondJob(): Job = JobBuilder("secondJob", jobRepo)
    .start(secondStep())
    .build()

  // Step (firstStep과 내용은 동일)
  @Bean
  @JobScope
  fun secondStep(): Step = StepBuilder("secondStep", jobRepo)
    .chunk<WinEntity, WinEntity>(10, transactionManager)
    .reader(winReader())
    .processor(winProcessor())
    .writer(winWriter())
    .build()

  // Reader
  @Bean
  @StepScope
  fun winReader(): RepositoryItemReader<WinEntity> = RepositoryItemReaderBuilder<WinEntity>()
    .name("winReader") // Reader Name
    .repository(winRepo) // Jpa Repository
    .methodName("findByWinGreaterThanEqual") // Jpa Function Name
    .arguments(listOf(10L)) // arguments()는 Jpa 함수에 인자를 넘기기 위해서 사용함. 근데 왜 List 타입으로 받냐~! 인자가 여러개일 수도 있고 순서를 유지하여 넘겨야 하기 때문이다.
    .pageSize(10) // Pageable에 들어갈 page size
    .sorts(mutableMapOf("id" to Sort.Direction.ASC)) // Pageable에 들어갈 정렬 방식
    .build()

  // Processor
  @Bean
  fun winProcessor(): ItemProcessor<WinEntity, WinEntity> = ItemProcessor {
    it.reward = true // win이 10이 넘거나 같은 데이터만 reward를 true로 변경
    it
  }

  // Writer (firstWriter와 내용은 동일)
  @Bean
  @StepScope
  fun winWriter(): RepositoryItemWriter<WinEntity> = RepositoryItemWriterBuilder<WinEntity>()
    .repository(winRepo)
    .methodName("save")
    .build()
}