package org.foodie.pojo.bo;

import lombok.Data;

/**
 * 用于创建订单的BO对象
 *
 * @author wustmz
 */
@Data
public class SubmitOrderBO {

    private String userId;
    private String itemSpecIds;
    private String addressId;
    private Integer payMethod;
    private String leftMsg;
}
