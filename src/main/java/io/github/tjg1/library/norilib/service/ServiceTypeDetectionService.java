/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import io.github.tjg1.library.norilib.BuildConfig;
import io.github.tjg1.library.norilib.clients.SearchClient;
import io.github.tjg1.library.norilib.util.HashUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
  private static final int REQUEST_TIMEOUT = 30000;

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
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.DANBOARD, "/posts.xml");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.DANBOARD_LEGACY, "/post/index.xml");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.GELBOARD, "/index.php?page=dapi&s=post&q=index");
    API_ENDPOINT_PATHS.put(SearchClient.Settings.APIType.SHIMMIE, "/api/danbooru/find_posts/index.xml");
    // Populate list of sites with known TLS support.
    TLS_SUPPORT = new ArrayList<>();
    TLS_SUPPORT.add("b163eea7c4d359284718c64ed351b92ff2d2144c9cf85a6ef40253c87fb1c4e6df8c5b7e78f04c747d5e674c103320672bc769a68e28d202e092b49a5a13a768"); // danbooru.donmai.us
    TLS_SUPPORT.add("cd939698bfab6cc0b6692c40be4e3696814cd5f04e68eba92614230014ad5b44683a626253f0f539ffac19d7859120227ae3fd7a15b4beca5c6c113a49e055af"); // yande.re
    TLS_SUPPORT.add("c96a860f0c9a50a0f90ebf645b07964dacc2356a255928eefe8bb707b7316141c1d428e855077f0acf8f5ae4bc3bf5c4c31742494311afecab8044433831f8c4"); // konachan
  }

  /** Called by the framework to instantiate the {@link io.github.tjg1.library.norilib.service.ServiceTypeDetectionService}. */
  public ServiceTypeDetectionService() {
    super("io.github.tjg1.library.norilib.ServiceTypeDetectionService");
  }

  /** Disables detection of the Danbooru 2.x API. Only intended for testing. */
  public static void disableDanbooruDetection() {
    API_ENDPOINT_PATHS.remove(SearchClient.Settings.APIType.DANBOARD);
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

    // Check host for E621 as it uses the same path as DANBOORU_LEGACY.
    if("c6ce2f20c50fbc7c67fd34489bfb95a8d2ac0de0d4a44c380f8e6a8eea336a6373e8d7c33ab1a23cd64aa62ee7b7a920d0e0245165b337924e26c65f3646641e".equals(HashUtils.sha512(uri.getHost(),"nori")) || // e621.net
        "29f0eb150146b597205df6b320ce551762459663b1c2333e29b3d08a0a7fcbc98644bf8e558ceefe8ceb3101463f7a04e14ab990215dce6bdbfb941951bb00fe".equals(HashUtils.sha512(uri.getHost(),"nori"))) { // e926.net
      broadcastIntent.putExtra(RESULT_CODE, RESULT_OK);
      broadcastIntent.putExtra(ENDPOINT_URL, "https://" + uri.getHost());
      broadcastIntent.putExtra(API_TYPE, SearchClient.Settings.APIType.E621.ordinal());
      sendBroadcast((broadcastIntent));
      return;
    }

    // Iterate over supported URI schemes for given URL.
    for (String uriScheme : (TLS_SUPPORT.contains(HashUtils.sha512(uri.getHost(),"nori")) ? URI_SCHEMES_PREFER_SSL : URI_SCHEMES)) {
      String baseUri = uriScheme + uri.getHost() + uri.getPath();
      // Iterate over each endpoint path.
      for (Map.Entry<SearchClient.Settings.APIType, String> entry : API_ENDPOINT_PATHS.entrySet()) {
        String url = baseUri + entry.getValue();

        try {
          // Fetch response.
          final Response<DataEmitter> response = Ion.with(this)
              .load(url)
              .setTimeout(REQUEST_TIMEOUT)
              .userAgent("nori/" + BuildConfig.VERSION_NAME)
              .followRedirect(false)
              .noCache()
              .asDataEmitter()
              .withResponse()
              .get();

          // Close the data emitter.
          response.getResult().close();

          // Make sure the response code was OK and that the HTTP client wasn't redirected along the way.
          if (response.getHeaders().code() == 200) {
            // Found an API endpoint.
            broadcastIntent.putExtra(RESULT_CODE, RESULT_OK);
            broadcastIntent.putExtra(ENDPOINT_URL, baseUri);
            broadcastIntent.putExtra(API_TYPE, entry.getKey().ordinal());
            sendBroadcast(broadcastIntent);
            return;
          }
        } catch (InterruptedException | ExecutionException e) {
          if (BuildConfig.DEBUG) {
            Log.d("ServiceDetectionService", e.toString());
          }
        }
      }
    }

    // End of the loop was reached without finding an API endpoint. Send error code to the BroadcastReceiver.
    sendBroadcast(broadcastIntent.putExtra(RESULT_CODE, RESULT_FAIL_NO_API));
  }
}
