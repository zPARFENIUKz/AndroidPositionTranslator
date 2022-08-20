package cl.coders.faketraveler;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MockLocationProvider {
    String providerName;
    Context ctx;

    /**
     * Class constructor
     *
     * @param name provider
     * @param ctx  context
     * @return Void
     */
    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        try
        {
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 1, 2);
            lm.setTestProviderEnabled(providerName, true);
        } catch(SecurityException e) {
            throw new SecurityException("Not allowed to perform MOCK_LOCATION");
        }
    }

    /**
     * Pushes the location in the system (mock). This is where the magic gets done.
     *
     * @param lat latitude
     * @param lon longitude
     * @return Void
     */
    public void pushLocation(final double lat, final double lon) {
        final LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        final Location mockLocation = new Location(providerName);
        CountDownTimer countDownTimer;
        final double[] latitude = new double[1];
        final double[] longitude = new double[1];
        latitude[0] = 0.0;
        longitude[0] = 0.0;
        String urlLatitude = "https://gentle-depths-24532.herokuapp.com/?code=5&username=sasha&geo=0";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlLatitude).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                {
                    String myResponse = response.body().string();
                    if (myResponse != "No User")
                    {
                        latitude[0] = Double.valueOf(myResponse);
                        String urlLongitude = "https://gentle-depths-24532.herokuapp.com/?code=5&username=sasha&geo=1";
                        OkHttpClient client1 = new OkHttpClient();
                        Request request1 = new Request.Builder().url(urlLongitude).build();
                        client1.newCall(request1).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful())
                                {
                                    String myResponse = response.body().string();
                                    if (myResponse != "No User")
                                    {
                                        longitude[0] = Double.valueOf(myResponse);
                                        mockLocation.setLatitude(latitude[0]);
                                        mockLocation.setLongitude(longitude[0]);
                                        mockLocation.setAltitude(3F);
                                        mockLocation.setTime(System.currentTimeMillis());
                                        //mockLocation.setAccuracy(16F);
                                        mockLocation.setSpeed(0.01F);
                                        mockLocation.setBearing(1F);
                                        mockLocation.setAccuracy(3F);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            mockLocation.setBearingAccuracyDegrees(0.1F);
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            mockLocation.setVerticalAccuracyMeters(0.1F);
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            mockLocation.setSpeedAccuracyMetersPerSecond(0.01F);
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                                        }
                                        lm.setTestProviderLocation(providerName, mockLocation);
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * Removes the provider
     *
     * @return Void
     */
    public void shutdown() {
        LocationManager lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        lm.removeTestProvider(providerName);
    }
}
