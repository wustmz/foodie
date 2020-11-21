package org.foodie.service;

import org.foodie.utils.PagedGridResult;

public interface ItemsEsService {

    PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);
}
