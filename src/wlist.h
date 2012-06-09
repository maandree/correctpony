#ifndef WLIST_H
#define WLIST_H

#include "correcthorse.h"

#include <stdlib.h>

struct wlist
{
    struct word *head;
    struct word *tail;
    size_t len;
};

struct word
{
    char *word;
    struct word *next;
};

struct word *word_init(const char *word);
void word_free(struct word *w);

struct wlist *wlist_init(void);
void wlist_free(struct wlist *wl);
size_t wlist_len(struct wlist *wl);

int wlist_add(struct wlist *wl, const char *word);
char *wlist_get(struct wlist *wl, size_t n);

struct wlist *wlist_read(const char *name);

#endif
