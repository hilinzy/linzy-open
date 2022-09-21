package io.github.hilinzy.mongo.service;

import cn.hutool.core.util.ObjectUtil;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import io.github.hilinzy.mongo.wrappers.MongoQueryWrapper;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  mongo封装mongoTemplate的查询方法
 * @author linzy
 * @date  2022/7/27
 **/
public class BaseMongoSrv<T> {
  @Resource
  private MongoTemplate mongoTemplate;

  public MongoTemplate getMongoTemplate(){
    return this.mongoTemplate;
  }

  //region 增 +++++++++++++++++++++++ +++++++++++++++++++++++++ ++++++++++++++++++++++++
  /**
   *单个保存
   */
  public T save(T t){
    return mongoTemplate.save(t);
  }

  public T save(T t, String collectionName){
    return mongoTemplate.save(t,collectionName);
  }

  /**
   *批量保存
   */
  public BulkWriteResult saveALL(List<T> list){
    Class<T> clz = this.getBaseClass();
    return mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, clz).insert(list).execute();
  }

  //endregion +++++++++++++++++++++++ +++++++++++++++++++++++++ ++++++++++++++++++++++++

  //region 删 ----------------------- ------------------------- ------------------------
  /**
   *根据条件移除文档
   */
  public DeleteResult removeByQuery(MongoQueryWrapper<T> mongoQueryWrapper){
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    return mongoTemplate.remove(query,clz);
  }

  /**
   *直接删除对象
   */
  public  DeleteResult remove(T t){
    return mongoTemplate.remove(t);
  }

  /**
   *根据id删除对象
   */
  public <E> DeleteResult removeById(E id){
    Class<T> clz = this.getBaseClass();
    Query query=new Query();
    Criteria criteria = Criteria.where("id").is(id);
    query.addCriteria(criteria);
    return mongoTemplate.remove(query,clz);
  }
  //endregion ----------------------- ------------------------- ------------------------

  //region 改 ************************ ************************ ************************
  /**
   *根据id修改，传入整个实体类
   */
  public void updateById(T t){
    Pair<Query, Update> a = this.createQueryAndUpdate(t);
    Query query = a.getFirst();
    Update update = a.getSecond();
    mongoTemplate.findAndModify(query,update,t.getClass());
  }

  /**
   *根据id修改，传入实体类集合
   */
  public void updateByIds(Collection<T> data){
    Class<T> cls = this.getBaseClass();
    List<Pair<Query, Update>> updates =new ArrayList<>();
    for (T t : data) {
      Pair<Query, Update> a = this.createQueryAndUpdate(t);
      updates.add(a);
    }
     mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, cls).updateMulti(updates).execute();
  }


  /**
   * 查询并修改（注意：该方法只会修改一个
   */
  public T findAndUpdateOne(MongoQueryWrapper<T> mongoQueryWrapper) {
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    Update update = mongoQueryWrapper.getUpdate();
    return mongoTemplate.findAndModify(query,update,clz);
  }

  /**
   *查询并修改，如果没查询到就新增，返回修改后的值
   */
  public T findAdnUpsertOneReturnNew(MongoQueryWrapper<T> mongoQueryWrapper){
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    Update update = mongoQueryWrapper.getUpdate();
    FindAndModifyOptions options = FindAndModifyOptions.options();
    options.upsert(true);
    options.returnNew(true);
    return mongoTemplate.findAndModify(query,update,options,clz);

  }

  /**
   * 批量查询并修改文档
   */
  public BulkWriteResult findAndUpdateAll(MongoQueryWrapper<T> mongoQueryWrapper) {
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    Update update = mongoQueryWrapper.getUpdate();
    return mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, clz).updateMulti(query, update).execute();
  }
  //endregion ************************ ************************ ************************

  //region 查 ======================== ======================== ========================
  /**
   * 获取集合的名字
   */
  public String getCollectionName(){
    Class<T> clz = this.getBaseClass();
    return mongoTemplate.getCollectionName(clz);
  }

  /**
   *根据主键id查询
   */
  public<E> T findById(E id){
    Class<T> clz = this.getBaseClass();
    return mongoTemplate.findById(id,clz);
  }

  /**
   *查询出所有的文档
   */
  public List<T> findAll(){
    Class<T> clz = this.getBaseClass();
    return mongoTemplate.findAll(clz);
  }

  /**
   *根据条件查询
   */
  public List<T> find(MongoQueryWrapper<T> mongoQueryWrapper){
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    return mongoTemplate.find(query, clz);
  }

  /**
   *根据条件查询
   */
  public T findOne(MongoQueryWrapper<T> mongoQueryWrapper){
    Class<T> clz = this.getBaseClass();
    Query query = mongoQueryWrapper.getQuery();
    return mongoTemplate.findOne(query, clz);
  }

  /**
   *聚合查询
   */
  public List<T> aggregate(Aggregation aggregation) {
    Class<T> clz = this.getBaseClass();
    String collectionName = mongoTemplate.getCollectionName(clz);
    return mongoTemplate.aggregate(aggregation,collectionName,clz).getMappedResults();
  }

  public<E> List<E> aggregate(Aggregation aggregation, Class<E> clz) {
    Class<T> baseClass = this.getBaseClass();
    String collectionName = mongoTemplate.getCollectionName(baseClass);
    return mongoTemplate.aggregate(aggregation,collectionName,clz).getMappedResults();
  }

  /**
   *查询统计
   */
  public Long count(MongoQueryWrapper<T> wrapper){
    Class<T> clz = this.getBaseClass();
    Query query = wrapper.getQuery();
    return mongoTemplate.count(query,clz);
  }

  //endregion ======================== ======================== ========================

  /**
   *私有方法，获取lambda表达式的字段名
   */
  private Class<T> getBaseClass(){
    Type[] params = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    return  (Class<T>) params[0];
  }


  /**
   * 构建 Query 和 Update对象
   */
  private Pair<Query,Update> createQueryAndUpdate(T t){
    Query query = new Query();
    Update update=new Update();
    Class<?> clz = t.getClass();
    Field[] fields = clz.getDeclaredFields();
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        String name = field.getName();
        Object invoke = field.get(t);
        if(ObjectUtil.isNotEmpty(field.getAnnotation(MongoId.class))){
          Criteria criteria = Criteria.where(name).is(invoke);
          query.addCriteria(criteria);
        }else if("updateTime".equals(name)){
          update.set(name,new Date());
        }else {
          if(invoke!=null) update.set(name,invoke);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return Pair.of(query,update);
  }


}
