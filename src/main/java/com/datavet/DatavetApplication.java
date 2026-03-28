package com.datavet;

import com.datavet.shared.infrastructure.config.DotenvInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DatavetApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(DatavetApplication.class)
				.initializers(new DotenvInitializer())
				.run(args);
	}
}
