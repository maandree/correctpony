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
LICENSES = $(DATA)/licenses
INFO = $(DATA)/info
DICT = $(DATA)/dict
COMMAND = correctpony
PKGNAME = correctpony

RANDOM_FILES = /dev/urandom /dev/random
DEFAULT_RANDOM = /dev/urandom
DICT_DIRS = /usr/share/dict:/usr/local/share/dict:~/share/dict

JAVA_OPTIMISE = -O

JAVAC = javac
JAR = jar
INSTALLED_PREFIX = /usr
INSTALLED_LIB = /lib
INSTALLED_BIN = /lib
INSTALLED_JARS = $(INSTALLED_PREFIX)$(INSTALLED_LIB)
JAVA_SHEBANG = $(INSTALLED_PREFIX)$(INSTALLED_BIN)/java

JAVADIRS = -s "src" -d "bin" -cp "src:$(INSTALLED_JARS)/ArgParser.jar"
JAVAFLAGS = -Xlint $(JAVA_OPTIMISE)
JAVA_FLAGS = $(JAVADIRS) $(JAVAFLAGS)

CLASSES = Correctpony

WORDLISTS = $(shell ls -1 wordlists | sed -e 's:.dict$$::')



.PHONY: all
all: cmd shell doc


.PHONY: doc
doc: info

.PHONY: info
info: correctpony.info.gz
%.info: info/%.texinfo
	$(MAKEINFO) "$<"
%.info.gz: %.info
	gzip -9c < "$<" > "$@"


.PHONY: pdf
pdf: correctpony.pdf
%.pdf: info/%.texinfo
	texi2pdf "$<"

pdf.gz: correctpony.pdf.gz
%.pdf.gz: %.pdf
	gzip -9c < "$<" > "$@"

pdf.xz: $correctpony.pdf.xz
%.pdf.xz: %.pdf
	xz -e9 < "$<" > "$@"


.PHONY: dvi
dvi: correctpony.dvi
%.dvi: info/%.texinfo
	$(TEXI2DVI) "$<"

dvi.gz: correctpony.dvi.gz
%.dvi.gz: %.dvi
	gzip -9c < "$<" > "$@"

dvi.xz: correctpony.dvi.xz
%.dvi.xz: %.dvi
	xz -e9 < "$<" > "$@"


.PHONY: ps
ps: correctpony.ps
%.ps: info/%.texinfo
	texi2pdf --ps "$<"

ps.gz: correctpony.ps.gz
%.ps.gz: %.dvi
	gzip -9c < "$<" > "$@"

ps.xz: correctpony.ps.xz
%.ps.xz: %.dvi
	xz -e9 < "$<" > "$@"


.PHONY: cmd
cmd: manifest java jar exec-jar

.PHONY: exec-jar
exec-jar: bin/correctpony

bin/correctpony: bin/Correctpony.jar
	echo '#!$(JAVA_SHEBASH) -jar' > "$@"
	cat "$<" >> "$@"

.PHONY:
manifest: bin/META-INF/MANIFEST.MF
bin/META-INF/MANIFEST.MF: META-INF/MANIFEST.MF
	@mkdir -p bin/META-INF
	cp "$<" "$@"
	sed -i 's|/usr/lib/|$(INSTALLED_JARS)/|' "$@"

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



.PHONY: install
install: install-cmd install-dict install-license install-doc install-shell

.PHONY: install-cmd
install-cmd: cmd
	install -Dm755 bin/correctpony -- "$(DESTDIR)$(PREFIX)$(BIN)/correctpony"

.PHONY: install-dict
install-dict:
	install -d -- "$(DESTDIR)$(PREFIX)$(DICT)"
	for D in $(WORDLISTS); do \
	    install -m644 "wordlists/$$D.dict" -- "$(DESTDIR)$(PREFIX)$(DICT)/$$D"; done

.PHONY: install-license
install-license:
	install -d -- "$(DESTDIR)$(PREFIX)$(LICENSES)/$(PKGNAME)"
	install -m644 COPYING LICENSE -- "$(DESTDIR)$(PREFIX)$(LICENSES)/$(PKGNAME)"

.PHONY: install-doc
install-doc: install-info

.PHONY: install-info
install-info: info
	install -Dm644 correctpony.info.gz -- "$(DESTDIR)$(PREFIX)$(INFO)/$(PKGNAME).info.gz"

.PHONY: install-shell
install-shell: install-bash install-fish install-zsh

.PHONY: install-bash
install-bash: bash
	install -Dm644 correctpony.bash-completion -- "$(DESTDIR)$(PREFIX)$(DATA)/bash-completion/completions/$(COMMAND)"

.PHONY: install-fish
install-fish: fish
	install -Dm644 correctpony.fish-completion -- "$(DESTDIR)$(PREFIX)$(DATA)/fish/completions/$(COMMAND).fish"

.PHONY: install-zsh
install-zsh: zsh
	install -Dm644 correctpony.zsh-completion -- "$(DESTDIR)$(PREFIX)$(DATA)/zsh/site-functions/_$(COMMAND)"



.PHONY: uninstall
uninstall:
	-rm -- "$(DESTDIR)$(PREFIX)$(BIN)/correctpony"
	-rm -- $(foreach D, $(WORDLISTS), "$(DESTDIR)$(PREFIX)$(DICT)/$(D)")
	-rm -- "$(DESTDIR)$(PREFIX)$(LICENSES)/$(PKGNAME)/COPYING"
	-rm -- "$(DESTDIR)$(PREFIX)$(LICENSES)/$(PKGNAME)/LICENSE"
	-rmdir -- "$(DESTDIR)$(PREFIX)$(LICENSES)/$(PKGNAME)"
	-rm -- "$(DESTDIR)$(PREFIX)$(INFO)/$(PKGNAME).info.gz"
	-rm -- "$(DESTDIR)$(PREFIX)$(DATA)/bash-completion/completions/$(COMMAND)"
	-rm -- "$(DESTDIR)$(PREFIX)$(DATA)/zsh/site-functions/_$(COMMAND)"
	-rm -- "$(DESTDIR)$(PREFIX)$(DATA)/fish/completions/$(COMMAND).fish"



.PHONY: clean
clean:
	-rm -rf -- bin *.configured *.*sh-completion *.{info,pdf,ps,dvi,gz,xz} *.{aux,cp,cps,fn,ky,log,pg,ps,toc,tp,vr}

