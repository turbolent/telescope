.PHONY: compile test integration-test test-changes integration-test-changes clean

compile:
	./pants compile src::

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
