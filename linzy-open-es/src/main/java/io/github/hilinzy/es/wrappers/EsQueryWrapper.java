package io.github.hilinzy.es.wrappers;

import io.github.hilinzy.common.annotation.MyFunction;
import io.github.hilinzy.common.utils.ColumnUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description Es封装条件的基类
 * @author linzy
 * @date 2022/4/29
 **/
public abstract class  EsQueryWrapper<T>{

  protected  NativeSearchQueryBuilder nativeSearchQueryBuilder;

  public NativeSearchQueryBuilder getNativeSearchQueryBuilder() {
    return nativeSearchQueryBuilder;
  }

  public void setNativeSearchQueryBuilder(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
    this.nativeSearchQueryBuilder = nativeSearchQueryBuilder;
  }

  /**
   * 排序 默认排序倒序
   */
  public EsQueryWrapper<T> sort(MyFunction<T, ?> mapper){
    return this.sort(mapper,false);
  }

  public EsQueryWrapper<T> sort(MyFunction<T, ?> mapper, Boolean isAsc){
    return sort(true,mapper,isAsc);
  }
  public EsQueryWrapper<T> sort(Boolean condition, MyFunction<T, ?> mapper, Boolean isAsc){

    if(condition){
      String fieldName = ColumnUtil.getFieldName(mapper);
      FieldSortBuilder fieldSortBuilder = SortBuilders
          .fieldSort(fieldName)
          .order(isAsc ? SortOrder.ASC : SortOrder.DESC);
      nativeSearchQueryBuilder.withSort(fieldSortBuilder);
    }
    return this;
  }

  /**
   *查询所有
   */
  public EsQueryWrapper<T> matchAll(){
    nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
    return this;
  }

  /**
   * 获取当前类泛型对应点的class
   */
//  protected Class<T> getBaseClass(){
//    Type[] params = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
//    return  (Class<T>) params[0];
//  }


}
