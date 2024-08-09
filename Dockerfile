FROM eclipse-temurin:17-jdk-alpine

# Timezone 설정을 위해 tzdata 패키지 설치
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && apk del tzdata

# 애플리케이션 JAR 복사
COPY ./goodsending/build/libs/*SNAPSHOT.jar project.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "project.jar"]
