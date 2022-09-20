package io.github.hilinzy.mongo.builders;

import cn.hutool.core.collection.CollUtil;
import io.github.hilinzy.common.ext.ColumnUtil;
import io.github.hilinzy.common.ext.MyFunction;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  聚合查询构造聚合条件
 * @author linzy
 * @date  2022/7/21
 **/
public class AggregationExtBuilder {
  /**
   *聚合-拆分数组内的数据
   */
  public static<T> UnwindOperation buildUnwind(MyFunction<T,?> function){
    String fieldName = ColumnUtil.getFieldName(function,"_",2);
    return Aggregation.unwind(fieldName);
  }

  /**
   *聚合-分组(统计，求总和，平均值，最大值，最小值)
   */
  public static<T> GroupOperationExtBuilder buildGroup(MyFunction<T,?> function){
    String fieldName = ColumnUtil.getFieldName(function,"_",2);
    GroupOperation group = Aggregation.group(fieldName).first(fieldName).as(fieldName);
    return GroupOperationExtBuilder.build(group);
  }

  /** --------------------------------------------------------------------------
   *聚合-构建查询
   */
  public static<T,E> MatchOperation buildMatch(MyFunction<T,?> function, E date){
    String fieldName = ColumnUtil.getFieldName(function,"_",2);
    return Aggregation.match(Criteria.where(fieldName).is(date));
  }

  public static MatchOperation buildMatch(Criteria criteria){
    return Aggregation.match(criteria);
  }

  /** --------------------------------------------------------------------------
   *聚合-构建分页参数
   */
  public static SkipOperation buildSkip(Long current, Long pageSize){
    return Aggregation.skip((current - 1) * pageSize);
  }

  public static LimitOperation buildLimit(Long pageSize){
    return  Aggregation.limit(pageSize);
  }

  /** --------------------------------------------------------------------------
   *聚合-构建分页查询参数整合方法
   */
  public static Aggregation buildPage(Long current, Long pageSize){
    SkipOperation skip = Aggregation.skip((current - 1) * pageSize);
    LimitOperation limit = Aggregation.limit(pageSize);
    return Aggregation.newAggregation(skip,limit);
  }

  public static<T,E> Aggregation buildPageQuery(MyFunction<T,?> function,E date,Long current, Long pageSize){
    String fieldName = ColumnUtil.getFieldName(function,"_",2);
    MatchOperation match = Aggregation.match(Criteria.where(fieldName).is(date));
    SkipOperation skip = Aggregation.skip((current - 1) * pageSize);
    LimitOperation limit = Aggregation.limit(pageSize);
    return Aggregation.newAggregation(match,skip,limit);
  }

  public static Aggregation buildPageQuery(Long current, Long pageSize,MatchOperation... matchOperations){
    SkipOperation skip = Aggregation.skip((current - 1) * pageSize);
    LimitOperation limit = Aggregation.limit(pageSize);
    List<AggregationOperation> listTemp = Arrays.asList(matchOperations);
    List<AggregationOperation> list = new ArrayList<>(listTemp);
    list.add(skip);
    list.add(limit);
    return Aggregation.newAggregation(list);
  }

  public static Aggregation buildPageQuery(Long current, Long pageSize, Collection<MatchOperation> matchOperations){
    SkipOperation skip = Aggregation.skip((current - 1) * pageSize);
    LimitOperation limit = Aggregation.limit(pageSize);
    List<AggregationOperation> list = new ArrayList<>();
    if(CollUtil.isNotEmpty(matchOperations)){
      list.addAll(matchOperations);
    }
    list.add(skip);
    list.add(limit);
    return Aggregation.newAggregation(list);
  }
}
