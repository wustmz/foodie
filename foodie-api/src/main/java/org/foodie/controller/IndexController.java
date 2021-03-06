package org.foodie.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.foodie.enums.YesOrNo;
import org.foodie.pojo.Carousel;
import org.foodie.pojo.Category;
import org.foodie.pojo.vo.CategoryVO;
import org.foodie.pojo.vo.NewItemsVO;
import org.foodie.service.ICarouselService;
import org.foodie.service.ICategoryService;
import org.foodie.utils.JsonUtils;
import org.foodie.utils.RedisOperator;
import org.foodie.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wustmz
 */
@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private ICarouselService carouselService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 如何管理缓存？
     * 1.后台运营系统，一旦广告发生更改，就可以删除缓存，然后重置
     * 2.定是重置，比如每天凌晨三点重置
     * 3.每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置
     *
     * @return
     */
    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public ServerResponse carousel() {
        String key = "carousel";

        String cache = redisOperator.get(key);

        List<Carousel> list;
        if (StringUtils.isBlank(cache)) {
            list = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set(key, JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(cache, Carousel.class);
        }

        return ServerResponse.ok(list);
    }

    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ServerResponse cats() {

        String key = "cats";
        String cache = redisOperator.get(key);
        List<Category> list;
        if (StringUtils.isBlank(cache)) {
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set(key, JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(cache, Category.class);
        }

        return ServerResponse.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public ServerResponse subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ServerResponse.errorMsg("分类不存在");
        }

        List<CategoryVO> list = categoryService.getSubCatList(rootCatId);
        return ServerResponse.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public ServerResponse sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ServerResponse.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return ServerResponse.ok(list);
    }

}
