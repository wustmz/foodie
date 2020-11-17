package org.foodie.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author steve.mei
 * @Version ElasticSearchConfig,  2020/11/15 下午9:29
 **/
@Configuration
public class ElasticSearchConfig {

    /**
     * 解决netty引起的issue 6.4.3
     */
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
