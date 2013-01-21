/**
 * correctpony - a passphrase generator inspired by http://xkcd.com/936/
 * Copyright (C) 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */
#ifndef WLIST_H
#define WLIST_H

#include <stdlib.h>

struct wlist
{
  struct word* head;
  struct word* tail;
  size_t len;
};

struct word
{
  char* word;
  struct word* next;
};

struct word *word_init(const char* word);
void word_free(struct word* w);

struct wlist *wlist_init(void);
void wlist_free(struct wlist* wl);
size_t wlist_len(struct wlist* wl);

int wlist_add(struct wlist* wl, const char* word);
char* wlist_get(struct wlist* wl, size_t n);

struct wlist* wlist_read(const char* name);

#endif
