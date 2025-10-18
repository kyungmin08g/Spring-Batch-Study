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
        println("Tasklet 방식")
        RepeatStatus.FINISHED
      },
      transactionManager
    ).build()
}