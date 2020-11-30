package com.imooc.service.impl.center;

import com.github.pagehelper.PageInfo;
import common.imooc.utils.PagedGridResult;

import java.util.List;

public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageList = new PageInfo<>();
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(list.size());
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
