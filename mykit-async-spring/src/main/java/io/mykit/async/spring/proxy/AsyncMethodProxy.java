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
import io.mykit.async.spring.exception.AsyncException;
import io.mykit.async.spring.utils.CommonUtil;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:56
 * @description 异步方法代理
 * @version 1.0.0
 */
@SuppressWarnings("all")
public class AsyncMethodProxy implements AsyncProxy{

    private final static Logger logger = LoggerFactory.getLogger(AsyncMethodProxy.class);

    @Override
    public Object buildProxy(Object target, boolean all) {
        return buildProxy(target, AsyncConstant.ASYNC_DEFAULT_TIME_OUT, all);
    }

    @Override
    public Object buildProxy(Object target, long timeout, boolean all) {
        Class<?> targetClass = CommonUtil.getClass(target);
        if (target instanceof Class) {
            throw new AsyncException("target is not object instance");
        }

        if (!ReflectionHelper.canProxy(targetClass)) {
            return target;
        }
        Class<?> proxyClass = AsyncProxyCache.getProxyClass(CommonUtil.buildkey(targetClass.getName(), all));
        if (proxyClass == null) {
            Enhancer enhancer = new Enhancer();
            if (targetClass.isInterface()) {
                enhancer.setInterfaces(new Class[]{targetClass});
            } else {
                enhancer.setSuperclass(targetClass);
            }
            enhancer.setNamingPolicy(AsyncNamingPolicy.INSTANCE);
            enhancer.setCallbackType(AsyncMethodInterceptor.class);
            proxyClass = enhancer.createClass();
            logger.debug("create proxy class:{}", targetClass);
            AsyncProxyCache.registerProxy(CommonUtil.buildkey(targetClass.getName(), all), proxyClass);
            AsyncProxyCache.registerMethod(target, timeout, all);
        }
        Enhancer.registerCallbacks(proxyClass, new Callback[]{new AsyncMethodInterceptor(target)});
        Object proxyObject = null;
        try {
            proxyObject = ReflectionHelper.newInstance(proxyClass);
        } finally {
            Enhancer.registerStaticCallbacks(proxyClass, null);
        }

        return proxyObject;
    }
}
