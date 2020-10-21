package org.foodie.pojo.vo;

import lombok.Data;

/**
 * 6个最新商品的简单数据类型
 */
@Data
public class SimpleItemVO {

    private String itemId;
    private String itemName;
    private String itemUrl;
}
