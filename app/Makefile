# Команду setup вызывает система проверки перед запуском тестов.
# Она собирает стили Tailwind и дистрибутив приложения.
setup:
	npm install
	npx @tailwindcss/cli -i tailwind.css -o src/main/resources/static/main.css --minify
	./gradlew installDist

sonar:
	./gradlew build sonar --info