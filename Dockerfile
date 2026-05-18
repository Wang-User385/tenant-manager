# 第一阶段：使用标准版 Maven 镜像（非 Alpine），网络更稳定
FROM maven:3.8-eclipse-temurin-8 AS build
WORKDIR /app

# 1. 先复制 pom.xml
COPY pom.xml .

# 2. 打包并下载依赖 (-U 参数强制更新，防止缓存导致的下载失败)
RUN mvn clean package -DskipTests -U

# 第二阶段：运行环境依然用轻量版 Alpine
FROM eclipse-temurin:8-jdk-alpine
WORKDIR /app

# 3. 从第一阶段复制打好的包
COPY --from=build /app/target/tenant-manager-0.0.1-SNAPSHOT.jar app.jar

# 4. 启动命令（支持环境变量）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
