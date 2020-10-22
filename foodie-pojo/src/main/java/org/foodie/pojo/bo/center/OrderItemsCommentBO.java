package org.foodie.pojo.bo.center;

import lombok.Data;

/**
 * @author wustmz
 */
@Data
public class OrderItemsCommentBO {

    private String commentId;
    private String itemId;
    private String itemName;
    private String itemSpecId;
    private String itemSpecName;
    private Integer commentLevel;
    private String content;
}