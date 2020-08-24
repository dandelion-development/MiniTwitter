package com.dandelion.minitwitter.retrofit;
// [Imports]
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// [Authenticate Interceptor]
/**
 * Authenticate Interceptor > Adds to API requests token authorization data in order to get access
 * - This class attaches in request headers token data for authentication
 * - For private requests where authentication is needed token data is used to verify identity
 */
public class AuthInterceptor implements Interceptor {
    // [Vars]
    private static final String REQUEST_HEADER_TYPE = "Authorization";
    private static final String REQUEST_BEARER = "Bearer";
    // [Interception]
    @Override
    public Response intercept(Chain chain) throws IOException {
        String bearerToken = REQUEST_BEARER + " " + SharedPreferencesManager.getStringValue(CommonItems.DATA_LABEL_TOKEN);
        Request request = chain.request().newBuilder().addHeader(REQUEST_HEADER_TYPE,bearerToken).build();
        return chain.proceed(request);
    }
}
