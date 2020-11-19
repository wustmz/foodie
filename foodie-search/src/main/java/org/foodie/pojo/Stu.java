package org.foodie.pojo;

/**
 * @Author steve.mei
 * @Version Stu,  2020/11/16 下午12:21
 **/

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author steve.mei
 * @version 1.0
 */
@Data
@Document(indexName = "stu", type = "_doc")
public class Stu {
    @Id
    private Integer id;
    @Field(store = true)
    private String name;
    @Field(store = true)
    private Integer age;
    @Field(store = true)
    private Float money;
    @Field(store = true)
    private String sign;
    @Field(store = true)
    private String description;
}
