package com.kyungmin.springbatchstudy.batch.if_table.repository

import com.kyungmin.springbatchstudy.batch.if_table.entity.WinEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WinRepository : JpaRepository<WinEntity, Long>