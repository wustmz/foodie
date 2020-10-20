package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.OrdersMapper;
import org.foodie.pojo.Orders;
import org.foodie.service.IOrdersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表  服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
