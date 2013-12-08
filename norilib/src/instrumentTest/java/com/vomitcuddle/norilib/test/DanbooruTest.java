package com.vomitcuddle.norilib.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.vomitcuddle.norilib.clients.Danbooru;

public class DanbooruTest extends AndroidTestCase {

  public DanbooruTest() {}

  @LargeTest
  public void testVerifyUrl() throws Throwable {
    assertEquals(true, Danbooru.verifyUrl("http://danbooru.donmai.us"));
    assertEquals(false, Danbooru.verifyUrl("http://gelbooru.com"));
    assertEquals(false, Danbooru.verifyUrl("http://dollbooru.org"));
    assertEquals(false, Danbooru.verifyUrl("http://yukkuri.shii.org"));
    assertEquals(false, Danbooru.verifyUrl("http://google.com"));
  }
}
