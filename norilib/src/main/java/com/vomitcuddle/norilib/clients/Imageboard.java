package com.vomitcuddle.norilib.clients;

import com.android.volley.Request;
import com.android.volley.Response;
import com.vomitcuddle.norilib.SearchResult;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** Base imageboard client class */
abstract class Imageboard {

  /**
   * Checks if URL returns a 200 OK status code.
   *
   * @param url URL to fetch.
   * @return True if 200 status code is returned.
   * @throws MalformedURLException Invalid URL.
   */
  protected static boolean checkUrl(String url) throws MalformedURLException {
    try {
      // Create new HTTP connection.
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      connection.setUseCaches(false);
      connection.setDoInput(true);

      // Get status code.
      final int statusCode = connection.getResponseCode();
      // Close connection.
      connection.disconnect();
      // Check status code.
      if (statusCode == HttpStatus.SC_OK)
        return true;
    } catch (MalformedURLException e) {
      throw e;
    } catch (IOException ignored) {
    }
    return false;
  }

  /**
   * Gets default query, usually "rating:safe".
   *
   * @return Default query.
   */
  public abstract String getDefaultQuery();

  /**
   * Checks whether the API endpoint requires explicit authentication.
   *
   * @return True if authentication is required.
   */
  public abstract boolean requiresAuthentication();

  /**
   * Fetch a list of {@link com.vomitcuddle.norilib.Image}s using Android Volley.
   *
   * @param tags          Tags to search for. Any tag combination that works on the web should work here.
   * @param pid           Page number.
   * @param listener      Listener to receive the {@link SearchResult} response.
   * @param errorListener Error listener, or null to ignore errors.
   * @return Android Volley {@link com.android.volley.Request} that has been added to the {@link com.android.volley.RequestQueue}.
   */
  public abstract Request<SearchResult> search(String tags, int pid, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener);

  /**
   * Fetch a list of {@link com.vomitcuddle.norilib.Image}s using Android Volley.
   *
   * @param tags          Tags to search for. Any tag combination that works on the web should work here.
   * @param listener      Listener to receive the {@link SearchResult} response.
   * @param errorListener Error listener, or null to ignore errors.
   * @return Android Volley {@link com.android.volley.Request} that has been added to the {@link com.android.volley.RequestQueue}.
   */
  public abstract Request<SearchResult> search(String tags, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener);

  /**
   * Parses API response into a {@link com.vomitcuddle.norilib.SearchResult}.
   *
   * @param data API returned from HTTP response.
   * @return Parsed {@link com.vomitcuddle.norilib.SearchResult}.
   * @throws Exception Error parsing response.
   */
  protected abstract SearchResult parseSearchResultResponse(String data) throws Exception;
}
