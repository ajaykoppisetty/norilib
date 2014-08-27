package com.cuddlesoft.norilib.test;

import com.cuddlesoft.norilib.clients.DanbooruLegacy;
import com.cuddlesoft.norilib.clients.SearchClient;

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