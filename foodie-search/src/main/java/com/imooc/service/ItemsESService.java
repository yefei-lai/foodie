package com.imooc.service;

import common.imooc.utils.PagedGridResult;

public interface ItemsESService {

    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);
}
