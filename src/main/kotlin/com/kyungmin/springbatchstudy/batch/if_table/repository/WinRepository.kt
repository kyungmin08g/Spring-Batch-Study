package com.kyungmin.springbatchstudy.batch.if_table.repository

import com.kyungmin.springbatchstudy.batch.if_table.entity.WinEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WinRepository : JpaRepository<WinEntity, Long> {
  // 특정 win보다 크거나 같은 데이터를 Pageable하는 함수
  fun findByWinGreaterThanEqual(
    win: Long,
    pageable: Pageable
  ): Page<WinEntity>
}