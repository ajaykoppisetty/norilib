/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import io.github.tjg1.library.norilib.clients.SearchClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Service that detects the {@link io.github.tjg1.library.norilib.clients.SearchClient} API type for given URL. */
public class ServiceTypeDetectionService extends IntentService {
  /** Result code returned when the service type was detected successfully. */
  public static final int RESULT_OK = 0x00;
  /** Result code returned when there was a problem with network connectivity. */
  public static final int RESULT_FAIL_NETWORK = 0x01;
  /** Result code returned when the URL given was not a valid URL. */
  public static final int RESULT_FAIL_INVALID_URL = 0x02;
  /** Result code returned when no valid API was found at given URL. */
  public static final int RESULT_FAIL_NO_API = 0x03;
  /** Action ID of the broadcast sent when the detection has completed. */
  public static final String ACTION_DONE = "io.github.tjg1.library.norilib.service.ServiceTypeDetectionService.done";
  /** Parcel ID to package the API endpoint URL to test. */
  public static final String ENDPOINT_URL = "io.github.tjg1.library.norilib.clients.SearchClient.Settings.url";
  /** Parcel ID used to send the result code back to the listening activity. */
  public static final String RESULT_CODE = "io.github.tjg1.library.norilib.service.ServiceTypeDetectionService.resultCode";
  /** Parcel ID used to send the {@link io.github.tjg1.library.norilib.clients.SearchClient.Settings.APIType#ordinal()} value back to the {@link android.content.BroadcastReceiver}. */
  public static final String API_TYPE = "io.github.tjg1.library.norilib.clients.SearchClient.Settings.APIType.ordinal";
  /** Hash map containing the expected API endpoint path for each {@link io.github.tjg1.library.norilib.clients.SearchClient.Settings.APIType}. */
  private static final AbstractMap<SearchClient.Settings.APIType, String> API_ENDPOINT_PATHS;
  /** Time to wait for the HTTP requests to complete. (Gelbooru tends to be slow :() */
  private static final int REQUEST_TIMEOUT = 30;

  /** Uri schemes to use when detecting services. */
  // Would really like to default to HTTPS here, but the sad truth is that most sites either force TLS on all users
  // (yande.re) or do not support TLS at all (most sites, although danbooru is a notable exception).
  public static final String[] URI_SCHEMES = {"http://", "https://"};
  /** Uri schemes list used to prefer using TLS connections for sites that are known to support it. */
  public static final String[] URI_SCHEMES_PREFER_SSL = {"https://", "http://"};
  /** List of site URIs known to support TLS. */
  public static final List<String> TLS_SUPPORT;

  static {
    // Populate the API type -> API endpoint path hash map.
    API_ENDPOINT_PATHS = new LinkedHashMap<>();
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.DANBOORU, "/posts.xml");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.DANBOORU_LEGACY, "/post/index.xml");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.GELBOORU, "/index.php?page=dapi&s=post&q=index");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.SHIMMIE, "/api/danbooru/find_posts/index.xml");
    // Populate list of sites with known TLS support.
    TLS_SUPPORT = new ArrayList<>();
    TLS_SUPPORT.add("danbooru.donmai.us");
    TLS_SUPPORT.add("yande.re");
    TLS_SUPPORT.add("konachan.com");
  }

  /** Called by the framework to instantiate the {@link io.github.tjg1.library.norilib.service.ServiceTypeDetectionService}. */
  public ServiceTypeDetectionService() {
    super("io.github.tjg1.library.norilib.ServiceTypeDetectionService");
  }

  /** Disables detection of the Danbooru 2.x API. Only intended for testing. */
  public static void disableDanbooruDetection() {
    API_ENDPOINT_PATHS.remove(SearchClient.Settings.APIType.DANBOORU);
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    // Extract SearchClient.Settings from the received Intent.
    final Uri uri = Uri.parse(intent.getStringExtra(ENDPOINT_URL));
    final Intent broadcastIntent = new Intent(ACTION_DONE);

    if (uri.getHost() == null || uri.getScheme() == null) {
      // The URL supplied is invalid.
      sendBroadcast(broadcastIntent.putExtra(RESULT_CODE, RESULT_FAIL_INVALID_URL));
      return;
    }

    // Create the HTTP client.
    final OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setConnectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
    okHttpClient.setReadTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

    // Iterate over supported URI schemes for given URL.
    for (String uriScheme : (TLS_SUPPORT.contains(uri.getHost()) ? URI_SCHEMES_PREFER_SSL : URI_SCHEMES)) {
      String baseUri = uriScheme + uri.getHost() + uri.getPath();
      // Iterate over each endpoint path.
      for (Map.Entry<SearchClient.Settings.APIType, String> entry : API_ENDPOINT_PATHS.entrySet()) {
        // Create a HTTP request object.
        final Request request = new Request.Builder()
            .url(baseUri + entry.getValue())
            .build();

        try {
          // Fetch response.
          final Response response = okHttpClient.newCall(request).execute();
          // Make sure the response code was OK and that the HTTP client wasn't redirected along the way.
          if (response.code() == 200 && response.priorResponse() == null) {
            // Found an API endpoint.
            broadcastIntent.putExtra(RESULT_CODE, RESULT_OK);
            broadcastIntent.putExtra(ENDPOINT_URL, baseUri);
            broadcastIntent.putExtra(API_TYPE, entry.getKey().ordinal());
            sendBroadcast(broadcastIntent);
            return;
          }
        } catch (IOException e) {
          // Network error. Notify the listeners and return.
          sendBroadcast(broadcastIntent.putExtra(RESULT_CODE, RESULT_FAIL_NETWORK));
          return;
        }
      }
    }

    // End of the loop was reached without finding an API endpoint. Send error code to the BroadcastReceiver.
    sendBroadcast(broadcastIntent.putExtra(RESULT_CODE, RESULT_FAIL_NO_API));
  }
}
