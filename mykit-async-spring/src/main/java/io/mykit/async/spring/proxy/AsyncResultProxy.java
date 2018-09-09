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
package io.mykit.async.spring.proxy;

import io.mykit.async.spring.cache.AsyncProxyCache;
import io.mykit.async.spring.constant.AsyncConstant;
import io.mykit.async.spring.core.AsyncFutureTask;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:02
 * @description 异步结果代理
 * @version 1.0.0
 */
public class AsyncResultProxy implements AsyncProxy {
    private final static Logger logger = LoggerFactory.getLogger(AsyncResultProxy.class);

    private AsyncFutureTask future;

    public AsyncResultProxy(AsyncFutureTask future) {
        this.future = future;
    }

    public Object buildProxy(Object t, boolean all) {
        return buildProxy(t, AsyncConstant.ASYNC_DEFAULT_TIME_OUT, true);
    }

    public Object buildProxy(Object t, long timeout, boolean all) {
        Class<?> returnClass = t.getClass();
        if (t instanceof Class) {
            returnClass = (Class) t;
        }
        Class<?> proxyClass = AsyncProxyCache.getProxyClass(returnClass.getName());
        if (proxyClass == null) {
            Enhancer enhancer = new Enhancer();
            if (returnClass.isInterface()) {
                enhancer.setInterfaces(new Class[]{returnClass});
            } else {
                enhancer.setSuperclass(returnClass);
            }
            enhancer.setNamingPolicy(AsyncNamingPolicy.INSTANCE);
            enhancer.setCallbackType(AsyncResultInterceptor.class);
            proxyClass = enhancer.createClass();
            logger.debug("create result proxy class:{}", returnClass);
            AsyncProxyCache.registerProxy(returnClass.getName(), proxyClass);
        }
        Enhancer.registerCallbacks(proxyClass, new Callback[]{new AsyncResultInterceptor(future, timeout)});
        Object proxyObject = null;
        try {
            proxyObject = ReflectionHelper.newInstance(proxyClass);
        } finally {
            Enhancer.registerStaticCallbacks(proxyClass, null);
        }
        return proxyObject;
    }
}
