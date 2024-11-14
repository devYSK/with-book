package com.yscorp.configservice

import org.springframework.cloud.config.server.environment.EnvironmentRepository
import org.springframework.stereotype.Repository

/**
 * CREATE TABLE config_properties (
 *     application VARCHAR(100),
 *     profile VARCHAR(100),
 *     label VARCHAR(100),
 *     key VARCHAR(255),
 *     value TEXT
 * );
 */
class DataBase {
}

//@Repository
//class JdbcEnvironmentRepository(
//    private val jdbcTemplate: JdbcTemplate
//) : EnvironmentRepository {
//
//    override fun findOne(application: String, profile: String, label: String): Environment {
//        val environment = Environment(application, profile)
//
//        val properties = jdbcTemplate.query(
//            "SELECT key, value FROM config_properties WHERE application = ? AND profile = ? AND label = ?",
//            arrayOf(application, profile, label)
//        ) { rs, _ ->
//            rs.getString("key") to rs.getString("value")
//        }.toMap()
//
//        val propertySource = PropertySource("database", properties)
//        environment.add(propertySource)
//        return environment
//    }
//}

/**
 * spring:
 *   cloud:
 *     config:
 *       server:
 *         bootstrap: true
 *         # 커스텀 EnvironmentRepository 사용
 *         environment-repository:
 *           jdbc:
 *             enabled: true
 *
 */