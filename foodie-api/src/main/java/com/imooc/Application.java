package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 扫描 mybastis 通用mappers 所在的包
@MapperScan(basePackages = "com.imooc.mapper")
//扫描所有包以及相关主键包
@ComponentScan(basePackages = {"com.imooc", "common.imooc.org.n3r.idworker"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
