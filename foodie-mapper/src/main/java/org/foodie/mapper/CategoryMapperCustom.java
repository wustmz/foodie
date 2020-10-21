package org.foodie.mapper;


import org.apache.ibatis.annotations.Param;
import org.foodie.pojo.vo.CategoryVO;
import org.foodie.pojo.vo.NewItemsVO;

import java.util.List;
import java.util.Map;

/**
 * 自定义customer mapper
 */
public interface CategoryMapperCustom {

    List<CategoryVO> getSubCatList(Integer rootCatId);

    List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}