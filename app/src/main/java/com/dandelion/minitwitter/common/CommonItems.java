package com.dandelion.minitwitter.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// [Common Items]: common items that are used by project are located in this class
public class CommonItems {
    // (API): Api common parameters
    public static final String API_MINITWITTER_ENDPOINT = "https://minitwitter.com:3001/apiv1/";
    public static final String URL_MINITWITTER_UPLOADS = "https://minitwitter.com/apiv1/uploads/";
    public static final String URL_MINITWITTER_PHOTOS = "https://minitwitter.com/apiv1/uploads/photos/";
    public static final String API_UDEMY_CODE = "UDEMYANDROID";
    // (Data Storage): data storage common labels (for access to SharedPreferences)
    public static final  String DATA_LABEL_TOKEN = "SHA_PREF_DATA_TOKEN";
    public static final  String DATA_LABEL_USER_NAME = "SHA_PREF_DATA_USER_NAME";
    public static final  String DATA_LABEL_EMAIL = "SHA_PREF_DATA_EMAIL";
    public static final  String DATA_LABEL_PHOTO_URL = "SHA_PREF_DATA_PHOTO_URL";
    public static final  String DATA_LABEL_CREATED = "SHA_PREF_DATA_CREATED";
    public static final  String DATA_LABEL_ACTIVE = "SHA_PREF_DATA_ACTIVE";
    // (Arguments): common parameters used as arguments values or labels
    public static final String TWEET_LIST_TYPE = "TWEET_LIST_TYPE";
    public static final int TWEET_LIST_ALL = 1;
    public static final int TWEET_LIST_FAV = 2;
    public static final String ARG_TWEET_ID = "ARG_TWEET_ID";
    // (Patterns): common data validation patterns
    public static final String PATTERN_VALIDATION_FOR_USER_NAME = "^([A-Za-z0-9]){4,}([A-Za-z0-9]||.[A-Za-z0-9]||-[A-Za-z0-9]){1,}([A-Za-z0-9]){1}";
    public static final String PATTERN_VALIDATION_FOR_USER_EMAIL = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    public static final String PATTERN_VALIDATION_FOR_USER_PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
    // (Request Code): common request codes used for track activities that provides result
    public static final int REQUEST_CODE_FOR_PHOTO_SELECTION = 7788;
}
