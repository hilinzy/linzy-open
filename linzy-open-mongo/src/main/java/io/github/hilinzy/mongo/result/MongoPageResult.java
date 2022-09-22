package io.github.hilinzy.mongo.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  封装mongo分页数据的结果集
 * @author linzy
 * @date  2022/9/22
 **/
@Data
public class MongoPageResult<T> implements Serializable {
  private static final long serialVersionUID = 4627515164385331418L;
  /*当前页*/
  private Integer page;

  /*总页数*/
  private Integer totalPage;

  /*总条数*/
  private Long count;

  /*数据*/
  private List<T> list;
}
