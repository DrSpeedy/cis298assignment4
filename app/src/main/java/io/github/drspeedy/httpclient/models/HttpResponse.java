package io.github.drspeedy.httpclient.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by doc on 11/11/16.
 */

public class HttpResponse {

    private int mResponseCode;
    private boolean mHasError;
    private InputStream mInputStream;
    private String mResponseString;

    public HttpResponse(HttpURLConnection connection) {
        try {
            setResponseCode(connection.getResponseCode());
            setResponseString(connection.getResponseMessage());

            if (connection.getInputStream() == null) {
                setHasError(true);
                setInputStream(connection.getErrorStream());
            } else {
                setHasError(false);
                setInputStream(connection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(int responseCode) {
        mResponseCode = responseCode;
    }

    public boolean hasError() {
        return mHasError;
    }

    public void setHasError(boolean hasError) {
        mHasError = hasError;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public void setInputStream(InputStream inputStream) {
        mInputStream = inputStream;
    }

    public String getResponseString() {
        return mResponseString;
    }

    public void setResponseString(String responseString) {
        mResponseString = responseString;
    }

    public interface Callback {
        void onHttpResponse(HttpResponse response);
    }
}
