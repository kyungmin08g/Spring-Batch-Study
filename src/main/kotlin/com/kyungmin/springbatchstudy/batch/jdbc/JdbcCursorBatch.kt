package com.kyungmin.springbatchstudy.batch.jdbc

import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JdbcCursorBatch(
  private val jobRepo: JobRepository,
  private val transactionManager: PlatformTransactionManager
) {

}