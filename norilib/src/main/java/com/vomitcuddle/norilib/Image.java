package com.vomitcuddle.norilib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vomitcuddle.norilib.util.SquareNetworkImageView;

import java.util.Date;

public class Image implements Parcelable {
  /** Class loader used when deserializing from {@link Parcel}. */
  public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
    @Override
    public Image createFromParcel(Parcel source) {
      return new Image(source);
    }

    @Override
    public Image[] newArray(int size) {
      return new Image[size];
    }
  };
  /** Image URL. */
  public String fileUrl;
  /** Image width */
  public Integer width;
  /** Image height */
  public Integer height;
  /** Web URL */
  public String webUrl;
  /** Thumbnail URL */
  public String previewUrl;
  /** Thumbnail width */
  public Integer previewWidth;
  /** Thumbnail height */
  public Integer previewHeight;
  /** Sample URL */
  public String sampleUrl;
  /** Sample width */
  public Integer sampleWidth;
  /** Sample height */
  public Integer sampleHeight;
  /** General tags */
  public String[] generalTags;
  /** Artist tags */
  public String[] artistTags;
  /** Character tags */
  public String[] characterTags;
  /** Copyright tags */
  public String[] copyrightTags;
  /** Image ID */
  public Long id;
  /** Parent ID */
  public Long parentId;
  /** Pixiv ID */
  public Long pixivId;
  /** Obscenity rating */
  public ObscenityRating obscenityRating;
  /** Popularity score */
  public Integer score;
  /** Source URL */
  public String source;
  /** MD5 hash */
  public String md5;
  /** Has comments */
  public boolean hasComments = false;
  /** Creation date */
  public Date createdAt;

  /** Default constructor. */
  public Image() {
  }

  /**
   * Constructor used when deserializing from a {@link Parcel}.
   *
   * @param in {@link Parcel} to read values from.
   */
  protected Image(Parcel in) {
    // Read values from parcel.
    fileUrl = in.readString();
    width = in.readInt();
    height = in.readInt();
    webUrl = in.readString();
    previewUrl = in.readString();
    previewWidth = in.readInt();
    previewHeight = in.readInt();
    sampleUrl = in.readString();
    sampleWidth = in.readInt();
    sampleHeight = in.readInt();
    generalTags = in.createStringArray();
    artistTags = in.createStringArray();
    characterTags = in.createStringArray();
    copyrightTags = in.createStringArray();
    id = in.readLong();
    parentId = in.readLong();
    pixivId = in.readLong();
    obscenityRating = (ObscenityRating) in.readSerializable();
    score = in.readInt();
    source = in.readString();
    md5 = in.readString();
    hasComments = in.readByte() != 0x00;
    final long tmpCreatedAt = in.readLong();
    createdAt = tmpCreatedAt != -1 ? new Date(tmpCreatedAt) : null;
  }

  /**
   * Create a NetworkImageView for this image.
   *
   * @param context     Context.
   * @param imageLoader Volley {@link com.android.volley.toolbox.ImageLoader}.
   * @param useSample   Use lower res sample URL.
   * @return Volley {@link com.android.volley.toolbox.NetworkImageView}.
   */
  public NetworkImageView getImageView(Context context, ImageLoader imageLoader, boolean useSample) {
    final NetworkImageView iv = new NetworkImageView(context);
    iv.setImageUrl(useSample ? sampleUrl : fileUrl, imageLoader);
    return iv;
  }

  /**
   * Create a thumbnail NetworkImageView for this image.
   *
   * @param context                Context.
   * @param imageLoader            Volley {@link com.android.volley.toolbox.ImageLoader}.
   * @param forceSquareAspectRatio Force a 1:1 aspect ratio.
   * @return Volley {@link com.android.volley.toolbox.NetworkImageView}.
   */
  public NetworkImageView getThumbnailView(Context context, ImageLoader imageLoader, boolean forceSquareAspectRatio) {
    final NetworkImageView iv = forceSquareAspectRatio ? new SquareNetworkImageView(context) : new NetworkImageView(context);
    iv.setImageUrl(previewUrl, imageLoader);
    return iv;
  }

  /**
   * Gets the pixiv.net URL for this image.
   *
   * @return URL to this {@link Image}'s Pixiv page, null if {@link #pixivId} not set.
   */
  public String getPixivUrl() {
    return pixivId != -1 ? "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + pixivId : null;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    // Write values to Parcel.
    dest.writeString(fileUrl);
    dest.writeInt(width);
    dest.writeInt(height);
    dest.writeString(webUrl);
    dest.writeString(previewUrl);
    dest.writeInt(previewWidth);
    dest.writeInt(previewHeight);
    dest.writeString(sampleUrl);
    dest.writeInt(sampleWidth);
    dest.writeInt(sampleHeight);
    dest.writeStringArray(generalTags);
    dest.writeStringArray(artistTags);
    dest.writeStringArray(characterTags);
    dest.writeStringArray(copyrightTags);
    dest.writeLong(id);
    dest.writeLong(parentId);
    dest.writeLong(pixivId);
    dest.writeSerializable(obscenityRating);
    dest.writeInt(score);
    dest.writeString(source);
    dest.writeString(md5);
    dest.writeByte((byte) (hasComments ? 0x01 : 0x00));
    dest.writeLong(createdAt != null ? createdAt.getTime() : -1L);
  }

  /** Obscenity ratings */
  public enum ObscenityRating {
    UNDEFINED,
    SAFE,
    QUESTIONABLE,
    EXPLICIT
  }
}
