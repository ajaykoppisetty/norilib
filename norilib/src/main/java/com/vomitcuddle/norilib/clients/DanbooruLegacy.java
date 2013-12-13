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
import java.util.Locale;
import java.util.Map;

/** Danbooru 1.x API client. */
public class DanbooruLegacy extends Imageboard {
  /** Date format used by Danbooru 1.x */
  protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
  /** API endpoint url. */
  protected final String mApiEndpoint;
  /** Username used for authentication. Can be null. */
  protected final String mUsername;
  /** Password used for authentication. Can be null. */
  protected final String mPassword;

  /**
   * Creates a new instance of the Danbooru 1.x API client without authentication.
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
   * Creates a new instance of the Danbooru 1.x API client with authentication.
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
  protected String getWebUrlFromImageId(long id) {
    return mApiEndpoint + "/post/show/" + id;
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
    final String url = String.format(Locale.US, mApiEndpoint + "/post/index.xml?tags=%s&limit=%d&page=&d", Uri.encode(tags), DEFAULT_LIMIT, pid + 1);
    // Create request and add it to the queue.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  public Request<SearchResult> search(String tags, Response.Listener<SearchResult> listener, Response.ErrorListener errorListener) {
    // Create URL.
    final String url = String.format(Locale.US, mApiEndpoint + "/post/index.xml?tags=%s&limit=%d", Uri.encode(tags), DEFAULT_LIMIT);
    // Create request and add it to the queue.
    final Request<SearchResult> request = new SearchResultRequest(url, tags, listener, errorListener);
    mRequestQueue.add(request);
    // Return request.
    return request;
  }

  @Override
  protected Map<String, String> getAuthHeaders() throws AuthFailureError {
    // TODO: Implement me.
    return Collections.emptyMap();
  }

  @Override
  protected SearchResult parseSearchResultResponse(String data) throws Exception {
    // Make sure data isn't null or empty.
    if (data == null || data.equals(""))
      return null;

    // Create new SearchResult.
    SearchResult searchResult = new SearchResult();

    // Create XML parser.
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    XmlPullParser xpp = factory.newPullParser();
    xpp.setInput(new StringReader(data));
    int eventType = xpp.getEventType();

    // Loop over XML elements.
    while (eventType != XmlPullParser.END_DOCUMENT) {
      if (eventType == XmlPullParser.START_TAG) {
        if (xpp.getName().equals("posts")) { // Root tag.
          // Parse attributes.
          for (int i = 0; i < xpp.getAttributeCount(); i++) {
            if (xpp.getAttributeValue(i).equals("count")) // Total image count across all pages.
              searchResult.count = Long.parseLong(xpp.getAttributeValue(i));
            else if (xpp.getAttributeValue(i).equals("offset")) // Offset, used for paging.
              searchResult.offset = Long.parseLong(xpp.getAttributeValue(i));
          }
        } else if (xpp.getName().equals("post")) { // Image tag.
          // Create new image.
          Image image = new Image();

          // Parse attributes.
          for (int i = 0; i < xpp.getAttributeCount(); i++) {
            // Get attribute name and value.
            String name = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);

            if (name.equals("file_url")) // Image URL
              image.fileUrl = value;
            else if (name.equals("width")) // Image width
              image.width = Integer.parseInt(value);
            else if (name.equals("height")) // Image height
              image.height = Integer.parseInt(value);

            else if (name.equals("preview_url")) // Thumbnail URL
              image.previewUrl = value;
            else if (name.equals("preview_width")) // Thumbnail width
              image.previewWidth = Integer.parseInt(value);
            else if (name.equals("preview_height")) // Thumbnail height
              image.previewHeight = Integer.parseInt(value);

            else if (name.equals("sample_url")) // Sample URL
              image.sampleUrl = value;
            else if (name.equals("sample_width")) // Sample width
              image.sampleWidth = Integer.parseInt(value);
            else if (name.equals("sample_height")) // Sample height
              image.sampleHeight = Integer.parseInt(value);

            else if (name.equals("id")) // Image ID
              image.id = Long.parseLong(value);
            else if (name.equals("parent_id")) // Image parent ID
              image.parentId = value.equals("") ? -1 : Long.parseLong(value);

            else if (name.equals("tags")) // Tags
              image.generalTags = value.trim().split(" ");

            else if (name.equals("rating")) { // Obscenity rating
              if (value.equals("s")) // Safe for work
                image.obscenityRating = Image.ObscenityRating.SAFE;
              else if (value.equals("q")) // Ambiguous
                image.obscenityRating = Image.ObscenityRating.QUESTIONABLE;
              else if (value.equals("e")) // Not safe for work
                image.obscenityRating = Image.ObscenityRating.EXPLICIT;
              else // Unknown / undefined
                image.obscenityRating = Image.ObscenityRating.UNDEFINED;
            } else if (name.equals("score")) // Popularity score
              image.score = Integer.parseInt(value);
            else if (name.equals("source")) // Source URL
              image.source = value;
            else if (name.equals("md5")) // MD5 checksum
              image.md5 = value;

            else if (name.equals("created_at")) // Creation date
              image.createdAt = parseDateFromString(value);
            else if (name.equals("has_comments")) // Has comments
              image.hasComments = value.equals("true");
          }

          // Append web URL.
          image.webUrl = getWebUrlFromImageId(image.id);
          // Append Pixiv ID.
          image.pixivId = parsePixivIdFromUrl(image.source);

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
    return new ServiceSettings(mApiEndpoint, ServiceSettings.ServiceType.DANBOORU_LEGACY, mUsername, mPassword);
  }


}
