package org.foodie.service.impl.center;


import com.github.pagehelper.PageInfo;
import org.foodie.utils.PagedGridResult;

import java.util.List;

/**
 * @author wustmz
 */
public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

}
