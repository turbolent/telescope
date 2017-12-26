.PHONY: compile \
	run-api run-debugger \
	dist dist-api dist-debugger \
	images api-images api-images \
	push-images push-api-images push-debugger-images \
	api-image-reference debugger-image-reference \
	test integration-test \
	test-changes integration-test-changes \
	clean

API_IMAGE_NAME = turbolent/telescope-api
DEBUGGER_IMAGE_NAME = turbolent/telescope-debugger
IMAGE_VERSION = $(shell git log -n 1 --pretty=format:%h -- .)

compile:
	./pants compile src::

run-api:
	./pants run src/scala/com/turbolent/questionServer:question-service

run-debugger:
	./pants run src/js/debugger:debugger-module

dist: dist-api dist-debugger

dist-api:
	./pants binary src/scala/com/turbolent/questionServer:question-service

dist-debugger:
	./pants bundle src/js/debugger

images: api-images debugger-images

api-images: dist-api
	docker build -f Dockerfile.api -t $(API_IMAGE_NAME):latest -t $(API_IMAGE_NAME):$(IMAGE_VERSION) .

debugger-images: dist-debugger
	docker build -f Dockerfile.debugger -t $(DEBUGGER_IMAGE_NAME):latest -t $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION) .

push-images: images push-api-images push-debugger-images

push-api-images:
	docker push $(API_IMAGE_NAME):latest
	docker push $(API_IMAGE_NAME):$(IMAGE_VERSION)

push-debugger-images:
	docker push $(DEBUGGER_IMAGE_NAME):latest
	docker push $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION)

api-image-reference:
	@echo $(API_IMAGE_NAME):$(IMAGE_VERSION)

debugger-image-reference:
	@echo $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION)

test:
	./pants --tag='-integration' test tests:: src::

integration-test:
	./pants --tag='+integration' test tests:: src::

test-changes:
	./pants --tag='-integration' --changed-parent=HEAD test

integration-test-changes:
	./pants --tag='+integration' --changed-parent=HEAD test

clean:
	./pants clean-all
