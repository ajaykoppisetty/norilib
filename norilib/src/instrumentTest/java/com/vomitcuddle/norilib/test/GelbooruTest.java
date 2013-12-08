package com.vomitcuddle.norilib.test;

import android.test.AndroidTestCase;

import com.vomitcuddle.norilib.clients.Gelbooru;

public class GelbooruTest extends AndroidTestCase {

  public void testVerifyUrl() throws Throwable {
    assertEquals(true, Gelbooru.verifyUrl("http://gelbooru.com"));
    assertEquals(false, Gelbooru.verifyUrl("http://danbooru.donmai.us"));
    assertEquals(false, Gelbooru.verifyUrl("http://dollbooru.org"));
    assertEquals(false, Gelbooru.verifyUrl("http://yukkuri.shii.org"));
    assertEquals(false, Gelbooru.verifyUrl("http://google.com"));
  }
}
