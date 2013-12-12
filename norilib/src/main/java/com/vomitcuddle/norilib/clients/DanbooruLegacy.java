package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/** Danbooru 1.x API client. */
public class DanbooruLegacy extends Imageboard {
  /** Images to fetch per page */
  protected static final int DEFAULT_LIMIT = 100;
  /** Date format used by Danbooru 1.x */
  protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
  /** Regex used for parsing pixiv IDs from URLs */
  private static final Pattern PIXIV_ID_FROM_URL_PATTERN = Pattern.compile("http://(?:www|i\\d)\\.pixiv\\.net/.+?(?:illust_id=|img/.+?/)(\\d+)");
  /** API endpoint url. */
  protected final String mApiEndpoint;
  /** Username used for authentication. Can be null. */
  protected final String mUsername;
  /** Password used for authentication. Can be null. */
  protected final String mPassword;

  /**
   * Create a new instance of the Danbooru 1.x API client.
   *
   * @param endpoint     URL to the API endpoint (example: http://yande.re), doesn't include path or trailing slashes.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   */
  public DanbooruLegacy(String endpoint, RequestQueue requestQueue) {
    super(requestQueue);
    mApiEndpoint = endpoint;
    // No authentication needed.
    mUsername = null;
    mPassword = null;
  }

  /**
   * Create a new instance of the Danbooru 1.x API client with authentication.
   *
   * @param endpoint     URL to the API endpoint (example: http://yande.re), doesn't include path or trailing slashes.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   * @param username     Username.
   * @param password     Password.
   */
  public DanbooruLegacy(String endpoint, RequestQueue requestQueue, String username, String password) {
    super(requestQueue);
    mApiEndpoint = endpoint;
    // Set authentication credentials.
    mUsername = username;
    mPassword = password;
  }

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
