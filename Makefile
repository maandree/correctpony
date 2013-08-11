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

JAVA_OPTIMISE = -O

JAVAC = javac
JAR = jar
INSTALLED_PREFIX = /usr
INSTALLED_LIB = /lib
INSTALLED_JARS = $(INSTALLED_PREFIX)$(INSTALLED_LIB)
DICT_DIRS = /usr/share/dict:/usr/local/share/dict:~/share/dict

JAVADIRS = -s "src" -d "bin" -cp "src:$(INSTALLED_JARS)/ArgParser.jar"
JAVAFLAGS = -Xlint $(JAVA_OPTIMISE)
JAVA_FLAGS = $(JAVADIRS) $(JAVAFLAGS)

CLASSES = Correctpony



.PHONY: all
all: cmd

.PHONY: cmd
cmd: launcher java jar

.PHONY:
launcher: bin/correctpony

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

bin/%.class: bin/%.java
	$(JAVAC) $(JAVA_FLAGS) "$<"

.PHONY: jar
jar: bin/Correctpony.jar

bin/Correctpony.jar: java bin/META-INF/MANIFEST.MF
	cd bin; $(JAR) cfm Correctpony.jar META-INF/MANIFEST.MF $(foreach CLASS, $(CLASSES), $(CLASS).class)



.PHONY: clean
clean:
	-rm -rf -- bin

