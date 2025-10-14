package com.kyungmin.springbatchstudy.batch.if_table.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "win_entity")
class WinEntity(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "username", nullable = false)
  val username: String,

  @Column(name = "win", nullable = false)
  val win: Long,

  @Column(name = "reward", nullable = false)
  val reward: Boolean
)