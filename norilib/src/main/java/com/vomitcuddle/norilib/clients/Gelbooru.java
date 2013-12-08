package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import java.net.MalformedURLException;

/** Gelbooru API client. */
public class Gelbooru extends Imageboard {

  /**
   * Checks if site at given URL exposes a Gelbooru API.
   *
   * @param url Base site URL. (eg.: http://gelbooru.com)
   * @return True if a Gelbooru API is found.
   * @throws MalformedURLException Invalid URL.
   */
  public static boolean verifyUrl(String url) throws MalformedURLException {
    final Uri uri = Uri.parse(url);
    return !(uri.getHost() == null || uri.getScheme() == null) && checkUrl(uri.getScheme() + "://" + uri.getHost() + "/index.php?page=dapi&s=post&q=index");
  }


}
