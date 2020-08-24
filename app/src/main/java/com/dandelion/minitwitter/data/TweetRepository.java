package com.dandelion.minitwitter.data;
// [Imports]
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.GlobalApp;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.retrofit.AuthTwitterClient;
import com.dandelion.minitwitter.retrofit.AuthTwitterService;
import com.dandelion.minitwitter.retrofit.request.RequestCreateTweet;
import com.dandelion.minitwitter.retrofit.response.Like;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import com.dandelion.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// [Tweet Repository]: Tweets data provider
/**
 * Tweet Repository > Get Tweets data by requesting to API (by using Client && Service classes)
 * - Retrieves Tweets data as LiveData that will be observed to reflect changes in Views
 * - Data is given by Client && Service classes (Retrofit) that request data by API methods
 * - This class is used with ModelView (TweetViewModel) classes in order to reflect data changes in real time
 */
public class TweetRepository {
    // [Vars]
    AuthTwitterClient authTwitterClient;
    AuthTwitterService authTwitterService;
    MutableLiveData<List<Tweet>> liveTweets;
    MutableLiveData<List<Tweet>> liveFavTweets;
    String userName;

    // (Constructor)

    public TweetRepository() {
        liveTweets = new MutableLiveData<>();   // Initializes LiveData object to prevent null exceptions
        retrofitInit();     // Initializes Twitter Client && Service
        getAllTweets();     // Gets all Tweets (from request response)
        userName = SharedPreferencesManager.getStringValue(CommonItems.DATA_LABEL_USER_NAME);
    }

    // [Methods]

    // Gets a list of all tweets from API
    public LiveData<List<Tweet>> getAllTweets() {
        // Retrieves -all- tweets from API
        Call<List<Tweet>> caller = authTwitterService.getAllTweets();
        caller.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                // On request success
                if (response.isSuccessful()) {
                    liveTweets.setValue(response.body()); // Gets all Tweets (from request response)
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_callback_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
        return liveTweets;
    }

    // Gets a list of -favorite- tweets from API
    public LiveData<List<Tweet>> getFavTweets() {
        // Makes an instance of favorite Tweets not exists yet
        if (liveFavTweets == null) {
            liveFavTweets = new MutableLiveData<>();
        }
        // Picks up favorite Tweets from all Tweets list
        List<Tweet> favTweets = new ArrayList<Tweet>();
        for (Tweet tweetItem: liveTweets.getValue()) {
            // Looks for logged user in order to check if liked tweet in list
            List<Like> likeList = tweetItem.getLikes();
            for (Like likeItem: likeList) {
                if (likeItem.getUsername().equals(userName)) {
                    favTweets.add(tweetItem);   // Adds tweet to favorites list
                    break;
                }
            }
        }
        liveFavTweets.setValue(favTweets);  // Updates Tweet list
        return liveFavTweets;
    }

    // Creates a new Tweet
    public void createTweet(String tweetMessage) {
        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(tweetMessage);
        Call<Tweet> caller = authTwitterService.createTweet(requestCreateTweet);
        caller.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                // On success
                if (response.isSuccessful()) {
                    // Adds a Tweet over copy list and imports previous elements for finally override original list
                    reloadTweets(response.body());
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_tweet_creation_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
    }

    // Deletes a Tweet
    public void deleteTweet(final int id) {
        Call<TweetDeleted> caller = authTwitterService.deleteTweet(id);
        caller.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if (response.isSuccessful()) {
                    // Moves all Tweets to cloned list except tweet designed to deletion by provided id
                    List<Tweet> clonedTweets = cloneTweetsExcept(id);
                    liveTweets.setValue(clonedTweets);  // Updates Tweets list (without deleted item)
                    getFavTweets();                     // Updates favorite Tweets list (to reflect changes if are)
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.message_tweet_deletion_failed);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
    }

    // Sets a Tweet like or removes it (if previously was liked)
    public void likeTweet(int idTweet) {
        Call<Tweet> caller = authTwitterService.likeTweet(idTweet);
        caller.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                // On success
                if (response.isSuccessful()) {
                    // Gets list with like value updated
                    likeForTweet(response.body());
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_tweet_creation_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
    }

    // Initializes Twitter Client && Service
    private void retrofitInit() {
        authTwitterClient = authTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
    }

    // Adds a new Tweet over copy list and imports previous Tweets from original list to finally override original list
    private void reloadTweets(Tweet tweet) {
        List<Tweet> reloadList = new ArrayList<Tweet>();
        reloadList.add(tweet);
        for (Tweet originalTweet: liveTweets.getValue()) {
            reloadList.add(originalTweet);
        }
        liveTweets.setValue(reloadList);
    }

    // Updates like value for specific Tweet
    private void likeForTweet(Tweet tweet) {
        List<Tweet> reloadList = new ArrayList<Tweet>();
        for (Tweet tweetItem: liveTweets.getValue()) {
            // If Tweet id meets liked Tweet ID data override is performed
            if (tweetItem.getId() == tweet.getId()) {
                reloadList.add(tweet);  // Updated Tweet
            } else {
                Tweet originalTweet = new Tweet(
                        tweetItem.getId(),
                        tweetItem.getMensaje(),
                        tweetItem.getLikes(),
                        tweetItem.getUser());
                reloadList.add(originalTweet); // Original Tweet copy
            }
        }
        liveTweets.setValue(reloadList);    // Updates Tweets list
        getFavTweets();                     // Updates Favorite Tweets list
    }

    // Gets a cloned Tweets list except specific tweet designed by id
    private List<Tweet> cloneTweetsExcept(int id) {
        // Moves all Tweets to cloned list except tweet designed to deletion by provided id
        List<Tweet> clonedTweets = new ArrayList<>();
        for (Tweet tweetItem: liveTweets.getValue()) {
            // On tweet id is not deletion id copy to list
            if (tweetItem.getId() != id) {
                clonedTweets.add(
                        new Tweet(
                                tweetItem.getId(),
                                tweetItem.getMensaje(),
                                tweetItem.getLikes(),
                                tweetItem.getUser()));
            }
        }
        return clonedTweets;
    }
}
