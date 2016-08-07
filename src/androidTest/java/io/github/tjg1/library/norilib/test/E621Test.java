package io.github.tjg1.library.norilib.test;

import io.github.tjg1.library.norilib.clients.E621;
import io.github.tjg1.library.norilib.clients.SearchClient;

 /**
 * Tests for the E621 client.
 */
public class E621Test extends SearchClientTestCase{

  @Override
  protected SearchClient createSearchClient(){
    return new E621("E621", "https://e621.net");
  }

  @Override
  protected String getDefaultTag() {
    return "blonde_hair";
  }
}
