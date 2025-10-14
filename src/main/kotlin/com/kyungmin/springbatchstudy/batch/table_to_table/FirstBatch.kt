package com.kyungmin.springbatchstudy.batch.table_to_table

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

/**
 * Table to Table First Batch Config Class
 */

@Configuration
class FirstBatch(
  private val jobRepo: JobRepository, // Spring Batch의 Job/Step 실행 상태를 관리하는 Component
  private val transactionManager: PlatformTransactionManager,
  private val beforeRepo: BeforeRepository,
  private val afterRepo: AfterRepository
) {

  // Job
  @Bean
  fun firstJob(): Job = JobBuilder("firstJob", jobRepo)
      .start(firstStep())
      .build()


  // Step
  @Bean
  @JobScope // 해당 Job이 끝나면 즉시 소멸하게 해주는 어노테이션 (덕분에 Job이 끝나면 자동으로 정리되어 메모리 관리가 효율적으로 이루어짐.)
  fun firstStep(): Step = StepBuilder("firstStep", jobRepo) // 첫번째 인자: Step의 이름, 두번째 인자: Job Repo
      .chunk<BeforeEntity, AfterEntity>(10, transactionManager) // 청크 단위로 10개씩 끊어서 처리 (예: 100개의 데이터를 10번씩 쪼개서 처리)
      .reader(beforeReader()) // 데이터를 읽는 Reader 지정
      .processor(processor()) // 데이터를 가공/변환하는 Processor 지정
      .writer(afterWriter()) // 데이터를 저장하는 Writer 지정
      .build()

  // Reader
  // RepositoryItemReader -> JPA Repository의 메서드를 호출하여 데이터를 Page 단위로 읽어오는 Reader (읽기 작업을 수행하는 친구)
  @Bean
  @StepScope // Step이 실행되는 동안에만 Bean이 활성화되고, Step이 끝나면 소멸됨.
  fun beforeReader(): RepositoryItemReader<BeforeEntity> = RepositoryItemReaderBuilder<BeforeEntity>()
      .name("beforeReader") // Step 내에서 Reader 식별용 이름 (필수 사항)
      .repository(beforeRepo) // 어떤 JpaRepository를 사용할지 지정할 수 있음.
      .methodName("findAll") // Repository의 어떤 메서드를 호출할지 지정 (해당 부분에서는 findAll)
      .pageSize(10) // 10개 단위로 페이징 처리
      .sorts(mutableMapOf("id" to Sort.Direction.ASC)) // 정렬 기준 (해당 부분에서는 ID를 기준으로 오름차순)
      .build()

  // Processor
  // ItemProcessor -> Reader에서 읽은 데이터를 가공하거나 변환하여 Writer로 전달하는 단계 (주로 비즈니스 로직 수행)
  @Bean
  fun processor(): ItemProcessor<BeforeEntity, AfterEntity> = ItemProcessor { before ->
    // BeforeEntity의 username 값을 AfterEntity의 username으로 매핑
    AfterEntity(null, before.username)
  }

  // Writer
  // RepositoryItemWriter -> JPA Repository를 사용하여 데이터를 쓰는(저장) Writer
  @Bean
  @StepScope
  fun afterWriter(): RepositoryItemWriter<AfterEntity> = RepositoryItemWriterBuilder<AfterEntity>()
    .repository(afterRepo) // 저장 대상 Repository 지정
    .methodName("save") // 호출할 JPA 메서드 (save)
    .build()
}