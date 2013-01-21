VERSION = git

PREFIX = /usr
# /system for Haiku
DATA = /share
# /data for Haiku
BIN = /bin

CC = cc
CPPFLAGS = -DWORDLIST_DIR=\"$(PREFIX)$(DATA)/correctpony\" -DVERSION=\"$(VERSION)\" -D_GNU_SOURCE

CFLAGS = -std=c89 -pedantic -Wall -g
LDFLAGS =
LIBS = 
