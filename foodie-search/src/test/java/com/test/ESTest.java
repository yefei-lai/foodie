package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Student;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建、更新、删除）
     * 索引就像是数据库里的表，我们平时是不会通过java代码频繁的去创建删除数据库表的
     * 我们只会针对数据做CRUD的操作
     * 在es中也是同理，我们尽量使用 ElasticsearchTemplate 对文档数据进行CRUD操作
     * 1. 属性（FieldType）类型不灵活
     * 2. 主分片和副本分片数无法设置
     */

    /**
     * 创建索引
     */
    @Test
    public void createIndexStu(){
        Student stu = new Student();
        stu.setStuId(1001L);
        stu.setName("Jack");
        stu.setAge(18);
        stu.setMoney(18.8f);
        stu.setSign("my name is Jack");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteIndexStu(){
        esTemplate.deleteIndex(Student.class);
    }

    //  --------------------------------------- 分割线 ------------------------------------------------

    /**
     * 更新文档数据（通过id）
     */
    @Test
    public void UpdateStuDoc(){

        /**
         * 构建要修改的字段信息
         */
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign", "my name is not Jack");
        sourceMap.put("money", 88.6f);

        /**
         * 构建indexRequest
         */
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                                    .withClass(Student.class)
                                    .withId("1001")
                                    .withIndexRequest(indexRequest)
                                    .build();
        esTemplate.update(updateQuery);
    }

    /**
     * 查询文档数据
     */
    @Test
    public void getStuDoc(){

        GetQuery getQuery = new GetQuery();
        getQuery.setId("1001");
        Student student = esTemplate.queryForObject(getQuery, Student.class);

        System.out.println(student);
    }

    /**
     * 删除文档数据
     */
    @Test
    public void deleteStuDoc(){

        esTemplate.delete(Student.class, "1001");
    }

    //  --------------------------------------- 分割线 ------------------------------------------------

    /**
     * 检索文档数据 分页查询
     */
    @Test
    public void searchStuDoc(){

        Pageable pageable = PageRequest.of(0, 10);

        // 条件：description中包含Jack的数据
        SearchQuery query = new NativeSearchQueryBuilder()
                                .withQuery(QueryBuilders.matchQuery("description", "Jack"))
                                .withPageable(pageable)
                                .build();

        AggregatedPage<Student> pagedStu = esTemplate.queryForPage(query, Student.class);
        // 检索后的总分页数
        pagedStu.getTotalPages();
        // 检索后的list
        List<Student> studentList = pagedStu.getContent();
    }

    /**
     * 搜索数据高亮显示、排序
     */
    @Test
    public void highlightStuDoc(){

        // 高亮样式
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable = PageRequest.of(0, 10);

        // 排序
        SortBuilder sortBuilder = new FieldSortBuilder("money")
                .order(SortOrder.DESC);

        // 条件：description中包含Jack的数据
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "Jack"))
                .withHighlightFields(new HighlightBuilder.Field("description")
                                    .preTags(preTag)
                                    .postTags(postTag))
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();

        AggregatedPage<Student> pagedStu = esTemplate.queryForPage(query, Student.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                // 替换高亮数据
                List<Student> stuListHighlight  = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit h : hits){
                    HighlightField highlightField = h.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();

                    Object stuId = (Object) h.getSourceAsMap().get("stuId");
                    String name = (String)h.getSourceAsMap().get("name");
                    Integer age = (Integer)h.getSourceAsMap().get("age");
                    String sign = (String)h.getSourceAsMap().get("sign");
                    Object money = (Object) h.getSourceAsMap().get("money");

                    Student stuHL = new Student();
                    stuHL.setDescription(description);
                    stuHL.setStuId(Long.valueOf(stuId.toString()));
                    stuHL.setName(name);
                    stuHL.setAge(age);
                    stuHL.setSign(sign);
                    stuHL.setMoney(Float.valueOf(money.toString()));
                    stuListHighlight.add(stuHL);
                }

                if (stuListHighlight.size() > 0){
                    return new AggregatedPageImpl<>((List<T>)stuListHighlight);
                }

                return null;
            }
        });
        // 检索后的总分页数
        pagedStu.getTotalPages();
        // 检索后的list
        List<Student> studentList = pagedStu.getContent();

    }
}
