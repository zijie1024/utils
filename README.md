<div align="center">
  <h1>🛠️ Utils Java Common Development Toolkit</h1>
  <p>
    <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
    <img src="https://img.shields.io/badge/Spring%20Boot-4.0.1-green" alt="Spring Boot">
    <img src="https://img.shields.io/badge/License-MIT-blue" alt="License">
    <img src="https://img.shields.io/badge/Build-Passing-brightgreen" alt="Build">
  </p>
</div>

This repository is a collection of common utility libraries built on Spring Boot 4.0.1 and Java 21, managed using Maven. The project aims to document and consolidate high-frequency technical solutions accumulated during daily development, covering various enhanced components from basic utilities to distributed middleware.

## 🧩 Common Module

The `com` module encapsulates general-purpose utilities. Tools that are not substantial enough to form a standalone module or do not logically fit into other modules are grouped here, including `JsonUtil`, etc.

`JsonUtil` acts as a wrapper for Jackson, abstracting away underlying checked exceptions, simplifying calling code, and providing unified serialization and deserialization support for the business layer.

## 📨 MQ Message Queue Module

The `mq` module contains various utilities built around message queues.

The `rabbit` package implements full-link reliable message delivery from producers to exchanges and down to queues, based on RabbitMQ's `ConfirmCallback` and `ReturnsCallback` mechanisms. It supports delayed messages, failure retries, message recalls, and other features.

## ⚡ Redis Cache Module

The `redis` module contains various utilities built around Redis, reducing the implementation cost of complex caching logic through highly abstracted annotations and utility classes.

The `limiter` package provides a high-performance distributed rate limiter based on Redis and Lua. Using polymorphic annotations and the Strategy pattern, it supports Fixed Window, Sliding Window, Token Bucket, and Leaky Bucket algorithms. It enables fine-grained traffic control by global API or individual IP to handle traffic spikes and ensure system availability.

The `idempotent` package provides an idempotency validation component based on AOP and Redis, accurately intercepting duplicate requests by dynamically extracting pre-generated tokens and leveraging Redis's atomic DEL operations, while incorporating a built-in exception rollback and compensation mechanism to gracefully support client retries.

The `lock` package provides an annotation-driven distributed lock component based on AOP and Redisson, featuring dynamic Key resolution via SpEL and support for multiple lock types through the factory pattern, delivering non-invasive distributed locking capabilities for business logic.

The `cache` package implements distributed caching via AOP + Redis, providing the `@ICache` annotation for quick integration of caching capabilities. It adopts the Cache Aside pattern, integrates Redisson distributed locks to prevent cache breakdown, and supports caching null values to mitigate cache penetration.

The `bloom` package provides management capabilities for Bloom Filters, encapsulating their initialization and maintenance logic.

## 🛡️ Code Verification Service Module

The `code` module builds a comprehensive verification service system, covering three forms: image CAPTCHAs, message OTPs (One-Time Passwords), and behavioral CAPTCHAs.

**Image CAPTCHAs** are implemented based on the EasyCaptcha component. It supports flexible switching between PNG and GIF formats and allows developers to customize character types, such as pure digits, arithmetic formulas, or Chinese characters.

**Message OTPs** are deeply integrated with Aliyun SMS services and QQ Mail SMTP services. Through the asynchronous sending interface provided by `MsgCodeManager`, the sending logic is decoupled from the main business thread using a thread pool. This effectively reduces blocking risks and improves system response performance.

**Behavioral CAPTCHAs** connect with Aliyun's Intelligent Verification service. The `BehaviorCodeManager` encapsulates the backend validation logic for behavioral verifications like slider puzzles, providing standard security protection for high-risk business scenarios.

## 📑 File Processing Module

The `file` module contains utilities related to file processing.

The `easyexcel` package acts as a wrapper for Alibaba EasyExcel. By implementing the `ReadListener` interface and utilizing the Template Method pattern, it extracts and unifies file import and export logic, employing stream reading and supporting batch processing.

## ☁️ OSS Object Storage Module

The `oss` module abstracts the differences between underlying storage platforms to achieve unified management of multi-cloud storage. `OssManager`, serving as the unified business entry point, supports flexible switching among various cloud storage providers, reducing the coupling between business code and specific storage vendors.

The `minio` package encapsulates standard file operation logic based on the MinIO SDK, focusing on file uploading and generating pre-authorized access links. It ensures the security of private storage resources through short-term signed URLs.

The `qiniu` package integrates Qiniu Cloud Object Storage Service, providing efficient file upload channels and the ability to issue download credentials for private spaces. It offers a mature solution for resource management in public cloud environments.