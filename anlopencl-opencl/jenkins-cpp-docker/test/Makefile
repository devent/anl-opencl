SHELL := /bin/bash
BASE_DIR := ..
DOCKER_INFO_FILE := $(BASE_DIR)/image/docker.yaml
REPOSITORY := $(shell yq e ".repository" $(DOCKER_INFO_FILE))
TYPE := $(shell yq e ".type" $(DOCKER_INFO_FILE))
VERSION := $(shell if [[ -n "$(TYPE)" ]]; then echo "$(TYPE)-`yq e '.version' $(DOCKER_INFO_FILE)`"; else echo "`yq e '.version' $(DOCKER_INFO_FILE)`" ; fi)
NAME := $(shell yq e ".name" $(DOCKER_INFO_FILE))
.DEFAULT_GOAL := build
YAML := test.yaml

.PHONY: test-up-root
test-up-root: ##@targets Starts the docker container with user root.
	@bash -c "\
	REPOSITORY=$(REPOSITORY) \
	NAME=$(NAME) \
	VERSION=$(VERSION) \
	docker-compose -f docker-compose-root.yaml up -d \
	"

.PHONY: test-down-root
test-down-root: ##@targets Stops the docker container with user root.
	@bash -c "\
	REPOSITORY=$(REPOSITORY) \
	NAME=$(NAME) \
	VERSION=$(VERSION) \
	docker-compose -f docker-compose-root.yaml down \
	"

.PHONY: test-up-jenkins
test-up-jenkins: ##@targets Starts the docker container with user jenkins.
	@bash -c "\
	REPOSITORY=$(REPOSITORY) \
	NAME=$(NAME) \
	VERSION=$(VERSION) \
	docker-compose -f docker-compose-jenkins.yaml up -d \
	"

.PHONY: test-down-jenkins
test-down-jenkins: ##@targets Stops the docker container with user jenkins.
	@bash -c "\
	REPOSITORY=$(REPOSITORY) \
	NAME=$(NAME) \
	VERSION=$(VERSION) \
	docker-compose -f docker-compose-jenkins.yaml down \
	"

include $(BASE_DIR)/utils/Makefile.help
include $(BASE_DIR)/utils/Makefile.functions
include $(BASE_DIR)/utils/Makefile.image
