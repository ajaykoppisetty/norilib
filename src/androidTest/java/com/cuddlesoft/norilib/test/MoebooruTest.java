package com.cuddlesoft.norilib.test;

import com.cuddlesoft.norilib.clients.DanbooruLegacy;
import com.cuddlesoft.norilib.clients.SearchClient;

/** Tests support for Moebooru-based boards in the DanbooruLegacy client. */
public class MoebooruTest extends SearchClientTestCase {

  @Override
  protected SearchClient createSearchClient() {
    return new DanbooruLegacy("yande.re", "https://yande.re");
  }
}
