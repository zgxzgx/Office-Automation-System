package cn.edu.shou.missive;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {




    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
//        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login2");
        registry.addViewController("/logoutsuccess").setViewName("logoutsuccess");
//        registry.addViewController("/newInstance.html").setViewName("xjlc");
//        registry.addViewController("/treeviewtest.html").setViewName("treeviewtest");
//        registry.addViewController("/FieldManage.html").setViewName("FieldManage");
//        registry.addViewController("/scheduler.html").setViewName("Scheduler");
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        super.configureMessageConverters(converters);

    }
}