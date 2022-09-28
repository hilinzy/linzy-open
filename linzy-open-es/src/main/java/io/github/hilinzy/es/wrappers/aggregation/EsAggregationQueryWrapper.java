package io.github.hilinzy.es.wrappers.aggregation;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.hilinzy.common.annotation.MyFunction;
import io.github.hilinzy.common.utils.ColumnUtil;
import io.github.hilinzy.es.wrappers.EsQueryWrapper;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.*;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description es聚合查询条件构造
 * @author linzy
 * @date 2022/5/5
 **/
public class EsAggregationQueryWrapper<T> extends EsQueryWrapper<T> {
  //聚合查询新聚合的名称
  final String TEM_NAMES="tem";
  //聚合查询所 TOP HIT的名称
  final String TOP="top";
  //聚合统计的分组
  final String GROUP="groupBy";
  //聚合统计
  final String CALCULATION="calculation";


  protected  NativeSearchQueryBuilder nativeSearchQueryBuilder;

  protected EsAggregationQueryWrapper() {
  }

  public static<T> EsAggregationQueryWrapper<T> build(){
    EsAggregationQueryWrapper<T> objectEsAggregationQueryWrapper = new EsAggregationQueryWrapper<>();
    objectEsAggregationQueryWrapper.nativeSearchQueryBuilder=new NativeSearchQueryBuilder();
    objectEsAggregationQueryWrapper.setNativeSearchQueryBuilder(objectEsAggregationQueryWrapper.nativeSearchQueryBuilder);
    return objectEsAggregationQueryWrapper;
  }

  /**
   * 聚合查询 默认:查3条，排序按系统给定分值
   * @param condition 具体聚合条件的集合
   */
  public EsAggregationQueryWrapper<T> aggregationQueryPage(MyFunction<T, ?> mapper, Collection<?> condition){
    return this.aggregationQueryPage(mapper,condition,null,null,null,null);
  }

  public EsAggregationQueryWrapper<T> aggregationQueryPage(MyFunction<T, ?> mapper, Collection<?> condition,String sortName,Boolean isAsc){
    return this.aggregationQueryPage(mapper,condition,sortName,isAsc,null,null);
  }

  public EsAggregationQueryWrapper<T> aggregationQueryPage(MyFunction<T, ?> mapper, Collection<?> condition,Integer page, Integer pageSize){
    return this.aggregationQueryPage(mapper,condition,null,null,page,pageSize);
  }

  public EsAggregationQueryWrapper<T> aggregationQueryPage(MyFunction<T, ?> mapper, Collection<?> condition, String sortName, Boolean isAsc, Integer page, Integer pageSize) {
    String fieldName = ColumnUtil.getFieldName(mapper);
    TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery(fieldName, condition);
    nativeSearchQueryBuilder.withQuery(termsQueryBuilder);
    TermsAggregationBuilder terms = AggregationBuilders.terms(TEM_NAMES).field(fieldName).size(1000);
    if(ObjectUtil.isAllNotEmpty(page,pageSize)){
      terms.subAggregation(AggregationBuilders.topHits(TOP).fetchSource(null, Strings.EMPTY_ARRAY)
          .sort(StrUtil.isBlank(sortName)? ScoreSortBuilder.NAME:sortName,ObjectUtil.isEmpty(isAsc)? SortOrder.ASC:SortOrder.DESC)
          .from(page * pageSize).size(pageSize));
    }else {
      terms.subAggregation(AggregationBuilders.topHits(TOP).fetchSource(null, Strings.EMPTY_ARRAY)
          .sort(StrUtil.isBlank(sortName)? ScoreSortBuilder.NAME:sortName,ObjectUtil.isEmpty(isAsc)?SortOrder.ASC:SortOrder.DESC));
    }
    nativeSearchQueryBuilder.addAggregation(terms);
    return this;
  }

  /**
   * 获取对应聚合查询接口的结果集
   */
  public  List<T> getAggregationPageResult(SearchHits<T> searchHits, Class<T> baseClass) {
//    Class<T> clz = (Class<T>) searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList()).get(0).getClass();
    List<T> list=new ArrayList<>();
    Aggregations aggregations = searchHits.getAggregations();
    assert aggregations != null;
    Terms terms = (Terms) (aggregations.get(TEM_NAMES));
    List<? extends Terms.Bucket> buckets = terms.getBuckets();
    for (Terms.Bucket bucket : terms.getBuckets()) {
      TopHits topHits = bucket.getAggregations().get(TOP);
      for (org.elasticsearch.search.SearchHit hit : topHits.getHits().getHits()) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        T doc =  BeanUtil.toBean(sourceAsMap, baseClass);
        list.add(doc);
      }
    }
    return list;
  }

  /**
   * 统计某个字段的数量
   * @param field 参与统计的字段
   * @param groupBy 聚合分组的字段
   */
  public EsAggregationQueryWrapper<T> aggregationQueryCalculation(MyFunction<T, ?> field, MyFunction<T, ?> groupBy){
   return this.aggregationQueryCalculation(field,groupBy,null);
  }

  /**
   * 统计某个字段的数量
   * @param field 参与统计的字段
   * @param groupBy 聚合分组的字段
   * @param conditions 组合字段的条件参数
   */
  public EsAggregationQueryWrapper<T> aggregationQueryCalculation(MyFunction<T, ?> field, MyFunction<T, ?> groupBy,Collection<?> conditions){
    String fieldName = ColumnUtil.getFieldName(field);
    String groupByName = ColumnUtil.getFieldName(groupBy);
    nativeSearchQueryBuilder.withQuery(CollUtil.isEmpty(conditions)?QueryBuilders.matchAllQuery():QueryBuilders.termsQuery(groupByName,conditions));
    TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(GROUP).field(groupByName);
    aggregationBuilder.subAggregation(AggregationBuilders.stats(CALCULATION).field(fieldName));
    nativeSearchQueryBuilder.addAggregation(aggregationBuilder);
    return this;
  }

  /**
   * 获取对应统计接口的结果集
   */
  public Map<String, Stats> getAggregationQueryCountResult(SearchHits<T> searchHits, Class<T> baseClass) {
    Map<String,Stats> resultMap=new HashMap<>();
    Aggregations aggregations = searchHits.getAggregations();
    assert aggregations != null;
    Terms terms = searchHits.getAggregations().get(GROUP);
    List<? extends Terms.Bucket> buckets = terms.getBuckets();
    buckets.forEach(e->{
      Stats stats = e.getAggregations().get(CALCULATION);
      resultMap.put(e.getKeyAsString(),stats);
    });
    return resultMap;
  }


}
