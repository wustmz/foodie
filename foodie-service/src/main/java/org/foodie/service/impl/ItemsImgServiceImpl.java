package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.ItemsImgMapper;
import org.foodie.pojo.ItemsImg;
import org.foodie.service.IItemsImgService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品图片  服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class ItemsImgServiceImpl extends ServiceImpl<ItemsImgMapper, ItemsImg> implements IItemsImgService {

}
