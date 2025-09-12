# 使用你已在本地拉取的镜像
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-slim

# 切换到 root 用户以获取安装权限，并安装网络诊断工具
# 安装完成后会切换回普通用户
USER root
RUN apt-get update && apt-get install -y curl iputils-ping && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 将 Maven 构建的 JAR 文件复制到容器的 /app 目录下
# 注意：这里的 JAR 文件名需要和你项目构建后的文件名一致
COPY target/welfare-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用程序运行的端口
EXPOSE 8080

# 设置容器启动时执行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]