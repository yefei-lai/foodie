package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
// 扫描 mybastis 通用mappers 所在的包
@MapperScan(basePackages = "com.imooc.mapper")
//扫描所有包以及相关主键包
@ComponentScan(basePackages = {"com.imooc", "common.imooc.org.n3r.idworker"})
//开启定时任务
@EnableScheduling
@EnableRedisHttpSession //开启使用redis作为springSession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
