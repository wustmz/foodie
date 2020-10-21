package org.foodie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.foodie.pojo.Category;
import org.foodie.pojo.vo.CategoryVO;
import org.foodie.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * <p>
 * 商品分类  服务类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 查询所有一级分类
     *
     * @return
     */
    List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id查询子分类信息
     *
     * @param rootCatId
     * @return
     */
    List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     *
     * @param rootCatId
     * @return
     */
    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);

}
