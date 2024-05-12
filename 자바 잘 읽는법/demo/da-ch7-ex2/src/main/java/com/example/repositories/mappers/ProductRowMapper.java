package com.example.repositories.mappers;

import com.example.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {

  @Override
  public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
    Product p = new Product();
    p.setId(rs.getInt("id"));
    p.setName(rs.getString("name"));
    return p;
  }
}
