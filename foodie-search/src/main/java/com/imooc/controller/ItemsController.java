package com.imooc.controller;

import com.imooc.service.ItemsESService;
import common.imooc.utils.IMOOCJSONResult;
import common.imooc.utils.PagedGridResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsESService itemsESService;

    @ApiOperation(value = "搜索商品列表")
    @GetMapping("/es/search")
    public IMOOCJSONResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize){
        if (null == keywords){
            return IMOOCJSONResult.errorMsg(null);
        }
        if (null == page){
            page = 1;
        }
        if (null == pageSize){
            pageSize = 20;
        }
        page --;
        PagedGridResult gridResult = itemsESService.searchItems(keywords, sort, page, pageSize);
        return IMOOCJSONResult.ok(gridResult);
    }

}
