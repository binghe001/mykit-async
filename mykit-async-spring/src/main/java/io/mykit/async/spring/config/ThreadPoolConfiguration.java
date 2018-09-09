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

import org.springframework.beans.factory.annotation.Value;

/**
 * @author liuyazhuang
 * @date 2018/9/9 21:17
 * @description 线程池配置信息
 * @version 1.0.0
 */
public class ThreadPoolConfiguration {

    @Value("${async.corePoolSize:}")
    private Integer corePoolSize;

    @Value("${async.maxPoolSize:}")
    private Integer maxPoolSize;

    @Value("${async.maxAcceptCount:}")
    private Integer maxAcceptCount;

    @Value("${async.rejectedExecutionHandler:CALLERRUN}")
    private String rejectedExecutionHandler;

    @Value("${async.allowCoreThreadTimeout:true}")
    private Boolean allowCoreThreadTimeout;

    @Value("${async.keepAliveTime:}")
    private Long keepAliveTime;

    public Integer getCorePoolSize() {
        return corePoolSize;
    }


    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }


    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }


    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }


    public Integer getMaxAcceptCount() {
        return maxAcceptCount;
    }


    public void setMaxAcceptCount(Integer maxAcceptCount) {
        this.maxAcceptCount = maxAcceptCount;
    }


    public String getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }


    public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }


    public Boolean getAllowCoreThreadTimeout() {
        return allowCoreThreadTimeout;
    }


    public void setAllowCoreThreadTimeout(Boolean allowCoreThreadTimeout) {
        this.allowCoreThreadTimeout = allowCoreThreadTimeout;
    }


    public Long getKeepAliveTime() {
        return keepAliveTime;
    }


    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }
}
