.PHONY: prepare compile \
	run-api run-debugger run-interface \
	dist dist-api dist-debugger dist-interface \
	images api-images debugger-images interface \
	push-images push-api-images push-debugger-images push-interface-images \
	api-image-reference debugger-image-reference interface-image-reference \
	test integration-test \
	test-changes integration-test-changes \
	clean

API_IMAGE_NAME = turbolent/telescope-api
DEBUGGER_IMAGE_NAME = turbolent/telescope-debugger
INTERFACE_IMAGE_NAME = turbolent/telescope-interface
IMAGE_VERSION = $(shell git log -n 1 --pretty=format:%h -- .)

prepare:
# workaround for pants, doesn't handle pom files, only jars (zip file), so replace with empty zip
	@mkdir -p .pants.d/ivy/jars/org.apache.jena/apache-jena-libs/poms
	@echo UEsFBgAAAAAAAAAAAAAAAAAAAAAAAA== | base64 -d > .pants.d/ivy/jars/org.apache.jena/apache-jena-libs/poms/apache-jena-libs-3.7.0.pom

compile: prepare
	./pants compile src::

run-api: prepare
	./pants run src/scala/com/turbolent/questionServer:question-service

run-debugger: prepare
	./pants run src/js/debugger:debugger-module

run-interface: prepare
	./pants run src/js/interface:interface-module

dist: dist-api dist-debugger

dist-api: prepare
	./pants binary src/scala/com/turbolent/questionServer:question-service

dist-debugger: prepare
	./pants bundle src/js/debugger

dist-interface: prepare
	./pants bundle src/js/interface

images: api-images debugger-images interface-images

api-images: dist-api
	docker build -f Dockerfile.api -t $(API_IMAGE_NAME):latest -t $(API_IMAGE_NAME):$(IMAGE_VERSION) .

debugger-images: dist-debugger
	docker build -f Dockerfile.debugger -t $(DEBUGGER_IMAGE_NAME):latest -t $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION) .

interface-images: dist-interface
	docker build -f Dockerfile.interface -t $(INTERFACE_IMAGE_NAME):latest -t $(INTERFACE_IMAGE_NAME):$(IMAGE_VERSION) .

push-images: images push-api-images push-debugger-images push-interface-images

push-api-images:
	docker push $(API_IMAGE_NAME):latest
	docker push $(API_IMAGE_NAME):$(IMAGE_VERSION)

push-debugger-images:
	docker push $(DEBUGGER_IMAGE_NAME):latest
	docker push $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION)

push-interface-images:
	docker push $(INTERFACE_IMAGE_NAME):latest
	docker push $(INTERFACE_IMAGE_NAME):$(IMAGE_VERSION)

api-image-reference:
	@echo $(API_IMAGE_NAME):$(IMAGE_VERSION)

debugger-image-reference:
	@echo $(DEBUGGER_IMAGE_NAME):$(IMAGE_VERSION)

interface-image-reference:
	@echo $(INTERFACE_IMAGE_NAME):$(IMAGE_VERSION)

test: prepare
	./pants --tag='-integration' test tests:: src::

integration-test: prepare
	./pants --tag='+integration' test tests:: src::

test-changes: prepare
	./pants --tag='-integration' --changed-parent=HEAD test

integration-test-changes: prepare
	./pants --tag='+integration' --changed-parent=HEAD test

clean:
	./pants clean-all
