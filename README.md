![arthas](static/arthas-mvel.png)

[![Build Status](https://travis-ci.org/alibaba/arthas.svg?branch=master)](https://travis-ci.org/alibaba/arthas) [![codecov](https://codecov.io/gh/alibaba/arthas/branch/master/graph/badge.svg)](https://codecov.io/gh/alibaba/arthas) [![maven](https://img.shields.io/maven-central/v/com.taobao.arthas/arthas-packaging.svg)](https://search.maven.org/search?q=g:com.taobao.arthas) ![license](https://img.shields.io/github/license/alibaba/arthas.svg) [![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/alibaba/arthas.svg)](http://isitmaintained.com/project/alibaba/arthas "Average time to resolve an issue") [![Percentage of issues still open](http://isitmaintained.com/badge/open/alibaba/arthas.svg)](http://isitmaintained.com/project/alibaba/arthas "Percentage of issues still open")

`Arthas-MVEL` use [MVEL](http://mvel.documentnode.com) as first-class command parser and support all of the features of [Arthas](https://github.com/alibaba/arthas).

[MVEL](http://mvel.documentnode.com) is a expression language just like [OGNL](https://commons.apache.org/proper/commons-ognl/language-guide.html) but supports more features such as `loop` and `function`.

## Installation

Install `Arthas-MVEL` just like `arthas`.

```bash
wget https://github.com/XhinLiang/arthas-mvel/releases/download/latest/boot.jar
java -jar boot.jar
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
