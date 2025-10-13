package com.kyungmin.springbatchstudy.batch.table_to_table.repository

import com.kyungmin.springbatchstudy.batch.table_to_table.entity.AfterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AfterRepository : JpaRepository<AfterEntity, Long>