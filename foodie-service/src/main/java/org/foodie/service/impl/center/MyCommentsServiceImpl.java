package org.foodie.service.impl.center;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import org.foodie.enums.YesOrNo;
import org.foodie.mapper.ItemsCommentsMapperCustom;
import org.foodie.mapper.OrderItemsMapper;
import org.foodie.mapper.OrderStatusMapper;
import org.foodie.mapper.OrdersMapper;
import org.foodie.pojo.OrderItems;
import org.foodie.pojo.OrderStatus;
import org.foodie.pojo.Orders;
import org.foodie.pojo.bo.center.OrderItemsCommentBO;
import org.foodie.pojo.vo.MyCommentVO;
import org.foodie.service.center.MyCommentsService;
import org.foodie.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    public OrderItemsMapper orderItemsMapper;

    @Autowired
    public OrdersMapper ordersMapper;

    @Autowired
    public OrderStatusMapper orderStatusMapper;

    @Autowired
    public ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        QueryWrapper<OrderItems> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(OrderItems::getOrderId, orderId);
        return orderItemsMapper.selectList(wrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String orderId, String userId,
                             List<OrderItemsCommentBO> commentList) {

        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemsCommentsMapperCustom.saveComments(map);

        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);

        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Orders::getId, orderId);
        ordersMapper.update(order, updateWrapper);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(LocalDateTime.now());

        UpdateWrapper<OrderStatus> orderStatusUpdateWrapper = new UpdateWrapper<>();
        orderStatusUpdateWrapper.lambda().eq(OrderStatus::getOrderId, orderId);
        orderStatusMapper.update(orderStatus, orderStatusUpdateWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId,
                                           Integer page,
                                           Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedGrid(list, page);
    }
}
