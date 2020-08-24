package com.dandelion.minitwitter.ui.tweets;
// [Imports]
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.data.TweetViewModel;

// [New Tweet Dialog Fragment]: full screen dialog fragment for new Tweets insertion
public class NewTweetDialogFragment extends DialogFragment implements View.OnClickListener {
    // [Vars]
    public final static String DIALOG_FRAGMENT_TAG = "NEW_TWEET_DIALOG_FRAGMENT";
    private ImageView dialogClose;
    private ImageView dialogAvatar;
    private Button dialogNewTweet;
    private EditText dialogTweetMessage;

    // [Methods]: overridable

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.FullScreenDialogStyle);    // Sets custom dialog style
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Gets fragment inflated (in order to show in screen)
        View view = inflater.inflate(R.layout.new_tweet_dialog_fragment,container,false);
        // Makes an instance of dialog elements
        dialogClose = view.findViewById(R.id.imageViewClose);
        dialogAvatar = view.findViewById(R.id.imageViewAvatar);
        dialogNewTweet = view.findViewById(R.id.buttonTweet);
        dialogTweetMessage = view.findViewById(R.id.editTextTweet);
        // Sets controls events for dialog actions
        dialogClose.setOnClickListener(this);
        dialogNewTweet.setOnClickListener(this);
        // Sets user profile avatar
        String profilePhotoUrl = SharedPreferencesManager.getStringValue(CommonItems.DATA_LABEL_PHOTO_URL);
        if (!profilePhotoUrl.equals("")) {
            // On having profile image
            Glide.with(getActivity())
                    .load(CommonItems.URL_MINITWITTER_PHOTOS + profilePhotoUrl)
                    .into(dialogAvatar);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        // Gets new tweet message
        String tweetMessage = dialogTweetMessage.getText().toString();
        // Performs action base on clicked elements
        switch (v.getId()) {
            case R.id.imageViewClose:
                // On Tweet message is not empty shows confirmation dialog
                if (!tweetMessage.isEmpty()) {
                    showConfirmDialog();    // Closes dialog (on confirmation)
                } else {
                    getDialog().dismiss();  // Closes dialog (without confirmation)
                }

                break;
            case R.id.buttonTweet:
                // On having message performs creation (empty message prevention)
                if (!tweetMessage.isEmpty()) {
                    // Makes an instance of ViewModel to invoke creation method and refresh data
                    ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
                    TweetViewModel tweetViewModel = viewModelProvider.get(TweetViewModel.class);
                    // Performs Tweet creation with provided message
                    tweetViewModel.insertTweet(tweetMessage);
                    // Closes new Tweet dialog
                    getDialog().dismiss();
                } else {
                    // Empty message is not allowed so displays message
                    String emptyMessage = getResources().getString(R.string.message_is_empty_notice);
                    Toast.makeText(this.getContext(),emptyMessage,Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    // [Methods]: custom methods

    // Confirmation dialog (in order to prevent lose Tweets after write)
    private void showConfirmDialog() {
        // Gets dialog messages from resources
        String dialogTitle = getResources().getString(R.string.confirmation_dialog_title);
        String dialogMessage = getResources().getString(R.string.confirmation_dialog_cancel);
        String dialogPositiveButton = getResources().getString(R.string.confirmation_dialog_button_ok);
        String dialogNegativeButton = getResources().getString(R.string.confirmation_dialog_button_cancel);
        // Makes new dialog setup by using builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(dialogTitle);
        dialogBuilder.setMessage(dialogMessage);
        dialogBuilder.setPositiveButton(dialogPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform close action on confirmation (Tweet will be lost)
                dialog.dismiss();               // Closes confirmation dialog
                getDialog().dismiss();          // Closes Tweet creation dialog
            }
        });
        dialogBuilder.setNegativeButton(dialogNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close confirmation dialog
                dialog.dismiss();
            }
        });

        // Creates confirmation dialog (from Builder settings)
        AlertDialog confirmDialog = dialogBuilder.create();
        confirmDialog.show();
    }
}
