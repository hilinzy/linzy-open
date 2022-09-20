package io.github.hilinzy.mongo.service;

import io.github.hilinzy.mongo.document.Incr;
import io.github.hilinzy.mongo.wrappers.MongoQueryWrapper;
import org.springframework.stereotype.Service;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  维护mongo集合的自增长id
 * @author linzy
 * @date  2022/7/27
 **/
@Service
public class IncrIdSrv extends BaseMongoSrv<Incr> {

  public  Long getIncrId(String collectionName){
    MongoQueryWrapper<Incr> build = MongoQueryWrapper.build(Incr.class);
    build.incr(Incr::getIncrId);
    build.eq(Incr::getCollectionName,collectionName);
    Incr incr = this.findAdnUpsertOneReturnNew(build);
    return incr.getIncrId();
  }
}
