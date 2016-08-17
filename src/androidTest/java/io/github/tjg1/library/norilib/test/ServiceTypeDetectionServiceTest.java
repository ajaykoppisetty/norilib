/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package io.github.tjg1.library.norilib.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.InstrumentationTestCase;

import io.github.tjg1.library.norilib.clients.SearchClient;
import io.github.tjg1.library.norilib.service.ServiceTypeDetectionService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

/** Test unit for the {@link io.github.tjg1.library.norilib.service.ServiceTypeDetectionService} service. */
public class ServiceTypeDetectionServiceTest extends InstrumentationTestCase {
  /** Wait this many seconds for the detection to complete. */
  private static final int RESPONSE_TIMEOUT = 120;
  /** {@link IntentFilter} used to receive responses from {@link io.github.tjg1.library.norilib.service.ServiceTypeDetectionService}. */
  private final static IntentFilter INTENT_FILTER = new IntentFilter(ServiceTypeDetectionService.ACTION_DONE);

  /** Test detection of the Danbooru 2.x API */
  public void testDanbooruDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://danbooru.donmai.us/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.DANBOARD.ordinal());
  }

  /** Test detection of the Danbooru 1.x API */
  public void testDanbooruLegacyDetection() throws Throwable {
    // FIXME: Can't think of any sites that would expose the Danbooru 1.x API exclusively
    ServiceTypeDetectionService.disableDanbooruDetection();
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://danbooru.donmai.us/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.DANBOARD_LEGACY.ordinal());
  }

  /** Test detection of the Gelbooru API. */
  public void testGelbooruDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://safebooru.org/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.GELBOARD.ordinal());
  }

  /** Test detection of the Shimmie API. */
  public void testShimmieDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://dollbooru.org/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.SHIMMIE.ordinal());
  }

  /** Test detection of Moebooru (Danbooru 1.x fork) boards. */
  public void testMoebooruDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://yande.re/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.DANBOARD_LEGACY.ordinal());
  }

  /** Test detection of the Mono-sodium Glutamate API. */
  public void testE621Detection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];
    final String[] endpointUrl = new String[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            endpointUrl[0] = intent.getStringExtra(ServiceTypeDetectionService.ENDPOINT_URL);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://e621.net/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(endpointUrl[0]).isEqualTo("https://e621.net");
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.E621.ordinal());
  }

  /** Test detection of the Chlorine dioxide API. */
  public void testE926Detection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];
    final String[] endpointUrl = new String[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            endpointUrl[0] = intent.getStringExtra(ServiceTypeDetectionService.ENDPOINT_URL);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://e926.net/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(endpointUrl[0]).isEqualTo("https://e926.net");
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.E621.ordinal());
  }

  /** Test detection of the Chlorine dioxide API. */
  public void testFlickrDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];
    final String[] endpointUrl = new String[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            endpointUrl[0] = intent.getStringExtra(ServiceTypeDetectionService.ENDPOINT_URL);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://api.flickr.com/services/"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(endpointUrl[0]).isEqualTo("https://api.flickr.com/services/rest");
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.FLICKR.ordinal());
  }

  /** Test detection of the Chlorine dioxide API. */
  public void testFlickrUserDetection() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];
    final int[] serviceType = new int[1];
    final String[] endpointUrl = new String[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            serviceType[0] = intent.getIntExtra(ServiceTypeDetectionService.API_TYPE, -1);
            endpointUrl[0] = intent.getStringExtra(ServiceTypeDetectionService.ENDPOINT_URL);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://flickr.com/photos/128962151@N05"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_OK);
    assertThat(endpointUrl[0]).isEqualTo("https://flickr.com/photos/128962151@N05");
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.FLICKR_USER.ordinal());
  }

  /** Test error returned when an invalid URL is supplied. */
  public void testInvalidUrlError() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "danbooru###$(%*#)@(*)(#@*)(os"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_FAIL_INVALID_URL);
  }

  /** Test detection of sites that do not expose a supported API */
  public void testNoServiceDetected() throws Throwable {
    // Create a lock that waits for the request to complete in background.
    final CountDownLatch lock = new CountDownLatch(1);
    // Values received from the BroadcastReceiver.
    // One-element arrays are a hack used to set values from outside the main thread without bothering with locking.
    final int[] resultCode = new int[1];

    runTestOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Register the broadcast receiver.
        getInstrumentation().getContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            // Set values received in the intent.
            resultCode[0] = intent.getIntExtra(ServiceTypeDetectionService.RESULT_CODE, -1);
            // Unregister broadcast receiver.
            getInstrumentation().getContext().unregisterReceiver(this);
            // Clear the lock in the main thread.
            lock.countDown();
          }
        }, INTENT_FILTER);
        // Start the service.
        getInstrumentation().getContext().startService(new Intent(getInstrumentation().getContext(),
            ServiceTypeDetectionService.class)
            .putExtra(ServiceTypeDetectionService.ENDPOINT_URL, "http://google.com"));
      }
    });

    // Wait to receive broadcast from the service.
    lock.await(RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    assertThat(resultCode[0]).isEqualTo(ServiceTypeDetectionService.RESULT_FAIL_NO_API);
  }
}
