.PHONY: compile run dist image test integration-test test-changes integration-test-changes clean

compile:
	./pants compile src::

run:
	./pants run src/scala/com/turbolent/questionServer:question-service

dist:
	./pants binary src/scala/com/turbolent/questionServer:question-service

image: dist
	docker build -t turbolent/telescope .

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
