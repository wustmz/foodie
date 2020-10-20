package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.ItemsCommentsMapper;
import org.foodie.pojo.ItemsComments;
import org.foodie.service.IItemsCommentsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表  服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class ItemsCommentsServiceImpl extends ServiceImpl<ItemsCommentsMapper, ItemsComments> implements IItemsCommentsService {

}
