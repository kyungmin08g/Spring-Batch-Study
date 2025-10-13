package com.kyungmin.springbatchstudy.config.datasource

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

/**
 * Spring Batch의 META Table을 위한 META DB Config Class
 */

@Configuration
class MetaDBConfig {

  @Bean
  @Primary // Bean 등록 우선순위 어노테이션
  @ConfigurationProperties(prefix = "spring.datasource-meta") // 설정 값을 가져와 DataSource에 주입함.
  fun metaDataSource(): DataSource {
    return DataSourceBuilder.create().build()
  }

  @Bean
  @Primary
  fun metaTransactionManager(): PlatformTransactionManager {
    // DataSourceTransactionManager는 JDBC용 트랜잭션 관리자 (META DB까지 JPA를 사용할 이유는 없기 때문에 JDBC로 진행)
    return DataSourceTransactionManager(metaDataSource())
  }
}