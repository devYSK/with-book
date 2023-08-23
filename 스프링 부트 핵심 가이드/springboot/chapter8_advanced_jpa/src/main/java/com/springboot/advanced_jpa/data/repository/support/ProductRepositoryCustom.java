package com.springboot.advanced_jpa.data.repository.support;

import com.springboot.advanced_jpa.data.entity.Product;
import java.util.List;

/**
 * 필요한 쿼리를 작성할 메소드를 정의하는 인터페이스
 * 예제 8.36
 */
public interface ProductRepositoryCustom {

    List<Product> findByName(String name);

}