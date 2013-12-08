package com.vomitcuddle.norilib.util;

import android.os.AsyncTask;

import com.vomitcuddle.norilib.ServiceSettings;
import com.vomitcuddle.norilib.clients.Danbooru;
import com.vomitcuddle.norilib.clients.DanbooruLegacy;
import com.vomitcuddle.norilib.clients.Gelbooru;
import com.vomitcuddle.norilib.clients.Shimmie2;

import java.net.MalformedURLException;

public class ServiceDetectionTask extends AsyncTask<String, Object, ServiceSettings.ServiceType> {

  @Override
  protected ServiceSettings.ServiceType doInBackground(String... params) {
    if (params.length == 0)
      return null;
    final String url = params[0];

    try {
      if (Danbooru.verifyUrl(url))
        return ServiceSettings.ServiceType.DANBOORU;
      else if (DanbooruLegacy.verifyUrl(url))
        return ServiceSettings.ServiceType.DANBOORU_LEGACY;
      else if (Gelbooru.verifyUrl(url))
        return ServiceSettings.ServiceType.GELBOORU;
      else if (Shimmie2.verifyUrl(url))
        return ServiceSettings.ServiceType.SHIMMIE2;
    } catch (MalformedURLException ignored) {
    }
    return null;
  }
}
