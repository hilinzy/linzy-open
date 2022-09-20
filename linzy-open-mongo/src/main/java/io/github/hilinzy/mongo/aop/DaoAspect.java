package io.github.hilinzy.mongo.aop;

import cn.hutool.core.util.ObjectUtil;
import io.github.hilinzy.mongo.annotation.DefaultValue;
import io.github.hilinzy.mongo.annotation.IncrId;
import io.github.hilinzy.mongo.service.IncrIdSrv;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description
 * @author linzy
 * @date  2022/9/8
 **/
@Slf4j
@Component
@Aspect
public class DaoAspect {
  @Resource
  MongoTemplate mongoTemplate;
  @Resource
  IncrIdSrv incrIdSrv;

  @Pointcut("execution(public * com.lazy.mongo.service.BaseMongoSrv.*save*(..))")
  public void doSave() {

  }

  @Before("doSave()")
  public void doBefore(JoinPoint joinPoint) throws Throwable {
    Object obj = joinPoint.getArgs()[0];
    if (obj instanceof Collection<?>) {
      for (Object o : (Collection<?>) obj) {
        String collectionName = mongoTemplate.getCollectionName(o.getClass());
        addFieldsValueForCreate(o, collectionName);
      }
    } else {
      String collectionName = mongoTemplate.getCollectionName(obj.getClass());
      addFieldsValueForCreate(obj, collectionName);
    }

  }

  /**
   *新增mongo文档时添加默认值
   * 1->创建时间，修改时间默认为当前时间
   * 2->有自定义注解IncrId标识时，添加自增id
   * 3->有自定义注解DefaultValue标识时，添加默认值(目前默认值只支持基本数据类型的包装类和String)
   */
  private void addFieldsValueForCreate(Object obj, String collectionName) {
    try {
      Field[] fields = obj.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        String fieldName = field.getName();
        if (fieldName.equals("createTime")||fieldName.equals("updateTime")) {
          field.set(obj, new Date());
        }  else if (ObjectUtil.isNotEmpty(field.getAnnotation(IncrId.class))) {
          Long incrId = incrIdSrv.getIncrId(collectionName);
          field.set(obj, incrId);
        } else if (ObjectUtil.isNotEmpty(field.getAnnotation(DefaultValue.class))
            && ObjectUtil.isEmpty(field.get(obj))) {
          Class<?> type = field.getType();
          if (type.getName().equals(String.class.getName())) {
            String value = field.getAnnotation(DefaultValue.class).value();
            field.set(obj, value);
            continue;
          }
          Class<?> subType = null;
          try {
            subType = (Class<?>) type.getField("TYPE").get(null);
          } catch (NoSuchFieldException e) {
            log.error("@DefaultValue定义在非基本包装类型上");
            continue;
          }
          if (subType.isPrimitive()) {
            String value = field.getAnnotation(DefaultValue.class).value();
            Object o = type.getConstructor(String.class).newInstance(value);
            field.set(obj, o);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
