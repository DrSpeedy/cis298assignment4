package io.github.drspeedy.httpclient;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.drspeedy.httpclient.models.HttpResponse;

/**
 * Created by doc on 11/12/16.
 */

public class HttpClient {

    private static final String TAG = HttpClient.class.getSimpleName();

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    // TODO: Do this stuff below here...
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    private URL mURL;

    public URL getURL() {
        return mURL;
    }

    public void setURL(URL URL) {
        mURL = URL;
    }

    public void httpRequest(@NonNull String requestType, @Nullable Map<String, String> requestProperties,
                            @Nullable byte[] requestBody, @Nullable HttpResponse.Callback callback) {
        new HttpRequestTask(requestType, requestProperties, requestBody, callback).execute();
    }

    public byte[] mapToUrlEncoded(Map<String, String> map) throws UnsupportedEncodingException {
        String params = "";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            params += key + "=" + value + "&";
        }

        Log.d(TAG, "mapToUrlEncoded() => " + params);
        String urlSafeParams = URLEncoder.encode(params, "utf-8");
        return params.getBytes();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, HttpResponse> {

        // TODO: Follow redirects? Add http timeout params. PUT DELETE
        private String mRequestType;
        private Map<String, String> mRequestProperties;
        private byte[] mRequestBody;
        private HttpResponse.Callback mCallback;

        /**
         * Construct the HttpRequestTask for background execution
         * @param requestType HTTP request type
         * @param requestProperties Header data
         * @param requestBody Body data
         * @param onHttpResponse Async callback to handle the response from the HTTP server
         */
        public HttpRequestTask(@NonNull String requestType, @Nullable Map<String, String> requestProperties,
                               @Nullable byte[] requestBody, @Nullable HttpResponse.Callback onHttpResponse) {
            mRequestType = requestType;
            mRequestProperties = new HashMap<>(requestProperties);
            mRequestBody = requestBody;
            mCallback = onHttpResponse;
        }

        @Override
        protected HttpResponse doInBackground(Void... params) {
            try {
                switch (mRequestType) {
                    case "POST":
                        return sendPostRequest();

                    case "GET":
                        return sendGetRequest();

                    default:
                        return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse httpResponse) {
            mCallback.onHttpResponse(httpResponse);
        }

        /**
         * Sends an HTTP(S) POST request
         * @return HttpResponse
         * @throws IOException
         */
        private HttpResponse sendPostRequest() throws IOException {
            HttpURLConnection connection = (HttpURLConnection) getURL().openConnection();

            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(true);

            connection.setDoOutput(true);
            connection.setRequestMethod(HTTP_POST);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(mRequestBody);
            out.flush();
            out.close();

            HttpResponse response = new HttpResponse(connection);
            connection.disconnect();

            return response;
        }

        /**
         * Sends an HTTP(S) GET request
         * @return HttpResponse
         * @throws IOException
         */
        private HttpResponse sendGetRequest() throws IOException {
            HttpURLConnection connection = (HttpURLConnection) getURL().openConnection();

            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(true);

            connection.setDoInput(true);
            connection.setRequestMethod(HTTP_GET);

            HttpResponse response = new HttpResponse(connection);
            connection.disconnect();

            return response;
        }
    }
}
