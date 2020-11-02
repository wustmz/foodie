package org.foodie.pojo.vo;

import lombok.Data;
import org.foodie.pojo.bo.ShopcartBO;

import java.util.List;

/**
 * @author wustmz
 */
@Data
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopCartList;
}