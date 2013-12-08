package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import java.net.MalformedURLException;

/** Danbooru 2.x API client. */
public class Danbooru extends Imageboard {

  /**
   * Checks if site at given URL exposes a Danbooru 2.x API.
   *
   * @param url Base site URL. (example: http://danbooru.donmai.us)
   * @return True if a Danbooru 2.x API was found.
   * @throws MalformedURLException Invalid URL.
   */
  public static boolean verifyUrl(String url) throws MalformedURLException {
    final Uri uri = Uri.parse(url);
    return !(uri.getHost() == null || uri.getScheme() == null) && checkUrl(uri.getScheme() + "://" + uri.getHost() + "/posts.xml");
  }
}
