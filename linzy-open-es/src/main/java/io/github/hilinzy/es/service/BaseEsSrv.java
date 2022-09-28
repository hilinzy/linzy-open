package io.github.hilinzy.es.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.hilinzy.common.exception.BaseException;
import io.github.hilinzy.es.wrappers.EsQueryWrapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description Es service基类
 * @author linzy
 * @date 2022/4/28
 **/
public class BaseEsSrv<T>  {

  @Resource
  private ElasticsearchRestTemplate estTemplate;

  @Resource
  private ElasticsearchConverter elasticsearchConverter;


  public ElasticsearchRestTemplate getBaseTemplate() {
    return estTemplate;
  }

  /**
   * 保存单个文档
   */
  public final void save(T t) {
    estTemplate.save(t);
  }

  /**
   * 保存单个文档
   */
  public final void saveOrUpdate(T t) {

    Class<T> clz = this.getBaseClass();
    IndexCoordinates index = estTemplate.getIndexCoordinatesFor(clz);

    Map<String, Object> src = BeanUtil.beanToMap(t);
    MapUtil.removeNullValue(src);
    String docId = this.getDocId(t);
    assert docId != null;

    UpdateQuery.Builder bd = UpdateQuery.builder(docId);
    bd.withRetryOnConflict(3);
    bd.withDocument(Document.from(src));
    bd.withUpsert(Document.from(src));
    bd.withRefresh(UpdateQuery.Refresh.True);
    UpdateQuery updateQuery = bd.build();

    estTemplate.update(updateQuery, index);
  }

  /**
   * 保存多个文档
   */
  public void save(Iterable<T> entities) {
    estTemplate.save(entities);
  }

  /**
   * 根据文档索引删除文档
   */
  public  void deleteById(String id) {
    Class<T> clz = getBaseClass();
    IndexCoordinates index = estTemplate.getIndexCoordinatesFor(clz);
    estTemplate.delete(id, index);
  }

  /**
   * 删除文档对象
   */
  public void delete(T entity) {
    estTemplate.delete(entity);
  }

  /**
   * 根据索引id修改文档
   */
  public void updateById(T t) {
    String docId = getDocId(t);
    IndexCoordinates indexCoordinates = estTemplate.getIndexCoordinatesFor(t.getClass());
    if (ObjectUtil.isEmpty(t)|| StrUtil.isBlank(docId)) return;
    Map<String, Object> map = BeanUtil.beanToMap(t);
    MapUtil.removeNullValue(map);
    UpdateQuery.Builder ub = UpdateQuery.builder(docId);
    ub.withDocument(Document.from(map));
    estTemplate.update(ub.build(), indexCoordinates);
  }

  /**
   * 批量更新
   */
  public void bulkUpdateOrInsert(List<T> documents){
    bulkUpdateOrInsert(documents,null);
  }

  /**
   * 批量更新
   */
  public void bulkUpdateOrInsert(List<T> documents, UpdateQuery.Refresh refresh){
    Class<T> clz = this.getBaseClass();
    IndexCoordinates index = estTemplate.getIndexCoordinatesFor(clz);
    List<UpdateQuery> queries = documents.stream().map(doc -> {
      Map<String, Object> src = BeanUtil.beanToMap(doc);
      MapUtil.removeNullValue(src);
      String docId = this.getDocId(doc);
      assert docId != null;

      UpdateQuery.Builder bd = UpdateQuery.builder(docId);
      bd.withRetryOnConflict(3);
      bd.withDocument(Document.from(src));
      bd.withUpsert(Document.from(src));
      if(refresh != null){
        bd.withRefresh(refresh);
      }
      return bd.build();
    }).collect(Collectors.toList());
    estTemplate.bulkUpdate(queries, index);
  }

  /**
   * description: 批量更新es数据的方法（EsTemplate方式）
   *
   * @param documents es中对应的数据类型 的 数据源 Class<clz>
   * @param idMapper  id的获取方法 → T::getId
   * @param script    脚本 → 更新的脚本（统一的更新脚本方式）
   * @Author 阿魔
   * @Date 2022/1/15 上午9:44
   */
  public  void bulkUpdateOrInsert( List<T> documents, Function<T, String> idMapper, String script) {
    Class<T> clz = this.getBaseClass();
    IndexCoordinates indices = estTemplate.getIndexCoordinatesFor(clz);
    List<UpdateQuery> uqList = documents.stream().map(document -> {
      Map<String, Object> src = BeanUtil.beanToMap(document);
      UpdateQuery.Builder uqb = UpdateQuery.builder(idMapper.apply(document));
      uqb.withRetryOnConflict(3);
      uqb.withScript(script);
      uqb.withParams(src);
      uqb.withUpsert(Document.from(src));
      return uqb.build();
    }).collect(Collectors.toList());

    estTemplate.bulkUpdate(uqList, indices);
  }

  /**
   * description: 不删除索引，清除数据
   *
   * @Author 阿魔
   * @Date 2021/12/7 上午11:04
   */
  public  void clear() {
    Class<T> clz = this.getBaseClass();
    IndexOperations indexOps = estTemplate.indexOps(clz);
    if (!indexOps.exists()) return;

    IndexCoordinates indices = estTemplate.getIndexCoordinatesFor(clz);
    NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
    MatchAllQueryBuilder all = QueryBuilders.matchAllQuery();
    NativeSearchQuery query = builder.withQuery(all).build();
    estTemplate.delete(query, clz, indices);
  }

  public <T> List<T> mustQuery(Class<T> clz, Map<String, String> mustMap) {
    NativeSearchQuery query = this.buildMustQuery(mustMap);
    SearchHits<T> searchRes = estTemplate.search(query, clz);
    return searchRes.stream().map(SearchHit::getContent).collect(Collectors.toList());
  }

  private NativeSearchQuery buildMustQuery(Map<String, String> mustMap) {
    NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    mustMap.forEach((key, value) ->
        boolQuery.must().add(QueryBuilders.matchQuery(key, value))
    );

    return builder.withQuery(boolQuery).build();
  }

  /**
   * 根据文档id查询
   */
  public<E> T getById(E id) {
    Class<T> baseClass = getBaseClass();
    return estTemplate.get(id.toString(),baseClass);
  }

  /**
   * @author metz
   * @date 2022/6/13 10:48
   * @description  查询一条记录
   */
  public T getOne(EsQueryWrapper<T> esQueryWrapper){

    SearchHits<T> search = search(esQueryWrapper);
    if(search.getTotalHits() > 1){
      throw new BaseException(StrUtil.format("Expected one result (or null) to be returned by selectOne(), but found: {}",search.getTotalHits()));
    }
    return search.getSearchHits().get(0).getContent();
  }

  /**
   * 根据传过来的wrapper查询
   */
  protected SearchHits<T> search(EsQueryWrapper<T> esQueryWrapper) {
    Class<T> baseClass = this.getBaseClass();
    NativeSearchQueryBuilder query = esQueryWrapper.getNativeSearchQueryBuilder();
    NativeSearchQuery searchQuery = query.build();
    return estTemplate.search(searchQuery,baseClass);
  }

  /**
   * 根据传过来的wrapper查询
   */
  protected SearchPage<T> searchPage(EsQueryWrapper<T> esQueryWrapper,Integer page,Integer pageSize) {

    page = page - 1;
    if(page < 0){
      page = 0;
    }

    Class<T> baseClass = this.getBaseClass();
    NativeSearchQueryBuilder query = esQueryWrapper.getNativeSearchQueryBuilder();
    Pageable pageable = PageRequest.of(page, pageSize);
    query.withPageable(pageable);
    NativeSearchQuery searchQuery = query.build();
    SearchHits<T> search = estTemplate.search(searchQuery, baseClass);
    return SearchHitSupport.searchPageFor(search, pageable);
  }
  /*-------- ---------- -------- ----------  私有方法  ----------- -------------- ------------ ----------- ----*/



  /**
    _      __   ____  _
   | |    / /\   / / \ \_/
   |_|__ /_/--\ /_/_  |_|
   * @description  获取泛型的实体类对象
   * @author linzy
   * @date 2022/4/28
   **/
  private Class<T> getBaseClass(){
    Type[] params = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
    return  (Class<T>) params[0];
  }

  /**
    _      __   ____  _
   | |    / /\   / / \ \_/
   |_|__ /_/--\ /_/_  |_|
   * @description 根据实体类获取es文档中的主键id
   * @author linzy
   * @date 2022/4/28
   **/
  private String getDocId(T t) {
    ElasticsearchPersistentEntity<?> requiredPersistentEntity = elasticsearchConverter.getMappingContext().getRequiredPersistentEntity(t.getClass());
    ElasticsearchPersistentProperty idProperty = requiredPersistentEntity.getIdProperty();
    if(idProperty==null){return null;}
    return stringIdRepresentation(requiredPersistentEntity.getPropertyAccessor(t).getProperty(idProperty));
  }

  private String stringIdRepresentation(Object id) {
    return  Objects.toString(id, null);
  }



}
