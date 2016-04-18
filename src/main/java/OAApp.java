//import cn.edu.shou.missive.JettyProperties;
import cn.edu.shou.missive.service.ScheduledTasks;
import cn.edu.shou.missive.service.UserRepository;
import cn.edu.shou.spring.ApplicationContextHolder;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.restlet.ext.servlet.ServerServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletConfig;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EntityScan({"cn.edu.shou.missive"})
@ComponentScan({"cn.edu.shou.missive"})
@Import(ScheduledTasks.class)
@ImportResource("activiti-standalone-context.xml")
@EnableAsync
@EnableAutoConfiguration
@EnableScheduling
@EnableConfigurationProperties
public class OAApp {



    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet()
    {
        return new DispatcherServlet();
    }

//    @Bean
//    public JettyProperties embeddedServletCustomizer() {
//        return new JettyProperties();
//    }

    @Bean
    MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("256MB");
        factory.setMaxRequestSize("256MB");
        return factory.createMultipartConfig();
    }


    /**
     * Configure the CharacterEncodingFilter to filt all request with encode utf-8
     * */
    @Bean
    public FilterRegistrationBean filterRegistrationBean () {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("utf-8");
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }


    /**
     * Configure the OpenEntityManagerInViewFilter to keep EntityManager in session
     * */
    @Bean
    public FilterRegistrationBean openSessionInView () {
        OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(openEntityManagerInViewFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }



    public static void main(String[] args) throws Exception {

        ApplicationContext ctx = SpringApplication.run(OAApp.class, args);
        ApplicationContextHolder.getInstance().setApplicationContext(ctx);

    }

}