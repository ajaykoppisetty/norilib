package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;

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


  @Override
  public String getDefaultQuery() {
    return "rating:safe";
  }

  @Override
  public boolean requiresAuthentication() {
    return false;
  }

  @Override
  public Request<SearchResult> search(String tags, int pid, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    return null;
  }

  @Override
  public Request<SearchResult> search(String tags, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    return null;
  }

  @Override
  protected SearchResult parseSearchResultResponse(String data) throws Exception {
    return null;
  }
}
