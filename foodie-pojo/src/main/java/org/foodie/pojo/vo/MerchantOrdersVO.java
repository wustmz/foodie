package org.foodie.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wustmz
 */
@Data
public class MerchantOrdersVO {

    @ApiModelProperty("商户订单号")
    private String merchantOrderId;
    @ApiModelProperty("商户方的发起用户的用户主键id")
    private String merchantUserId;
    @ApiModelProperty("实际支付总金额（包含商户所支付的订单费邮费总额）")
    private Integer amount;
    @ApiModelProperty("支付方式 1:微信   2:支付宝")
    private Integer payMethod;
    @ApiModelProperty("支付成功后的回调地址（学生自定义）")
    private String returnUrl;
}