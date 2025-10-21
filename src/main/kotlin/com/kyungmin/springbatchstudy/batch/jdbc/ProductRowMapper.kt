package com.kyungmin.springbatchstudy.batch.jdbc

import com.kyungmin.springbatchstudy.batch.jdbc.entity.Product
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Product용 Row Mapper Class
 * - RowMapper : JDBC에서 DB 결과(ResultSet)를 객체로 매핑해주는 Interface
 */

class ProductRowMapper : RowMapper<Product> {

  // ENUM 상수를 만들어 내부에서만 사용 가능하도록 만듦.
  enum class Columns(val value: String) {
    ID_COLUMN("id"),
    NAME("name"),
    DESC("description"),
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