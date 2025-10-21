package com.kyungmin.springbatchstudy.batch.jdbc.controller

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Job 실행을 위한 JDBC Batch Rest Controller
 */

@RestController
@RequiredArgsConstructor
class JdbcBatchController(
  private val jobLauncher: JobLauncher,
  private val jobRegistry: JobRegistry
) {

  @GetMapping("/jdbc/{value}")
  fun runCursorBatch(
    @PathVariable("value") value: String,
    @RequestParam("batch") batch: String
  ): ResponseEntity<String> {
    val jobParameters = JobParametersBuilder()
      .addString("value", value)
      .toJobParameters()

    when(batch) {
      "cursor" -> jobLauncher.run(jobRegistry.getJob("jdbcCursorJob"), jobParameters)
      "paging" -> jobLauncher.run(jobRegistry.getJob("jdbcPagingJob"), jobParameters)
    }

    return ResponseEntity.ok("JDBC Job OK!")
  }
}