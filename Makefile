include config.mk

SRCDIR = src
OBJDIR = obj

_OBJ = correctpony wlist
OBJ = $(addprefix $(OBJDIR)/,$(addsuffix .o,$(_OBJ)))

all : $(OBJDIR) $(OBJ) correctpony

$(OBJDIR) :
	@mkdir $(OBJDIR)

$(OBJDIR)/%.o : $(SRCDIR)/%.c
	@echo CC -c $<
	@$(CC) $(CPPFLAGS) $(CFLAGS) -c -o $@ $<

correctpony : $(OBJ) $(OBJDIR)/correctpony.o
	@$(CC) $(CPPFLAGS) $(CFLAGS) -o $@ $^ $(LDFLAGS) $(LIBS)

install : all
	@echo installing executable files to ${DESTDIR}${PREFIX}/bin
	mkdir -p ${DESTDIR}${PREFIX}/bin
	install -m 755 correctpony{,-security} ${DESTDIR}${PREFIX}/bin
	@echo installing wordlists to ${DESTDIR}${PREFIX}/share/correctpony
	mkdir -p ${DESTDIR}${PREFIX}/share/correctpony
	install -m 644 wordlists/* ${DESTDIR}${PREFIX}/share/correctpony
	cat wordlists/* | sort | uniq > ${DESTDIR}${PREFIX}/share/correctpony/everything
	@echo installing bash-completion script
	mkdir -p /usr/share/bash-completion/completions
	install -m 644 bash-completion /usr/share/bash-completion/completions/correctpony

uninstall :
	rm -rf ${DESTDIR}${PREFIX}/share/correctpony
	unlink ${DESTDIR}${PREFIX}/bin/correctpony
	unlink ${DESTDIR}${PREFIX}/bin/correctpony-security
	unlink /usr/share/bash-completion/completions/correctpony

clean :
	@echo cleaning
	@rm -rf $(OBJDIR)

.PHONY: clean install
