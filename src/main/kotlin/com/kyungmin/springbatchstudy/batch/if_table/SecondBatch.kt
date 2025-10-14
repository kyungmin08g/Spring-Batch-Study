package com.kyungmin.springbatchstudy.batch.if_table

import com.kyungmin.springbatchstudy.batch.if_table.entity.WinEntity
import com.kyungmin.springbatchstudy.batch.if_table.repository.WinRepository
import com.kyungmin.springbatchstudy.batch.table_to_table.entity.AfterEntity
import com.kyungmin.springbatchstudy.batch.table_to_table.entity.BeforeEntity
import com.kyungmin.springbatchstudy.batch.table_to_table.repository.AfterRepository
import com.kyungmin.springbatchstudy.batch.table_to_table.repository.BeforeRepository
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
import java.util.Collections

@Configuration
class SecondBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager,
  private val winRepo: WinRepository
) {

  // Job
  @Bean
  fun secondJob(): Job = JobBuilder("secondJob", jobRepo)
    .start(secondStep())
    .build()

  // Step
  @Bean
  @JobScope
  fun secondStep(): Step = StepBuilder("secondStep", jobRepo)
    .chunk<WinEntity, WinEntity>(10, transactionManager)
    .reader(winReader())
    .processor(processor())
    .writer(winWriter())
    .build()

  // Reader
  @Bean
  @StepScope
  fun winReader(): RepositoryItemReader<WinEntity> = RepositoryItemReaderBuilder<WinEntity>()
    .name("winReader")
    .repository(winRepo)
    .methodName("findByWinGreaterThanEqual")
    .arguments(Collections.singletonList(10L))
    .pageSize(10)
    .sorts(mutableMapOf("id" to Sort.Direction.ASC))
    .build()

  // Processor
  @Bean
  fun processor(): ItemProcessor<WinEntity, WinEntity> = ItemProcessor {
    it.reward = true
    it
  }

  // Writer
  @Bean
  @StepScope
  fun winWriter(): RepositoryItemWriter<WinEntity> = RepositoryItemWriterBuilder<WinEntity>()
    .repository(winRepo)
    .methodName("save")
    .build()
}