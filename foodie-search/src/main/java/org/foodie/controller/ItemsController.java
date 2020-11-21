package org.foodie.controller;

import org.apache.commons.lang3.StringUtils;
import org.foodie.service.ItemsEsService;
import org.foodie.utils.PagedGridResult;
import org.foodie.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author steve.mei
 * @Version ItemsController,  2020/11/21 下午8:23
 **/
@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsEsService itemsEsService;

    @GetMapping("/es/search")
    public ServerResponse search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return ServerResponse.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page--;

        PagedGridResult grid = itemsEsService.searchItems(keywords,
                sort,
                page,
                pageSize);

        return ServerResponse.ok(grid);
    }
}
