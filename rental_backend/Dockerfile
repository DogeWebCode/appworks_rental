# 第一階段：使用 Maven 編譯應用程式
FROM maven:3.8.5 AS builder

# 設定工作目錄
WORKDIR /app

# 複製專案的 pom.xml 和 source code
COPY pom.xml .
COPY src ./src

# 使用 Maven 進行編譯與打包
RUN mvn clean package -DskipTests

# 第二階段：使用較輕量的 Alpine JRE 映像
FROM eclipse-temurin:17-jre-alpine

# 設定工作目錄
WORKDIR /app

EXPOSE 8080

# 複製從第一階段構建出來的 jar 文件
COPY --from=builder /app/target/rental_backend-0.0.1-SNAPSHOT.jar .
COPY application.properties /app/application.properties

# 執行應用程式
CMD ["java", "-jar", "rental_backend-0.0.1-SNAPSHOT.jar"]
