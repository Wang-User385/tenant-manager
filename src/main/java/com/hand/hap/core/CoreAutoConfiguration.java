package com.hand.hap.core;

import com.bstek.ureport.console.UReportServlet;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.AdminServlet;
import com.hand.hap.core.json.CustomStringDeserializer;
import com.hand.hap.core.json.DateTimeDeserializer;
import com.hand.hap.core.json.DateTimeSerializer;
import com.hand.hap.core.web.HapEnhanceFilter;
import com.hand.hap.security.AuthenticationRequestContextFilter;
import com.hand.hap.security.CORSFilter;
import com.hand.hap.security.filter.IPChangeVerifierFilter;
import com.hand.hls.autoconfigure.OSSAutoConfiguration;
import com.hand.hls.plugin.jacob.servlet.OfficeConvertServlet;
import com.hand.hls.utils.SpringContextHolder;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 36000)
@ImportAutoConfiguration(OSSAutoConfiguration.class)
@ImportResource({
        "classpath:spring/applicationContext.xml",
        "classpath:spring/applicationContext-redis.xml",
        "classpath:spring/applicationContext-leafCache.xml",
        "classpath:spring/applicationContext-activiti.xml",
        "classpath:spring/appServlet/servlet-activiti.xml",
        "classpath:spring/applicationContext-cxf.xml",
        "classpath:spring/applicationContext-ext.xml",
        "classpath:spring/applicationContext-gateway.xml",
        "classpath:spring/applicationContext-interface.xml",
        "classpath:spring/applicationContext-leaf.xml",
        "classpath:spring/applicationContext-mail.xml",
        "classpath:spring/applicationContext-msg.xml",
        "classpath:spring/applicationContext-job.xml",
        "classpath:spring/applicationContext-oauth2.xml",
        "classpath:spring/applicationContext-security.xml",
        "classpath:spring/applicationContext-websocket.xml",
        "classpath:spring/applicationContext-ureportcore.xml"})
//@ConditionalOnProperty(matchIfMissing = true, havingValue = "false", prefix = "hap", name = "liquibase")
public class CoreAutoConfiguration {

    @Value("${cors.allow.all:false}")
    private boolean corsAllowAll;

    @Bean
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CORSFilter());
        Map<String, String> initParameters = new HashMap<>();
        if (corsAllowAll) {
            initParameters.put("allowedMappings", "/**");
        } else {
            initParameters.put("allowedMappings", "/oauth/**;/api/**;/r/api/**");
        }
        initParameters.put("allowedHeader", "*");
        initParameters.put("allowedOrigin", "*");
        initParameters.put("allowedMethod", "*");
        registration.setInitParameters(initParameters);
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean encodingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CharacterEncodingFilter());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("encoding", "UTF-8");
        initParameters.put("forceEncoding", "true");
        registration.setInitParameters(initParameters);
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("encodingFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean hapEnhanceFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new HapEnhanceFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("hapEnhanceFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean hlsIpChangeFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new IPChangeVerifierFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("hlsIpChangeFilter");
        //内外网分离功能：把该过滤器放置在SessionRepositoryFilter与springSecurityFilterChain之间
        //实现切换网络后，不满足条件就立马弹出登录失效的弹窗
        registration.setOrder(-2147483597);
        return registration;
    }

    @Bean
    public FilterRegistrationBean instrumentedFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new InstrumentedFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("instrumentedFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean requestContextFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new AuthenticationRequestContextFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("requestContextFilter");
        return registration;
    }

    @Bean
    public ServletRegistrationBean metrics() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new AdminServlet());
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/metrics/*");
        return registration;
    }

    @Bean
    public ServletRegistrationBean ureport() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new UReportServlet());
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/ureport/*");
        return registration;
    }

    @Bean
    public ServletRegistrationBean jacob() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new OfficeConvertServlet());
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/jacob/*");
        return registration;
    }

//    @Bean
//    public ServletRegistrationBean service() {
//        ServletRegistrationBean registration = new ServletRegistrationBean(new FacadeServlet());
//        registration.setAsyncSupported(true);
//        registration.addUrlMappings("*.lview", "*.lsc");
//        return registration;
//    }

//    @Bean
//    public ServletRegistrationBean autocrud() {
//        ServletRegistrationBean registration = new ServletRegistrationBean(new AutoCrudServlet());
//        registration.setAsyncSupported(true);
//        registration.addUrlMappings("/autocrud/*");
//        return registration;
//    }

    /*@Bean
    public FilterRegistrationBean view() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new BootFacadeFilter());
        registration.setAsyncSupported(true);
        registration.addUrlPatterns("/*");
        registration.setName("view");
        return registration;
    }*/

    @Bean
    public ServletRegistrationBean CXFServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CXFServlet());
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.addUrlMappings("/ws/*");
        return registration;
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }


    @Bean
    public Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializerByType(Date.class, new DateTimeSerializer());
        builder.deserializerByType(Date.class, new DateTimeDeserializer());
        builder.deserializerByType(String.class, new CustomStringDeserializer());
        builder.failOnEmptyBeans(false);
        return builder;
    }

    // 在Spring Boot中，注册的Filter Bean都会应用到所有请求上，而在Spring MVC中，只有在其他Bean或者web.xml中显式调用才会生效，
    // 这个方法将处理这些Filter Bean，将他们禁用，使其行为与Spring MVC相同。详见：http://dimafeng.com/2015/11/27/dynamic-bean-definition/，
    // 由于与Mybits的Bean处理冲突，只能在Mybits中显式调用该函数完成处理，详见：com.hand.hap.mybatis.spring.MapperScannerConfigurer.postProcessBeanDefinitionRegistry
    public static void postProcessBeanFactory(BeanDefinitionRegistry registry) throws BeansException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) registry;
        Arrays.stream(beanFactory.getBeanNamesForType(javax.servlet.Filter.class))
                .forEach(name -> {
                    if ("springSessionRepositoryFilter".equals(name)) {
                        return;
                    }
                    BeanDefinition definition = BeanDefinitionBuilder
                            .genericBeanDefinition(FilterRegistrationBean.class)
                            .setScope(BeanDefinition.SCOPE_SINGLETON)
                            .addConstructorArgReference(name)
                            .addConstructorArgValue(new ServletRegistrationBean[]{})
                            .addPropertyValue("enabled", false)
                            .getBeanDefinition();
                    beanFactory.registerBeanDefinition(name + "FilterRegistrationBean", definition);
                });
    }

}
