package com.scheduler;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JHeliosSchedulerManagerApplication {

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(JHeliosSchedulerManagerApplication.class, args);
	}

}
