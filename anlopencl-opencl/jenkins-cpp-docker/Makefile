SHELL := /bin/bash
BASE_DIR := .

.PHONY: readme
readme: ##@targets Updates the README.textile from the README.md
	pandoc -f textile -t markdown_mmd -o README.md README.textile

.PHONY: build
build: ##@targets Builds the docker image.
	$(MAKE) -C image build

.PHONY: clean
clean: ##@targets Removes the local docker image.
	$(MAKE) -C image clean

.PHONY: deploy
deploy: ##@targets Deploys the docker image to the repository.
	$(MAKE) -C image deploy

.PHONY: test-up-root
test-up-root: ##@targets Starts the docker container with user root.
	$(MAKE) -C image build
	$(MAKE) -C test test-up-root
	docker ps

.PHONY: test-down-root
test-down-root: ##@targets Stops the docker container with user root.
	$(MAKE) -C test test-down-root

.PHONY: test-up-jenkins
test-up-jenkins: ##@targets Starts the docker container with user jenkins.
	$(MAKE) -C image build
	$(MAKE) -C test test-up-jenkins
	docker ps

.PHONY: test-down-jenkins
test-down-jenkins: ##@targets Stops the docker container with user jenkins.
	$(MAKE) -C test test-down-jenkins

include $(BASE_DIR)/utils/Makefile.help
