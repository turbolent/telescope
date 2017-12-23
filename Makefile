.PHONY: compile run dist images push-images test integration-test test-changes integration-test-changes clean

IMAGE_NAME = turbolent/telescope
IMAGE_VERSION = $(shell git log -n 1 --pretty=format:%h -- .)

compile:
	./pants compile src::

run:
	./pants run src/scala/com/turbolent/questionServer:question-service

dist:
	./pants binary src/scala/com/turbolent/questionServer:question-service

images: dist
	docker build -t $(IMAGE_NAME):latest -t $(IMAGE_NAME):$(IMAGE_VERSION) .

push-images: images
	docker push $(IMAGE_NAME):latest
	docker push $(IMAGE_NAME):$(IMAGE_VERSION)

test:
	./pants --tag='-integration' test tests::

integration-test:
	./pants --tag='+integration' test tests::

test-changes:
	./pants --tag='-integration' --changed-parent=HEAD test

integration-test-changes:
	./pants --tag='+integration' --changed-parent=HEAD test

clean:
	./pants clean-all
