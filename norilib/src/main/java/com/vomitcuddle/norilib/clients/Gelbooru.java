package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;

/** Gelbooru API client. */
public class Gelbooru extends Imageboard {
  /** Username used for authentication. Can be null. */
  private final String mUsername;
  /** Password used for authentication. Can be null. */
  private final String mPassword;

  /**
   * Creates a new instance of the Gelbooru API client without user authentication.
   *
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   */
  public Gelbooru(RequestQueue requestQueue) {
    super(requestQueue);
    // No authentication needed.
    mUsername = null;
    mPassword = null;
  }

  /**
   * Creates a new instance of the Gelbooru API client with user authentication.
   *
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   * @param username     Username.
   * @param password     Password.
   */
  public Gelbooru(RequestQueue requestQueue, String username, String password) {
    super(requestQueue);
    // Set credentials.
    mUsername = username;
    mPassword = password;
  }

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
  protected Map<String, String> getAuthHeaders() throws AuthFailureError {
    // TODO: Implement me.
    return Collections.emptyMap();
  }

  @Override
  protected SearchResult parseSearchResultResponse(String data) throws Exception {
    return null;
  }
}
