package org.foodie.search;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.foodie.SearchApplication;
import org.foodie.pojo.Stu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author steve.mei
 * @Version SearchTest,  2020/10/19 9:07 下午
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class SearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    // 创建索引
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setId(101);
        stu.setAge(25);
        stu.setName("wustmz");
        IndexQuery query = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(query);
    }

    @Test
    // 创建索引
    public void createIndexStus() {
        Stu stu = new Stu();
        stu.setId(105);
        stu.setAge(22);
        stu.setName("spider man");
        stu.setMoney(20.2f);
        stu.setSign("I am spider man");
        stu.setDescription("I with i am spider man");
        IndexQuery query = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(query);
    }

    @Test
    // 删除索引
    public void deleteIndex() {
        elasticsearchTemplate.deleteIndex(Stu.class);
    }

    @Test
    // 删除数据
    public void deleteStuDoc() {
        elasticsearchTemplate.delete(Stu.class, "101");
    }

    @Test
    // 更新
    public void updateStuDoc() {
        Map<String, Object> param = new HashMap<>();
        param.put("money", 88.6f);
        param.put("sign", "I am not super man");
        param.put("age", 33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(param);
        UpdateQuery updateQuery = new UpdateQueryBuilder().
                withClass(Stu.class).withId("101").
                withIndexRequest(indexRequest).build();
        elasticsearchTemplate.update(updateQuery);
    }

    @Test
    // 获取
    public void getStuDoc() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("101");
        Stu stu = elasticsearchTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu);
    }

    @Test
    // 分页查询
    public void searchStuDoc() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("description", "save man")).
                withPageable(pageable).
                build();
        AggregatedPage<Stu> pageStu = elasticsearchTemplate.queryForPage(searchQuery, Stu.class);
        System.out.println("检索后的总分页数目为: " + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        stuList.forEach(System.out::println);
    }

    @Test
    // 分页查询
    public void highlightStuDoc() {
        // 高亮
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(0, 10);

        // 排序
        SortBuilder sortBuilder = new FieldSortBuilder("money").order(SortOrder.ASC);
        SortBuilder sortBuilderAge = new FieldSortBuilder("age").order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("description", "save man")).
                withPageable(pageable).
                withHighlightFields(new HighlightBuilder.Field("description").preTags(preTag).postTags(postTag)).
                withSort(sortBuilder).
                withSort(sortBuilderAge).
                build();
        AggregatedPage<Stu> pageStu = elasticsearchTemplate.queryForPage(searchQuery, Stu.class);
        System.out.println("检索后的总分页数目为: " + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        stuList.forEach(System.out::println);
    }


}
