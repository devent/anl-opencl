SHELL := /bin/bash
BASE_DIR := ..
DOCKER_INFO_FILE := docker.yaml
REPOSITORY := $(shell yq e ".repository" $(DOCKER_INFO_FILE))
TYPE := $(shell yq e ".type" $(DOCKER_INFO_FILE))
VERSION := $(shell if [[ -n "$(TYPE)" ]]; then echo "$(TYPE)-`yq e '.version' $(DOCKER_INFO_FILE)`"; else echo "`yq e '.version' $(DOCKER_INFO_FILE)`" ; fi)
NAME := $(shell yq e ".name" $(DOCKER_INFO_FILE))
.DEFAULT_GOAL := build

.PHONY: build
build: _build ##@targets Builds the docker image.

.PHONY: clean
clean: _clean ##@targets Removes the local docker image.

.PHONY: deploy
deploy: _deploy ##@targets Deploys the docker image to the repository.

include $(BASE_DIR)/utils/Makefile.help
include $(BASE_DIR)/utils/Makefile.functions
include $(BASE_DIR)/utils/Makefile.image
