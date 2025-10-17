package com.kyungmin.springbatchstudy.batch.jdbc

import com.kyungmin.springbatchstudy.batch.jdbc.entity.Product
import com.kyungmin.springbatchstudy.config.datasource.DataDBConfig

import javax.sql.DataSource

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
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
@Import(value = [DataDBConfig::class ])
class JdbcPagingBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: JdbcTransactionManager,

  @param:Qualifier("dataSource")
  private val dataSource: DataSource,
) {

    // Job (동일)
    @Bean
    fun jdbcPagingJob(): Job = JobBuilder("jdbcPagingJob", jobRepo)
      .start(pagingStep())
      .build()

    // Step (동일)
    @Bean
    @JobScope
    fun pagingStep(): Step = StepBuilder("pagingStep", jobRepo)
      .chunk<Product, Product>(20, transactionManager)
      .reader(pagingReader())
      .processor(pagingProcessor())
      .writer(pagingWriter())
      .build()

    // Reader
    @Bean
    @StepScope
    fun pagingReader(): JdbcPagingItemReader<Product> = JdbcPagingItemReaderBuilder<Product>()
        .name("pagingReader")
        .selectClause("SELECT id, name, description, price")
        .fromClause("FROM product")
        .whereClause("WHERE description LIKE 'user%'")
        .dataSource(dataSource)
        .rowMapper(ProductRowMapper())
        .pageSize(20)
        .sortKeys(mapOf("id" to Order.ASCENDING))
        .build()

    // Processor
    @Bean
    fun pagingProcessor(): ItemProcessor<Product, Product> = ItemProcessor {
      it.name = "Type" // name을 "Type"으로 수정
      it
    }

    // Writer
    @Bean
    @StepScope
    fun pagingWriter(): JdbcBatchItemWriter<Product> {
      val query = "UPDATE product SET name = :name where id = :productId"

      return JdbcBatchItemWriterBuilder<Product>()
        .sql(query)
        .dataSource(dataSource)
        .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
        .assertUpdates(true)
        .build()
    }
}