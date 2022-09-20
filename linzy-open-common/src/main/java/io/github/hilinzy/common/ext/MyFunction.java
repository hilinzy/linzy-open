package io.github.hilinzy.common.ext;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface MyFunction<T,R> extends Function<T,R>, Serializable {
}