# syntax=docker/dockerfile:1

##########################
# 1-BOSQICH: Build
##########################
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Avval faqat pom.xml ni ko'chiramiz -> dependency'lar alohida layerda cache bo'ladi.
# Kod o'zgarsa ham, pom.xml o'zgarmasa, Maven qayta dependency yuklamaydi.
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline -q

# Endi qolgan kodni ko'chiramiz va build qilamiz
COPY src ./src
RUN mvn -B clean package -DskipTests -q \
    && java -Djarmode=layertools -jar target/cicd-demo.jar extract --destination target/extracted

##########################
# 2-BOSQICH: Runtime
##########################
FROM eclipse-temurin:21-jre-alpine AS runtime

# Xavfsizlik: root emas, alohida non-root user ostida ishga tushiramiz
RUN addgroup -S spring && adduser -S spring -G spring

# curl - healthcheck uchun kerak (alpine'da yo'q, qo'shamiz)
RUN apk add --no-cache curl

WORKDIR /app

# Spring Boot layered jar bosqichlari (eng kam o'zgaradigandan eng ko'p o'zgaradigangacha)
COPY --from=build /workspace/target/extracted/dependencies/ ./
COPY --from=build /workspace/target/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/target/extracted/application/ ./

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM konteyner ichida xotira limitlarini avtomatik aniqlashi uchun
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
