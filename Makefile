# weaves

## Just a reminder how to generate TAGS

all: TAGS

clean:
	$(RM) TAGS

TAGS:
	ctags -e -o $@ -R src/main misc
