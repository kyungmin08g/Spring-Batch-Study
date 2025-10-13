package com.kyungmin.springbatchstudy.config.datasource

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class MetaDBConfig {

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource-meta")
  fun metaDataSource(): DataSource {
    return DataSourceBuilder.create().build()
  }

  @Bean
  @Primary
  fun metaTransactionManager(): PlatformTransactionManager {
    return DataSourceTransactionManager(metaDataSource())
  }
}