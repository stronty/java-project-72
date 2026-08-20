# Стили Tailwind собираются в отдельном образе с Node: они не хранятся в репозитории
# как артефакт, а генерируются перед сборкой приложения.
FROM node:22 AS styles

WORKDIR /app

COPY app/package.json app/package-lock.json app/tailwind.css ./

RUN npm install

# Шаблоны нужны Tailwind, чтобы найти все используемые классы.
COPY app/src/main/resources/templates src/main/resources/templates

RUN npx @tailwindcss/cli -i tailwind.css -o src/main/resources/static/main.css --minify

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

# Подкладываем собранные стили в исходники перед сборкой fat-jar.
COPY --from=styles /app/src/main/resources/static/main.css src/main/resources/static/main.css

# Собираем fat-jar (плагин shadow), в который упакованы все классы и библиотеки.
RUN ./gradlew --no-daemon shadowJar

# Jte компилирует шаблоны на лету через javax.tools (JavaClassCompiler),
# поэтому в финальном образе нужен полный JDK с javac, а не только JRE.
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/app-1.0-SNAPSHOT-all.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 7070

# Render задаёт PORT через переменную окружения, приложение читает его в main().
CMD ["java", "-jar", "app.jar"]