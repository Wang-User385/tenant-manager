FROM maven:3.8-eclipse-temurin-8-alpine AS build
WORKDIR /app
COPY pom.xml .
# 直接打包，同时下载依赖，更稳定
RUN mvn clean package -DskipTests

FROM eclipse-temurin:8-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/tenant-manager-0.0.1-SNAPSHOT.jar app.jar

# 修改点：使用 sh -c 启动，确保能读取 Render 的 JAVA_OPTS 环境变量
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
