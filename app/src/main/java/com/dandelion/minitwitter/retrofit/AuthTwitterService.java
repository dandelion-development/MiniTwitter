package com.dandelion.minitwitter.retrofit;
// [Imports]
import com.dandelion.minitwitter.retrofit.request.RequestCreateTweet;
import com.dandelion.minitwitter.retrofit.request.RequestUserProfile;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import com.dandelion.minitwitter.retrofit.response.TweetDeleted;
import com.dandelion.minitwitter.retrofit.response.UploadPhoto;
import com.dandelion.minitwitter.retrofit.response.UserProfile;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

// [Auth Twitter Interface]: service for request where authentication is required
public interface AuthTwitterService {
    // (Tweets)
    @GET("tweets/all")
    Call<List<Tweet>> getAllTweets();

    @POST("tweets/create")
    Call<Tweet> createTweet(@Body RequestCreateTweet requestCreateTweet);

    @POST("tweets/like/{id}")
    Call<Tweet> likeTweet(@Path("id") int id);

    @DELETE("tweets/{id}")
    Call<TweetDeleted> deleteTweet(@Path("id") int id);

    // (Users)
    @GET("users/profile")
    Call<UserProfile> getUserProfile();

    @PUT("users/profile")
    Call<UserProfile> updateUserProfile(@Body RequestUserProfile requestUserProfile);

    @Multipart
    @POST("users/uploadprofilephoto")
    Call<UploadPhoto> uploadProfilePhoto(@Part("file\"; filename=\"photo.jpeg\" ")RequestBody file);
}
