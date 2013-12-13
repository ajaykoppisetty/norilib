/*
 * This file is part of norilib.
 * Copyright (c) 2013 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vomitcuddle.norilib.clients;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.vomitcuddle.norilib.SearchResult;
import com.vomitcuddle.norilib.ServiceSettings;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Base imageboard client class */
public abstract class Imageboard {
  /** Images to fetch per page */
  protected static final int DEFAULT_LIMIT = 100;
  /** Regex used for parsing pixiv IDs from URLs */
  private static final Pattern PIXIV_ID_FROM_URL_PATTERN = Pattern.compile("http://(?:www|i\\d)\\.pixiv\\.net/.+?(?:illust_id=|img/.+?/)(\\d+)");
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
   * Gets Pixiv ID from Pixiv image URL.
   *
   * @param url URL to image on Pixiv.
   * @return Pixiv ID or 1L if ID could not be found.
   */
  protected long parsePixivIdFromUrl(String url) {
    // Make sure URL isn't empty of null.
    if (url == null || url.equals(""))
      return -1L;
    // Match regex against URL.
    final Matcher matcher = PIXIV_ID_FROM_URL_PATTERN.matcher(url);
    if (matcher.find()) {
      // Match found.
      return Long.parseLong(matcher.group(1));
    }
    // Match not found.
    return -1L;
  }

  /**
   * Get web URL from image ID.
   *
   * @param id Image ID.
   * @return Link to image on the Imageboard's website.
   */
  protected abstract String getWebUrlFromImageId(long id);

  /** Parse date from API string */
  protected abstract Date parseDateFromString(String date) throws ParseException;

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
   * Exports current settings as {@link com.vomitcuddle.norilib.ServiceSettings}.
   *
   * @return {@link com.vomitcuddle.norilib.ServiceSettings} from which the client can be recreated.
   */
  protected abstract ServiceSettings exportServiceSettings();

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
     *
     * @param url           URL to fetch.
     * @param query         Query string being searched for.
     * @param listener      Listener to receive the {@link SearchResult} response.
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
