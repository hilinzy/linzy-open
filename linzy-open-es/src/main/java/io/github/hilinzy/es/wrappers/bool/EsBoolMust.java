package io.github.hilinzy.es.wrappers.bool;

import io.github.hilinzy.common.annotation.MyFunction;
import io.github.hilinzy.common.utils.ColumnUtil;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description 相当于mysql的and
 * @author linzy
 * @date 2022/5/6
 **/
public class EsBoolMust<T> extends EsBoolQueryWrapper<T> {

  protected EsBoolMust(EsBoolQueryWrapper<T> parent) {
    super(parent);
  }

  /**
   * term 精确查询
   */
  public <E> EsBoolMust<T> termQuery(MyFunction<T, ?> mapper, E data) {
    return termQuery(true, mapper, data);
  }

  public <E> EsBoolMust<T> termQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.termQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================模糊查找 ==============================================**== */

  /**
   * match 模糊检索
   */
  public <E> EsBoolMust<T> matchQuery(MyFunction<T, ?> mapper, E data) {
    return matchQuery(true, mapper, data);
  }

  public <E> EsBoolMust<T> matchQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.matchQuery(fieldName, data));
    }
    return this;
  }

  /**
   * fuzzy 模糊检索
   */
  public <E> EsBoolMust<T> fuzzyQuery(MyFunction<T, ?> mapper, E data) {
    return fuzzyQuery(true, mapper, data);
  }

  public <E> EsBoolMust<T> fuzzyQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.fuzzyQuery(fieldName, data));
    }
    return this;
  }

  /**
   * prefix 前缀查询
   */
  public EsBoolMust<T> prefixQuery(MyFunction<T, ?> mapper, String prefix) {
    return prefixQuery(true, mapper, prefix);
  }

  public EsBoolMust<T> prefixQuery(boolean condition, MyFunction<T, ?> mapper, String prefix) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.prefixQuery(fieldName, prefix));
    }
    return this;
  }

  /**
   * wildcard 模糊检索
   */
  public EsBoolMust<T> wildcardQuery(MyFunction<T, ?> mapper, String keyWord) {
    return wildcardQuery(true, mapper, keyWord);
  }

  public EsBoolMust<T> wildcardQuery(boolean condition, MyFunction<T, ?> mapper, String keyWord) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.wildcardQuery(fieldName, keyWord));
    }
    return this;
  }

  /**
   * matchPhrase 模糊检索
   */
  public <E> EsBoolMust<T> matchPhraseQuery(MyFunction<T, ?> mapper, E data) {
    return matchPhraseQuery(true, mapper, data);
  }

  public <E> EsBoolMust<T> matchPhraseQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if (condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.matchPhraseQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================范围查找 ==============================================**== */

  /**
   * 类似mysql的in
   */
  public EsBoolMust<T> in(MyFunction<T, ?> mapper, Object... values) {
    return in(true, mapper, values);
  }

  public EsBoolMust<T> in(boolean condition, MyFunction<T, ?> mapper, Object... values) {
    if (condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.termsQuery(fieldName, values));
    }
    return this;
  }

  public EsBoolMust<T> in(MyFunction<T, ?> mapper, Collection<?> values) {
    return in(true, mapper, values);
  }

  public EsBoolMust<T> in(boolean condition, MyFunction<T, ?> mapper, Collection<?> values) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.termsQuery(fieldName, values));
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
  public EsBoolMust<T> range(MyFunction<T, ?> mapper, Object from, boolean isFromEq, Object to, boolean isToEq) {
    return range(true, mapper, from,isFromEq, to, isToEq);
  }

  public EsBoolMust<T> range(boolean condition, MyFunction<T, ?> mapper, Object from, boolean isFromEq, Object to, boolean isToEq) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      if(isFromEq && isToEq) {
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gte(from).lte(to));
        return this;
      }
      if(isFromEq) {
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gte(from).lt(to));
        return this;
      }
      if(isToEq) {
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gt(from).lte(to));
        return this;
      }
      boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gt(from).lt(to));
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
  public  EsBoolMust<T> between(MyFunction<T, ?> mapper, Object from, Object to){
    return between(true, mapper, from, to);
  }

  public  EsBoolMust<T> between(boolean condition, MyFunction<T, ?> mapper, Object from, Object to){
    return range(condition, mapper, from, true, to, true);
  }

  /**
   * @param mapper 查询字段
   * @param from   >=
   */
  public EsBoolMust<T> gte(MyFunction<T, ?> mapper, Object from) {
    return gte(true, mapper, from);
  }

  public EsBoolMust<T> gte(boolean condition, MyFunction<T, ?> mapper, Object from) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gte(from));
    }
    return this;
  }

  public EsBoolMust<T> gt(MyFunction<T, ?> mapper, Object from) {
    return gt(true, mapper, from);
  }

  public EsBoolMust<T> gt(boolean condition, MyFunction<T, ?> mapper, Object from) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).gt(from));
    }
    return this;
  }

  /**
   * @param mapper 查询字段
   * @param to     <=
   */
  public EsBoolMust<T> lte(MyFunction<T, ?> mapper, Object to) {
    return lte(true, mapper, to);
  }

  public EsBoolMust<T> lte(boolean condition, MyFunction<T, ?> mapper, Object to) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).lte(to));
    }
    return this;
  }

  public EsBoolMust<T> lt(MyFunction<T, ?> mapper, Object to) {
    return lt(true, mapper, to);
  }

  public EsBoolMust<T> lt(boolean condition, MyFunction<T, ?> mapper, Object to) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.must().add(QueryBuilders.rangeQuery(fieldName).lt(to));
    }
    return this;
  }
}
