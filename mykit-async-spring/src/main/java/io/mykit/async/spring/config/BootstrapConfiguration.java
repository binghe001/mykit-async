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
package io.mykit.async.spring.config;

import io.mykit.async.spring.constant.AsyncConstant;
import io.mykit.async.spring.core.AsyncExecutor;
import io.mykit.async.spring.inject.SpringBeanPostProcessor;
import io.mykit.async.spring.inject.TransactionBuilder;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:20
 * @description 启动类配置信息
 * @version 1.0.0
 */
@Configuration
@Import({ThreadPoolConfiguration.class, ExecutorConfiguration.class})
public class BootstrapConfiguration {

    @Autowired
    private ThreadPoolConfiguration threadPoolConfiguration;
    @Autowired
    private ExecutorConfiguration executorConfiguration;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initialize() throws Exception {
        Map<String, AsyncConfigurer> configurerBeanMaps = applicationContext.getBeansOfType(AsyncConfigurer.class);
        AsyncConfigurer asyncConfigurer = null;
        if (CollectionUtils.isEmpty(configurerBeanMaps)) {
            asyncConfigurer = new DefaultAsyncConfigurer();
        } else {
            if (configurerBeanMaps.size() > 1) {
                throw new NoUniqueBeanDefinitionException(AsyncConfigurer.class, configurerBeanMaps.size(), "Multiple beans found among candidates: " + configurerBeanMaps.keySet());
            }
            asyncConfigurer = configurerBeanMaps.entrySet().iterator().next().getValue();
        }

        if (asyncConfigurer == null) {
            asyncConfigurer = new DefaultAsyncConfigurer();
        }
        asyncConfigurer.configureExecutorConfiguration(executorConfiguration);
        asyncConfigurer.configureThreadPool(threadPoolConfiguration);
        AsyncExecutor.initializeThreadPool(threadPoolConfiguration);
        if (asyncConfigurer.getRunnableAround() != null) {
            AsyncExecutor.setRunnableAround(asyncConfigurer.getRunnableAround());
        }
        AsyncConstant.ASYNC_DEFAULT_TRACE_LOG = executorConfiguration.getTraced();
    }

    @Bean
    public SpringBeanPostProcessor springBeanPostProcessor() {
        return new SpringBeanPostProcessor();
    }

    @Bean
    public TransactionBuilder transactionBuilder() {
        return new TransactionBuilder();
    }

    @PreDestroy
    public void destory() throws Exception {
        AsyncExecutor.setIsDestroyed(true);
        AsyncExecutor.destroy();
    }
}
