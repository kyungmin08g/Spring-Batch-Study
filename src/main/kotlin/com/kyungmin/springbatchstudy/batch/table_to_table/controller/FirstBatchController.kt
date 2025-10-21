package com.kyungmin.springbatchstudy.batch.table_to_table.controller

import lombok.RequiredArgsConstructor
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Job 실행을 위한 Rest Controller
 */

@RestController
@RequiredArgsConstructor
class FirstBatchController(
  private val jobLauncher: JobLauncher, // Spring Batch에서 Job을 실행하는 Component (Job 실행자)
  private val jobRegistry: JobRegistry // Spring Batch Bean으로 등록된 Job들을 조회 or 등록할 수 있는 Interface
) {

  @GetMapping("/first/{value}")
  fun runFirstBatch(@PathVariable("value") value: String): ResponseEntity<String> {
    // JobParameters는 Step에 인자로 넘겨줄 수도 있고 Job을 구분하기 위해서 사용됨. (현재 상황에서는 Job을 구분하기 위한 용도로 사용)
    // 또, 동일한 인자가 들어오는 경우에는 Spring Batch가 실패함.
    val jobParameters = JobParametersBuilder()
      .addString("value", value)
      .toJobParameters()

    jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters) // Job 실행 (첫번째 인자: 어떤 Job을 실행할 것인가?(Job Name), 두번째 인자: Job Parameters Object)

    return ResponseEntity.ok("OK!")
  }
}