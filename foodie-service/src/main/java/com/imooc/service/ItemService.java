package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.ShopcartVO;
import common.imooc.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品id查询详情
     * @param id
     * @return
     */
    public Items queryItemById(String id);

    /**
     * 根据商品id查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格列表
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     * @param itemId
     */
    public CommentLevelCountVO queryCommentCounts(String itemId);

    /**
     * 根据商品id和等级查询商品评价信息(分页)
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(Integer page, Integer pageSize, String itemId, Integer level);

    /**
     * 搜索商品列表
     * @param page
     * @param pageSize
     * @param keywords
     * @param sort
     * @return
     */
    public PagedGridResult searchItems(Integer page, Integer pageSize,
                                       String keywords, String sort);


    /**
     * 根据分类id搜索商品列表
     * @param page
     * @param pageSize
     * @param catId
     * @param sort
     * @return
     */
    public PagedGridResult searchItems(Integer page, Integer pageSize,
                                       Integer catId, String sort);

    /**
     * 根据规格ids查询最新购物车中的商品数据(用于刷新渲染购物车中的商品数据)
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取商品规格信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品id获得商品主图url
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     * 减少库存
     * @param specId
     * @param buyCounts
     */
    public void decreaseItemSpecStock(String specId, int buyCounts);

}
