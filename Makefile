# Приложение лежит во вложенной директории app, поэтому корневой Makefile
# делегирует цели через make -C (make сначала меняет директорию, а потом
# читает Makefile уже из неё). Это нужно для работы в Github Actions.
.PHONY: setup sonar

setup:
	$(MAKE) -C app setup

sonar:
	$(MAKE) -C app sonar