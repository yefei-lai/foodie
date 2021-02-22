package com.imooc.service.impl;

import com.imooc.es.pojo.Items;
import com.imooc.service.ItemsESService;
import common.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchTemplate esTemplate;


    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        // 高亮样式
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(page, pageSize);

        // 排序
        SortBuilder sortBuilder = null;

        if (sort.equals("c")){
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);

        } else if (sort.equals("p")){
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);

        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);

        }

        String itemNameField = "itemName";

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameField, keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameField)
                        .preTags(preTag)
                        .postTags(postTag))
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();

        AggregatedPage<Items> pagedItems = esTemplate.queryForPage(query, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                // 替换高亮数据
                List<Items> itemListHighlight  = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit h : hits){
                    HighlightField highlightField = h.getHighlightFields().get(itemNameField);
                    String itemName = highlightField.getFragments()[0].toString();

                    String itemId = (String) h.getSourceAsMap().get("itemId");
                    String imgUrl = (String)h.getSourceAsMap().get("imgUrl");
                    Integer price = (Integer)h.getSourceAsMap().get("price");
                    Integer sellCounts = (Integer) h.getSourceAsMap().get("sellCounts");

                    Items item = new Items();
                    item.setItemId(itemId);
                    item.setImgUrl(imgUrl);
                    item.setPrice(price);
                    item.setSellCounts(sellCounts);

                    itemListHighlight.add(item);
                }

                return new AggregatedPageImpl<>((List<T>)itemListHighlight
                , pageable, searchResponse.getHits().totalHits);
            }
        });

        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(pagedItems.getContent());
        pagedGridResult.setPage(page + 1);
        pagedGridResult.setTotal(pagedItems.getTotalPages());
        pagedGridResult.setRecords(pagedItems.getTotalElements());


        return pagedGridResult;
    }
}
