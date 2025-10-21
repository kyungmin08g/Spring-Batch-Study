package com.kyungmin.springbatchstudy.batch.jdbc.repository

import com.kyungmin.springbatchstudy.batch.jdbc.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Product Entity Repository
 */

@Repository
interface ProductRepository : JpaRepository<Product, Long>