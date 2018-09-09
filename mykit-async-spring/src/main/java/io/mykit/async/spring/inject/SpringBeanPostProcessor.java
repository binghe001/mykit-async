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
package io.mykit.async.spring.inject;

import io.mykit.async.spring.annotation.Async;
import io.mykit.async.spring.constant.AsyncConstant;
import io.mykit.async.spring.core.AsyncExecutor;
import io.mykit.async.spring.template.AsyncTemplate;
import io.mykit.async.spring.utils.CommonUtil;
import io.mykit.async.spring.utils.ReflectionHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:45
 * @description SpringBean Post 处理器
 * @version 1.0.0
 */
public class SpringBeanPostProcessor implements BeanPostProcessor, Ordered {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return processAasynBean(bean, beanName);
    }

    public Object processAasynBean(Object bean, String beanName) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return bean;
        }
        for (Method method : methods) {
            Async annotation = ReflectionHelper.findAsyncAnnatation(bean, method);
            if (annotation != null) {
                return AsyncTemplate.getAsyncProxy(AsyncTemplate.ProxyType.CGLIB).buildProxy(bean, AsyncConstant.ASYNC_DEFAULT_TIME_OUT, false);
            }
        }
        if (CommonUtil.getClass(bean).isAssignableFrom(TransactionBuilder.class)) {
            AsyncExecutor.setTransactionBuilder((TransactionBuilder) bean);
        }
        return bean;

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
