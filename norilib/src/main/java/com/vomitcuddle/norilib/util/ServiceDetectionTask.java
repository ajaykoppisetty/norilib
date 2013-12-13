/*
 * This file is part of norilib.
 * Copyright (c) 2013 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
