package io.github.hilinzy.mongo.builders;

import io.github.hilinzy.common.ext.ColumnUtil;
import io.github.hilinzy.common.ext.MyFunction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  分组构造器
 * @author linzy
 * @date  2022/7/27
 **/
public class GroupOperationExtBuilder {

  @Setter
  @Getter
  private GroupOperation groupOperation;

  public static GroupOperationExtBuilder build(GroupOperation group) {
    GroupOperationExtBuilder groupOperationBuilder = new GroupOperationExtBuilder();
    groupOperationBuilder.setGroupOperation(group);
    return groupOperationBuilder;
  }

  public GroupOperationExtBuilder count() {
    this.setGroupOperation(groupOperation.count().as("count"));
    return this;
  }

  public <T> GroupOperationExtBuilder sum(MyFunction<T, ?> function) {
    String fieldName = ColumnUtil.getFieldName(function,"-",2);
    this.setGroupOperation(groupOperation.sum(fieldName).as("sum"));
    return this;
  }

  public <T> GroupOperationExtBuilder avg(MyFunction<T, ?> function) {
    String fieldName = ColumnUtil.getFieldName(function,"-",2);
    this.setGroupOperation(groupOperation.avg(fieldName).as("avg"));
    return this;
  }

  public <T> GroupOperationExtBuilder max(MyFunction<T, ?> function) {
    String fieldName = ColumnUtil.getFieldName(function,"-",2);
    this.setGroupOperation(groupOperation.max(fieldName).as("max"));
    return this;
  }

  public <T> GroupOperationExtBuilder min(MyFunction<T, ?> function) {
    String fieldName = ColumnUtil.getFieldName(function,"-",2);
    this.setGroupOperation(groupOperation.min(fieldName).as("min"));
    return this;
  }
}
