package org.foodie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author steve.mei
 * @Version Swagger2,  2020/10/19 10:49 下午
 **/
@Configuration
@EnableSwagger2
public class Swagger2 {

    /**
     * 配置swagger2核心配置 docket
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        //指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //定义api文档的汇总信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.foodie.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //文档页标题
                .title("电商平台接口api")
                .contact(new Contact("foodie", "www.mz.com", "abc@test.com"))
                .description("专为吃货提供的api文档")
                .version("1.0.1")
                //网站地址
                .termsOfServiceUrl("https://www.foodie.com")
                .build();
    }

}
