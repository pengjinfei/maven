package com.pengjinfei.maven.configuration.integration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Configuration
@ImportResource(locations = {"classpath:integration.xml"})
public class IntegrationConfiguration {

}

