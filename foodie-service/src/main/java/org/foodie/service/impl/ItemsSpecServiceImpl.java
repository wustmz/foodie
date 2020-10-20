package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.ItemsSpecMapper;
import org.foodie.pojo.ItemsSpec;
import org.foodie.service.IItemsSpecService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品规格 每一件商品都有不同的规格，不同的规格又有不同的价格和优惠力度，规格表为此设计 服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class ItemsSpecServiceImpl extends ServiceImpl<ItemsSpecMapper, ItemsSpec> implements IItemsSpecService {

}
