/**
 * correctpony - a passphrase generator inspired by http://xkcd.com/936/
 * Copyright (c) 2012  Mattias Andrée (maandree@kth.se)
 *
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details.
 */
#include "correctpony.h"
#include "wlist.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

struct word *word_init(const char *word)
{
    struct word *w = malloc(sizeof *w);

    if (w)
    {
        if (!(w->word = strdup(word)))
        {
            free(w);
            return NULL;
        }

        w->next = NULL;
    }

    return w;
}

void word_free(struct word *w)
{
    if (!w)
        return;

    free(w->next);
    free(w);
}


struct wlist *wlist_init(void)
{
    struct wlist *wl = malloc(sizeof *wl);

    if (wl)
    {
        wl->head = NULL;
        wl->tail = NULL;
        wl->len = 0;
    }

    return wl;
}

void wlist_free(struct wlist *wl)
{
    word_free(wl->head);
    free(wl);
}

size_t wlist_len(struct wlist *wl)
{
    return wl->len;
}


int wlist_add(struct wlist *wl, const char *word)
{
    struct word *new = word_init(word);

    if (!new)
        return -1;

    if (!wl->head)
        wl->head = new;
    else
        wl->tail->next = new;

    wl->tail = new;
    wl->len++;

    return 0;
}

char *wlist_get(struct wlist *wl, size_t n)
{

    struct word *w;

    if (!wl->head || n >= wl->len)
        return NULL;

    if (!n)
        return wl->head->word;

    w = wl->head;

    while (n-- && w)
    {
        w = w->next;
    }

    return w->word;
}


struct wlist *wlist_read(const char *name)
{
    struct wlist *wl;
    char buf[1024];
    const char *path;

    FILE *f;

    wl = wlist_init();

    if (!wl)
        return NULL;

    if (*name == '/' || *name == '.')
        path = name;
    else
    {
        snprintf(buf, sizeof buf, WORDLIST_DIR "/%s", name);
        path = buf;
    }

    if (!(f = fopen(path, "r")))
    {
        fprintf(stderr, "cannot open wordlist file %s: ", path);
        perror("");
        wlist_free(wl);
        return NULL;
    }

    while (fgets(buf, sizeof buf, f))
    {
        size_t len = strlen(buf);

        /* remove (cr)lf */
        if (buf[len-1] == '\n')
        {
            if (buf[len-2] == '\r')
                buf[len-2] = '\0';
            else
                buf[len-1] = '\0';
        }

        wlist_add(wl, buf);
    }

    fclose(f);

    return wl;
}
