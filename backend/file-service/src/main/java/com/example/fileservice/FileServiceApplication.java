package com.example.fileservice;

import com.example.fileservice.dao.S3Dao;
import com.example.fileservice.exception.ObjectAlreadyExistsException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;
@EnableWebMvc
@SpringBootApplication
public class FileServiceApplication {

    public static void main(String[] args) throws ObjectAlreadyExistsException {
        SpringApplication.run(FileServiceApplication.class, args);
//        File file = new File("/Users/gaiqianmian/Desktop/team2/mics-yours/file-service/src/main/resources/testpic.jpg");
//        S3Dao profileDao = new S3Dao();
//        // print current pwd
//        System.out.println(System.getProperty("user.dir"));
//        profileDao.putObject("test.jpg", file);
    }

}
