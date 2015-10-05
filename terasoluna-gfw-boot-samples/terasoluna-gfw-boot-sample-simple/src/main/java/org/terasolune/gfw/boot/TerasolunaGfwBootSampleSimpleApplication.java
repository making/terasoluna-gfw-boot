package org.terasolune.gfw.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TerasolunaGfwBootSampleSimpleApplication {
    @Autowired
    ErrorService errorService;

    @RequestMapping("/err")
    void err() {
        errorService.throwBusinessException();
    }

    public static void main(String[] args) {
        SpringApplication.run(TerasolunaGfwBootSampleSimpleApplication.class, args);
    }
}
