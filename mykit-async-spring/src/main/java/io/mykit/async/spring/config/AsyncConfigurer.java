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

import io.mykit.async.spring.pool.RunnableAround;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:18
 * @description 异步配置器
 * @version 1.0.0
 */
public interface AsyncConfigurer {

    /**
     * 配置执行器
     * @param configuration 执行器配置信息
     */
    void configureExecutorConfiguration(ExecutorConfiguration configuration);

    /**
     * 配置线程池
     * @param configuration 线程池配置信息
     */
    void configureThreadPool(ThreadPoolConfiguration configuration);

    /**
     * 获取异步任务环绕通知
     * @return 异步任务环绕通知
     */
    RunnableAround getRunnableAround();
}
