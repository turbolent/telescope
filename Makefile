.PHONY: compile test test-changes clean

compile:
	./pants compile src::

test:
	./pants test tests::

test-changes:
	./pants --changed-parent=HEAD test

clean:
	./pants clean-all
