package com.vomitcuddle.norilib;

import android.os.Parcel;
import android.os.Parcelable;

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

  public ServiceSettings() {
  }

  public ServiceSettings(Parcel in) {
    this.apiUrl = in.readString();
    this.apiType = (ServiceType) in.readSerializable();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(apiUrl);
    dest.writeSerializable(apiType);
  }

  public static enum ServiceType {
    DANBOORU,
    DANBOORU_LEGACY,
    GELBOORU,
    SHIMMIE2
  }
}
