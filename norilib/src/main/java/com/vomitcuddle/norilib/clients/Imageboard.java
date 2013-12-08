package com.vomitcuddle.norilib.clients;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** Base imageboard client class */
public abstract class Imageboard {

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
}
