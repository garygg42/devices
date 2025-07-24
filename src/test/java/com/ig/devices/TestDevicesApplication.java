package com.ig.devices;

import org.springframework.boot.SpringApplication;

public class TestDevicesApplication {

	public static void main(String[] args) {
		SpringApplication.from(DevicesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
