/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package com.cuddlesoft.norilib.test;

import com.cuddlesoft.norilib.clients.Gelbooru;
import com.cuddlesoft.norilib.clients.SearchClient;

/**
 * Tests for the Gelbooru API client.
 */
public class GelbooruTest extends SearchClientTestCase {

  @Override
  protected SearchClient createSearchClient() {
    return new Gelbooru("Gelbooru", "http://gelbooru.com");
  }
}
