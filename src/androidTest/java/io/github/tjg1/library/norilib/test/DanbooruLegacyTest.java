/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.test;

import io.github.tjg1.library.norilib.clients.DanbooruLegacy;
import io.github.tjg1.library.norilib.clients.SearchClient;

/**
 * Tests for the Danbooru 1.x API client.
 */
public class DanbooruLegacyTest extends SearchClientTestCase {
  // TODO: Test Basic Auth authentication.

  @Override
  protected SearchClient createSearchClient() {
    return new DanbooruLegacy("Danbooru", "https://danbooru.donmai.us");
  }
}