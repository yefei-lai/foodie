package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemService;
import common.imooc.enums.CommentLevel;
import common.imooc.enums.YesOrNo;
import common.imooc.utils.DesensitizationUtil;
import common.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

    @Override
    public Items queryItemById(String id) {
        return itemsMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example itemImgExp = new Example(ItemsImg.class);
        Example.Criteria criteria = itemImgExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(itemImgExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemSpecExp = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemSpecExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(itemSpecExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example itemParamExp = new Example(ItemsParam.class);
        Example.Criteria criteria = itemParamExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsParamMapper.selectOneByExample(itemParamExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountVO queryCommentCounts(String itemId) {

        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountVO commentLevelCountVO = new CommentLevelCountVO();
        commentLevelCountVO.setTotalCounts(totalCounts);
        commentLevelCountVO.setGoodCounts(goodCounts);
        commentLevelCountVO.setNormalCount(normalCounts);
        commentLevelCountVO.setBadCounts(badCounts);
        return commentLevelCountVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId, Integer level){
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (null != level){
            condition.setCommentLevel(level);
        }
        Integer count = itemsCommentsMapper.selectCount(condition);
        return count;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(Integer page,
                                                  Integer pageSize,
                                                  String itemId, Integer level) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        for (ItemCommentVO itemCommentVO : list){
            //昵称脱敏
            itemCommentVO.setNickname(DesensitizationUtil.commonDisplay(itemCommentVO.getNickname()));
        }
        return setterPagedGrid(list, page);
    }

    private PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageList = new PageInfo();
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(list.size());
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer page, Integer pageSize, String keywords, String sort) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);

        return setterPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer page, Integer pageSize, Integer catId, String sort) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {

        String ids[] = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);

        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int buyCounts) {

        // synchronized 不推荐使用，集群下无用，性能低下
        // 锁数据库：不推荐，导致数据库性能低下
        // 分布式锁 zookeeper redis

        // lockUtil.getLock(); -- 加锁

        // 1. 查询库存

        // 2. 判断库存是否足够

        // lockUtil.unLock(); -- 解锁

        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1){
            throw new RuntimeException("订单创建失败，原因：库存不足");
        }
    }
}
