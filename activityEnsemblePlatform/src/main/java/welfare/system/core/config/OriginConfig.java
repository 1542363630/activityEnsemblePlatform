package welfare.system.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/*
* 跨域配置
* */

@Configuration
public class OriginConfig {

    //请求跨域许可配置

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cors = new CorsConfiguration();
        //允许所有请求源
        cors.addAllowedOriginPattern("*");
        //允许所有请求头
        cors.addAllowedHeader("*");
        //允许发送GET和POST请求
        cors.addAllowedMethod("GET");
        cors.addAllowedMethod("POST");
        //允许跨域发送cookie
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cors);  //封装
        return new CorsFilter(source);
    }
}
