package com.pengjinfei.maven;

import com.pengjinfei.maven.dto.Product;
import com.pengjinfei.maven.service.ProductCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@Slf4j
public class Application {

    @Autowired
    ProductCodeService productCodeService;

    /**
     * main method.
     * @param args args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * simple hello controller.
     * @return "hello"
     */
    @GetMapping("/hello")
    public Product sayHello() {
        Product product=new Product();
        product.setName("hello");
        log.info("product = {}",product);
        return product;
    }

    @GetMapping("/code")
    public String code() {
       return productCodeService.getCode();
    }

}
