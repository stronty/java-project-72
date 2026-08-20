setup:
	npm install
	npx @tailwindcss/cli -i tailwind.css -o src/main/resources/static/main.css --minify
	./gradlew installDist

sonar:
	./gradlew build sonar --info

test:
	./gradlew test
