package com.stefan.springjwt;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.stefan.springjwt.uploadservice.FilesStorageService;

@SpringBootApplication
@EnableAsync
public class SpringBootSecurityJwtApplication implements CommandLineRunner {
	@Resource
  	FilesStorageService storageService;

	public static void main(String[] args) {

		try {
   			 SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
	}

	@Override
	public void run(String... arg) throws Exception {
		//storageService.deleteAll();
		storageService.init();
	}

}
