package com.kyungmin.springbatchstudy.batch.job

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * Job Batch Config Class
 */

@Configuration
class JobBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {

  private val log = KotlinLogging.logger {}

  @Bean
  fun job(): Job = JobBuilder("job", jobRepo)
    .start(step1())
    .on("*").to(step2())
    /*
      - on() : 특정 조건을 걸 수 있는 함수 (파라미터 -> 패턴(pattern) ex. *(와일드 카드 -> 실패/성공 여부 관계없이 무조건 실행))
      단, on() 함수는 from() 함수가 있고 그 함수가 실행 됐을 경우에는 무시됨.
      - to() : 특정 조건에 따른 Step을 설정 가능한 함수 (파라미터 -> 스텝(step))
     */
    .from(step1()).on("FAILED").to(step3())
    .from(step1()).on("COMPLETED").to(step4())
    /*
      - from() : 특정 스텝(Step)에 따른 결과를 반환 받고 그에 맞는 on() 함수로 조건을 비교하여 다음 스텝으로 넘어가는 함수
     */
    .end()
    .listener(CustomJobExecutionListener()) // JobExecutionListener Interface를 상속 받은 Custom Class나, Bean으로 주입한 JobExecutionListener 함수를 넣는 함수 (Listener 정의)
    .build()

  @Bean
  fun step1(): Step = StepBuilder("step1", jobRepo)
    .chunk<String, String>(2, transactionManager)
    .reader(reader())
    .writer(writer())
    .build()

  @Bean
  fun step2(): Step = StepBuilder("step2", jobRepo)
    .chunk<String, String>(2, transactionManager)
    .reader(reader())
    .writer(writer())
    .build()

  @Bean
  fun step3(): Step = StepBuilder("step3", jobRepo)
    .chunk<String, String>(2, transactionManager)
    .reader(reader())
    .writer(writer())
    .build()

  @Bean
  fun step4(): Step = StepBuilder("step4", jobRepo)
    .chunk<String, String>(2, transactionManager)
    .reader(reader())
    .writer(writer())
    .build()


  @Bean
  fun reader(): ItemReader<String> {
    var number = 0

    return ItemReader {
      number++

      // number가 8 이상이면 null 반환하여 Step 종료
      val response = if (number > 8) null else number.toString()

      log.info { "ItemReader >> $number" }
      response
    }
  }

  @Bean
  fun writer(): ItemWriter<String?> {
    return ItemWriter {
      it.items.forEach { item -> println(item) }
    }
  }
}

/**
 * 실행결과
 *
 * 2025-10-21T19:09:24.521+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [FlowJob: [name=job]] launched with the following parameters: [{'value':'{value=9, type=class java.lang.String, identifying=true}'}]
 * 2025-10-21T19:09:24.537+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.b.job.CustomJobExecutionListener   : CustomJobExecutionListener beforeJob() 실행
 * 2025-10-21T19:09:24.544+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step1]
 * 2025-10-21T19:09:24.549+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 1
 * 2025-10-21T19:09:24.550+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 2
 * 1
 * 2
 * 2025-10-21T19:09:24.553+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 3
 * 2025-10-21T19:09:24.553+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 4
 * 3
 * 4
 * 2025-10-21T19:09:24.556+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 5
 * 2025-10-21T19:09:24.556+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 6
 * 5
 * 6
 * 2025-10-21T19:09:24.559+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 7
 * 2025-10-21T19:09:24.559+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 8
 * 7
 * 8
 * 2025-10-21T19:09:24.561+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 9
 * 2025-10-21T19:09:24.563+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.step.AbstractStep         : Step: [step1] executed in 19ms
 * 2025-10-21T19:09:24.574+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.job.SimpleStepHandler     : Executing step: [step4]
 * 2025-10-21T19:09:24.577+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.springbatchstudy.batch.job.JobBatch  : ItemReader >> 10
 * 2025-10-21T19:09:24.579+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.batch.core.step.AbstractStep         : Step: [step4] executed in 5ms
 * 2025-10-21T19:09:24.583+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] c.k.s.b.job.CustomJobExecutionListener   : CustomJobExecutionListener afterJob() 실행
 * 2025-10-21T19:09:24.586+09:00  INFO 18288 --- [Spring-Batch-Study] [nio-8080-exec-1] o.s.b.c.l.s.TaskExecutorJobLauncher      : Job: [FlowJob: [name=job]] completed with the following parameters: [{'value':'{value=9, type=class java.lang.String, identifying=true}'}] and the following status: [COMPLETED] in 58ms
 */