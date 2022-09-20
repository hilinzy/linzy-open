package io.github.hilinzy.mongo.annotation;

import java.lang.annotation.*;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  标识自增id(类型long)
 * @author linzy
 * @date  2022/9/8
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface IncrId {

}
