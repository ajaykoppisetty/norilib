package com.vomitcuddle.norilib.test;

import android.test.AndroidTestCase;

import com.vomitcuddle.norilib.clients.DanbooruLegacy;

public class DanbooruLegacyTest extends AndroidTestCase {

  public void testVerifyUrl() throws Throwable {
    assertEquals(true, DanbooruLegacy.verifyUrl("http://yukkuri.shii.org"));
    assertEquals(true, DanbooruLegacy.verifyUrl("http://danbooru.donmai.us"));
    assertEquals(false, DanbooruLegacy.verifyUrl("http://gelbooru.com"));
    assertEquals(false, DanbooruLegacy.verifyUrl("http://dollbooru.org"));
    assertEquals(false, DanbooruLegacy.verifyUrl("http://google.com"));
  }

}
