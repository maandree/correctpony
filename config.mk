VERSION = alt-git

PREFIX = /usr/local

CC = cc
CPPFLAGS = -DWORDLIST_DIR=\"$(PREFIX)/share/correcthorse\" -DVERSION=\"$(VERSION)\" -D_GNU_SOURCE

CFLAGS = -std=c99 -pedantic -Wall -g
LDFLAGS =
LIBS = 
