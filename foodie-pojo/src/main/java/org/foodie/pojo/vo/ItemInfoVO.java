package org.foodie.pojo.vo;

import lombok.Data;
import org.foodie.pojo.Items;
import org.foodie.pojo.ItemsImg;
import org.foodie.pojo.ItemsParam;
import org.foodie.pojo.ItemsSpec;

import java.util.List;

/**
 * 商品详情VO
 *
 * @author wustmz
 */
@Data
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
