# foodie mall
> 这是一个商城项目，有单体和微服务版，基于springboot，springcloud等目前常用的技术栈，主要是为了记录一个项目从单体到微服务发展的一个过程，方便自己学习和回顾。

Tomcat部署架构

![Tomcat部署架构](https://i.loli.net/2020/10/26/k4AEuhLyn6VSBCr.png)

Nginx部署架构
![Nginx部署架构.png](https://i.loli.net/2020/10/26/8C9yKHfwQZdnIXE.png)

> 通过LVS+Keepalived+Nginx实现高可用集群

# 安装与配置Redis

- 解压

    ```bash
    tar -zxvf redis-5.0.5.tar.gz
    ```

- 安装gcc编译环境

    ```bash
    yum install gcc-c++
    ```

- 进入redis-5.0.5目录，进行安装

    ```bash
    make && make install
    ```

- 复制redis.conf到/usr/local/redis中，配置redis.conf

    ```bash
    # 设置密码
    requirepass mz2020

    bind 0.0.0.0 

    daemonize yes
    ```

- 配置redis开机启动，在utils下，拷贝redis_init_script到/etc/init.d目录
- 配置redis.conf文件地址

    ```bash
    CONF="/usr/local/redis/redis.conf"
    ```

- redis_init_script中添加

    ```bash
    #chkconfig: 22345 10 90
    #description: Start and Stop redis
    ```

- 注册redis为开机启动

    ```bash
    chkconfig redis_init_script on
    ```