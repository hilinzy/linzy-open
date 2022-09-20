package io.github.hilinzy.mongo.wrappers;

import cn.hutool.core.util.ArrayUtil;
import io.github.hilinzy.common.ext.ColumnUtil;
import io.github.hilinzy.common.ext.MyFunction;
import lombok.Getter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  mongodb条件构造器
 * @author linzy
 * @date  2022/7/20
 **/
public class MongoQueryWrapper<T> {

  @Getter
  private Query query;

  @Getter
  private Update update;

  public MongoQueryWrapper(Class<T> clz) {
  }

  /**
   *创建条件构造器
   */
  public static <T> MongoQueryWrapper<T> build(Class<T> clz) {
    MongoQueryWrapper<T> objectMongoQueryWrapper = new MongoQueryWrapper<>(clz);
    objectMongoQueryWrapper.query = new Query();
    objectMongoQueryWrapper.update=new Update();
    return objectMongoQueryWrapper;
  }

  /**
   * 构造key=value的查询条件
   */
  public <E> MongoQueryWrapper<T> eq(Boolean condition, MyFunction<T, ?> mapper, E data) {
    if(condition) return this.eq(mapper,data);
    return this;
  }
  public <E> MongoQueryWrapper<T> eq(MyFunction<T, ?> mapper, E data) {
    String fieldName = ColumnUtil.getFieldName(mapper);
    Criteria criteria = Criteria.where(fieldName).is(data);
    this.query.addCriteria(criteria);
    return this;
  }

  /**
   *构造key in values的查询条件
   */
  public <E> MongoQueryWrapper<T> in(Boolean condition,MyFunction<T, ?> mapper, Collection<E> data) {
    if(condition) return this.in(mapper,data);
    return this;
  }
  public <E> MongoQueryWrapper<T> in(MyFunction<T, ?> mapper, Collection<E> data) {
    String fieldName = ColumnUtil.getFieldName(mapper);
    Criteria criteria = Criteria.where(fieldName).in(data);
    this.query.addCriteria(criteria);
    return this;
  }

  /**
   *构造或查询条件
   */
  public MongoQueryWrapper<T> or(Boolean condition, Map<MyFunction<T, ?>, Object> map) {
    if(condition) return this.or(map);
    return this;
  }
  public MongoQueryWrapper<T> or(Map<MyFunction<T, ?>, Object> map) {
    List<Criteria> list = new ArrayList<>();
    map.forEach((k, v) -> {
      Criteria criteria = Criteria.where(ColumnUtil.getFieldName(k)).is(v);
      list.add(criteria);
    });
    Criteria[] criteriaArr = ArrayUtil.toArray(list, Criteria.class);
    Criteria criteria = new Criteria().orOperator(criteriaArr);
    this.query.addCriteria(criteria);
    return this;
  }

  /**
   *构造修改参数，通过Map传入多个key,value的修改值
   */
  public MongoQueryWrapper<T> update(Map<MyFunction<T, ?>, Object> map) {
    map.forEach((k,v)->{
      String fieldName = ColumnUtil.getFieldName(k);
      update.set(fieldName, v);
    });
    return this;
  }

  /**
   *构造修改参数，可用链式调用update().update()
   */
  public<E> MongoQueryWrapper<T> update(MyFunction<T, ?> mapper, E data) {
    String fieldName = ColumnUtil.getFieldName(mapper);
     update.set(fieldName,data);
     return this;
  }

  /**
   *构造数值增长的修改参数
   */
  public  MongoQueryWrapper<T> incr(MyFunction<T, ?> mapper){
    String fieldName = ColumnUtil.getFieldName(mapper);
    update.inc(fieldName);
    return this;
  }

  /**
   *构造数值增长的修改参数重载方法，可以指定修改的值
   */
  public  MongoQueryWrapper<T> incr(MyFunction<T, ?> mapper, Number inc){
    String fieldName = ColumnUtil.getFieldName(mapper);
    update.inc(fieldName,inc);
    return this;
  }
}
