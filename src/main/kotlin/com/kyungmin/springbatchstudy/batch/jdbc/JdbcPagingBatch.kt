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

/**
 * JDBC용 Batch Config Class (Paging)
 */

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
    /*
     JdbcPagingItemReader : 데이터를 Page 단위로 읽어오는 ItemReader (효율적)
     */
    fun pagingReader(): JdbcPagingItemReader<Product> = JdbcPagingItemReaderBuilder<Product>()
      .name("pagingReader") // Reader Name
      .selectClause("SELECT id, name, description, price") // SELECT Query
      .fromClause("FROM product") // FROM Query
      .whereClause("WHERE description LIKE 'user%'") // WHERE Query
      .dataSource(dataSource) // DataSource
      .rowMapper(ProductRowMapper()) // Row Mapper
      .pageSize(20) // Page Size
      .fetchSize(20) // Fetch Size
      .sortKeys(mapOf("id" to Order.ASCENDING)) // 오름차순 정렬
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

/**
 * 실행결과 (비교용)
 *
 * Cursor Reader 방식
 * - Job: [SimpleJob: [name=jdbcCursorJob]] launched with the following parameters: [{'value':'{value=2, type=class java.lang.String, identifying=true}'}]
 * - Executing step: [cursorStep]
 * - Step: [cursorStep] executed in 99ms
 * - Job: [SimpleJob: [name=jdbcCursorJob]] completed with the following parameters: [{'value':'{value=2, type=class java.lang.String, identifying=true}'}]
 * - and the following status: [COMPLETED] in 157ms
 *
 * ============================================================================================================================================================================================================
 *
 * Paging Reader 방식
 * - Job: [SimpleJob: [name=jdbcPagingJob]] launched with the following parameters: [{'value':'{value=2, type=class java.lang.String, identifying=true}'}]
 * - Executing step: [pagingStep]
 * - Step: [pagingStep] executed in 79ms
 * - Job: [SimpleJob: [name=jdbcPagingJob]] completed with the following parameters: [{'value':'{value=2, type=class java.lang.String, identifying=true}'}]
 * - and the following status: [COMPLETED] in 106ms
 */