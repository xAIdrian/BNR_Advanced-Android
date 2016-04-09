package com.bignerdranch.android.initialtwittersyncadapter.web;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TwitterOauthHelper {

    private static final String TAG = "TwitterOauthHelper";
    private static final String CONSUMER_KEY = "fkWQuNorrm6szQHG3UbP4OzJH";
    private static final String CONSUMER_SECRET = "GL4ivRcgfCG3nqKPiPFTlripMxFEFENpOMZCTRmcOsitinu4zO";
    private static final String KEY_SPEC = "HmacSHA1";
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static TwitterOauthHelper sTwitterOauthHelper;

    private String mOauthTimestamp;
    private String mOauthNonce;
    private String mOauthToken;
    private String mOauthTokenSecret;
    private String mBaseEndpoint;
    private String mRequestType;
    private String mRequestEndpoint;

    private TreeMap<String, String> mHeaderParameters = new TreeMap<>();

    public static TwitterOauthHelper get() {
        if (sTwitterOauthHelper == null) {
            sTwitterOauthHelper = new TwitterOauthHelper();
        }
        return sTwitterOauthHelper;
    }

    private TwitterOauthHelper() {

    }

    private void newRequest() {
        mOauthNonce = "";
        mOauthTimestamp = "";
        mRequestEndpoint = "";
        mRequestType = null;
    }

    private void setupHeaderParameters() {
        mHeaderParameters.put("oauth_consumer_key", CONSUMER_KEY);
        mHeaderParameters.put("oauth_nonce", generateOauthNonce());
        mHeaderParameters.put("oauth_signature_method", getOauthSignatureMethod());
        mHeaderParameters.put("oauth_timestamp", generateOauthTimestamp());
        mHeaderParameters.put("oauth_version", getOauthVersion());
    }

    public void setOauthToken(String oauthToken, String oauthTokenSecret) {
        mOauthToken = oauthToken;
        mOauthTokenSecret = oauthTokenSecret;
        mHeaderParameters.put("oauth_token", mOauthToken);
    }

    public void resetOauthToken() {
        mOauthToken = "";
        mOauthTokenSecret = "";
        mHeaderParameters.remove("oauth_token");
    }

    private void setupBaseEndpoint() {
        int ampIndex = mRequestEndpoint.indexOf("?");
        if (ampIndex != -1) {
            mBaseEndpoint = mRequestEndpoint.substring(0, ampIndex);
        } else {
            mBaseEndpoint = mRequestEndpoint;
        }
    }

    public String getAuthorizationHeaderString(Request request)
            throws UnsupportedEncodingException, InvalidKeyException,
            NoSuchAlgorithmException {
        newRequest();
        mRequestEndpoint = request.urlString();
        mRequestType = request.method();
        setupBaseEndpoint();
        setupHeaderParameters();

        Uri requestUri = Uri.parse(mRequestEndpoint);
        // Iterate over query params and add to header params hash
        Set<String> paramSet = requestUri.getQueryParameterNames();
        for (String paramName : paramSet) {
            String paramValue = requestUri.getQueryParameter(paramName);
            mHeaderParameters.put(paramName, paramValue);
        }

        // sort header params by key and form auth string
        String authString = "OAuth " +
                "oauth_consumer_key=\"" + CONSUMER_KEY + "\"" +
                ",oauth_nonce=\"" + generateOauthNonce() + "\"" +
                ",oauth_signature=\"" +
                URLEncoder.encode(generateOauthSignature(), "UTF-8") + "\"" +
                ",oauth_signature_method=\"" + getOauthSignatureMethod() + "\"" +
                ",oauth_timestamp=\"" + generateOauthTimestamp() + "\"";
        if (!TextUtils.isEmpty(mOauthToken)) {
            authString += ",oauth_token=\"" + mOauthToken + "\"";
        }
        authString += ",oauth_version=\"" + getOauthVersion() + "\"";
        return authString;
    }

    private String generateOauthSignature() {
        try {
            return computeOauthSignature(getSignatureBaseString(), getSigningKey());
        } catch (NoSuchAlgorithmException |
                InvalidKeyException |
                UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to generate Oauth Signature", e);
        }
        return null;
    }

    private String computeOauthSignature(String baseString, String keyString)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, KEY_SPEC);

        Mac mac = Mac.getInstance(KEY_SPEC);
        mac.init(secretKey);

        byte[] baseText = mac.doFinal(baseString.getBytes());

        return new String(Base64.encode(baseText, Base64.DEFAULT)).trim();
    }

    private String getSignatureBaseString() throws UnsupportedEncodingException {
        return getRequestType() + "&" +
                URLEncoder.encode(mBaseEndpoint, "UTF-8") + "&" +
                URLEncoder.encode(generateParameterString(), "UTF-8");
    }

    private String getRequestType() {
        return mRequestType;
    }

    private String getSigningKey() throws UnsupportedEncodingException {
        String key = URLEncoder.encode(CONSUMER_SECRET, "UTF-8") + "&";
        if (!TextUtils.isEmpty(mOauthTokenSecret)) {
            key += URLEncoder.encode(mOauthTokenSecret, "UTF-8");
        }
        return key;
    }

    // parameters need to be in alphabetical order by key
    private String generateParameterString() {
        String paramString = "";
        Set<String> authHeaderParamSet = mHeaderParameters.keySet();
        boolean firstItem = true;
        for (String param : authHeaderParamSet) {
            if (!firstItem) {
                paramString += "&";
            } else {
                firstItem = false;
            }
            paramString += param + "=" + mHeaderParameters.get(param);
        }
        return paramString;
    }

    // OAuth nonce needs to be unique alphanumeric string
    private String generateOauthNonce() {
        if (TextUtils.isEmpty(mOauthNonce)) {
            mOauthNonce = UUID.randomUUID().toString();
            mOauthNonce = mOauthNonce.replaceAll("-", "");
        }
        return mOauthNonce;
    }

    private String generateOauthTimestamp() {
        if (TextUtils.isEmpty(mOauthTimestamp)) {
            mOauthTimestamp = "" +
                    (System.currentTimeMillis() / MILLISECONDS_PER_SECOND);
        }
        return mOauthTimestamp;
    }

    private String getOauthVersion() {
        return "1.0";
    }

    private String getOauthSignatureMethod() {
        return "HMAC-SHA1";
    }
}
