FROM node:22 AS styles

WORKDIR /app

COPY app/package.json app/package-lock.json app/tailwind.css ./

RUN npm install

COPY app/src/main/resources/templates src/main/resources/templates

RUN npx @tailwindcss/cli -i tailwind.css -o src/main/resources/static/main.css --minify

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY app/gradlew .
COPY app/gradle gradle
COPY app/settings.gradle.kts .
COPY app/build.gradle.kts .
COPY app/gradle.properties .
COPY app/config config

RUN ./gradlew --no-daemon dependencies

COPY app/src src

COPY --from=styles /app/src/main/resources/static/main.css src/main/resources/static/main.css

RUN ./gradlew --no-daemon shadowJar

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/app-1.0-SNAPSHOT-all.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 7070

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
