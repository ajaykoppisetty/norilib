/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package com.cuddlesoft.norilib.clients;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.cuddlesoft.norilib.Image;
import com.cuddlesoft.norilib.SearchResult;
import com.cuddlesoft.norilib.Tag;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Client for the Danbooru 1.x API.
 */
public class DanbooruLegacy implements SearchClient {
  /**
   * Number of images per search results page.
   * Best to use a large value to minimize number of unique HTTP requests.
   */
  private static final int DEFAULT_LIMIT = 100;
  /** OkHTTP Client. */
  private final OkHttpClient okHttpClient = new OkHttpClient();
  /** Human-readable service name. */
  protected final String name;
  /** URL to the HTTP API Endpoint - the server implementing the API. */
  protected final String apiEndpoint;
  /** Username used for authentication. (optional) */
  protected final String username;
  /** Password used for authentication. (optional) */
  protected final String password;

  /**
   * Create a new Danbooru 1.x client without authentication.
   *
   * @param endpoint URL to the HTTP API Endpoint - the server implementing the API.
   */
  public DanbooruLegacy(String name, String endpoint) {
    this.name = name;
    this.apiEndpoint = endpoint;
    this.username = null;
    this.password = null;
  }

  /**
   * Create a new Danbooru 1.x client with authentication.
   *
   * @param endpoint URL to the HTTP API Endpoint - the server implementing the API.
   * @param username Username used for authentication.
   * @param password Password used for authentication.
   */
  public DanbooruLegacy(String name, String endpoint, String username, String password) {
    this.name = name;
    this.apiEndpoint = endpoint;
    this.username = username;
    this.password = password;

    // Enable HTTP basic authentication.
    okHttpClient.setAuthenticator(new Authenticator() {
      @Override
      public Request authenticate(Proxy proxy, Response response) throws IOException {
        final String credential = Credentials.basic(DanbooruLegacy.this.username, DanbooruLegacy.this.password);
        return response.request().newBuilder()
            .header("Authorization", credential)
            .build();
      }

      @Override
      public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
      }
    });
  }

  @Override
  public SearchResult search(String tags) throws IOException {
    // Return results for page 0.
    return search(tags, 0);
  }

  @Override
  public SearchResult search(String tags, int pid) throws IOException {
    // Create HTTP request.
    final Request request = new Request.Builder()
        .url(createSearchURL(tags, pid, DEFAULT_LIMIT))
        .build();
    // Get HTTP response.
    final Response response = okHttpClient.newCall(request).execute();
    final String body = response.body().string();

    // Return parsed SearchResult.
    return parseXMLResponse(body, tags, pid);
  }

  @Override
  public void search(String tags, SearchCallback callback) {
    // Return results for page 0.
    search(tags, 0, callback);
  }

  @Override
  public void search(final String tags, final int pid, final SearchCallback callback) {
    // Fetch results on a background thread.
    new AsyncTask<Void, Void, SearchResult>() {
      /** Error returned when attempting to fetch the SearchResult. */
      private IOException error;

      @Override
      protected SearchResult doInBackground(Void... voids) {
        try {
          return DanbooruLegacy.this.search(tags, pid);
        } catch (IOException e) {
          // Hold on to the error for now and handle it on the main UI thread in #postExecute().
          error = e;
        }
        return null;
      }

      @Override
      protected void onPostExecute(SearchResult searchResult) {
        // Pass the result or error to the SearchCallback.
        if (error != null || searchResult == null) {
          callback.onFailure(error);
        } else {
          callback.onSuccess(searchResult);
        }
      }
    }.execute();
  }

  /**
   * Parse an XML response returned by the API.
   *
   * @param body   HTTP Response body.
   * @param tags   Tags used to retrieve the response.
   * @param offset Current paging offset.
   * @return A {@link com.cuddlesoft.norilib.SearchResult} parsed from given XML.
   */
  protected SearchResult parseXMLResponse(String body, String tags, int offset) throws IOException {
    // Create variables to hold the values as XML is being parsed.
    final List<Image> imageList = new ArrayList<>(DEFAULT_LIMIT);

    try {
      // Create an XML parser factory and disable namespace awareness for security reasons.
      // See: (http://lists.w3.org/Archives/Public/public-xmlsec/2009Dec/att-0000/sws5-jensen.pdf).
      final XmlPullParserFactory xmlParserFactory = XmlPullParserFactory.newInstance();
      xmlParserFactory.setNamespaceAware(false);

      // Create a new XML parser and feed HTTP response data into it.
      final XmlPullParser xpp = xmlParserFactory.newPullParser();
      xpp.setInput(new StringReader(body));

      // Iterate over each XML element and handle pull parser "events".
      while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
        if (xpp.getEventType() == XmlPullParser.START_TAG) {
          if (xpp.getName().equals("post")) {
            // <post> tags contain metadata for each image.
            final Image image = new Image();

            // Extract image metadata from XML attributes.
            for (int i = 0; i < xpp.getAttributeCount(); i++) {
              // Get name and value of current XML attribute.
              final String name = xpp.getAttributeName(i);
              final String value = xpp.getAttributeValue(i);

              // Set the appropriate value for each tag name.
              if (name.equals("file_url")) {
                image.fileUrl = normalizeUrl(value);
              } else if (name.equals("width")) {
                image.width = Integer.parseInt(value);
              } else if (name.equals("height")) {
                image.height = Integer.parseInt(value);
              } else if (name.equals("preview_url")) {
                image.previewUrl = normalizeUrl(value);
              } else if (name.equals("preview_width")) {
                image.previewWidth = Integer.valueOf(value);
              } else if (name.equals("preview_height")) {
                image.previewHeight = Integer.valueOf(value);
              } else if (name.equals("sample_url")) {
                image.sampleUrl = normalizeUrl(value);
              } else if (name.equals("sample_width")) {
                image.sampleWidth = Integer.valueOf(value);
              } else if (name.equals("sample_height")) {
                image.sampleHeight = Integer.valueOf(value);
              } else if (name.equals("tags")) {
                image.tags = Tag.arrayFromString(value, Tag.Type.GENERAL);
              } else if (name.equals("id")) {
                image.id = value;
              } else if (name.equals("parent_id")) {
                image.parentId = value;
              } else if (name.equals("rating")) {
                image.obscenityRating = Image.ObscenityRating.fromString(value);
              } else if (name.equals("score")) {
                image.score = Integer.parseInt(value);
              } else if (name.equals("md5")) {
                image.md5 = value;
              } else if (name.equals("created_at") || name.equals("date")) {
                image.createdAt = dateFromString(value);
              }
            }

            // Append values not returned by the API.
            image.webUrl = webUrlFromId(image.id);
            image.pixivId = Image.getPixivIdFromUrl(image.source);
            // Use original file if low-resolution sample does not exist.
            if (image.sampleUrl == null) {
              image.sampleUrl = image.fileUrl;
              image.sampleWidth = image.width;
              image.sampleHeight = image.height;
            }
            // Add Image to search result.
            imageList.add(image);
          }
        }
        // Get next XMLPullParser event.
        xpp.next();
      }
    } catch (XmlPullParserException | ParseException e) {
      // Convert into IOException.
      // Needed for consistent method signatures in the SearchClient interface for different APIs.
      // (Throwing an XmlPullParserException would be fine, until dealing with an API using JSON, etc.)
      throw new IOException(e);
    }
    // Create and return a SearchResult.
    return new SearchResult(imageList.toArray(new Image[imageList.size()]), Tag.arrayFromString(tags), offset);
  }

  @Override
  public String getDefaultQuery() {
    // Show all safe-for-work images by default.
    return "";
  }

  @Override
  public Settings getSettings() {
    return new Settings(Settings.APIType.DANBOORU_LEGACY, name, apiEndpoint, username, password);
  }

  /**
   * Convert a relative image URL to an absolute URL.
   *
   * @param url URL to convert.
   * @return Absolute URL.
   */
  protected String normalizeUrl(String url) {
    // Return empty string for empty URLs.
    if (url == null || url.isEmpty()) {
      return "";
    }
    // Prepend API endpoint path if url is relative.
    final Uri uri = Uri.parse(url);
    if (uri.isRelative()) {
      return apiEndpoint + url;
    }
    // URL already absolute.
    return url;
  }

  /**
   * Get a URL viewable in the system web browser for given Image ID.
   *
   * @param id {@link com.cuddlesoft.norilib.Image} ID.
   * @return URL for viewing the image in the browser.
   */
  protected String webUrlFromId(String id) {
    return apiEndpoint + "/post/show/" + id;
  }

  /**
   * Create a {@link java.util.Date} object from String date representation used by this API.
   *
   * @param date Date string.
   * @return Date converted from given String.
   */
  protected Date dateFromString(String date) throws ParseException {
    // Parser for the date format used by upstream Danbooru 1.x.
    final DateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    if (TextUtils.isDigitsOnly(date)) {
      // Moebooru-based boards (Danbooru 1.x fork) use Unix timestamps.
      return new Date(Integer.valueOf(date));
    } else {
      return DATE_FORMAT_DEFAULT.parse(date);
    }
  }

  /**
   * Generate request URL to the search API endpoint.
   *
   * @param tags  Space-separated tags.
   * @param pid   Page number (0-indexed).
   * @param limit Images to fetch per page.
   * @return URL to search results API.
   */
  protected String createSearchURL(String tags, int pid, int limit) {
    // Page numbers are 1-indexed for this API.
    final int page = pid + 1;

    return String.format(Locale.US, apiEndpoint + "/post/index.xml?tags=%s&limit=%d&page=%d", Uri.encode(tags), limit, page);
  }

  @Override
  public AuthenticationType requiresAuthentication() {
    return AuthenticationType.OPTIONAL;
  }
}
