package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import java.net.MalformedURLException;

/** Danbooru 1.x API client. */
public class DanbooruLegacy extends Imageboard {

  /**
   * Checks if site at given URL exposes a Danbooru 1.x API.
   *
   * @param url Base site URL. (example: https://yande.re)
   * @return True if a Danbooru 1.x API was found.
   * @throws MalformedURLException Invalid URL.
   */
  public static boolean verifyUrl(String url) throws MalformedURLException {
    final Uri uri = Uri.parse(url);
    return !(uri.getHost() == null || uri.getScheme() == null) && checkUrl(uri.getScheme() + "://" + uri.getHost() + "/post/index.xml");
  }
}
