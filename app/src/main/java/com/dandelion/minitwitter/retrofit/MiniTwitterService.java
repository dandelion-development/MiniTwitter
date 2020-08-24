package com.dandelion.minitwitter.retrofit;
// [Imports]
import com.dandelion.minitwitter.retrofit.request.RequestLogin;
import com.dandelion.minitwitter.retrofit.request.RequestSignup;
import com.dandelion.minitwitter.retrofit.response.ResponseAuth;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// [Mini Twitter Service]: Retrofit based application service

/**
 * MiniTwitter Service > REST API based service interface using Retrofit
 * - This class contains API supported methods that can be used
 * - Request && Response classes are previously generated based on API schema
 * - All methods are Async so are defined by using Call expression
 * - In order to make easy configure, test and deploy APIs is recommended use some tool
 * - For this sample Swagger has been used in order to create and manage APIs
 * - Too is required implement Retrofit && GSon libraries in order to make easier APIs consumption
 */
public interface MiniTwitterService {
    @POST("auth/login")
    Call<ResponseAuth> doLogin(@Body RequestLogin requestLogin);

    @POST("auth/signup")
    Call<ResponseAuth> doSignUp(@Body RequestSignup requestSignup);
}
