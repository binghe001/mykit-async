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
package io.mykit.async.spring.cache;

import io.mykit.async.spring.annotation.Async;
import io.mykit.async.spring.bean.AsyncMethod;
import io.mykit.async.spring.bean.AsyncRetry;
import io.mykit.async.spring.utils.CommonUtil;
import io.mykit.async.spring.utils.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:09
 * @description 异步代理缓存
 * @version 1.0.0
 */
public class AsyncProxyCache {

    /**
     * 代理类缓存
     */
    private static ConcurrentMap<String, Class<?>> proxyClasss = new ConcurrentHashMap<>(100);
    /**
     * 本地对象缓存
     */
    private static ConcurrentMap<String, Object> nativeObjects = new ConcurrentHashMap<String, Object>(100);
    /**
     * 代理方法缓存
     */
    private static ConcurrentMap<String, AsyncMethod> proxyMethods = new ConcurrentHashMap<String, AsyncMethod>(500);


    /**
     * 如果存在对应的key的ProxyClass就返回，没有则返回null
     * @param key 缓存中存在的注册Key
     * @return 对应的key的ProxyClass
     */
    public static Class<?> getProxyClass(String key) {
        return proxyClasss.get(key);
    }

    /**
     * 注册对应的proxyClass到Map
     * @param key 缓存的key
     * @param proxyClass 待注册的代理类
     */
    public static void registerProxy(String key, Class<?> proxyClass) {
        proxyClasss.putIfAbsent(key, proxyClass);
    }

    /**
     * 注册对应的异步方法到代理方法缓存
     * @param key 指定的Key
     * @param asyncMethod 需要注册的异步方法
     */
    public static void putAsyncMethod(String key, AsyncMethod asyncMethod) {
        proxyMethods.putIfAbsent(key, asyncMethod);
    }

    /**
     * 注册方法信息
     * @param bean 代注册的Bean
     * @param timeout 超时时间
     * @param all 是否全部注册
     */
    public static void registerMethod(Object bean, long timeout, boolean all) {
        Method[] methods = CommonUtil.getClass(bean).getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return;
        }

        if (!all) {
            nativeObjects.putIfAbsent(CommonUtil.getClass(bean).getName(), bean);
        } else {
            Object nativeObject = nativeObjects.get(CommonUtil.getClass(bean).getName());
            if (nativeObject != null) bean = nativeObject;
        }

        for (Method method : methods) {
            if (!all) {
                Async annotation = ReflectionHelper.findAsyncAnnatation(bean, method);
                if (annotation != null) {
                    AsyncMethod asyncMethod = new AsyncMethod(bean, method, annotation.timeout(), new AsyncRetry(annotation.maxAttemps(), annotation.exceptions()));
                    putAsyncMethod(CommonUtil.buildkey(bean, method), asyncMethod);
                }
            } else {
                Class<?> returnClass = method.getReturnType();
                if (Void.TYPE.isAssignableFrom(returnClass) || ReflectionHelper.canProxy(returnClass)) {
                    AsyncMethod asyncMethod = new AsyncMethod(bean, method, timeout, new AsyncRetry(0, Throwable.class));
                    putAsyncMethod(CommonUtil.buildkey(bean, method), asyncMethod);
                }
            }
        }
    }

    /**
     * 判断代理方法缓存中是否存在相应的key
     * @param key 指定的key
     * @return true:存在; false:不存在
     */
    public static boolean containMethod(String key) {
        return proxyMethods.containsKey(key);
    }

    /**
     * 根据key从代理方法缓存中获取指定的异步方法
     * @param key 指定的Key
     * @return 异步方法信息
     */
    public static AsyncMethod getAsyncMethod(String key) {
        return proxyMethods.get(key);
    }
}
