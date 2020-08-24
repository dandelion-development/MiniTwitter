package com.dandelion.minitwitter.retrofit;

import com.dandelion.minitwitter.common.CommonItems;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// [Mini Twitter Client]: API client based on MiniTwitterService where are specified available methods
public class MiniTwitterClient {
    // [Vars]
    private static MiniTwitterClient instance = null;
    private MiniTwitterService miniTwitterService;
    private Retrofit retrofit;

    // (Constructor)
    public MiniTwitterClient() {
        retrofit = new Retrofit.Builder()
            .baseUrl(CommonItems.API_MINITWITTER_ENDPOINT)          // API endpoint address
            .addConverterFactory(GsonConverterFactory.create())     // Response conversion model
            .build();
        // Makes an instance of service
        miniTwitterService = retrofit.create(MiniTwitterService.class);
    }

    // [Methods]

    // MiniTwitter Client instance creation (Singleton Pattern)
    public static MiniTwitterClient getInstance() {
        // If don't have a previous instance of MiniTwitterClient new instance is created
        // ** Singleton pattern model (in order to avoid use resources and preserve memory)
        if (instance == null) {
            instance = new MiniTwitterClient();
        }
        return instance;
    }

    // Gets a MiniTwitter Service instance (that is created in constructor as default)
    public MiniTwitterService getMiniTwitterService() {
        return miniTwitterService;
    }
}
