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
    .from(step1()).on("FAILED").to(step3())
    .from(step1()).on("COMPLETED").to(step4())
    .end()
    .listener(CustomJobExecutionListener())
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