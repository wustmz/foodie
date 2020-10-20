package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.CategoryMapper;
import org.foodie.pojo.Category;
import org.foodie.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类  服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
