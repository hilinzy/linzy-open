package io.github.hilinzy.mongo.annotation;

import java.lang.annotation.*;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description 标记默认值 目前只支持基本类的包装类和String
 * @author linzy
 * @date  2022/9/8
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface DefaultValue {
  String value();
}
