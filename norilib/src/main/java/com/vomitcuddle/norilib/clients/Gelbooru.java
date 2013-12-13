package com.vomitcuddle.norilib.clients;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;
import com.vomitcuddle.norilib.ServiceSettings;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Gelbooru API client. */
public class Gelbooru extends DanbooruLegacy {
  /** Date format used by Gelbooru. */
  protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
  /** Default API endpoint = safebooru.org */
  private static final String DEFAULT_API_ENDPOINT = "http://safebooru.org";

  /**
   * Creates a new instance of the Gelbooru API client without user authentication.
   *
   * @param endpoint     URL to the API endpoint, doesn't include path or trailing slashes.
   *                     Defaults to http://safebooru.org if null.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   */
  public Gelbooru(String endpoint, RequestQueue requestQueue) {
    super(endpoint != null ? endpoint : DEFAULT_API_ENDPOINT, requestQueue);
  }

  /**
   * Creates a new instance of the Gelbooru API client with user authentication.
   *
   * @param endpoint     URL to the API endpoint, doesn't include path or trailing slashes.
   *                     Defaults to http://safebooru.org if null.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   * @param username     Username.
   * @param password     Password.
   */
  public Gelbooru(String endpoint, RequestQueue requestQueue, String username, String password) {
    super(endpoint != null ? endpoint : DEFAULT_API_ENDPOINT, requestQueue, username, password);
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
  protected String getWebUrlFromImageId(long id) {
    return String.format(Locale.US, "%s//index.php?page=post&s=view&id=%d", mApiEndpoint, id);
  }

  @Override
  protected Date parseDateFromString(String date) throws ParseException {
    return DATE_FORMAT.parse(date);
  }

  @Override
  public Request<SearchResult> search(String tags, int pid, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    // Create URL.
    final String url = String.format(Locale.US, "%s/index.php?page=dapi&s=post&q=index&tags=%s&pid=%d&limit=%d", mApiEndpoint, Uri.encode(tags), pid, DEFAULT_LIMIT);
    // Create request and add it to the RequestQueue.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  public Request<SearchResult> search(String tags, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    // Create URL.
    final String url = String.format(Locale.US, "%s//index.php?page=dapi&s=post&q=index&tags=%s&limit=%d", mApiEndpoint, Uri.encode(tags), DEFAULT_LIMIT);
    // Create request and add it to the RequestQueue.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  protected ServiceSettings exportServiceSettings() {
    return new ServiceSettings(mApiEndpoint, ServiceSettings.ServiceType.GELBOORU, mUsername, mPassword);
  }
}
