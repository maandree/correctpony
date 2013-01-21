VERSION = git

PREFIX = /usr

CC = cc
CPPFLAGS = -DWORDLIST_DIR=\"$(PREFIX)/share/correctpony\" -DVERSION=\"$(VERSION)\" -D_GNU_SOURCE

CFLAGS = -std=c89 -pedantic -Wall -g
LDFLAGS =
LIBS = 
