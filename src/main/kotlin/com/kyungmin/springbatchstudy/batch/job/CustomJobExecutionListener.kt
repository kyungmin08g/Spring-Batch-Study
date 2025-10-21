package com.kyungmin.springbatchstudy.batch.job

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component

@Component
class CustomJobExecutionListener : JobExecutionListener {

  private val log = KotlinLogging.logger {}

  override fun beforeJob(jobExecution: JobExecution) {
    super.beforeJob(jobExecution)
    log.info { "CustomJobExecutionListener beforeJob() 실행" }
  }

  override fun afterJob(jobExecution: JobExecution) {
    super.afterJob(jobExecution)
    log.info { "CustomJobExecutionListener afterJob() 실행" }
  }
}