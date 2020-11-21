package org.foodie.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.foodie.pojo.Items;
import org.foodie.pojo.Stu;
import org.foodie.service.ItemsEsService;
import org.foodie.utils.PagedGridResult;
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
import java.util.Map;

/**
 * @Author steve.mei
 * @Version ItemsEsServiceImpl,  2020/11/21 下午8:41
 **/
@Slf4j
@Service
public class ItemsEsServiceImpl implements ItemsEsService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        // 高亮
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(page, pageSize);

        // 排序
//        SortBuilder sortBuilder = new FieldSortBuilder("money").order(SortOrder.ASC);
//        SortBuilder sortBuilderAge = new FieldSortBuilder("age").order(SortOrder.DESC);
        String itemNameFiled = "itemName";

        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(QueryBuilders.matchQuery(itemNameFiled, keywords)).
                withPageable(pageable).
                withHighlightFields(new HighlightBuilder.Field(itemNameFiled).preTags(preTag).postTags(postTag)).
//                withSort(sortBuilder).
//                withSort(sortBuilderAge).
        build();
        AggregatedPage<Items> pageItems = elasticsearchTemplate.queryForPage(searchQuery, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Items> itemsList = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get(itemNameFiled);
                    String itemsName = highlightField.getFragments()[0].toString();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    String itemId = (String) sourceAsMap.get("itemId");
                    String imgUrl = (String) sourceAsMap.get("imgUrl");
                    Integer price = (Integer) sourceAsMap.get("price");
                    Integer sellCounts = (Integer) sourceAsMap.get("sellCounts");

                    Items items = new Items();
                    items.setItemId(itemId);
                    items.setItemName(itemsName);
                    items.setImgUrl(imgUrl);
                    items.setPrice(price);
                    items.setSellCounts(sellCounts);

                    itemsList.add(items);
                }
                return new AggregatedPageImpl<>((List<T>) itemsList,
                        pageable,
                        searchResponse.getHits().totalHits);
            }
        });
        PagedGridResult result = new PagedGridResult();
        result.setRows(pageItems.getContent());
        result.setPage(page + 1);
        result.setTotal(pageItems.getTotalPages());
        result.setRecords(pageItems.getTotalElements());
        return result;
    }
}
