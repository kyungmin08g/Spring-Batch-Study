package com.kyungmin.springbatchstudy.batch.table_to_table

import com.kyungmin.springbatchstudy.batch.table_to_table.entity.AfterEntity
import com.kyungmin.springbatchstudy.batch.table_to_table.entity.BeforeEntity
import com.kyungmin.springbatchstudy.batch.table_to_table.repository.AfterRepository
import com.kyungmin.springbatchstudy.batch.table_to_table.repository.BeforeRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
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

@Configuration
class FirstBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager,
  private val beforeRepo: BeforeRepository,
  private val afterRepo: AfterRepository
) {

  @Bean
  fun firstJob(): Job = JobBuilder("firstJob", jobRepo)
      .start(firstStep())
      .build()


  @Bean
  fun firstStep(): Step = StepBuilder("firstStep", jobRepo)
      .chunk<BeforeEntity, AfterEntity>(10, transactionManager)
      .reader(beforeReader())
      .processor(processor())
      .writer(afterWriter())
      .build()

  @Bean
  fun beforeReader(): RepositoryItemReader<BeforeEntity> = RepositoryItemReaderBuilder<BeforeEntity>()
      .name("beforeReader")
      .repository(beforeRepo)
      .methodName("findAll")
      .pageSize(10)
      .sorts(mutableMapOf("id" to Sort.Direction.ASC))
      .build()

  @Bean
  fun processor(): ItemProcessor<BeforeEntity, AfterEntity> = ItemProcessor {
    AfterEntity(null, it.username)
  }

  @Bean
  fun afterWriter(): RepositoryItemWriter<AfterEntity> = RepositoryItemWriterBuilder<AfterEntity>()
    .repository(afterRepo)
    .methodName("save")
    .build()
}