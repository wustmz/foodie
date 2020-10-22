package org.foodie.pojo.vo;

import lombok.Data;

/**
 * 用于展示商品评价数量的vo
 *
 * @author wustmz
 */
@Data
public class CommentLevelCountsVO {

    public Integer totalCounts;
    public Integer goodCounts;
    public Integer normalCounts;
    public Integer badCounts;
}
