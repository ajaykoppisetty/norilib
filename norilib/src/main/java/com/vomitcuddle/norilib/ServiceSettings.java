package com.vomitcuddle.norilib;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.RequestQueue;
import com.vomitcuddle.norilib.clients.Danbooru;
import com.vomitcuddle.norilib.clients.DanbooruLegacy;
import com.vomitcuddle.norilib.clients.Gelbooru;
import com.vomitcuddle.norilib.clients.Imageboard;
import com.vomitcuddle.norilib.clients.Shimmie2;

public class ServiceSettings implements Parcelable {
  public static final Creator<ServiceSettings> CREATOR = new Creator<ServiceSettings>() {
    @Override
    public ServiceSettings createFromParcel(Parcel source) {
      return new ServiceSettings(source);
    }

    @Override
    public ServiceSettings[] newArray(int size) {
      return new ServiceSettings[size];
    }
  };
  /** URL to the service's API endpoint. */
  public String apiUrl;
  /** API version/type. */
  public ServiceType apiType;
  /** API Username */
  public String username;
  /** API Password */
  public String password;

  public ServiceSettings() {
  }

  public ServiceSettings(Parcel in) {
    this.apiUrl = in.readString();
    this.apiType = (ServiceType) in.readSerializable();
    if (in.readByte() == 0x01)
      this.username = in.readString();
    if (in.readByte() == 0x01)
      this.password = in.readString();
  }

  /**
   * Creates a new Imageboard based on the {@link com.vomitcuddle.norilib.ServiceSettings}.
   *
   * @param requestQueue Volley {@link com.android.volley.RequestQueue}.
   * @return Imageboard client with current settings.
   */
  public Imageboard createClient(RequestQueue requestQueue) {
    switch (apiType) {
      case DANBOORU:
        if (username != null && password != null)
          return new Danbooru(requestQueue, apiUrl, username, password);
        return new Danbooru(requestQueue, apiUrl);
      case DANBOORU_LEGACY:
        if (username != null && password != null)
          return new DanbooruLegacy(apiUrl, requestQueue, username, password);
        return new Danbooru(requestQueue, apiUrl);
      case GELBOORU:
        if (username != null && password != null)
          return new Gelbooru(apiUrl, requestQueue, username, password);
        return new Gelbooru(apiUrl, requestQueue);
      case SHIMMIE2:
        if (username != null && password != null)
          return new Shimmie2(apiUrl, requestQueue, username, password);
        return new Shimmie2(apiUrl, requestQueue);
      default:
        return null;
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(apiUrl);
    dest.writeSerializable(apiType);
    // Write 0x01 if username isn't null.
    dest.writeByte((byte) (username != null ? 0x01 : 0x00));
    if (username != null)
      dest.writeString(username);
    // Write 0x01 if password isn't null.
    dest.writeByte((byte) (password != null ? 0x01 : 0x00));
    if (password != null)
      dest.writeString(password);
  }

  public static enum ServiceType {
    DANBOORU,
    DANBOORU_LEGACY,
    GELBOORU,
    SHIMMIE2
  }
}
