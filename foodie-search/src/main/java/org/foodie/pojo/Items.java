package org.foodie.pojo;

/**
 * @Author steve.mei
 * @Version Stu,  2020/11/16 下午12:21
 **/

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author steve.mei
 * @version 1.0
 */
@Data
@Document(indexName = "foodie-items-ik", type = "doc", createIndex = false)
public class Items {
    @Id
    @Field(store = true, type = FieldType.Text, index = false)
    private String itemId;

    @Field(store = true, type = FieldType.Text)
    private String itemName;

    @Field(store = true, type = FieldType.Text, index = false)
    private String imgUrl;

    @Field(store = true, type = FieldType.Integer)
    private Integer price;

    @Field(store = true, type = FieldType.Integer)
    private Integer sellCounts;
}
