# Сборка происходит в образе с JDK, а запуск — в лёгком образе с JRE.
# Render.com собирает сервис по Dockerfile (java-проекты на Render не имеют
# нативного Java-рантайма), поэтому окружение с Java и Gradle должно быть
# полностью внутри образа.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Сначала копируем только конфигурацию сборки, чтобы слой с зависимостями
# кэшировался: при изменении исходников зависимости не перекачиваются заново.
COPY app/gradlew .
COPY app/gradle gradle
COPY app/settings.gradle.kts .
COPY app/build.gradle.kts .
COPY app/gradle.properties .
COPY app/config config

RUN ./gradlew --no-daemon dependencies

COPY app/src src

# Собираем fat-jar (плагин shadow), в который упакованы все классы и библиотеки.
RUN ./gradlew --no-daemon shadowJar

# Финальный образ только с рантаймом — без компилятора и средств сборки.
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/app-1.0-SNAPSHOT-all.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 7070

# Render задаёт PORT через переменную окружения, приложение читает его в main().
CMD ["java", "-jar", "app.jar"]