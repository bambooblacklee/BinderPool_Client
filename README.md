# BinderPool_Client
进程间通信，Binder连接池客户端

配合[Binder连接池远程服务端](https://github.com/bambooblacklee/BinderPool)  使用

根据任主席的《Android开发艺术探索》学习而来，对于书中在一个应用中开启多进程，实现的多进程通信演变转化为不同应用间的多进程通信。

## 主要修改如下
  1、按照按照任务划分，BinderPool应运行在client端中，将其复制到client端（server端的BinderPool可以删除），将IBinderPool.aidl、ICompute.aidl、ISecurityCenter.aidl三个AIDL文件复制到client端。
  
  2、微调BinderPool中的connectBindPoolService方法、改为隐式调用service。
  
  3、改造BinderService中的Binder，将BinderPool中应运行在server端中的BinderPoolImpl从BinderPool中转移到BinderPoolService中。BinderPool中的常量也同时移动到BinderPoolService中。

