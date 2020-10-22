package org.foodie.pojo.vo;

import lombok.Data;

/**
 * @author wustmz
 */
@Data
public class ShopcartVO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private String priceDiscount;
    private String priceNormal;
}
