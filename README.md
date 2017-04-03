CloudStorage_android
=================================== 
一个云计算比赛用的app开发项目<br />  
大概是第一次用安卓做开发项目<br />  
所以还有很懂地方不会的说<br />  
所以麻烦各位大佬加以指导了qwq<br />  

设计架构  
----------------------------------- 
系统功能是基于 OpenStack Swif实现的<br />  

1、 Swift 云存储云端服务：能够连接 OpenStack Swift 云存储，需要在 Android 端引入<br />
SDK，同样使用上面的 SDK（openstack-java-sdk_v1.1.jar）。目前 SDK 是成熟的工<br />
程，直接引入即可。实现的原理基于 HTTP 协议连接云存储 Swift Restful 服务。同<br />
样使用封装的 OpenStackClientService 进行异步网络操作。<br />

2、 登录：还原和登录各定义一个 Activity。Swift 账户管理使用 Keystone 完成，这里需<br />
要 Keystone 的访问 SDK，openstack-java-sdk 提供了对 OpenStack 各服务访问的接<br />
口，可以直接使用。<br />

3、 文件系统：目前 SDK 提供了对 Swift 云存储的操作，云存储不是一种文件系统(File<br />
System)，为了实现文件，基于 Swift 的 SDK 接口，封装模拟文件操作的类文件系<br />
统，命名为 OssFilesystem。<br />

4、 主界面：使用一个 Activity 来完成主界面，导航通过 NavigationView 控件实现、文<br />
件列表通过 Fragment 来实现。不同的分类操作不同，我们使用不同的 Fragment 实<br />
现，降低程序的复杂性。<br />

5、 所有文档：实现展示所有文件类别，采用 Fragment。<br />

6、 分类：实现文档、图片、视频分类的文件列表，采用 Fragment<br />

7、 回收站：实现回收站的文件列表，采用 Fragment。<br />

8、 文件上传：上传本地文件，实现一个本地存储文件导航选择窗口 Activity，完成文<br />
件选择和上传<br 

9、 工具类，包括文件操作，处理本地缓存，一个文件打开时，需要下载本地。<br />
了提供性能，对当前操作文件路径进行缓存，跟踪状态信息。<br />

<br />
