# Copyright © 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
# 
# Copying and distribution of this file, with or without modification,
# are permitted in any medium without royalty provided the copyright
# notice and this notice are preserved.  This file is offered as-is,
# without any warranty.
# 
# [GNU All Permissive License]


PREFIX = /usr
BIN = /bin
DATA = /share
MISC = $(DATA)/misc
LICENSES = $(DATA)/licenses
INFO = $(DATA)/info
SH_SHEBANG = $(BIN)/sh

RANDOM_FILES = /dev/urandom /dev/random
DEFAULT_RANDOM = /dev/urandom
DICT_DIRS = /usr/share/dict:/usr/local/share/dict:~/share/dict

JAVA_OPTIMISE = -O

JAVAC = javac
JAR = jar
INSTALLED_PREFIX = /usr
INSTALLED_LIB = /lib
INSTALLED_JARS = $(INSTALLED_PREFIX)$(INSTALLED_LIB)

JAVADIRS = -s "src" -d "bin" -cp "src:$(INSTALLED_JARS)/ArgParser.jar"
JAVAFLAGS = -Xlint $(JAVA_OPTIMISE)
JAVA_FLAGS = $(JAVADIRS) $(JAVAFLAGS)

CLASSES = Correctpony



.PHONY: all
all: cmd shell

.PHONY: cmd
cmd: launcher manifest java jar

.PHONY:
launcher: bin/correctpony

.PHONY:
manifest: bin/META-INF/MANIFEST.MF
bin/META-INF/MANIFEST.MF: META-INF/MANIFEST.MF
	@mkdir -p bin/META-INF
	cp "$<" "$@"
	sed -i 's|/usr/lib/|$(INSTALLED_JARS)/|' "$@"

bin/correctpony: src/correctpony.sh
	@mkdir -p bin
	cp "$<" "$@"
	sed -i 's:^#!/bin/sh$$:#!$(SH_SHEBASH):' "$@"
	sed -i 's|bin/|$(PREFIX)$(MISC)/|' "$@"

.PHONY: java
java: $(foreach CLASS, $(CLASSES), bin/$(CLASS).class)

bin/%.java: src/%.java
	@mkdir -p bin
	cp "$<" "$@"
	sed -i '/DICT_DIRS/s|/usr/share/dict:/usr/local/share/dict:~/share/dict|$(DICT_DIRS)|' "$@"
	sed -i 's:"/dev/urandom":"$(DEFAULT_RANDOM)":' "$@"

bin/%.class: bin/%.java
	$(JAVAC) $(JAVA_FLAGS) "$<"

.PHONY: jar
jar: bin/Correctpony.jar

bin/Correctpony.jar: java bin/META-INF/MANIFEST.MF
	cd bin; $(JAR) cfm Correctpony.jar META-INF/MANIFEST.MF $(foreach CLASS, $(CLASSES), $(CLASS).class)

.PHONY: shell
shell: bash fish zsh

.PHONY: bash
bash: correctpony.bash-completion

.PHONY: fish
fish: correctpony.fish-completion

.PHONY: zsh
zsh: correctpony.zsh-completion

correctpony.auto-completion.configured: correctpony.auto-completion
	cp "$<" "$@"
	sed -i 's:/dev/urandom /dev/random:$(RANDOM_FILES):' "$@"

correctpony.%sh-completion: correctpony.auto-completion.configured
	auto-auto-complete $*sh --source "$<" --output "$@"



.PHONY: clean
clean:
	-rm -rf -- bin *.configured *.*sh-completion

