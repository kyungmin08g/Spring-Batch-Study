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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

/**
 * JDBC용 Batch Config Class (Cursor)
 */

@Configuration
@Import(value = [ DataDBConfig::class ]) // 실제로 저장될 DB Config 파일을 Import (@Import 선언시 해당 Config Class에 등록된 Bean을 사용할 수 있음!)
class JdbcCursorBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: JdbcTransactionManager, // DataDBConfig Class에 등록된 JdbcTransactionManager Bean을 사용하기 위함.

  @param:Qualifier("dataSource") // @Qualifier 어노테이션으로 어떤 Bean을 사용할건지 작성해야 함. 지금은 내가 Meta DataSource에 @Primary(Bean 등록 우선순위)를 선언했기 때문임.
  private val dataSource: DataSource,
) {

  // Job (동일)
  @Bean
  fun jdbcCursorJob(): Job = JobBuilder("jdbcCursorJob", jobRepo)
    .start(cursorStep())
    .build()

  // Step (동일)
  @Bean
  @JobScope
  /**
   * JdbcCursorItemReader : JDBC 기반으로 데이터를 읽을 때 사용하는 Reader
   * - 커서(Cursor) 방식 : DB에서 한 행씩 읽어서 메모리에 올려 처리
   */
  fun cursorStep(): Step = StepBuilder("cursorStep", jobRepo)
    .chunk<Product, Product>(50, transactionManager)
    .reader(cursorReader())
    .processor(cursorProcessor())
    .writer(cursorWriter())
    .build()

  // Reader
  @Bean
  @StepScope
  fun cursorReader(): JdbcCursorItemReader<Product> {
    val query = "SELECT id, name, description, price FROM product" // SQL

    return JdbcCursorItemReaderBuilder<Product>()
      .name("cursorReader") // Reader Name
      .sql(query) // 작성한 SQL
      .dataSource(dataSource) // 생성자 주입으로 받은 DataSource
      .fetchSize(50)
      /*
        fetchSize()의 파라미터인 fetchSize : 한 번에 가져오는 행의 수
        JdbcCursorItemReader는 기본적으로 한 행씩 읽기 때문에 불필요한 네트워크 I/O 발생함.
        이를 해결하기 위해서 fetchSize() 함수는 한 번 DB에 요청할 때 n개 행씩 미리 가져와 성능을 향상시킴.
        (일반적으로 chunkSize와 동일하게 설정함.)

        그럼에도 결국 JdbcCursorItemReader가 한 행씩 처리하게 되는데 하지만 네트워크 I/O는 덜 일어나서 성능이 향상됨.
        fetchSize() 함수는 메모리에 데이터를 미리 올리는거지 JdbcCursorItemReader가 처리하는건 아님. 메모리에 올리고 JdbcCursorItemReader가 내부적으로 ResultSet을 사용해서 처리함.
       */
      .rowMapper(ProductRowMapper()) // Field Mapping용 RowMapper
      .build()
  }

  // Processor
  @Bean
  /*
   processor는 반드시 필요한건 아닌데 일단 적용..
   */
  fun cursorProcessor(): ItemProcessor<Product, Product> = ItemProcessor {
    it.name = "Keyboard" // 모든 name을 "Keyboard"로 수정
    it
  }

  // Writer
  @Bean
  @StepScope
  /**
   * JdbcBatchItemWriter : JDBC를 통해 데이터를 DB에 일괄(여러 Item을 모아서 한번에 DB에 반영(Batch)) 저장할 때 사용하는 Writer (기본으로 벌크(Bulk) 처리 -> 성능 향상!)
   * - JdbcBatchItemWriter는 내부적으로 JdbcTemplate.batchUpdate() or NamedParameterJdbcOperations.batchUpdate() 함수를 사용하여 Batch 처리함.
   * - 직접 jdbcTemplate.batchUpdate()를 호출하지 X, JdbcBatchItemWriter가 chunk 단위로 모은 데이터를 한 번에 batch 실행해 줌.
   */
  fun cursorWriter(): JdbcBatchItemWriter<Product> {
    val query = "UPDATE product SET name = :name where id = :productId" // :{Entity에 작성된 필드명} (ex) :productId)

    return JdbcBatchItemWriterBuilder<Product>()
      .sql(query) // SQL
      .dataSource(dataSource) // 생성자 주입으로 받은 DataSource
      .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider()) // 객체의 프로퍼티 이름을 SQL 파라미터 이름으로 Mapping하기 위한 제공자
      .assertUpdates(true)
      /*
        JdbcBatchItemWriter에서 업데이트가 몇 건 일어났는지 확인하는 속성
        - true : SQL 실행 후, 1건 이상이 영향을 받지 않으면 예외 발생 (Good)
        - false : 영향을 받은 row 수와 상관없이 다음으로 Pass~
        즉, 안전하게 업데이트가 됐느냐!
       */
      .build()
  }
}

/**
 * 실행결과 (비교용)
 * Job: [SimpleJob: [name=jdbcCursorJob]] launched with the following parameters: [{'value':'{value=e, type=class java.lang.String, identifying=true}'}]
 * Executing step: [cursorStep]
 * Step: [cursorStep] executed in 47ms
 * Job: [SimpleJob: [name=jdbcCursorJob]] completed with the following parameters: [{'value':'{value=e, type=class java.lang.String, identifying=true}'}]
 * and the following status: [COMPLETED] in 82ms
 * (Jdbc 방식은 기본적으로 Hibernate Log 미지원)
 */