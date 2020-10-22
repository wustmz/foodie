package org.foodie.service.impl.center;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import org.foodie.enums.OrderStatusEnum;
import org.foodie.enums.YesOrNo;
import org.foodie.mapper.OrderStatusMapper;
import org.foodie.mapper.OrdersMapper;
import org.foodie.mapper.OrdersMapperCustom;
import org.foodie.pojo.OrderStatus;
import org.foodie.pojo.Orders;
import org.foodie.pojo.vo.MyOrdersVO;
import org.foodie.pojo.vo.OrderStatusCountsVO;
import org.foodie.service.center.MyOrdersService;
import org.foodie.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrdersServiceImpl extends BaseService implements MyOrdersService {

    @Autowired
    public OrdersMapperCustom ordersMapperCustom;

    @Autowired
    public OrdersMapper ordersMapper;

    @Autowired
    public OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (orderStatus != null) {
            map.put("orderStatus", orderStatus);
        }

        PageHelper.startPage(page, pageSize);

        List<MyOrdersVO> list = ordersMapperCustom.queryMyOrders(map);

        return setterPagedGrid(list, page);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {

        QueryWrapper<OrderStatus> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(OrderStatus::getOrderId, orderId)
                .eq(OrderStatus::getOrderStatus, OrderStatusEnum.WAIT_DELIVER.type);

        OrderStatus orderStatus = orderStatusMapper.selectOne(wrapper);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        orderStatus.setDeliverTime(LocalDateTime.now());

        orderStatusMapper.updateById(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {

        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(Orders::getUserId, userId)
                .eq(Orders::getId, orderId)
                .eq(Orders::getIsDelete, YesOrNo.NO.type);

        return ordersMapper.selectOne(wrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {

        QueryWrapper<OrderStatus> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(OrderStatus::getOrderId, orderId)
                .eq(OrderStatus::getOrderStatus, OrderStatusEnum.WAIT_RECEIVE.type);

        OrderStatus orderStatus = orderStatusMapper.selectOne(wrapper);
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(LocalDateTime.now());

        int result = orderStatusMapper.updateById(orderStatus);

        return result == 1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String userId, String orderId) {

        Orders orders = this.queryMyOrder(userId, orderId);
        orders.setIsDelete(YesOrNo.YES.type);
        orders.setUpdatedTime(LocalDateTime.now());

        int result = ordersMapper.updateById(orders);

        return result == 1;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVO getOrderStatusCounts(String userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);
        int waitCommentCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        return new OrderStatusCountsVO(waitPayCounts,
                waitDeliverCounts,
                waitReceiveCounts,
                waitCommentCounts);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<OrderStatus> list = ordersMapperCustom.getMyOrderTrend(map);

        return setterPagedGrid(list, page);
    }
}
