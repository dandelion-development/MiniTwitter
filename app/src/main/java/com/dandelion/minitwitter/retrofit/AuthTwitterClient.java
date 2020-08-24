package com.dandelion.minitwitter.retrofit;

import com.dandelion.minitwitter.common.CommonItems;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// [Auth Twitter Client]: API client based on MiniTwitterService where are specified available methods

/**
 *  Auth Twitter CLient
 *  - Client for requests where authentication token is required
 *  - This class implements -Interceptor- class in order to include security token in requests
 *  - Interceptor takes will attach token in request header data
 */
public class AuthTwitterClient {
    // [Vars]
    private static AuthTwitterClient instance = null;
    private AuthTwitterService authTwitterService;
    private Retrofit retrofit;

    // (Constructor)
    public AuthTwitterClient() {
        // Interceptor instance > token attachment in headers
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new AuthInterceptor());
        OkHttpClient httpClient = httpClientBuilder.build();
        // Retrofit constructor
        retrofit = new Retrofit.Builder()
            .baseUrl(CommonItems.API_MINITWITTER_ENDPOINT)          // API endpoint address
            .addConverterFactory(GsonConverterFactory.create())     // Response conversion model
            .client(httpClient)                                     // Http client user for requests
                                                                    // ** This includes interceptor for security token
            .build();
        // Makes an instance of service
        authTwitterService = retrofit.create(AuthTwitterService.class);
    }

    // [Methods]

    // MiniTwitter Client instance creation (Singleton Pattern)
    public static AuthTwitterClient getInstance() {
        // If don't have a previous instance of MiniTwitterClient new instance is created
        // ** Singleton pattern model (in order to avoid use resources and preserve memory)
        if (instance == null) {
            instance = new AuthTwitterClient();
        }
        return instance;
    }

    // Gets a MiniTwitter Service instance (that is created in constructor as default)
    public AuthTwitterService getAuthTwitterService() {
        return authTwitterService;
    }
}
