# 作者简介: 
Adam Lu(刘亚壮)，高级软件架构师，Java编程专家，Spring、MySQL内核专家，开源分布式消息引擎Mysum发起者、首席架构师及开发者，Android开源消息组件Android-MQ独立作者，国内知名开源分布式数据库中间件Mycat核心架构师、开发者，精通Java, C, C++, Python, Hadoop大数据生态体系，熟悉MySQL、Redis内核，Android底层架构。多年来致力于分布式系统架构、微服务、分布式数据库、大数据技术的研究，曾主导过众多分布式系统、微服务及大数据项目的架构设计、研发和实施落地。在高并发、高可用、高可扩展性、高可维护性和大数据等领域拥有丰富的经验。对Hadoop、Spark、Storm等大数据框架源码进行过深度分析并具有丰富的实战经验。

# 作者联系方式
QQ：2711098650

# 框架简述
mykit架构中独立出来的mykit-async异步编程框架，本异步框架实现了在Spring的基础上重写和扩展了异步执行的流程，注意提供了如下功能：  
1、提供声明式异步编程（只需在方法上加@Async注解就OK了）；  
2、解决异步多层嵌套带来的线程阻塞问题；  
3、提供异步事件编程
  
# 框架结构描述
对高并发下的业务提供异步操作的能力，同时解决了Spring异步多层嵌套带来的线程阻塞问题，框架主要提供的功能如下：  
1、提供声明式异步编程（只需在方法上加@Async注解就OK了）；  
2、解决异步多层嵌套带来的线程阻塞问题；  
3、提供异步事件编程  
  
# 功能描述
mykit-async 是一个基于Spring的异步并行框架；主要包括以下几个方面的功能，具体如下：

1、提供注解声明方式异步执行，对原代码无侵入（解决spring-async对有返回结果的需包装成Future对象问题）；  
2、提供编程式异步方法；  
3、提供异步事件编程；   
4、解决多层异步嵌套带来的线程阻塞问题（目前spring-async依然存在此问题）；  

  
## mykit-async-spring
mykit-async 架构下主要以Spring为基础实现的异步编程框架，重写和扩展了Spring异步编程的接口和实现，并提供了Spring异步编程中一些没有的功能；

## mykit-async-spring-test
主要是对mykit-async-spring提供的测试工程，测试入口为：io.mykit.async.spring.test.AsyncTest
 
  
# 使用说明
1、引用mykit-async-spring说明  
1)在pom.xml中添加如下配置：
```
<dependency>
    <groupId>io.mykit.async</groupId>
    <artifactId>mykit-async-spring</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2)在项目的Spring配置文件中加上如下配置：
```
<context:component-scan base-package="io.mykit.async.spring"/>
```
或者在需要开启异步功能的类上加上如下注解
```
@EnableAsync
```
注意：此注解为：io.mykit.async.spring.annotation.EnableAsync  
  
在Spring的配置文件中加入如下配置：
```
 <context:property-placeholder location="classpath*:properties/async-default.properties, classpath*: properties/async.properties" ignore-unresolvable="true"/>
```
来引入异步配置文件，classpath*:properties/async-default.properties文件为框架默认提供的异步配置文件  
classpath*: properties/async.properties文件为自定义的异步配置文件，注意配置顺序必须为上述示例中的配置顺序，  
这样自定义的配置文件属性会覆盖框架默认的配置文件属性。  
框架默认的异步配置文件的内容如下：  
```
#核心线程数(默认CPU核数*4)
async.corePoolSize=8
#最大线程数
async.maxPoolSize=24
#最大队列size
async.maxAcceptCount=100
#线程空闲时间
async.keepAliveTime=10000
#拒绝服务处理方式 (不建议修改)
async.rejectedExecutionHandler=CALLERRUN
#是否允许线程自动超时销毁(不建议修改)
async.allowCoreThreadTimeout=true
```
# 代码演示
## 前期准备
### 1、创建测试实体类
```
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
package io.mykit.async.spring.test.entity;

/**
 * @author liuyazhuang
 * @date 2018/9/9 22:31
 * @description 测试实体类
 * @version 1.0.0
 */
public class User {

    private String name;
    private int age;

    public User() {
    }

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

```

### 2、创建测试的Service——TeacherService
```
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
}

```

### 3、创建测试Service——UserService
```
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
 * @description 用户Service类
 * @version 1.0.0
 */
@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(TeacherService.class);

    @Async
    public User addUser(User user) {

        logger.info("正在添加用户{}", user.getName());

        return user;
    }
}

```
注意：这里的addUser方法，我们加上了@Async注解(这里的注解为：io.mykit.async.spring.annotation.Async)  
  
到此，我们的准备工作完成。
    
接下来，就是实际测试的类型代码：  
 
## 框架功能演示
### 1、添加@Async注解
```
@Async
public User addUser(User user) {
    logger.info("正在添加用户{}", user.getName());
    return user;
}
```
### 2、测试异步执行调用
```
@Test
public void testAsyncAnnotation() {
    User user1 = userService.addUser(new User(34, "李一"));
    User user2 = userService.addUser(new User(35, "李二"));
    logger.info("异步任务已执行");
    logger.info("执行结果  任务1：{}  任务2：{}", user1.getName(), user2.getName());
}
```

### 3、测试编程式异步
```
@Test
public void testAsyncSaveUser() {
    User user = new User();
    user.setName("张三");
    user.setAge(18);

    UserService service = AsyncTemplate.buildProxy(this.userService, 1000);
    service.addUser(user);
    logger.info("调用结束");
}
```

### 4、测试异步事件编程
```
@Test
public void testAsyncTemplate() {

    AsyncTemplate.submit(new AsyncCallable<User>() {

        @Override
        public User doAsync() {
            return teacherService.addTeacher(new User(12, "李三"));
        }
    }, new AsyncFutureCallback<User>() {
        @Override
        public void onSuccess(User user) {
            logger.info("添加用户成功：{}", user.getName());
        }

        @Override
        public void onFailure(Throwable t) {
            logger.info("添加用户失败：{}", t);
        }
    });

    logger.info("调用结束");
}
```
## 提示：
具体测试用例请参见mykit-async-spring-test工程
