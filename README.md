# AndroidEasyTools

![Jietu20191208-193301](https://tva1.sinaimg.cn/large/006tNbRwly1g9pjv5c1byj30b40bbdg8.jpg)

> 通用代码解决方案，一直开发一直爽是我们追求的理念。

在日常开发中，我们总会被很多需求折腾，遇到有些问题，就得寻找相应的解决方案，可是网上的方案五花八门，难道就没有一个真正好用的解决方案吗？

Android-EasyTools正是解决我们日常开发难题，快乐编程，不断汇总常见工具类，并进行简单封装，让使用起来更加简单易用。

[![](https://jitpack.io/v/Blue-knife/Android-EasyTools.svg)](https://jitpack.io/#Blue-knife/Android-EasyTools)



### 使用方式：

#### Gradle

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

#### 导入你需要的模块	

```
def version = '1.0'
```

| 组件                                                         | 功能           |
| ------------------------------------------------------------ | -------------- |
| implementation 'com.github.Blue-knife.Android-EasyTools:bullet_ktx:$version' | ktx扩展相关    |
| implementation 'com.github.Blue-knife.Android-EasyTools:bullet_ui:$version' | 自定义view相关 |
| implementation 'com.github.Blue-knife.Android-EasyTools:bullet_base:$version' | 通用业务组件   |
| ...                                                          | ...            |



### 具体组件介绍

#### bullet_ktx

ktx扩展方法与常用独立工具(即不包含第三方组件)，利用Kt特性提高开发效率。暂时未提供目录，具体查看代码；

#### bullet_ui

一些项目中会用到的自定义view与 ui相关；

#### bullet_base

通用工具合集，包含常用的各种工具，某些工具需要搭配其他三方库一起使用，具体暂时查看代码；



#### component

**作为独立组件合集，有些工具，如果将其定位为一个简单的类，或许太重，我们更希望的是一些项目中快速开发的业务工具参与进来，我们希望这个组件合集可以给你参考作用，简易而不庞大，便于快速理解及使用。**





目前通用的解决方案有：

- [利用 Android DownloadManager 进行文件下载;](https://blog.csdn.net/petterp/article/details/102762261)
- [使用Glide、rebound实现3D层叠式卡片图片展示;](https://blog.csdn.net/duihuapixiu/article/details/102795767)
- [基于FragmentDialog 封装高效 dialog ，可直接使用，可继承使用，具体使用方式见 源码请查看 dialog 包;](https://blog.csdn.net/baidu_40389775/article/details/102470687)
- 一个符合阿里编码规范的全局线程池。
- Android流式布局 FlowLayout。
- Android状态栏配置，系统OS获取工具。
- [一键式打开相机，相册，调用系统裁剪库，扫描二维码，生成二维码](https://github.com/Blue-knife/Android-EasyTools/blob/master/app/src/main/java/com/business/tools/camera/CameraActivity.java)
- [自定义TextView。指定一个或多个字的颜色，可批量设置](https://blog.csdn.net/baidu_40389775/article/details/102622214)
- [JobService 的使用 Demo](https://github.com/Blue-knife/Android-EasyTools/tree/master/app/src/main/java/com/business/tools/service)
- [新增一键式下载](https://blog.csdn.net/baidu_40389775/article/details/104911075)
- [简洁的通知栏工具]
- [基于Dialog 封装的一键式弹框，可满足日常需求](https://github.com/Blue-knife/Android-EasyTools/blob/master/app/src/main/java/com/business/tools/test/DialogActivity.kt)
- [一键式换肤框架，可实现深度定制需求](https://github.com/Blue-knife/Android-EasyTools/blob/master/bullet_core/src/main/java/com/example/core/base/BaseSkinActivity.kt) 
- [图片选择器，适配 Q](https://github.com/Blue-knife/Android-EasyTools/blob/master/app/src/main/java/com/business/tools/test/selectimage/UpLoadPhotoActivity.kt)
- [自定义 MaterialEditText](https://github.com/LvKang-insist/Android-EasyTools/blob/master/bullet_ui/src/main/java/com/example/ui/customView/MaterialEditText.kt)
- [自定义通用式垂直轮播控件，使用简单，对生命周期进行处理](https://github.com/LvKang-insist/Android-EasyTools/blob/master/bullet_ui/src/main/java/com/example/ui/customView/CarouselView.java)
- [自定义时间轴](https://blog.csdn.net/baidu_40389775/article/details/107694125)

我们非常欢迎你的PR，一切为了更快的效率。
