package com.imooc.service.impl;

import com.imooc.service.ItemsESService;
import common.imooc.utils.PagedGridResult;
import org.springframework.stereotype.Service;

@Service
public class ItemESServiceImpl implements ItemsESService {


    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        return null;
    }
}
