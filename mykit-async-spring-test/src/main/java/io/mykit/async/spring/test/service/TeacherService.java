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
package io.mykit.async.spring.test.service;

import io.mykit.async.spring.annotation.Async;
import io.mykit.async.spring.test.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:32
 * @description 教师Service类
 * @version 1.0.0
 */
@Service
public class TeacherService {
    private final static Logger logger = LoggerFactory.getLogger(TeacherService.class);

    public User addTeacher(User user) {

        logger.info("正在添加教师{}", user.getName());

        return user;
    }

    @Async
    public User addAsyncTeacher(User user) {

        logger.info("正在添加教师{}", user.getName());

        return user;
    }

}
