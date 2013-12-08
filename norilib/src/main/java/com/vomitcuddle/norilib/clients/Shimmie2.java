package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import java.net.MalformedURLException;

/** Shimmie2 Danbooru API client */
public class Shimmie2 extends Imageboard {

  /**
   * Checks if site exposes a Shimmie2 API.
   *
   * @param url Base site URL. (example: http://dollbooru.org)
   * @return True if a Shimmie2 API was found.
   * @throws MalformedURLException Invalid URL.
   */
  public static boolean verifyUrl(String url) throws MalformedURLException {
    final Uri uri = Uri.parse(url);
    return !(uri.getHost() == null || uri.getScheme() == null) && checkUrl(uri.getScheme() + "://" + uri.getHost() + "/api/danbooru/find_posts/index.xml");
  }
}
