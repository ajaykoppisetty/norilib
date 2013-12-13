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

import android.net.Uri;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.vomitcuddle.norilib.Image;
import com.vomitcuddle.norilib.SearchResult;
import com.vomitcuddle.norilib.ServiceSettings;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Danbooru 2.x API client. */
public class Danbooru extends Imageboard {
  /** Default API endpoint = danbooru */
  private static final String DEFAULT_API_ENDPOINT = "http://danbooru.donmai.us";
  /** Date format used by the API. */
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
  /** Number of images to fetch per request. */
  private static final int DEFAULT_LIMIT = 100;
  /** API endpoint. */
  private final String mApiEndpoint;
  /** Username used for authentication, can be null. */
  private final String mUsername;
  /** API key used for authentication, can be null. */
  private final String mApiKey;

  /**
   * Creates a new instance of the Danbooru 2.x client without user authentication.
   *
   * @param endpoint  API Endpoint, uses http://danbooru.donmai.us if null.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   */
  public Danbooru(String endpoint, RequestQueue requestQueue) {
    super(requestQueue);
    // Use danbooru as default API endpoint.
    mApiEndpoint = endpoint != null ? endpoint : DEFAULT_API_ENDPOINT;
    // No authentication needed.
    mUsername = null;
    mApiKey = null;
  }

  /**
   * Creates a new instance of the Danbooru 2.x client with user authentication.
   *
   * @param endpoint     API Endpoint, uses http://danbooru.donmai.us if null.
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   * @param username     Username (optional).
   * @param apiKey       API key (optional).
   */
  public Danbooru(String endpoint, RequestQueue requestQueue, String username, String apiKey) {
    super(requestQueue);
    // Use danbooru as default API endpoint.
    mApiEndpoint = endpoint != null ? endpoint : DEFAULT_API_ENDPOINT;
    // Set authentication credentials.
    mUsername = username;
    mApiKey = apiKey;
  }

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

  @Override
  protected SearchResult parseSearchResultResponse(String xml) throws Exception {
    if (xml == null || xml.equals(""))
      return null;

    // Create new SearchResult.
    SearchResult searchResult = new SearchResult();

    // Create XML parser.
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    XmlPullParser xpp = factory.newPullParser();
    xpp.setInput(new StringReader(xml));
    int eventType = xpp.getEventType();

    Image image = new Image();

    // Loop over XML elements.
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        // Get tag name.
        final String name = xpp.getName();

        if (xpp.getName().equals("post")) { // Create new image
          image = new Image();
        } else if (name.equals("large-file-url")) // Image URL
          image.fileUrl = mApiEndpoint + xpp.nextText();
        else if (name.equals("image-width")) // Image width
          image.width = Integer.parseInt(xpp.nextText());
        else if (name.equals("image-height")) // Image height
          image.height = Integer.parseInt(xpp.nextText());
        else if (name.equals("preview-file-url")) { // Thumbnail URL
          image.previewUrl = mApiEndpoint + xpp.nextText();
          // FIXME: API doesn't return thumbnail dimensions.
          image.previewWidth = 150;
          image.previewHeight = 150;
        } else if (name.equals("file-url")) { // Sample URL
          image.sampleUrl = mApiEndpoint + xpp.nextText();
          // FIXME: API doesn't return sample dimensions.
          image.sampleWidth = 850;
          image.sampleHeight = 850;
        } else if (name.equals("tag-string-general")) // General tags
          image.generalTags = xpp.nextText().trim().split(" ");
        else if (name.equals("tag-string-artist")) // Artist tags
          image.artistTags = xpp.nextText().trim().split(" ");
        else if (name.equals("tag-string-character")) // Character tags
          image.characterTags = xpp.nextText().trim().split(" ");
        else if (name.equals("tag-string-copyright")) // Copyright tags
          image.copyrightTags = xpp.nextText().trim().split(" ");
        else if (name.equals("id")) // Image ID
          image.id = Long.parseLong(xpp.nextText());
        else if (name.equals("parent-id")) // Parent ID
          image.parentId = xpp.getAttributeValue(null, "nil") != null ? -1 : Long.parseLong(xpp.nextText());
        else if (name.equals("pixiv-id")) // Pixiv ID
          image.pixivId = xpp.getAttributeValue(null, "nil") != null ? -1 : Long.parseLong(xpp.nextText());
        else if (name.equals("rating")) { // Obscenity rating
          final String rating = xpp.nextText();

          if (rating.equals("s")) // Safe for work
            image.obscenityRating = Image.ObscenityRating.SAFE;
          else if (rating.equals("q")) // Ambiguous
            image.obscenityRating = Image.ObscenityRating.QUESTIONABLE;
          else if (rating.equals("e")) // Explicit
            image.obscenityRating = Image.ObscenityRating.EXPLICIT;
          else // Unknown / Undefined
            image.obscenityRating = Image.ObscenityRating.UNDEFINED;
        } else if (name.equals("score")) // Popularity score
          image.score = Integer.parseInt(xpp.nextText());
        else if (name.equals("source")) // Source URL
          image.source = xpp.nextText();
        else if (name.equals("md5")) // MD5 checksum
          image.md5 = xpp.nextText();
        else if (name.equals("last-commented-at")) // Has comment
          image.hasComments = !xpp.nextText().equals("");
        else if (name.equals("created-at")) // Creation date
          image.createdAt = parseDateFromString(xpp.nextText());

      } else if (eventType == XmlPullParser.END_TAG) {
        if (xpp.getName().equals("post")) {
          // Append web URL.
          image.webUrl = getWebUrlFromImageId(image.id);
          // Add image to results.
          searchResult.images.add(image);
        }

      }
      // Get next XML element.
      eventType = xpp.next();
    }
    return searchResult;
  }

  @Override
  protected ServiceSettings exportServiceSettings() {
    return new ServiceSettings(mApiEndpoint, ServiceSettings.ServiceType.DANBOORU, mUsername, mApiKey);
  }

  @Override
  protected String getWebUrlFromImageId(long id) {
    return String.format(Locale.US, "%s/posts/%d", mApiEndpoint, id);
  }

  @Override
  protected Date parseDateFromString(String date) throws ParseException {
    return DATE_FORMAT.parse(date);
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
    // Create URL.
    final String url = String.format(Locale.US, mApiEndpoint + "/posts.xml?tags=%s&page=%d&limit=%d", Uri.encode(tags), pid + 1, DEFAULT_LIMIT);
    // Create request.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    // Add request to queue.
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  public Request<SearchResult> search(String tags, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    // Create URl.
    final String url = String.format(Locale.US, mApiEndpoint + "/posts.xml?tags=%s&limit=%d", Uri.encode(tags), DEFAULT_LIMIT);
    // Create request.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    // Add request to queue.
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  protected Map<String, String> getAuthHeaders() throws AuthFailureError {
    // Set authentication headers.
    if (mUsername != null && mApiKey != null) {
      final Map<String, String> headers = new HashMap<String, String>();
      headers.put("Authorization", Base64.encodeToString((mUsername + ":" + mApiKey).getBytes(), Base64.DEFAULT));
      return headers;
    }
    // No authentication needed.
    return Collections.emptyMap();
  }
}
