package com.kyungmin.springbatchstudy.batch.jdbc

import com.kyungmin.springbatchstudy.batch.jdbc.entity.Product
import com.kyungmin.springbatchstudy.config.datasource.DataDBConfig
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@Configuration
@Import(value = [ DataDBConfig::class ])
class JdbcCursorBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: JdbcTransactionManager,
  private val dataSource: DataSource,
) {

  // Job
  @Bean
  fun jdbcCursorJob(): Job = JobBuilder("jdbcCursorJob", jobRepo)
    .start(cursorStep())
    .build()

  // Step
  @Bean
  @JobScope
  fun cursorStep(): Step = StepBuilder("cursorStep", jobRepo)
    .chunk<Product, Product>(2, transactionManager)
    .reader(cursorReader())
    .processor(cursorProcessor())
    .writer(cursorWriter())
    .build()

  // Reader
  @Bean
  @StepScope
  fun cursorReader(): JdbcCursorItemReader<Product> {
    val query = "SELECT * FROM product"

    return JdbcCursorItemReaderBuilder<Product>()
      .name("cursorReader")
      .sql(query)
      .dataSource(dataSource)
      .rowMapper(ProductRowMapper())
      .build()
  }

  // Processor
  @Bean
  fun cursorProcessor(): ItemProcessor<Product, Product> = ItemProcessor {
    it.name = "Keyboard"
    it
  }

  // Writer
  @Bean
  @StepScope
  fun cursorWriter(): JdbcBatchItemWriter<Product> {
    val query = "UPDATE product SET name = :name where id = :id"

    return JdbcBatchItemWriterBuilder<Product>()
      .sql(query)
      .dataSource(dataSource)
      .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
      .assertUpdates(true)
      .build()
  }
}