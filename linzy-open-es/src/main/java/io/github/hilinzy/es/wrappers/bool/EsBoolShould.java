package io.github.hilinzy.es.wrappers.bool;

import io.github.hilinzy.common.annotation.MyFunction;
import io.github.hilinzy.common.utils.ColumnUtil;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;
/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description 相当于mysql的or
 * @author linzy
 * @date 2022/5/6
 **/
public class EsBoolShould <T> extends EsBoolQueryWrapper<T>{

  protected EsBoolShould(EsBoolQueryWrapper<T> parent) {
    super(parent);
  }


  /**
   * term 精确查询
   */
  public<E> EsBoolShould<T> termQuery(MyFunction<T, ?> mapper, E data) {
    return termQuery(true, mapper, data);
  }

  public<E> EsBoolShould<T> termQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.termQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================模糊查找 ==============================================**== */

  /**
   * match 模糊检索
   */
  public<E> EsBoolShould<T> matchQuery(MyFunction<T, ?> mapper, E data) {
    return matchQuery(true, mapper, data);
  }

  public<E> EsBoolShould<T> matchQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.matchQuery(fieldName, data));
    }
    return this;
  }

  /**
   * fuzzy 模糊检索
   */
  public<E> EsBoolShould<T> fuzzyQuery(MyFunction<T, ?> mapper, E data) {
    return fuzzyQuery(true, mapper, data);
  }

  public<E> EsBoolShould<T> fuzzyQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if (condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.fuzzyQuery(fieldName, data));
    }
    return this;
  }

  /**
   *prefix 前缀查询
   */
  public<E> EsBoolShould<T> prefixQuery(MyFunction<T, ?> mapper, String prefix) {
    return prefixQuery(true, mapper, prefix);
  }

  public<E> EsBoolShould<T> prefixQuery(boolean condition, MyFunction<T, ?> mapper, String prefix) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.prefixQuery(fieldName, prefix));
    }
    return this;
  }

  /**
   * wildcard 模糊检索
   */
  public<E> EsBoolShould<T> wildcardQuery(MyFunction<T, ?> mapper, String keyWord) {
    return wildcardQuery(true, mapper, keyWord);
  }

  public<E> EsBoolShould<T> wildcardQuery(boolean condition, MyFunction<T, ?> mapper, String keyWord) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.wildcardQuery(fieldName, keyWord));
    }
    return this;
  }

  /**
   * matchPhrase 模糊检索
   */
  public<E> EsBoolShould<T> matchPhraseQuery(MyFunction<T, ?> mapper, E data) {
    return matchPhraseQuery(true, mapper, data);
  }

  public<E> EsBoolShould<T> matchPhraseQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.matchPhraseQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================范围查找 ==============================================**== */

  /**
   * 类似mysql的in
   */
  public  EsBoolShould<T> in(MyFunction<T, ?> mapper,Object...values){
    return in(true, mapper, values);
  }

  public  EsBoolShould<T> in(boolean condition, MyFunction<T, ?> mapper,Object...values){
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.termsQuery(fieldName, values));
    }
    return this;
  }

  /**
   * 类似mysql的in
   */
  public  EsBoolShould<T> in(MyFunction<T, ?> mapper, Collection<?> values){
    return in(true, mapper, values);
  }

  public  EsBoolShould<T> in(boolean condition, MyFunction<T, ?> mapper, Collection<?> values){
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.should().add(QueryBuilders.termsQuery(fieldName, values));
    }
    return this;
  }

  /**
   * 区间(边界可选)
   * @param mapper 查询字段
   * @param from >=
   * @param isFromEq 是否包含from
   * @param to <=
   * @param isToEq 是否包含to
   */
  public EsBoolShould<T> range(MyFunction<T, ?> mapper, Object from, boolean isFromEq, Object to, boolean isToEq) {
    return range(true, mapper, from,isFromEq, to, isToEq);
  }

  public EsBoolShould<T> range(boolean condition, MyFunction<T, ?> mapper, Object from, boolean isFromEq, Object to, boolean isToEq) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      if(isFromEq && isToEq) {
        boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gte(from).lte(to));
        return this;
      }
      if(isFromEq) {
        boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gte(from).lt(to));
        return this;
      }
      if(isToEq) {
        boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gt(from).lte(to));
        return this;
      }
      boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gt(from).lt(to));
    }
    return this;
  }

  /**
   * 区间(包含边界)
   * @param mapper
   * @param from
   * @param to
   * @return
   */
  public  EsBoolShould<T> between(MyFunction<T, ?> mapper, Object from, Object to){
    return between(true, mapper, from, to);
  }

  public  EsBoolShould<T> between(boolean condition, MyFunction<T, ?> mapper, Object from, Object to){
    return range(condition, mapper, from, true, to, true);
  }

  /**
   *
   * @param mapper 查询字段
   * @param from >=
   */
  public  EsBoolShould<T> gte(MyFunction<T, ?> mapper, Object from){
    return gte(true, mapper, from);
  }

  public  EsBoolShould<T> gte(boolean condition, MyFunction<T, ?> mapper, Object from){
    String fieldName = ColumnUtil.getFieldName(mapper);
    boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gte(from));
    return this;
  }

  public  EsBoolShould<T> gt(MyFunction<T, ?> mapper, Object from){
    return gt(true, mapper, from);
  }

  public  EsBoolShould<T> gt(boolean condition, MyFunction<T, ?> mapper, Object from){
    String fieldName = ColumnUtil.getFieldName(mapper);
    boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).gt(from));
    return this;
  }

  /**
   *
   * @param mapper 查询字段
   * @param to <=
   */
  public  EsBoolShould<T> lte(MyFunction<T, ?> mapper, Object to){
    return lte(true, mapper, to);
  }

  public  EsBoolShould<T> lte(boolean condition, MyFunction<T, ?> mapper, Object to){
    String fieldName = ColumnUtil.getFieldName(mapper);
    boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).lte(to));
    return this;
  }

  public  EsBoolShould<T> lt(MyFunction<T, ?> mapper, Object to){
    return lt(true, mapper, to);
  }

  public  EsBoolShould<T> lt(boolean condition, MyFunction<T, ?> mapper, Object to){
    String fieldName = ColumnUtil.getFieldName(mapper);
    boolQueryBuilder.should().add(QueryBuilders.rangeQuery(fieldName).lt(to));
    return this;
  }

}
