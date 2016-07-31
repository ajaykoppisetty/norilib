/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.test;

import io.github.tjg1.library.norilib.clients.SearchClient;
import io.github.tjg1.library.norilib.clients.Shimmie;

/**
 * Tests for the Shimmie2 API client.
 */
public class ShimmieTests extends SearchClientTestCase {

  @Override
  protected SearchClient createSearchClient() {
    return new Shimmie("Dollbooru", "http://dollbooru.org");
  }

  @Override
  protected String getDefaultTag() {
    return "blonde_hair";
  }
}
