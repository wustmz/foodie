package org.foodie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.foodie.pojo.Carousel;

import java.util.List;

/**
 * <p>
 * 轮播图  服务类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
public interface ICarouselService extends IService<Carousel> {

    /**
     * 查询所有轮播图列表
     * @param isShow
     * @return
     */
    List<Carousel> queryAll(Integer isShow);
}
