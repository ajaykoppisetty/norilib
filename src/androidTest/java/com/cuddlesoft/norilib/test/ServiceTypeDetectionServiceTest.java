/*
 * This file is part of nori.
 * Copyright (c) 2014 Tomasz Jan Góralczyk <tomg@fastmail.uk>
 * License: ISC
 */

package com.cuddlesoft.norilib.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.InstrumentationTestCase;

import com.cuddlesoft.norilib.clients.SearchClient;
import com.cuddlesoft.norilib.service.ServiceTypeDetectionService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

/** Test unit for the {@link com.cuddlesoft.norilib.service.ServiceTypeDetectionService} service. */
public class ServiceTypeDetectionServiceTest extends InstrumentationTestCase {
  /** Wait this many seconds for the detection to complete. */
  private static final int RESPONSE_TIMEOUT = 120;
  /** {@link IntentFilter} used to receive responses from {@link com.cuddlesoft.norilib.service.ServiceTypeDetectionService}. */
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
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.DANBOORU.ordinal());
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
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.DANBOORU_LEGACY.ordinal());
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
    assertThat(serviceType[0]).isEqualTo(SearchClient.Settings.APIType.GELBOORU.ordinal());
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
