package io.github.hilinzy.es.wrappers.bool;

import io.github.hilinzy.es.wrappers.EsQueryWrapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;


/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description EsBool查询条件构造
 * @author linzy
 * @date 2022/5/4
 **/
public  class EsBoolQueryWrapper<T> extends EsQueryWrapper<T> {
  protected BoolQueryBuilder boolQueryBuilder;

  public BoolQueryBuilder getBoolQueryBuilder() {
    return boolQueryBuilder;
  }

  public void setBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder) {
    this.boolQueryBuilder = boolQueryBuilder;
  }

  protected EsBoolQueryWrapper() {
  }

  protected EsBoolQueryWrapper(EsBoolQueryWrapper<T> parent) {
    this.nativeSearchQueryBuilder=parent.nativeSearchQueryBuilder;
    this.boolQueryBuilder = parent.boolQueryBuilder;
  }

  public static <T> EsBoolQueryWrapper<T> build(){
    EsBoolQueryWrapper<T> objectEsBoolQueryWrapper = new EsBoolQueryWrapper<>();
    objectEsBoolQueryWrapper.boolQueryBuilder= new BoolQueryBuilder();
    NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
    nativeSearchQueryBuilder.withQuery( objectEsBoolQueryWrapper.boolQueryBuilder);
    objectEsBoolQueryWrapper.setNativeSearchQueryBuilder(nativeSearchQueryBuilder);
    return objectEsBoolQueryWrapper;
  }

  /**
   *  相当于mysql的and
  **/
  public EsBoolMust<T> must(){
   return  new EsBoolMust<T>(this);
  }

  /**
   *  相当于mysql的or
   **/
  public EsBoolShould<T> should(){
    return  new EsBoolShould<T>(this);
  }

  /**
   *  相当于mysql的not in
   **/
  public EsBoolMustNot<T> mustNot(){
    return  new EsBoolMustNot<T>(this);
  }

  /**
   *  相当于mysql的and
   **/
  public EsBoolFilter<T> filter(){ return  new EsBoolFilter<T>(this);}



}
