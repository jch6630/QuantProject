/**
 * 
 */
/**
 * 
 */
module Quant {
	requires java.logging;
	requires java.net.http;
	requires com.fasterxml.jackson.databind;

	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql; // JDBC를 위한 기본 모듈
	requires com.zaxxer.hikari; // HikariCP 모듈 추가

	requires org.slf4j;
	requires org.mariadb.jdbc;
	requires org.apache.logging.log4j; // 로그 관련 모듈 (있을 경우)

	requires okhttp3;
	requires com.google.gson; // Gson 모듈 의존성 추가

	requires java.base;
	
	opens com.jch to javafx.fxml; // MyBatis가 접근할 수 있도록 열어줌
	opens com.jch.DTO to com.fasterxml.jackson.databind;


	exports com.jch;
	exports com.jch.DTO to com.fasterxml.jackson.databind;
}