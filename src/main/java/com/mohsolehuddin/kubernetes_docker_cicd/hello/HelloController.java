package com.mohsolehuddin.kubernetes_docker_cicd.hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping()
    ResponseEntity<?> getHello (){
        return ResponseEntity.status(HttpStatus.OK).body("Hello World");
    }
}