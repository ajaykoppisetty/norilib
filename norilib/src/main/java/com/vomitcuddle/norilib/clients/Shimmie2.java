package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;

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
