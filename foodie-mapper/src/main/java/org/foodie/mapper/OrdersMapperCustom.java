package org.foodie.mapper;


import org.apache.ibatis.annotations.Param;
import org.foodie.pojo.OrderStatus;
import org.foodie.pojo.vo.MyOrdersVO;

import java.util.List;
import java.util.Map;

/**
 * @author wustmz
 */
public interface OrdersMapperCustom {

    List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);

    List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);

}
