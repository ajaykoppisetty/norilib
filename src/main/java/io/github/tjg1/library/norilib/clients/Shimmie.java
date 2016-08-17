/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.clients;

import android.content.Context;

import java.util.Locale;

/**
 * Client for the Shimmie2 API.
 * Shimmie2 provides an extension that enables a Danbooru 1.x-based API.
 */
public class Shimmie extends DanbooruLegacy {
  public Shimmie(Context context, String name, String endpoint) {
    super(context, name, endpoint);
  }

  public Shimmie(Context context, String name, String endpoint, String username, String password) {
    super(context, name, endpoint, username, password);
  }

  @Override
  public Settings getSettings() {
    return new Settings(Settings.APIType.SHIMMIE, name, apiEndpoint, username, password);
  }

  @Override
  protected String webUrlFromId(String id) {
    return String.format(Locale.US, "%s/post/view/%s", apiEndpoint, id);
  }

  @Override
  protected String createSearchURL(String tags, int pid, int limit) {
    // Page numbers are 1-indexed for this api.
    final int page = pid + 1;

    return String.format(Locale.US, "%s/api/danbooru/find_posts/index.xml?tags=%s&page=%d&limit=%d", apiEndpoint, tags, page, limit);
  }
}
