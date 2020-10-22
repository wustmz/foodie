package org.foodie.pojo.vo;

import lombok.Data;

/**
 * @author wustmz
 */
@Data
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
}