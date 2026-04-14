package com.lesadrax.registrationclient.data.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //private static final String BASE_URL = "http://192.168.0.126:8081"; // Remplacez par l'URL de base de votre API
    private static final String BASE_URL = "http://167.172.239.61:8081"; // Remplacez par l'URL de base de votre API
    private static Retrofit retrofit = null;

    public static Retrofit getClient(final String token) {
        // Créer un intercepteur pour ajouter le token dans les en-têtes


        if (retrofit == null) {

            System.out.println("Headers envoyés : " + token);

            String addHeader = "Bearer " + token;
            System.out.println("Headers envoyés : " + addHeader);

        // Construire un client HTTP avec l'intercepteur
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS);

        client.addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", addHeader)
                            .build();

            System.out.println("Headers envoyés : " + request.headers());
            System.out.println("Headers envoyés : " + addHeader);


            return chain.proceed(request);
        });

        // Initialiser Retrofit

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
