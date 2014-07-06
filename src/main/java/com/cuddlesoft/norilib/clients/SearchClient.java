/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package com.cuddlesoft.norilib.clients;

import com.cuddlesoft.norilib.SearchResult;

import java.io.IOException;

/**
 * Interface for a client consuming a Danbooru style API.
 */
public interface SearchClient {

  /**
   * Fetch first page of results containing images with the given set of tags.
   *
   * @param tags Search query. A space-separated list of tags.
   * @return A {@link com.cuddlesoft.norilib.SearchResult} containing a set of Images.
   * @throws IOException Network error.
   */
  public SearchResult search(String tags) throws IOException;

  /**
   * Search for images with the given set of tags.
   *
   * @param tags Search query. A space-separated list of tags.
   * @param pid  Page number. (zero-indexed)
   * @return A {@link com.cuddlesoft.norilib.SearchResult} containing a set of Images.
   * @throws java.io.IOException Network error.
   */
  public SearchResult search(String tags, int pid) throws IOException;

  /**
   * Asynchronously fetch first page of results containing image with the given set of tags.
   *
   * @param tags     Search query. A space-separated list of tags.
   * @param callback Callback listening for the SearchResult returned in the background.
   */
  public void search(String tags, SearchCallback callback);

  /**
   * Asynchronously search for images with the given set of tags.
   *
   * @param tags     Search query. A space-separated list of tags.
   * @param pid      Page number. (zero-indexed)
   * @param callback Callback listening for the SearchResult returned in the background.
   */
  public void search(String tags, int pid, SearchCallback callback);

  /**
   * Get a SFW default query to search for when an app is launched.
   *
   * @return Safe-for-work query to search for when an app is launched.
   */
  public String getDefaultQuery();

  /**
   * Check if the API server requires or supports optional authentication.
   * <p/>
   * This is used in the API server settings activity as follows:
   * If REQUIRED, the user will need to supply valid credentials.
   * If OPTIONAL, the credential form is shown, but can be left empty.
   * If NONE, the credential form will not be shown.
   *
   * @return {@link com.cuddlesoft.norilib.clients.SearchClient.AuthenticationType} value for this API backend.
   */
  public abstract AuthenticationType requiresAuthentication();

  /** Callback listening for an {@link com.cuddlesoft.norilib.SearchResult} from an asynchronous request fetched on a background thread. */
  public static interface SearchCallback {
    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem or timeout.
     *
     * @param e Exception that caused the failure.
     */
    public void onFailure(IOException e);

    /**
     * Called when the SearchResult was successfully returned by the remote server.
     *
     * @param searchResult Search result.
     */
    public void onSuccess(SearchResult searchResult);
  }

  /** API authentication types */
  public enum AuthenticationType {
    REQUIRED,
    OPTIONAL,
    NONE
  }

}
