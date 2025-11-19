package com.motivation.quotes.app.api;

import android.util.Log;

import com.growwthapps.dailypost.v2.BuildConfig;
import com.motivation.quotes.app.MyApplication;
import com.motivation.quotes.app.utils.Constant;
import com.motivation.quotes.app.utils.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static String App_URl = BuildConfig.APP_URL;

    public static ApiService getApiDataService() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", new PreferenceManager(MyApplication.getAppContext()).getString(Constant.api_key))
                            .header("Content-Type", "text/plain")
                            .method(original.method(), original.body())
                            .build();

                    Response response = chain.proceed(request);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < 3) {
                        Log.d("ApiClient", "Retrying request (" + tryCount + ")");
                        tryCount++;
                        response.close();
                        response = chain.proceed(original);
                    }

                    return response;
                })
                .addInterceptor(new CurlLoggingInterceptor()) // 👈 Add cURL interceptor here
                .build();

        return new Retrofit.Builder()
                .baseUrl(App_URl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }

    // Custom cURL logging interceptor
    private static class CurlLoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            StringBuilder curlCmd = new StringBuilder("curl -X ")
                    .append(request.method())
                    .append(" '")
                    .append(request.url())
                    .append("'");

            // Add headers
            for (String name : request.headers().names()) {
                curlCmd.append(" -H '")
                        .append(name)
                        .append(": ")
                        .append(request.header(name))
                        .append("'");
            }

            // Add body (if present)
            if (request.body() != null) {
                okio.Buffer buffer = new okio.Buffer();
                request.body().writeTo(buffer);
                String body = buffer.readUtf8();
                curlCmd.append(" --data '").append(body).append("'");
            }

            Log.d("CURL", curlCmd.toString());
            return chain.proceed(request);
        }
    }
}
