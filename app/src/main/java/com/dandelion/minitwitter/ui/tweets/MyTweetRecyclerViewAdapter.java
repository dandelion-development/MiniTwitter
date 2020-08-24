package com.dandelion.minitwitter.ui.tweets;
// [Imports]
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.data.TweetViewModel;
import com.dandelion.minitwitter.retrofit.response.Like;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import java.util.List;
// [Rcycler View]: recycler view adapter
public class MyTweetRecyclerViewAdapter extends RecyclerView.Adapter<MyTweetRecyclerViewAdapter.ViewHolder> {
    // [Vars]
    private Context appContext;
    private List<Tweet> mValues;
    private String userName;
    private TweetViewModel tweetViewModel;

    // (Constructor)
    public MyTweetRecyclerViewAdapter(Context appContext, List<Tweet> items) {
        this.appContext = appContext;
        this.mValues = items;
        this.userName = SharedPreferencesManager.getStringValue(CommonItems.DATA_LABEL_USER_NAME);
        // Makes a instance of ViewModel (for manager data update methods)
        ViewModelProvider viewModelProvider = new ViewModelProvider((FragmentActivity) appContext);
        this.tweetViewModel = viewModelProvider.get(TweetViewModel.class);
    }

    // [Methods]: overridable methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Prevents empty list before drawing
        if (mValues != null) {
            holder.mItem = mValues.get(position);
            // Tweet text data
            holder.textViewUser.setText("@" + mValues.get(position).getUser().getUsername());
            holder.textViewMessage.setText(mValues.get(position).getMensaje());
            holder.textViewLikesCount.setText(String.valueOf(mValues.get(position).getLikes().size()));
            // On user has photo
            if (!holder.mItem.getUser().getPhotoUrl().equals("")) {
                String photo_url = CommonItems.URL_MINITWITTER_PHOTOS + holder.mItem.getUser().getPhotoUrl();
                Glide.with(appContext).load(photo_url).into(holder.imageAvatar);
            } else {
                Glide.with(appContext).load(R.drawable.ic_logo_minituiter_mini).into(holder.imageAvatar);
            }
            // Sets tweet dialog menu visibility (only owner can performs some action over tweets)
            if (holder.mItem.getUser().getUsername().equals(userName)) {
                holder.imageShowMenu.setVisibility(View.VISIBLE);
            } else {
                holder.imageShowMenu.setVisibility(View.GONE);
            }
            // Sets Tweet actions menu control management
            holder.imageShowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Opens dialog menu for tweet deletion
                    tweetViewModel.openDialogTweetMenu(appContext, holder.mItem.getId());
                }
            });
            // Sets default Like controls style (colors and others)
            Glide.with(appContext)
                    .load(R.drawable.ic_like_border_24)
                    .into(holder.imageLike);
            holder.textViewLikesCount.setTextColor(appContext.getResources().getColor(android.R.color.black));
            holder.textViewLikesCount.setTypeface(null,Typeface.NORMAL);
            // Sets onClick listener for Likes
            holder.imageLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Performs a like update by using ViewModel methods
                    tweetViewModel.likeForTweet(holder.mItem.getId());
                }
            });
            // Verifies if me clicked like for this Tweet
            for (Like like: holder.mItem.getLikes()) {
                if (like.getUsername().equals(userName)) {
                    Glide.with(appContext).load(R.drawable.ic_like_pink_24).into(holder.imageLike);
                    holder.textViewLikesCount.setTextColor(appContext.getResources().getColor(R.color.colorMeLikes));
                    holder.textViewLikesCount.setTypeface(null, Typeface.BOLD);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        // Prevents empty list by checking before
        if (mValues != null) {
            return mValues.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageAvatar;
        public final ImageView imageLike;
        public final ImageView imageShowMenu;
        public final TextView textViewUser;
        public final TextView textViewMessage;
        public final TextView textViewLikesCount;
        public Tweet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageAvatar = view.findViewById(R.id.imageViewAvatar);
            imageLike = view.findViewById(R.id.imageViewLike);
            imageShowMenu = view.findViewById(R.id.imageViewShowMenu);
            textViewUser = (TextView) view.findViewById(R.id.textViewUser);
            textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
            textViewLikesCount = (TextView) view.findViewById(R.id.textViewLikesCount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewUser.getText() + "'";
        }
    }

    // [Methods]: custom methods

    // Updates Tweets data (overrides data)
    public void updateTweetData(List<Tweet> listTweets) {
        this.mValues = listTweets;      // Overrides Tweets data
        notifyDataSetChanged();         // Refresh Tweets list
    }
}