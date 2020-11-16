package com.imooc.controller;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.service.ItemService;
import common.imooc.utils.IMOOCJSONResult;
import common.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = "商品信息展示的相关接口")
@RestController
@RequestMapping("items")
public class ItemsController extends BaseController{

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId){
        if (null == itemId){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        Items items = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItems(items);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId){
        if (null == itemId){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        CommentLevelCountVO commentLevelCountVO = itemService.queryCommentCounts(itemId);
        return IMOOCJSONResult.ok(commentLevelCountVO);
    }

    @ApiOperation(value = "查询商品评价")
    @GetMapping("/comments")
    public IMOOCJSONResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize){
        if (null == itemId){
            return IMOOCJSONResult.errorMsg("商品不存在");
        }
        if (null == page){
            page = 1;
        }
        if (null == pageSize){
            pageSize = COMMENT_PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.queryPagedComments(page, pageSize, itemId, level);
        return IMOOCJSONResult.ok(gridResult);
    }

    @ApiOperation(value = "搜索商品列表")
    @GetMapping("/search")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords", value = "搜索关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize){
        if (null == keywords){
            return IMOOCJSONResult.errorMsg(null);
        }
        if (null == page){
            page = 1;
        }
        if (null == pageSize){
            pageSize = PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.searchItems(page, pageSize, keywords, sort);
        return IMOOCJSONResult.ok(gridResult);
    }

    @ApiOperation(value = "通过分类id搜索商品列表")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId", value = "三级分类id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize){
        if (null == catId){
            return IMOOCJSONResult.errorMsg(null);
        }
        if (null == page){
            page = 1;
        }
        if (null == pageSize){
            pageSize = PAGE_SIZE;
        }
        PagedGridResult gridResult = itemService.searchItems(page, pageSize, catId, sort);
        return IMOOCJSONResult.ok(gridResult);
    }

}
