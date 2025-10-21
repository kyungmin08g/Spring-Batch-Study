package com.kyungmin.springbatchstudy.batch.job

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component

/**
 * JobExecutionListener : Job의 시작과 끝을 제어(혹은 감지)하는 Interface
 * (Job 실행 전후에 특정 로직을 수행할 수 있게 해주는 콜백 Interface)
 */

@Component
class CustomJobExecutionListener : JobExecutionListener {

  private val log = KotlinLogging.logger {}

  /**
   * Job이 시작됐을 경우 호출됨
   */
  override fun beforeJob(jobExecution: JobExecution) {
    super.beforeJob(jobExecution)
    log.info { "CustomJobExecutionListener beforeJob() 실행" }
  }

  /**
   * Job이 끝났을 경우 호출됨.
   */
  override fun afterJob(jobExecution: JobExecution) {
    super.afterJob(jobExecution)
    log.info { "CustomJobExecutionListener afterJob() 실행" }
  }
}