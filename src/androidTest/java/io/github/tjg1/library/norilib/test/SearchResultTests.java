/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.test;

import android.os.Bundle;
import android.test.AndroidTestCase;

import io.github.tjg1.library.norilib.Image;
import io.github.tjg1.library.norilib.SearchResult;
import io.github.tjg1.library.norilib.Tag;

import static org.fest.assertions.api.Assertions.assertThat;

/** Tests the {@link io.github.tjg1.library.norilib.SearchResult} test. */
public class SearchResultTests extends AndroidTestCase {

  /** Verify that SearchResult can be written and read from Parcels correctly. */
  public void testWriteToParcel() throws Throwable {
    final SearchResult original = getMockSearchResult();
    final SearchResult unParceled;
    final Bundle bundle = new Bundle();

    // Parcel and un-parcel the original SearchResult.
    bundle.putParcelable("search-result", original);
    unParceled = bundle.getParcelable("search-result");

    // Verify un-parceled data.
    assertThat(unParceled.getImages()).containsOnly(original.getImages());
    assertThat(unParceled.getCurrentOffset()).isEqualTo(original.getCurrentOffset());
    assertThat(unParceled.getQuery()).containsOnly(original.getQuery());
    assertThat(unParceled.hasNextPage()).isEqualTo(original.hasNextPage());
  }

  /** Tests the {@link SearchResult#filter(io.github.tjg1.library.norilib.Tag...)}  method. */
  public void testFilterWithTags() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    searchResult.filter(new Tag("duck"));
    assertThat(searchResult.getImages()).hasSize(1);
    assertThat(searchResult.getImages()[0].tags[0].getName()).isEqualTo("bird");
  }

  /** Tests the {@link io.github.tjg1.library.norilib.SearchResult#addImages(io.github.tjg1.library.norilib.Image[], int)} method. */
  public void testAddImages() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    searchResult.addImages(new Image[]{ImageTests.getMockImage(Image.ObscenityRating.EXPLICIT, new Tag("quack"))}, 20);
    assertThat(searchResult.getImages()).hasSize(3);
    assertThat(searchResult.getImages()[2].obscenityRating).isEqualTo(Image.ObscenityRating.EXPLICIT);
    assertThat(searchResult.getCurrentOffset()).isEqualTo(20);
  }

  /** Tests the {@link io.github.tjg1.library.norilib.SearchResult#getImages()} method. */
  public void testGetImages() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    assertThat(searchResult.getImages()).isNotEmpty();
  }

  /** Tests the {@link io.github.tjg1.library.norilib.SearchResult#filter(io.github.tjg1.library.norilib.Image.ObscenityRating...)} method. */
  public void testFilterWithObscenityRating() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    searchResult.filter(Image.ObscenityRating.QUESTIONABLE);
    assertThat(searchResult.getImages()).hasSize(1);
    assertThat(searchResult.getImages()[0].obscenityRating).isEqualTo(Image.ObscenityRating.QUESTIONABLE);
  }

  /** Tests the {@link io.github.tjg1.library.norilib.SearchResult#getCurrentOffset()} method. */
  public void testGetCurrentOffset() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    assertThat(searchResult.getCurrentOffset()).isEqualTo(0);
    searchResult.addImages(new Image[]{searchResult.getImages()[0]}, 30);
    assertThat(searchResult.getCurrentOffset()).isEqualTo(30);
  }

  /** Tests the {@link io.github.tjg1.library.norilib.SearchResult#onLastPage()} method. */
  public void testOnLastPage() throws Throwable {
    final SearchResult searchResult = getMockSearchResult();
    assertThat(searchResult.hasNextPage()).isTrue();
    searchResult.onLastPage();
    assertThat(searchResult.hasNextPage()).isFalse();
  }

  /** Create a SearchResult with fake data suitable for testing. */
  static SearchResult getMockSearchResult() {
    final Image[] images = new Image[]{
        ImageTests.getMockImage(Image.ObscenityRating.SAFE,
            new Tag("duck"), new Tag("quack")),
        ImageTests.getMockImage(Image.ObscenityRating.QUESTIONABLE,
            new Tag("bird"))
    };
    return new SearchResult(images, new Tag[]{new Tag("Tag")}, 0);
  }
}
