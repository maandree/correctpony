include config.mk

SRCDIR = src
OBJDIR = obj

_OBJ = correctpony wlist
OBJ = $(addprefix $(OBJDIR)/,$(addsuffix .o,$(_OBJ)))

PROGRAM=correctpony
BOOK=$(PROGRAM)
BOOKDIR=./

all : $(OBJDIR) $(OBJ) correctpony info


info: $(BOOK).info.gz
%.info: $(BOOKDIR)%.texinfo
	$(MAKEINFO) "$<"
%.info.gz: %.info
	gzip -9c < "$<" > "$@"


pdf: $(BOOK).pdf
%.pdf: $(BOOKDIR)%.texinfo
	texi2pdf "$<"

pdf.gz: $(BOOK).pdf.gz
%.pdf.gz: %.pdf
	gzip -9c < "$<" > "$@"

pdf.xz: $(BOOK).pdf.xz
%.pdf.xz: %.pdf
	xz -e9 < "$<" > "$@"


dvi: $(BOOK).dvi
%.dvi: $(BOOKDIR)%.texinfo
	$(TEXI2DVI) "$<"

dvi.gz: $(BOOK).dvi.gz
%.dvi.gz: %.dvi
	gzip -9c < "$<" > "$@"

dvi.xz: $(BOOK).dvi.xz
%.dvi.xz: %.dvi
	xz -e9 < "$<" > "$@"


$(OBJDIR) :
	@mkdir $(OBJDIR)

$(OBJDIR)/%.o : $(SRCDIR)/%.c
	@echo CC -c $<
	@$(CC) $(CPPFLAGS) $(CFLAGS) -c -o $@ $<

correctpony : $(OBJ) $(OBJDIR)/correctpony.o
	@$(CC) $(CPPFLAGS) $(CFLAGS) -o $@ $^ $(LDFLAGS) $(LIBS)

install : all
	@echo -e '\e[01m'installing executable files to "$(DESTDIR)$(PREFIX)$(BIN)"'\e[00m'
	mkdir -p "$(DESTDIR)$(PREFIX)$(BIN)"
	install -m 755 correctpony{,-security} "$(DESTDIR)$(PREFIX)$(BIN)"
	sed -i s/"\/usr\/share\/"/"$$(echo "$(PREFIX)$(DATA)/" | sed -e 's/\//\\\//g')"/g "$(DESTDIR)$(PREFIX)$(BIN)/correctpony-security"
	@echo -e '\e[01m'installing wordlists to "$(DESTDIR)$(PREFIX)$(DATA)/correctpony"'\e[00m'
	mkdir -p "$(DESTDIR)$(PREFIX)$(DATA)/correctpony"
	install -m 644 wordlists/* "$(DESTDIR)$(PREFIX)$(DATA)/correctpony"
	cat wordlists/* | sort | uniq > "$(DESTDIR)$(PREFIX)$(DATA)/correctpony/everything"
	@echo -e '\e[01m'installing bash-completion script'\e[00m'
	mkdir -p "$(DESTDIR)$(PREFIX)$(DATA)/bash-completion/completions"
	install -m 644 bash-completion "$(DESTDIR)$(PREFIX)$(DATA)/bash-completion/completions/correctpony"
	@echo -e '\e[01m'installing license files'\e[00m'
	mkdir -p "$(DESTDIR)$(PREFIX)$(DATA)/licenses/correctpony"
	install -m 644 COPYING "$(DESTDIR)$(PREFIX)$(DATA)/licenses/correctpony"
	@echo -e '\e[01m'installing info manual'\e[00m'
	mkdir -p "$(DESTDIR)$(PREFIX)$(DATA)/info"
	install -m 644 "$(BOOK).info.gz" "$(DESTDIR)$(PREFIX)$(DATA)/info"

uninstall :
	rm -rf "$(DESTDIR)$(PREFIX)$(DATA)/correctpony"
	unlink "$(DESTDIR)$(PREFIX)$(BIN)/correctpony"
	unlink "$(DESTDIR)$(PREFIX)$(BIN)/correctpony-security"
	unlink "$(DESTDIR)$(PREFIX)$(DATA)/bash-completion/completions/correctpony"
	unlink "$(DESTDIR)$(PREFIX)$(DATA)/info/$(BOOK).info.gz"

clean :
	@echo cleaning
	@rm -rf $(OBJDIR)
	@rm -r *.{t2d,aux,cp,cps,fn,ky,log,pg,pgs,toc,tp,vr,vrs,op,ops,bak,info,pdf,ps,dvi,gz} 2>/dev/null || exit 0

.PHONY: clean install

