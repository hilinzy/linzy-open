package io.github.hilinzy.es.wrappers.bool;

import io.github.hilinzy.common.annotation.MyFunction;
import io.github.hilinzy.common.utils.ColumnUtil;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;

/**
 _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 *
 * @author linzy
 * @description 相当于mysql的not in
 * @date 2022/5/5
 **/
public class EsBoolMustNot<T> extends EsBoolQueryWrapper<T> {

  protected EsBoolMustNot(EsBoolQueryWrapper<T> parent) {
    super(parent);
  }


  /**
   * term 精确查询
   */
  public <E> EsBoolMustNot<T> termQuery(MyFunction<T, ?> mapper, E data) {
    return termQuery(true, mapper, data);
  }

  public <E> EsBoolMustNot<T> termQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.termQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================模糊查找 ==============================================**== */

  /**
   * match 模糊检索
   */
  public <E> EsBoolMustNot<T> matchQuery(MyFunction<T, ?> mapper, E data) {
    return matchQuery(true, mapper, data);
  }

  public <E> EsBoolMustNot<T> matchQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.matchQuery(fieldName, data));
    }
    return this;
  }

  /**
   * fuzzy 模糊检索
   */
  public <E> EsBoolMustNot<T> fuzzyQuery(MyFunction<T, ?> mapper, E data) {
    return fuzzyQuery(true, mapper, data);
  }

  public <E> EsBoolMustNot<T> fuzzyQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.fuzzyQuery(fieldName, data));
    }
    return this;
  }

  /**
   * prefix 前缀查询
   */
  public EsBoolMustNot<T> prefixQuery(MyFunction<T, ?> mapper, String prefix) {
    return prefixQuery(true, mapper, prefix);
  }

  public EsBoolMustNot<T> prefixQuery(boolean condition, MyFunction<T, ?> mapper, String prefix) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.prefixQuery(fieldName, prefix));
    }
    return this;
  }

  /**
   * wildcard 模糊检索
   */
  public EsBoolMustNot<T> wildcardQuery(MyFunction<T, ?> mapper, String keyWord) {
    return wildcardQuery(true, mapper, keyWord);
  }

  public EsBoolMustNot<T> wildcardQuery(boolean condition, MyFunction<T, ?> mapper, String keyWord) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.wildcardQuery(fieldName, keyWord));
    }
    return this;
  }

  /**
   * matchPhrase 模糊检索
   */
  public <E> EsBoolMustNot<T> matchPhraseQuery(MyFunction<T, ?> mapper, E data) {
    return matchPhraseQuery(true, mapper, data);
  }

  public <E> EsBoolMustNot<T> matchPhraseQuery(boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.matchPhraseQuery(fieldName, data));
    }
    return this;
  }

  /* ==**==============================================范围查找 ==============================================**== */

  /**
   * 类似mysql的not in
   */
  public EsBoolMustNot<T> in(MyFunction<T, ?> mapper, Object... values) {
    return in(true, mapper, values);
  }

  public EsBoolMustNot<T> in(boolean condition, MyFunction<T, ?> mapper, Object... values) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.termsQuery(fieldName, values));
    }
    return this;
  }

  public EsBoolMustNot<T> in(MyFunction<T, ?> mapper, Collection<?> values) {
    return  in(true, mapper, values);
  }

  public EsBoolMustNot<T> in(boolean condition, MyFunction<T, ?> mapper, Collection<?> values) {
    if(condition) {
      String fieldName = ColumnUtil.getFieldName(mapper);
      boolQueryBuilder.mustNot().add(QueryBuilders.termsQuery(fieldName, values));
    }
    return this;
  }
}
