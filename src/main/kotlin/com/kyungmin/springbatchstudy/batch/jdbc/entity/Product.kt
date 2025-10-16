package com.kyungmin.springbatchstudy.batch.jdbc.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

/**
 * JDBC Batch를 구현하고, 테스트 하기 위한 Product Entity
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
class Product(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  val productId: Long? = null,

  @Column(name = "name", nullable = false)
  val name: String,

  @Column(name = "description")
  val desc: String?,

  @Column(name = "price", nullable = false)
  val price: Long
)