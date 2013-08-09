/**
 * correctpony - a passphrase generator inspired by http://xkcd.com/936/
 * Copyright (C) 2012, 2013  Mattias Andrée (maandree@member.fsf.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
