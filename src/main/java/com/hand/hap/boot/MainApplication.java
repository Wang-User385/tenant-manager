package com.hand.hap.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

@SpringBootApplication
@PropertySource("classpath:hap-default-config.properties")
@ComponentScan(value = {"com.hand.hls.interfacePlatform.utils","com.hand.hls.elecSeal.utils","com.hand.hls.wsdl.components"})
public class MainApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private static String liquibase = null;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private Environment environment;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }

    public static void main(String[] args) {
        liquibase = System.getProperty("hap.liquibase");
        if (liquibase != null && !"false".equals(liquibase)) {
            System.setProperty("spring.session.store-type", "none");
            System.setProperty("spring.autoconfigure.exclude", "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration, org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration");
        }
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (liquibase != null && !"false".equals(liquibase)) {
            if (System.getProperty("spring.datasource.url") == null) {
                System.setProperty("spring.datasource.url", environment.getProperty("spring.datasource.url"));
            }
//            CustomSpringLiquibase springLiquibase = new CustomSpringLiquibase();
//            springLiquibase.setDataSource(dataSource);
//            springLiquibase.setResourceLoader(resourceLoader);
//            springLiquibase.setClearCheckSum(true);
//            springLiquibase.setChangeLog("classpath:/com/hand/hap/db/liquibase.groovy");
//            springLiquibase.afterPropertiesSet();
//            MetadataDriverDelegate.syncMetadata(dataSource);
            context.close();
        }
    }
}