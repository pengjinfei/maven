package com.pengjinfei.maven.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
@Configuration
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public MdcInterceptor mdcInterceptor() {
        return new MdcInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcInterceptor());
    }
}
