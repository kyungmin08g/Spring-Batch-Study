package com.kyungmin.springbatchstudy.batch.tasklet

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * Tasklet 방식 Batch Config Class
 */

@Configuration
class TaskletBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager,
) {

  @Bean
  fun taskletJob(): Job = JobBuilder("taskletJob", jobRepo)
    .start(taskletStep())
    .build()

  @Bean
  @JobScope
  fun taskletStep(): Step = StepBuilder("taskletStep", jobRepo)
    .tasklet(
      { contribution, chunkContext ->
        println("Tasklet 방식") // 비즈니스 로직 부분
        RepeatStatus.FINISHED // 작업이 완료됨을 알려줌.
      },
      transactionManager // Transaction Manager
    ).build()
}

/**
 * 실행결과
 * Job: [SimpleJob: [name=taskletJob]] launched with the following parameters: [{'value':'{value=a, type=class java.lang.String, identifying=true}'}]
 * Executing step: [taskletStep]
 * 출력 -> Tasklet 방식
 * Step: [taskletStep] executed in 21ms
 * Job: [SimpleJob: [name=taskletJob]] completed with the following parameters: [{'value':'{value=a, type=class java.lang.String, identifying=true}'}]
 * and the following status: [COMPLETED] in 66ms
 */