package com.kyungmin.springbatchstudy.batch.jdbc

import com.kyungmin.springbatchstudy.batch.jdbc.entity.Product
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class ProductRowMapper : RowMapper<Product> {

  enum class Columns(val value: String) {
    ID_COLUMN("productId"),
    NAME("name"),
    DESC("desc"),
    PRICE("price")
  }

  override fun mapRow(
    rs: ResultSet,
    rowNum: Int
  ): Product = Product(
    rs.getLong(Columns.ID_COLUMN.value),
    rs.getString(Columns.NAME.value),
    rs.getString(Columns.DESC.value),
    rs.getLong(Columns.PRICE.value)
  )
}