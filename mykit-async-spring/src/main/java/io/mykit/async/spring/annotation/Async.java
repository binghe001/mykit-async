/**
 * Copyright 2018-2118 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.async.spring.annotation;

import io.mykit.async.spring.constant.AsyncConstant;

import java.lang.annotation.*;

/**
 * @author liuyazhuang
 * @version 1.0.0
 * @date 2018/9/9 12:08
 * @description 异步注解
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {

    /**
     * 调用超时设置，单位毫秒(默认0.不超时)
     */
    long timeout() default AsyncConstant.ASYNC_DEFAULT_TIME_OUT;

    /**
     * 异步调用最多重试的次数
     */
    int maxAttemps() default AsyncConstant.ASYNC_DEFAULT_RETRY;

    /**
     * 异常信息
     */
    Class<? extends Throwable>[] exceptions() default Throwable.class;
}
