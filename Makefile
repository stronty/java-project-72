.PHONY: setup sonar test

setup:
	$(MAKE) -C app setup

sonar:
	$(MAKE) -C app sonar

test:
	$(MAKE) -C app test
