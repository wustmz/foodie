package org.foodie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.foodie.pojo.OrderStatus;
import org.foodie.pojo.Orders;
import org.foodie.pojo.bo.SubmitOrderBO;
import org.foodie.pojo.vo.OrderVO;

/**
 * <p>
 * 订单表  服务类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 用于创建订单相关信息
     *
     * @param submitOrderBO
     */
    OrderVO createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param orderStatus
     */
    void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    void closeOrder();
}
