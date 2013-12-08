package com.vomitcuddle.norilib;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceDetectionTask extends AsyncTask<ServiceSettings, Object, ServiceSettings> {
  /** Expected API path for Danbooru 2.x APIs. */
  private static final String API_PATH_DANBOORU = "/posts.xml";
  /** Expected API path for Danbooru 1.x APIs. */
  private static final String API_PATH_DANBOORU_LEGACY = "/post/index.xml";
  /** Expected API path for Gelbooru APIs */
  private static final String API_PATH_GELBOORU = "/index.php?page=dapi&s=post&q=index";
  /** Expected API path for Shimmie2 APIs */
  private static final String API_PATH_SHIMMIE2 = "/api/danbooru/find_posts/index.xml";

  @Override
  protected ServiceSettings doInBackground(ServiceSettings... params) {
    if (params.length == 0)
      return null;
    final ServiceSettings serviceSettings = params[0];
    final Uri uri = Uri.parse(serviceSettings.apiUrl);

    // Make sure URI is valid.
    if (uri.getHost() == null || uri.getScheme() == null)
      return null;
    // Create a base URL, discarding any path/query the user may have supplied.
    final String baseUrl = uri.getScheme() + "://" + uri.getHost();

    for (String apiPath : new String[]{API_PATH_DANBOORU, API_PATH_DANBOORU_LEGACY, API_PATH_GELBOORU, API_PATH_SHIMMIE2}) {
      // Create a new HTTP connection.
      try {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + apiPath).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        // Get HTTP response code.
        final int responseCode = connection.getResponseCode();

        // If response code is okay, return the ServiceSettings with updated URL/ServiceType.
        if (responseCode == HttpStatus.SC_OK) {
          if (apiPath.equals(API_PATH_DANBOORU))
            serviceSettings.apiType = ServiceSettings.ServiceType.DANBOORU;
          else if (apiPath.equals(API_PATH_DANBOORU_LEGACY))
            serviceSettings.apiType = ServiceSettings.ServiceType.DANBOORU_LEGACY;
          else if (apiPath.equals(API_PATH_GELBOORU))
            serviceSettings.apiType = ServiceSettings.ServiceType.GELBOORU;
          else if (apiPath.equals(API_PATH_SHIMMIE2))
            serviceSettings.apiType = ServiceSettings.ServiceType.SHIMMIE2;
          // Replace user-supplied API URL with baseUrl
          serviceSettings.apiUrl = baseUrl;

          // Return updated settings.
          return serviceSettings;
        }
      } catch (MalformedURLException e) {
        // Invalid URL.
        return null;
      } catch (IOException ignored) {
      }
    }
    return null;
  }
}
