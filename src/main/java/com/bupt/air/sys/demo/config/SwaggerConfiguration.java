package com.bupt.air.sys.demo.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfiguration {
    @Bean
    public Docket createDemoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Swagger示例")
                        .description("模拟空调系统")
                        .version("1.0")
                        .build())//设置API基本信息
                .select()// 选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.basePackage("com.bupt.air.sys.demo")) // 设置对哪些包内的api进行监控
                .paths(PathSelectors.ant("/api/**")) // 仅监控路径/api/**
                //.paths(PathSelectors.any()) // 对所有路径进行监控
                .build()
                .groupName("Swagger Air System Demo");
    }
}
