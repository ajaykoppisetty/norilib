package com.vomitcuddle.norilib.test;

import android.os.Parcel;
import android.test.AndroidTestCase;

import com.vomitcuddle.norilib.Image;

import java.util.Arrays;
import java.util.Date;

/**
 * {@link com.vomitcuddle.norilib.Image} test class.
 * TODO: Test {@link Image#getImageView(android.content.Context, com.android.volley.toolbox.ImageLoader, boolean)}
 * TODO: Test {@link Image#getThumbnailView(android.content.Context, com.android.volley.toolbox.ImageLoader, boolean)}
 */
public class ImageTest extends AndroidTestCase {
  public Image image;
  public Parcel imageParcel;

  @Override
  public void setUp() throws Exception {
    // Create a new Image.
    image = new Image();
    image.fileUrl = "http://safebooru.org//images/749/9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg";
    image.width = 1000;
    image.height = 750;
    image.sampleUrl = "http://safebooru.org//images/749/9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg";
    image.sampleWidth = 900;
    image.sampleHeight = 600;
    image.webUrl = "http://safebooru.org/index.php?page=post&s=view&id=755027";
    image.previewUrl = "http://safebooru.org/thumbnails/749/thumbnail_9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg";
    image.previewWidth = 150;
    image.previewHeight = 112;
    image.generalTags = new String[]{"arm_grab", "bad_id", "black_hair", "blush", "bust", "female",
        "gloves", "hair_ribbon", "hairband", "jacket", "multiple_girls", "pink_eyes", "pink_hair",
        "purple_eyes", "ribbon", "scarf", "shared_scarf", "smile", "snowing", "twintails", "violey_eyes",
        "wink", "yuri"};
    image.artistTags = new String[]{"tsuyushiro"};
    image.copyrightTags = new String[]{"mahou_shoujo_madoka_magica"};
    image.characterTags = new String[]{"kaname_madoka", "akemi_homura"};
    image.id = 755027L;
    image.parentId = -1L;
    image.pixivId = 24377242L;
    image.obscenityRating = Image.ObscenityRating.SAFE;
    image.score = 2;
    image.source = "http://img75.pixiv.net/img/tsuyushiro/24377242.jpg";
    image.md5 = "46cecad6d347e612aa2679eaa58a918e";
    image.hasComments = false;
    image.createdAt = new Date(1326560522);
    // Parcel image.
    imageParcel = Parcel.obtain();
    image.writeToParcel(imageParcel, 0);
    imageParcel.setDataPosition(0);
  }

  /** Tests un-parceling */
  public void testParcel() throws Throwable {
    Image image = Image.CREATOR.createFromParcel(imageParcel);
    assert image != null;
    assertEquals("http://safebooru.org//images/749/9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg", image.fileUrl);
    assertEquals(1000, (int) image.width);
    assertEquals(750, (int) image.height);
    assertEquals("http://safebooru.org//images/749/9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg", image.sampleUrl);
    assertEquals(900, (int) image.sampleWidth);
    assertEquals(600, (int) image.sampleHeight);
    assertEquals("http://safebooru.org/index.php?page=post&s=view&id=755027", image.webUrl);
    assertEquals("http://safebooru.org/thumbnails/749/thumbnail_9636623c63a6fdd58250a7b822965dbdbe9adc6c.jpg", image.previewUrl);
    assertEquals(150, (int) image.previewWidth);
    assertEquals(112, (int) image.previewHeight);
    assertTrue(Arrays.deepEquals(new String[]{"arm_grab", "bad_id", "black_hair", "blush", "bust", "female",
        "gloves", "hair_ribbon", "hairband", "jacket", "multiple_girls", "pink_eyes", "pink_hair",
        "purple_eyes", "ribbon", "scarf", "shared_scarf", "smile", "snowing", "twintails", "violey_eyes",
        "wink", "yuri"}, image.generalTags));
    assertTrue(Arrays.deepEquals(new String[]{"tsuyushiro"}, image.artistTags));
    assertTrue(Arrays.deepEquals(new String[]{"mahou_shoujo_madoka_magica"}, image.copyrightTags));
    assertTrue(Arrays.deepEquals(new String[]{"kaname_madoka", "akemi_homura"}, image.characterTags));
    assertEquals(755027L, (long) image.id);
    assertEquals(-1L, (long) image.parentId);
    assertEquals(24377242L, (long) image.pixivId);
    assertEquals(Image.ObscenityRating.SAFE, image.obscenityRating);
    assertEquals(2, (int) image.score);
    assertEquals("http://img75.pixiv.net/img/tsuyushiro/24377242.jpg", image.source);
    assertEquals("46cecad6d347e612aa2679eaa58a918e", image.md5);
    assertEquals(false, image.hasComments);
    assertEquals(1326560522L, image.createdAt.getTime());
  }

  public void testGetPixivUrl() throws Throwable {
    assertEquals("http://www.pixiv.net/member_illust.php?mode=medium&illust_id=24377242", image.getPixivUrl());
  }

  @Override
  protected void tearDown() throws Exception {
    imageParcel.recycle();
  }
}
