/**
 * Copyright 2018-2118 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.async.spring.template;

import io.mykit.async.spring.constant.AsyncConstant;
import io.mykit.async.spring.core.*;
import io.mykit.async.spring.exception.AsyncException;
import io.mykit.async.spring.proxy.AsyncMethodProxy;
import io.mykit.async.spring.proxy.AsyncProxy;
import io.mykit.async.spring.proxy.AsyncResultProxy;
import io.mykit.async.spring.utils.ReflectionHelper;
import io.mykit.async.spring.utils.ValidationUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:03
 * @description 异步方法模板
 * @version 1.0.0
 */
@SuppressWarnings("all")
public class AsyncTemplate {

    private static AsyncProxy cglibProxy = new AsyncMethodProxy();

    public enum ProxyType {
        CGLIB
    }

    /**
     * <p>
     * <p>
     * 获取代理方式：</br> ProxyType.SPRING 返回Spring Aop代理</br> ProxyType.CGLIB
     * 返回Cglib代理
     *
     * </p>
     */
    public static AsyncProxy getAsyncProxy(ProxyType type) {
        return cglibProxy;
    }

    /**
     * <p>
     * <p>
     * 构建代理类</br>
     *
     * </p>
     *
     * @param t 需要被代理的类
     * @return T
     * 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T> T buildProxy(T t) {
        return buildProxy(t, 0);
    }

    /**
     * <p>
     * <p>
     * 构建代理类
     *
     * </p>
     *
     * @param t       需要被代理的类
     * @param timeout 超时时间（单位：毫秒）
     * @return T
     * 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T> T buildProxy(T t, long timeout) {
        return (T) getAsyncProxy(ProxyType.CGLIB).buildProxy(t, timeout, true);
    }

    /**
     * <p>
     * <p>
     * 构建代理类
     *
     * </p>
     *
     * @param T         需要被代理的类
     * @param proxyType 代理类型
     * @return T
     * 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T> T buildProxy(T t, ProxyType proxyType) {
        return buildProxy(t, AsyncConstant.ASYNC_DEFAULT_TIME_OUT, proxyType);
    }

    /**
     * <p>
     * <p>
     * 构建代理类
     *
     * </p>
     *
     * @param T         需要被代理的类
     * @param timeout   超时时间（单位：毫秒）
     * @param proxyType 代理类型
     * @return T
     * 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T> T buildProxy(T t, long timeout, ProxyType proxyType) {
        return (T) getAsyncProxy(proxyType).buildProxy(t, timeout, true);
    }

    /**
     * <p>
     * <p>
     * 异步执行AsyncCallable.doAsync
     *
     * </p>
     */
    public static void execute(AsyncCallable<Void> callable) {
        AsyncExecutor.execute(callable);
    }
    /**
     * <p>
     * <p>
     * 异步执行AsyncCallable.doAsync
     *
     * </p>
     */
    public static void execute(AsyncFutureCallable<Void> callable, AsyncFutureCallback<Void> callback) {
        AsyncExecutor.execute(callable, callback);
    }

    /**
     * <p>
     * <p>
     * 异步执行 AsyncCallable.doAsync方法
     *
     * </p>
     *
     * @param AsyncCallable<T> 需要实现的接口
     * @return T
     * 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T> T submit(AsyncCallable<T> callable) {
        Type type = callable.getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            // 未指定AsyncCallback的泛型信息
            throw new AsyncException("must be specify AsyncCallable<T> for T type");
        }
        Class<?> returnClass = ReflectionHelper.getGenericClass((ParameterizedType) type, 0);
        return submit(callable, returnClass, callable.timeout());
    }

    /**
     * <p>
     * <p>
     * 异步执行AsyncCallable.doAsync；并且回调AsyncFutureCallback
     *
     * </p>
     */
    public static <T> void submit(AsyncCallable<T> callable, AsyncFutureCallback<T> asyncFutureCallback) {

        AsyncExecutor.submit(callable, asyncFutureCallback);
    }

    /**
     * 遍历List 异步执行AsyncFunction.doAsync；
     *
     * @param list
     * @param function 需要实现的抽象类
     * @return List<E>
     * E 必须带有返回参数且不支持Void,Array及Integer,Long,String,Boolean等Final修饰类</br>
     * 如果需要返回以上类型，可以创建对象包装；如：
     * {@linkplain io.mykit.async.spring.bean.AsyncResult}
     */
    public static <T, E> List<E> submit(List<T> list, final AsyncFunction<T, E> function) {
        List<E> asyncs = new ArrayList<E>();
        if (CollectionUtils.isEmpty(list)) {
            return asyncs;
        }
        if (function == null) {
            return asyncs;
        }

        Class<?> returnClass = ReflectionHelper.getGenericClass(function.getClass(), 1);
        for (final T t : list) {
            asyncs.add(submit(new AsyncCallable<E>() {
                public E doAsync() {
                    return function.doAsync(t);
                }
            }, returnClass, function.timeout()));
        }
        return asyncs;
    }

    private static <T> T submit(AsyncCallable<T> callback, Class<?> returnClass, long timeout) {
        ValidationUtils.checkNotNull(callback);
        ValidationUtils.checkNotNull(returnClass, "must be specify return type");

        if (!ReflectionHelper.canProxy(returnClass)) {
            return callback.doAsync();
        }
        if (Void.TYPE.isAssignableFrom(returnClass)) {
            AsyncExecutor.execute(callback);
            return null;
        }

        AsyncFutureTask<T> future = AsyncExecutor.submit(callback);
        return (T) new AsyncResultProxy(future).buildProxy(returnClass, timeout, true);
    }
}
