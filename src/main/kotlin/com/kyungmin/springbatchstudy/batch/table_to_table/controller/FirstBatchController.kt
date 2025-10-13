package com.kyungmin.springbatchstudy.batch.table_to_table.controller

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class FirstBatchController(
  private val jobLauncher: JobLauncher,
  private val jobRegistry: JobRegistry
) {

  @GetMapping("/first/{value}")
  fun runFirstBatch(@PathVariable("value") value: String): ResponseEntity<String> {
    val jobParameters = JobParametersBuilder()
      .addString("value", value)
      .toJobParameters()

    jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters)

    return ResponseEntity.ok("OK!")
  }
}