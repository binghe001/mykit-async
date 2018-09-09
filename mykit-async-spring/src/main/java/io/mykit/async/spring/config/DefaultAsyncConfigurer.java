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
import io.mykit.async.spring.constant.HandleMode;
import io.mykit.async.spring.pool.RunnableAround;
import org.springframework.util.StringUtils;

/**
 * @author liuyazhuang
 * @version 1.0.0
 * @date 2018/9/9 21:22
 * @description 默认的异步配置信息
 */
public class DefaultAsyncConfigurer implements AsyncConfigurer {

    @Override
    public void configureExecutorConfiguration(ExecutorConfiguration configuration) {
        if (configuration == null) {
            return;
        }
        configuration.setTraced(false);
    }

    @Override
    public RunnableAround getRunnableAround() {
        return null;
    }

    @Override
    public void configureThreadPool(ThreadPoolConfiguration configuration) {
        if (configuration == null) {
            return;
        }
        Integer corePoolSize = configuration.getCorePoolSize();
        Integer maxPoolSize = configuration.getMaxPoolSize();
        Integer maxAcceptCount = configuration.getMaxAcceptCount();
        Long keepAliveTime = configuration.getKeepAliveTime();
        Boolean allowCoreThreadTimeout = configuration.getAllowCoreThreadTimeout();
        String rejectedExecutionHandler = configuration.getRejectedExecutionHandler();

        if (!StringUtils.hasText(rejectedExecutionHandler)) {
            rejectedExecutionHandler = HandleMode.CALLERRUN.toString();
        } else {
            if (!HandleMode.REJECT.toString().equals(rejectedExecutionHandler) && !HandleMode.CALLERRUN.toString().equals(rejectedExecutionHandler)) {
                throw new IllegalArgumentException("Invalid configuration properties async.rejectedExecutionHandler");
            }
        }

        if (corePoolSize == null || corePoolSize <= 0) {
            corePoolSize = Runtime.getRuntime().availableProcessors() * 4;
        }
        if (maxPoolSize == null || maxPoolSize <= 0) {
            maxPoolSize = corePoolSize * 2;
        }
        if (maxAcceptCount == null || maxAcceptCount < 0) {
            maxAcceptCount = corePoolSize;
        }

        if (keepAliveTime == null || keepAliveTime < 0) {
            keepAliveTime = AsyncConstant.ASYNC_DEFAULT_KEEPALIVETIME;
        }
        if (allowCoreThreadTimeout == null) {
            allowCoreThreadTimeout = true;
        }

        configuration.setAllowCoreThreadTimeout(allowCoreThreadTimeout);
        configuration.setCorePoolSize(corePoolSize);
        configuration.setKeepAliveTime(keepAliveTime);
        configuration.setMaxAcceptCount(maxAcceptCount);
        configuration.setMaxPoolSize(maxPoolSize);
        configuration.setRejectedExecutionHandler(rejectedExecutionHandler);
    }
}
