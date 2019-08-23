# 简介

一个极为巧合的情况下接触到了JFinal，但是由于本人的技术栈一直以来都是以SpringBoot为主，所以还是希望能够将JFinal集成到SpringBoot中去使用，毕竟Spring的生态还是无比健全的。当前的一些教程，基本都是比较浅的集成方式，SpringBoot与JFinal集成到一起，结果还是各是各的，互不相通，故而此starter的产生。

该项目主要利用SpringBoot的自动配置来将JFinal与SpringBoot深度集成，让SpringBoot与JFinal从此不分彼此。

- 源码地址
  - Github:https://github.com/ArtIsLong/jfinal-spring-boot-starter.git
  - Gitee:https://gitee.com/artislong/jfinal-spring-boot-starter.git
- 我的博客:https://www.chenmin.info
- 简书:https://www.jianshu.com/u/46d989a94f20

**自制的小工具，欢迎使用和Star，如果使用过程中遇到问题，可以提出Issue，我会尽力完善该工具**

# 功能介绍

1. 提供`JfinalScan`注解，可扫描标识类加载Bean，如加载继承了Controller、实现了IPlugin的类到Spring中
2. 提供`RouterPath`注解，定义自定义Controller的路由controllerKey
3. 简化JFinal的数据源，ActiveRecordPlugin默认从Spring加载DataSource

# 框架基础版本

- SpringBoot：2.1.7
- JFinal：4.3
- cron4j：2.2.5
- caffeine：2.6.2

# 如何使用

## 添加依赖

pom.xml

~~~xml
<dependency>
  <groupId>com.github.artislong</groupId>
  <artifactId>jfinal-spring-boot-starter</artifactId>
  <version>1.0</version>
</dependency>
~~~

build.gradle

~~~groovy
compile 'com.github.artislong:jfinal-spring-boot-starter:1.0'
~~~

## 添加基础配置

```yaml
jfinal:
  # 配置数据库方言，不配置时，默认使用MySQL方言
  dialect: com.jfinal.plugin.activerecord.dialect.MysqlDialect
  # 自动生成的MappingKit，必须配
  kit-classes:
    - model._MappingKit
  # JFinal的SQL模板路径
  sql-templates: 
    - classpath:template/*.sql
  # 是否显示SQL
  show-sql: true
```

## 其他配置

~~~yaml
jfinal:
  dev-mode: true
  date-pattern: yyyy-MM-dd HH:mm:ss
  taskInfoMap:
    test01:
      cron: '*/5 * * * *'
      task: cn.hzsoftware.erp.work.cron.Test01
      daemon: true
      enable: true
~~~

更多配置请查看`JfinalProperties`类。

