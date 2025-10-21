package com.kyungmin.springbatchstudy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.support.JdbcTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


/**
 * JPA 사용을 위한 Application DB Config Class
 */

@Configuration
@EnableJpaRepositories(
  basePackages = [ "com.kyungmin.springbatchstudy" ], // JPA를 사용할 패키지 지정
  entityManagerFactoryRef = "entityManagerFactory", // LocalContainerEntityManagerFactoryBean Bean 이름 (entityManagerFactory)
  transactionManagerRef = "dataTransactionManager" // PlatformTransactionManager Bean 이름 (dataTransactionManager)
)
class DataDBConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource-data")
  fun dataSource(): DataSource {
    return DataSourceBuilder.create().build()
  }

  /*
   JPA 사용을 위한 Bean 구성
   */
  @Bean
  fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
    val em = LocalContainerEntityManagerFactoryBean() // JPA용 EntityManagerFactory를 생성하고 관리하는 Bean
    em.dataSource = dataSource() // DataSource
    em.jpaVendorAdapter = HibernateJpaVendorAdapter()
    em.setPackagesToScan("com.kyungmin.springbatchstudy") // Entity Scan 범위

    // Properties 정의 (application.yml 파일에서 했던 JPA 관련 속성들)
    val properties: HashMap<String, Any> = HashMap()
    properties["hibernate.hbm2ddl.auto"] = "update"
    properties["hibernate.show_sql"] = true
    properties["hibernate.format_sql"] = false
    em.setJpaPropertyMap(properties)

    return em
  }

  @Bean
  fun dataTransactionManager(): PlatformTransactionManager {
    // JPA로 Transaction을 관리하기 위해 JpaTransactionManager를 선언
    val transactionManager = JpaTransactionManager() // JPA or Hibernate 용도
    transactionManager.dataSource = dataSource() // Transaction의 관리 타겟인 DataSource

    return transactionManager
  }

  /**
   * JDBC용 Transaction Manager
   */
  @Bean
  fun transactionManager(): JdbcTransactionManager {
    return JdbcTransactionManager(dataSource())
  }
}