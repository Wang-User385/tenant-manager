package com.hand.hap.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hand.hap.core.interceptor.MonitorInterceptor;
import com.hand.hap.core.json.JacksonMapper;
import com.hand.hap.core.web.DefaultFreeMarkerView;
import com.hand.hap.core.web.FormView;
import com.hand.hap.core.web.view.ViewTagFactory;
import com.hand.hls.interceptor.RequestContextInterceptor;
import leaf.view.LeafViewResolver.LeafView;
import leaf.view.LeafViewResolver.LeafViewConfig;
import leaf.view.LeafViewResolver.LeafViewResolver;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.List;
import java.util.Properties;

@Configuration
@ComponentScan({"**.controllers", "**.adaptor"})
public class LeafWebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean(value = "localeResolver")
    public SessionLocaleResolver sessionLocaleResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(LocaleUtils.toLocale("zh_CN"));
        return localeResolver;
    }

    @Bean(value = "themeResolver")
    public SessionThemeResolver sessionThemeResolver() {
        SessionThemeResolver themeResolver = new SessionThemeResolver();
        themeResolver.setDefaultThemeName("bootstrap");
        return themeResolver;
    }

    @Bean(value = "screenTagFactory")
    public ViewTagFactory viewTagFactory() {
        ViewTagFactory viewTagFactory = new ViewTagFactory();
        viewTagFactory.setBasePackage("com.hand.hap.core.web.view.ui");
        return viewTagFactory;
    }

    @Bean("freemarkerConfig")
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPaths(
                "classpath:/leaf_static",
                "classpath:/leaf_static/WEB-INF/view",
                "classpath:/leaf_static/WEB-INF/templates",
                "classpath:/META-INF/resources/leaf_static",
                "classpath:/META-INF/resources/leaf_static/WEB-INF/view",
                "classpath:/META-INF/resources/leaf_static/WEB-INF/templates",
                "/");
        freeMarkerConfigurer.setDefaultEncoding("UTF-8");
        freeMarkerConfigurer.setPreferFileSystemAccess(false);
        Properties properties = new Properties();
        properties.setProperty("auto_import", "spring.ftl as spring");
        properties.setProperty("template_update_delay", "2");
        properties.setProperty("number_format", "#");
        properties.setProperty("date_format", "yyyy-MM-dd");
        properties.setProperty("time_format", "HH:mm:ss");
        properties.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        freeMarkerConfigurer.setFreemarkerSettings(properties);
        return freeMarkerConfigurer;
    }

    @Bean("defaultFreeMarkerView")
    public FreeMarkerViewResolver defaultFreeMarkerView() {
        FreeMarkerViewResolver defaultFreeMarkerView = new FreeMarkerViewResolver();
        defaultFreeMarkerView.setViewClass(DefaultFreeMarkerView.class);
        defaultFreeMarkerView.setSuffix(".html");
        defaultFreeMarkerView.setOrder(0);
        setCommonFreeMarkerViewResolverConfig(defaultFreeMarkerView);
        return defaultFreeMarkerView;
    }

    @Bean("leafFreeMarkerView")
    public LeafViewResolver leafViewResolver() {
        LeafViewResolver leafViewResolver = new LeafViewResolver();
        leafViewResolver.setViewClass(LeafView.class);
        leafViewResolver.setSuffix(".lview");
        leafViewResolver.setOrder(1);
        return leafViewResolver;
    }

    @Bean("lscFreeMarkerView")
    public LeafViewResolver lscFreeMarkerView() {
        LeafViewResolver lscFreeMarkerView = new LeafViewResolver();
        lscFreeMarkerView.setViewClass(LeafView.class);
        lscFreeMarkerView.setSuffix(".lsc");
        lscFreeMarkerView.setOrder(2);
        return lscFreeMarkerView;
    }

    @Bean("formView")
    public FreeMarkerViewResolver formView() {
        FreeMarkerViewResolver formView = new FreeMarkerViewResolver();
        formView.setViewClass(FormView.class);
        formView.setOrder(3);
        setCommonFreeMarkerViewResolverConfig(formView);
        return formView;
    }

    @Bean("screenView")
    public FreeMarkerViewResolver screenView() {
        FreeMarkerViewResolver screenView = new FreeMarkerViewResolver();
        screenView.setViewClass(DefaultFreeMarkerView.class);
        screenView.setSuffix(".view");
        screenView.setOrder(4);
        setCommonFreeMarkerViewResolverConfig(screenView);
        return screenView;
    }


    private void setCommonFreeMarkerViewResolverConfig(FreeMarkerViewResolver freeMarkerViewResolver) {
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        freeMarkerViewResolver.setRequestContextAttribute("base");
        freeMarkerViewResolver.setExposeRequestAttributes(true);
        freeMarkerViewResolver.setExposeSessionAttributes(true);
        freeMarkerViewResolver.setExposeSpringMacroHelpers(true);
        freeMarkerViewResolver.setAllowSessionOverride(true);
    }



    @Bean("leafViewConfig")
    public LeafViewConfig leafViewConfig(){
        LeafViewConfig leafViewConfig = new LeafViewConfig();
        leafViewConfig.setLviewPath("/leaf_static");
        return leafViewConfig;
    }

    @Bean("objectMapper")
    public ObjectMapper myMapper() {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/login.html").setViewName("/login");
        registry.addViewController("/index").setViewName("redirect:/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MonitorInterceptor());
        registry.addInterceptor(new RequestContextInterceptor());
        super.addInterceptors(registry);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //开启路径后缀匹配
        super.configurePathMatch(configurer);
        configurer.setUseRegisteredSuffixPatternMatch(true);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        JacksonMapper jacksonMapper = new JacksonMapper();
        converter.setObjectMapper(jacksonMapper);
        converters.add(byteArrayHttpMessageConverter);
        converters.add(converter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/lib/**").addResourceLocations("classpath:/leaf_static/lib/");
        registry.addResourceHandler("/resource/**").addResourceLocations("classpath:/leaf_static/resources/");
        registry.addResourceHandler("/leafresources/**").addResourceLocations("classpath:/leaf_static/resources/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/leaf_static/leaf/css/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/leaf_static/leaf/images/");
        registry.addResourceHandler("/javascripts/**").addResourceLocations("classpath:/leaf_static/leaf/javascripts/");
        registry.addResourceHandler("/kindeditor/**").addResourceLocations("classpath:/leaf_static/leaf/kindeditor/");
        registry.addResourceHandler("/office_edit_online/**").addResourceLocations("classpath:/leaf_static/leaf/office_edit_online/");
        registry.addResourceHandler("/modules/**").addResourceLocations("classpath:/leaf_static/modules/");
        registry.addResourceHandler("/editor-app/**").addResourceLocations("classpath:/leaf_static/resources/editor-app/");
        registry.addResourceHandler("/diagram-viewer/**").addResourceLocations("classpath:/leaf_static/resources/diagram-viewer/");
    }
}