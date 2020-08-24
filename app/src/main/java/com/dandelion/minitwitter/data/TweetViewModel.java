package com.dandelion.minitwitter.data;
// [Imports]
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import com.dandelion.minitwitter.ui.tweets.BottomModalTweetFragment;

import java.util.List;
// [Tweet View Model]: represents Tweet data by using data repository as source
/**
 * Tweet View Model > Data representation class by using repository as data source
 * - This class reflects source data as ViewModel to show information in application
 * - Data source is provided by repository (TweetRepository) who provides LiveData
 * - Source data is type LiveData so changes can be reflected in ViewModel
 */
public class TweetViewModel extends AndroidViewModel {
    // [Vars]
    private TweetRepository tweetRepository;
    private LiveData<List<Tweet>> liveTweets;
    private LiveData<List<Tweet>> liveFavTweets;

    // (Constructor)
    public TweetViewModel(@NonNull Application application) {
        super(application);
        tweetRepository = new TweetRepository();        // Make a new instance of data repository
        liveTweets = tweetRepository.getAllTweets();    // Loads data from repository
    }

    // [Methods]

    // Gets -All- Tweets data that are loaded from repository
    public LiveData<List<Tweet>> getTweets() {
        return liveTweets;
    }
    // Gets -Favorite- Tweets data that are loaded from repository
    public LiveData<List<Tweet>> getFavTweets() {
        liveFavTweets = tweetRepository.getFavTweets();
        return liveFavTweets;
    }
    // Gets updated -All- Tweets data by requesting it to server
    public LiveData<List<Tweet>> getLiveTweets() {
        liveTweets = tweetRepository.getAllTweets();    // Gets updated data from repository response
        return liveTweets;
    }

    // Gets updated -Favorite- Tweets data by requesting it to server
    public LiveData<List<Tweet>> getLiveFavTweets() {
        getLiveTweets();        // Gets a refresh Tweets data from server
        return getFavTweets();  // Returns favorite Tweets data got from refreshed (all tweets) list
    }

    // Inserts new Tweet
    public void insertTweet(String tweetMessage) {
        tweetRepository.createTweet(tweetMessage);
    }

    // Deletes a Tweet
    public void deleteTweet(int id) { tweetRepository.deleteTweet(id); }

    // Sets like for Tweet
    public void likeForTweet(int idTweet) {tweetRepository.likeTweet(idTweet);}

    // Gets a dialog for manage Tweet actions
    public void openDialogTweetMenu(Context appContext, int id) {
        BottomModalTweetFragment dialogTweetMenu = BottomModalTweetFragment.newInstance(id);
        dialogTweetMenu.show(((AppCompatActivity) appContext).getSupportFragmentManager(),"DialogTweetMenu");
    }
}
