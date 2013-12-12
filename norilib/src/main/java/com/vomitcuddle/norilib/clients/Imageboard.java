package com.vomitcuddle.norilib.clients;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.vomitcuddle.norilib.SearchResult;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/** Base imageboard client class */
abstract class Imageboard {
  /** Volley {@link com.android.volley.RequestQueue}. */
  protected final RequestQueue mRequestQueue;

  /**
   * Base constructor for all API clients.
   *
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   */
  public Imageboard(RequestQueue requestQueue) {
    mRequestQueue = requestQueue;
  }

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
   * Generated authentication headers for all requests.
   *
   * @return Map of HTTP headers to send with all API requests.
   * @throws AuthFailureError Authentication error.
   */
  protected abstract Map<String, String> getAuthHeaders() throws AuthFailureError;

  /**
   * Parses API response into a {@link com.vomitcuddle.norilib.SearchResult}.
   *
   * @param data API returned from HTTP response.
   * @return Parsed {@link com.vomitcuddle.norilib.SearchResult}.
   * @throws Exception Error parsing response.
   */
  protected abstract SearchResult parseSearchResultResponse(String data) throws Exception;

  /**
   * Volley request fetching a {@link com.vomitcuddle.norilib.SearchResult}.
   */
  protected class SearchResultRequest extends Request<SearchResult> {
    /** Response listener */
    private final Response.Listener<SearchResult> mListener;
    /** Query string we're searching for. */
    private final String mQuery;

    /**
     * Create a new Volley {@link com.vomitcuddle.norilib.clients.Imageboard.SearchResultRequest}.
     * @param url URL to fetch.
     * @param query Query string being searched for.
     * @param listener Listener to receive the {@link SearchResult} response.
     * @param errorListener Error listener, or null to ignore errors.
     */
    public SearchResultRequest(String url, String query, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
      super(Method.GET, url, errorListener);
      mListener = listener;
      mQuery = query;
      setRequestQueue(mRequestQueue);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
      // Use abstract method to get the auth headers.
      return getAuthHeaders();
    }

    @Override
    protected Response<SearchResult> parseNetworkResponse(NetworkResponse response) {
      try {
        // Parse HTTP response.
        return Response.success(parseSearchResultResponse(new String(response.data, HttpHeaderParser.parseCharset(response.headers))),
            HttpHeaderParser.parseCacheHeaders(response));
      } catch (Exception e) {
        return Response.error(new VolleyError("Error processing data."));
      }
    }

    @Override
    protected void deliverResponse(SearchResult response) {
      // Append search query to response.
      if (response != null)
        response.query = mQuery;
      // Deliver response, if not canceled and response listener isn't null.
      if (!isCanceled() && mListener != null)
        mListener.onResponse(response);
    }
  }
}
