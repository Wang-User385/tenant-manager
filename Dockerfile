# 第一阶段：构建
FROM maven:3.8-eclipse-temurin-8 AS build
WORKDIR /app
# 1. 复制 pom.xml 下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline
# 2. 复制源代码（必须有这一步！）
COPY src ./src
# 3. 打包
RUN mvn clean package -DskipTests -U

# 第二阶段：运行
FROM eclipse-temurin:8-jdk-alpine
WORKDIR /app
# 4. 复制 JAR 包
COPY --from=build /app/target/tenant-manager-0.0.1-SNAPSHOT.jar app.jar
# 5. 启动（支持环境变量）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
