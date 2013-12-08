package com.vomitcuddle.norilib.test;

import android.test.AndroidTestCase;

import com.vomitcuddle.norilib.clients.Shimmie2;

public class Shimmie2Test extends AndroidTestCase {

  public void testVerifyUrl() throws Throwable {
    assertEquals(true, Shimmie2.verifyUrl("http://dollbooru.org"));
    assertEquals(false, Shimmie2.verifyUrl("http://gelbooru.com"));
    assertEquals(false, Shimmie2.verifyUrl("http://danbooru.donmai.us"));
    assertEquals(false, Shimmie2.verifyUrl("http://yukkuri.shii.org"));
    assertEquals(false, Shimmie2.verifyUrl("http://google.com"));
  }
}
