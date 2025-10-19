package com.kyungmin.springbatchstudy.batch.skip.controller

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Job 실행을 위한 Skip Rest Controller
 */

@RestController
@RequiredArgsConstructor
class SkipBatchController(
  private val jobLauncher: JobLauncher,
  private val jobRegistry: JobRegistry
) {

  @GetMapping("/skip/{value}")
  fun runSecondBatch(@PathVariable("value") value: String): ResponseEntity<String> {
    val jobParameters = JobParametersBuilder()
      .addString("value", value)
      .toJobParameters()

    jobLauncher.run(jobRegistry.getJob("skipReaderJob"), jobParameters)

    return ResponseEntity.ok("Skip OK!")
  }
}