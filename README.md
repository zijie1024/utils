<div align="center">
  <h1>🛠️ Utils Java 通用开发工具集</h1>
  <p>
    <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
    <img src="https://img.shields.io/badge/Spring%20Boot-4.0.1-green" alt="Spring Boot">
    <img src="https://img.shields.io/badge/License-MIT-blue" alt="License">
    <img src="https://img.shields.io/badge/Build-Passing-brightgreen" alt="Build">
  </p>
</div>

本仓库是基于 Spring Boot 4.0.1 和 Java 21 构建的通用工具库集合，采用 Maven 进行项目管理。项目旨在记录和沉淀日常开发中积累的高频技术解决方案，涵盖了从基础工具到分布式中间件的各类增强组件。

## 🧩 Common 基础模块

`com` 模块包含通用小工具的封装，那些不足以单成一个模块或不好归于其他模块的工具都将归于此模块，包括 JsonUtil 等。

JsonUtil 对 Jackson 进行封装，屏蔽了底层受检异常，简化了调用代码，为上层业务提供统一的序列化和反序列化支持。

## 📨 MQ 消息队列模块

`mq` 模块包含围绕消息队列进行封装的各类工具。

`rabbit` 包基于 RabbitMQ 的 ConfirmCallback 和 ReturnsCallback 回调机制，实现了消息从生产者到交换机以及再到队列的全链路可靠投递，支持延迟消息、失败重试、消息撤回等功能。


## ⚡  Redis 缓存模块

`redis` 模块包含围绕 redis 进行封装的各类工具，通过高度抽象的注解与工具类降低复杂缓存逻辑的实现成本。

`cache` 包通过 AOP + Redis 实现分布式缓存，提供 @ICache 注解以便快速接入缓存能力。采用旁路缓存策略，集成 Redisson 分布式锁以防止缓存击穿，支持缓存空值以缓解缓存穿透。

`bloom` 包提供对布隆过滤器的管理能力，封装了过滤器的初始化与维护逻辑。

## 🛡️ Code 验证码服务模块

`code` 模块构建了全场景的验证码服务体系，覆盖了图形、消息及行为验证三种形态。

图形验证码基于 EasyCaptcha 组件实现，不仅支持 PNG 和 GIF 两种格式的灵活切换，还允许开发者自定义纯数字、算术公式或中文字符等多种字符类型。

消息验证码深度整合了阿里云短信服务与 QQ 邮箱 SMTP 服务，通过 MsgCodeManager 提供的异步发送接口，利用线程池将发送逻辑从业务主线程剥离，有效降低了阻塞风险并提升了系统响应性能。

行为验证码对接了阿里云智能验证服务，通过 BehaviorCodeManager 封装了滑块拼图等行为验证的后端校验逻辑，为高安全等级的业务场景提供了标准的防护支持。

## 📑 File 文件处理模块

`file` 模块包含文件处理相关的工具。

`easyexcel` 包对 Alibaba EasyExcel 进行封装，通过实现 ReadListener 接口，采用模板方法模式，抽取并统一了文件的导入导出逻辑，采用流式读取，支持批量处理。

## ☁️ OSS 对象存储模块

`oss` 模块屏蔽底层存储平台的差异，实现多云存储的统一管理。OssManager 作为统一业务入口，支持在多种云存储之间灵活切换，降低了业务代码与具体存储厂商的耦合度。

`minio` 包基于 MinIO SDK 封装了标准的文件操作逻辑，重点实现了文件上传与预授权访问链接生成功能，通过短期有效的签名 URL 保障了私有存储资源的安全性。

`qiniu` 包集成了七牛云对象存储服务，提供了高效的文件上传通道以及私有空间下载凭证的签发能力，为公有云环境下的资源管理提供了成熟的解决方案。