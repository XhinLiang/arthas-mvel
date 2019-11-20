![arthas](static/arthas-mvel.png)

[![Build Status](https://travis-ci.org/alibaba/arthas.svg?branch=master)](https://travis-ci.org/alibaba/arthas) [![codecov](https://codecov.io/gh/alibaba/arthas/branch/master/graph/badge.svg)](https://codecov.io/gh/alibaba/arthas) [![maven](https://img.shields.io/maven-central/v/com.taobao.arthas/arthas-packaging.svg)](https://search.maven.org/search?q=g:com.taobao.arthas) ![license](https://img.shields.io/github/license/alibaba/arthas.svg) [![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/alibaba/arthas.svg)](http://isitmaintained.com/project/alibaba/arthas "Average time to resolve an issue") [![Percentage of issues still open](http://isitmaintained.com/badge/open/alibaba/arthas.svg)](http://isitmaintained.com/project/alibaba/arthas "Percentage of issues still open")

`Arthas-MVEL` use [MVEL](http://mvel.documentnode.com) as first-class command parser and support all of the features of [Arthas](https://github.com/alibaba/arthas).

[MVEL](http://mvel.documentnode.com) is a expression language just like [OGNL](https://commons.apache.org/proper/commons-ognl/language-guide.html) but supports more features such as `loop` and `function`.

## Installation

<<<<<<< HEAD
Install `Arthas-MVEL` just like `arthas`.

```bash
wget statics.xhinliang.com/arthas-boot.jar
=======
[中文说明/Chinese Documentation](README_CN.md)

### Background

Often times, the production system network is inaccessible from the local development environment. If issues are encountered in production systems, it is impossible to use IDEs to debug the application remotely. More importantly, debugging in production environment is unacceptable, as it will suspend all the threads, resulting in the suspension of business services. 

Developers could always try to reproduce the same issue on the test/staging environment. However, this is tricky as some issues cannot be reproduced easily on a different environment, or even disappear once restarted. 

And if you're thinking of adding some logs to your code to help troubleshoot the issue, you will have to go through the following lifecycle; test, staging, and then to production. Time is money! This approach is inefficient! Besides, the issue may not be reproducible once the JVM is restarted, as described above.

Arthas was built to solve these issues. A developer can troubleshoot your production issues on-the-fly. No JVM restart, no additional code changes. Arthas works as an observer, which will never suspend your existing threads.

### Key features

* Check whether a class is loaded, or where the class is being loaded. (Useful for troubleshooting jar file conflicts)
* Decompile a class to ensure the code is running as expected.
* View classloader statistics, e.g. the number of classloaders, the number of classes loaded per classloader, the classloader hierarchy, possible classloader leaks, etc.
* View the method invocation details, e.g. method parameter, return object, thrown exception, and etc.
* Check the stack trace of specified method invocation. This is useful when a developers wants to know the caller of the said method.
* Trace the method invocation to find slow sub-invocations.
* Monitor method invocation statistics, e.g. qps, rt, success rate and etc.
* Monitor system metrics, thread states and cpu usage, gc statistics, and etc.
* Supports command line interactive mode, with auto-complete feature enabled.
* Supports telnet and websocket, which enables both local and remote diagnostics with command line and browsers.
* Supports JDK 6+.
* Supports Linux/Mac/Windows.


### Online Tutorials(Recommend)

* [Arthas Basics](https://alibaba.github.io/arthas/arthas-tutorials?language=en&id=arthas-basics)
* [Arthas Advanced](https://alibaba.github.io/arthas/arthas-tutorials?language=en&id=arthas-advanced)

### Quick start

#### Use `arthas-boot`(Recommend)

Download`arthas-boot.jar`，Start with `java` command:

```bash
curl -O https://alibaba.github.io/arthas/arthas-boot.jar
>>>>>>> 653b21760a83256a1e157e97a7693a65fc9d8488
java -jar arthas-boot.jar
```

## Usage

Install `Arthas-MVEL` just like `arthas`.

```java

// support all commands of arthas
$ classloader
 name                                       numberOfInstances  loadedCountTotal
 sun.misc.Launcher$AppClassLoader           1                  28077
 com.taobao.arthas.agent.ArthasClassloader  3                  3847
 BootstrapClassLoader                       1                  3588
 sun.reflect.DelegatingClassLoader          320                320
 sun.misc.Launcher$ExtClassLoader           1                  10
 sun.reflect.misc.MethodUtil                1                  1
Affect(row-cnt:6) cost in 45 ms.

$ version
3.1.2.20190805151842
$ keymap
 Shortcut                         Description                     Name
 // ...

// some mvel expressions
$ abc = 123
@Integer[123]
$ abc = abc * 2
@Integer[246]

// call static function
$ java.lang.System.currentTimeMillis()
@Long[1564990990861]

// import class
$ import java.lang.System
@Class[
    ANNOTATION=@Integer[8192],
    ENUM=@Integer[16384],
    SYNTHETIC=@Integer[4096],
    cachedConstructor=null,
    // more

// call static function without FQCN
$ System.currentTimeMillis()
@Long[1564991009477]

// call function
$ joiner = com.google.common.base.Joiner.on("_")
@Joiner[
    separator=@String[_],
]
$ joiner.join("abc", "efg", "123")
@String[abc_efg_123]

// loop
$ count = 1
@Integer[1]
$ for (int i =0; i < 100; i++) { count = count + 1;}
null
$ count
@Integer[101]

// define a function
$ incrByTime = def (raw, time) { for (int i =0; i < time; i++) { raw = raw + 1;}; raw; }
@PrototypalFunctionInstance[
    resolverFactory=@MapVariableResolverFactory[
        variables=@HashMap[isEmpty=true;size=0],
    ],
]

// call function
$ incrByTime(1, 50)
@Integer[51]

// define a function to load bean
$ getBeanByName = def (name) { com.some.static.function.you.can.getBean(name) }
@PrototypalFunctionInstance[
    resolverFactory=@MapVariableResolverFactory[
        variables=@HashMap[isEmpty=true;size=0],
    ],
]

// call function of bean
// bean "userService" will be loaded by the function "getBeanByName"
$ userService.getById(123L)
@UserModel[
    serialVersionUID=@Long[-8752733010881684427],
    userId=@Long[123L],
    userName=@String[testUserName],
    // ...
```
<<<<<<< HEAD
=======

#### Web Console

* https://alibaba.github.io/arthas/en/web-console

![web console](site/src/site/sphinx/_static/web-console-local.png)


### Known Users

Welcome to register the company name in this issue: https://github.com/alibaba/arthas/issues/111 (in order of registration)

![Alibaba](static/alibaba.png)
![Alipay](static/alipay.png)
![Aliyun](static/aliyun.png)
![Taobao](static/taobao.png)
![Tmall](static/tmall.png)
![微医](static/weiyi.png)
![卓越教育](static/zhuoyuejiaoyu.png)
![狐狸金服](static/hulijingfu.png)
![三体云](static/santiyun.png)
![证大文化](static/zhengdawenhua.png)
![连连支付](static/lianlianpay.png)
![Acmedcare+](static/acmedcare.png)
![好慷](static/homeking365_log.png)
![来电科技](static/laidian.png)
![四格互联](static/sigehulian.png)
![ICBC](static/icbc.png)
![陆鹰](static/luying.png)
![玩友时代](static/wangyoushidai.png)
![她社区](static/tashequ.png)
![龙腾出行](static/longtengchuxing.png)
![foscam](static/foscam.png)
![二维火](static/2dfire.png)
![lanxum](static/lanxum_com.png)
![纳里健康](static/ngarihealth.png)
![掌门1对1](static/zhangmen.png)
![offcn](static/offcn.png)
![sia](static/sia.png)
![振安资产](static/zhenganzichang.png)
![菠萝](static/bolo.png)
![中通快递](static/zto.png)
![光点科技](static/guangdian.png)
![广州工程技术职业学院](static/gzvtc.jpg)
![mstar](static/mstar.png)
![xwbank](static/xwbank.png)
![imexue](static/imexue.png)
![keking](static/keking.png)
![secoo](static/secoo.jpg)
![viax](static/viax.png)
![yanedu](static/yanedu.png)
![duia](static/duia.png)
![哈啰出行](static/hellobike.png)
![hollycrm](static/hollycrm.png)
![citycloud](static/citycloud.jpg)
![yidianzixun](static/yidianzixun.png)
![神州租车](static/zuche.png)
![天眼查](static/tianyancha.png)
![商脉云](static/anjianyun.png)
![三新文化](static/sanxinbook.png)
![雪球财经](static/xueqiu.png)
![百安居](static/bthome.png)
![安心保险](static/95303.png)
![杭州源诚科技](static/hzyc.png)
![91moxie](static/91moxie.png)
![智慧开源](static/wisdom.png)
![富佳科技](static/fujias.png)
![鼎尖软件](static/dingjiansoft.png)
![广通软件](static/broada.png)
![九鼎瑞信](static/evercreative.jpg)
![小米有品](static/xiaomiyoupin.png)
![欧冶云商](static/ouyeel.png)
![投投科技](static/toutou.png)
![饿了么](static/ele.png)
![58同城](static/58.png)
![上海浪沙](static/runsa.png)
![符律科技](static/fhldtech.png)
![顺丰科技](static/sf.png)
![新致软件](static/newtouch.png)
![北京华宇信息](static/thunisoft.png)
![太平洋保险](static/cpic.png)
![旅享网络](static/risingch.png)
![水滴互联](static/shuidihuzhu.png)
![贝壳找房](static/ke.png)
![嘟嘟牛](static/dodonew.png)
![云幂信息](static/yunmixinxi.png)
![随手科技](static/sui.png)
![妈妈去哪儿](static/mamaqunaer.jpg)
![云实信息](static/realscloud.png)
![BBD数联铭品](static/bbdservice.png)
![伙伴集团](static/zhaoshang800.png)
![数梦工场](static/dtdream.png)
![安恒信息](static/dbappsecurity.png)
![亚信科技](static/asiainfo.png)
![云舒写](static/yunshuxie.png)
![微住](static/iweizhu.png)
![月亮小屋](static/bluemoon.png)
![大搜车](static/souche.png)
![今日图书](static/jinritushu.png)
![竹间智能](static/emotibot.png)
![数字认证](static/bjca.png)
![360金融](static/360jinrong.png)
![安居客](static/anjuke.jpg)
![qunar](static/qunar.png)
![ctrip](static/ctrip.png)
### Derivative Projects

* [Bistoury: A project that integrates Arthas](https://github.com/qunarcorp/bistoury)
* [A fork of arthas using MVEL](https://github.com/XhinLiang/arthas)

### Credit

#### Contributors

This project exists thanks to all the people who contribute.

<a href="https://github.com/alibaba/arthas/graphs/contributors"><img src="https://opencollective.com/arthas/contributors.svg?width=890&button=false" /></a>

#### Projects

* [greys-anatomy](https://github.com/oldmanpushcart/greys-anatomy): The Arthas code base has derived from Greys, we thank for the excellent work done by Greys.
* [termd](https://github.com/termd/termd): Arthas's terminal implementation is based on termd, an open source library for writing terminal applications in Java.
* [crash](https://github.com/crashub/crash): Arthas's text based user interface rendering is based on codes extracted from [here](https://github.com/crashub/crash/tree/1.3.2/shell)
* [cli](https://github.com/eclipse-vertx/vert.x/tree/master/src/main/java/io/vertx/core/cli): Arthas's command line interface implementation is based on cli, open sourced by vert.x
* [compiler](https://github.com/skalogs/SkaETL/tree/master/compiler) Arthas's memory compiler.
* [Apache Commons Net](https://commons.apache.org/proper/commons-net/) Arthas's telnet client.
>>>>>>> 653b21760a83256a1e157e97a7693a65fc9d8488
