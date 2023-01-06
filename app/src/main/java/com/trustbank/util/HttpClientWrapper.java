package com.trustbank.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.trustbank.util.AppConstants.isReadDeviceIDFeatureEnabled;
import static com.trustbank.util.TrustMethods.LogMessage;

public class HttpClientWrapper {

    public static void prepareHeader(HttpURLConnection conn) {
        conn.setRequestProperty("api_key", AppConstants.API_KEY);
        conn.setRequestProperty("nonce", String.valueOf(UUID.randomUUID()));
        conn.setRequestProperty("req_ts", String.valueOf(System.currentTimeMillis() / 1000));
    }

    //Used in pin activation
    public static String postWithoutHeader(String requestUrl, String postValues) {

        try {
            if (requestUrl.contains("http://")) {

                URL url;
                String response = "";
                HttpURLConnection conn = null;
                try {
                    url = new URL(requestUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.
                    conn.setRequestProperty("Content-Type", "text/plain");
                    /* conn.setRequestProperty("action", actionName);*/
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
//                    response+=line;

                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        response = "";
                    }
                } catch (IOException ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            } else if (requestUrl.contains("https://")) {

                URL url;
                String response = "";
                HttpsURLConnection conn = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    url = new URL(requestUrl);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setHostnameVerifier(DUMMY_VERIFIER);
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("Content-Type", "text/plain");
                    /* conn.setRequestProperty("action", actionName);*/
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        throw new Exception("Invalid response code received from server. Response code: " + String.valueOf(responseCode));
                    }

                } catch (Exception ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;
    }

    //Used in pin activation
    public static String postWitAuthHeader(String requestUrl, String postValues, String auth_token) {

        try {
            if (requestUrl.contains("http://")) {

                URL url;
                String response = "";
                HttpURLConnection conn = null;
                try {
                    url = new URL(requestUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("auth_token", auth_token);
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
//                    response+=line;
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        response = "";
                    }
                } catch (IOException ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            } else if (requestUrl.contains("https://")) {

                URL url;
                String response = "";
                HttpsURLConnection conn = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    url = new URL(requestUrl);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setHostnameVerifier(DUMMY_VERIFIER);
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("auth_token", auth_token);

                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        throw new Exception("Invalid response code received from server. Response code: " + String.valueOf(responseCode));
                    }

                } catch (Exception ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;

    }

    //    with Hearder
    public static String postWithActionAuthToken(String requestUrl, String postValues, String actionName,
                                                 String auth_token) {

        try {
            if (requestUrl.contains("http://")) {

                URL url;
                String response = "";
                HttpURLConnection conn = null;
                try {
                    url = new URL(requestUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);  //Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true); //Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("Content-Type", "text/plain");
                    conn.setRequestProperty("action", actionName);
                    conn.setRequestProperty("auth_token", auth_token);

                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
//                    response+=line;

                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        response = "";
                    }
                } catch (IOException ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            } else if (requestUrl.contains("https://")) {

                URL url;
                String response = "";
                HttpsURLConnection conn = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    url = new URL(requestUrl);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setHostnameVerifier(DUMMY_VERIFIER);

                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("Content-Type", "text/plain");
                    conn.setRequestProperty("action", actionName);
                    conn.setRequestProperty("auth_token", auth_token);

                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        throw new Exception("Invalid response code received from server. Response code: " + String.valueOf(responseCode));
                    }

                } catch (Exception ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;

    }

    //    with Hearder
    public static String post(String requestUrl, String postValues, String actionName) {

        try {
            if (requestUrl.contains("http://")) {

                URL url;
                String response = "";
                HttpURLConnection conn = null;
                try {
                    url = new URL(requestUrl);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("Content-Type", "text/plain");
                    conn.setRequestProperty("action", actionName);
//            conn.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {

                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        response = "";
                    }
                } catch (IOException ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            } else if (requestUrl.contains("https://")) {

                URL url;
                String response = "";
                HttpsURLConnection conn = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    url = new URL(requestUrl);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setHostnameVerifier(DUMMY_VERIFIER);
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("Content-Type", "text/plain");
                    conn.setRequestProperty("action", actionName);
//            conn.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        throw new Exception("Invalid response code received from server. Response code: " + String.valueOf(responseCode));
                    }

                } catch (Exception ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;

    }

    public static String getResponceDirectalyGET(String url, String auth_token) {

        try {
            if (url.contains("http://")) {

                String response = "";
                HttpURLConnection httpURLConnection = null;

                try {
                    URL u = new URL(url);
                    httpURLConnection = (HttpURLConnection) u.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);

                    httpURLConnection.setRequestProperty("auth_token", auth_token);
//            httpURLConnection.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        httpURLConnection.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(httpURLConnection);

                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();


                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return /*MessageBodyManipulator.decryptResponseBody(*/response/*)*/;
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            } else if (url.contains("https://")) {

                String response = "";
                HttpsURLConnection httpURLConnection = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    URL u = new URL(url);
                    httpURLConnection = (HttpsURLConnection) u.openConnection();
                    httpURLConnection.setHostnameVerifier(DUMMY_VERIFIER);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);

                    httpURLConnection.setRequestProperty("auth_token", auth_token);
//            httpURLConnection.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        httpURLConnection.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(httpURLConnection);

                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();

                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return /*MessageBodyManipulator.decryptResponseBody(*/response/*)*/;
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;
    }

    public static String getResponseWithoutAuth(String url) {

        try {

            if (url.contains("http://")) {

                String response = "";
                HttpURLConnection httpURLConnection = null;

                try {
                    URL u = new URL(url);
                    httpURLConnection = (HttpURLConnection) u.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);

                    prepareHeader(httpURLConnection);


                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();


                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return MessageBodyManipulator.decryptResponseBody(response);
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            } else if (url.contains("https://")) {

                String response = "";
                HttpsURLConnection httpURLConnection = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    URL u = new URL(url);
                    httpURLConnection = (HttpsURLConnection) u.openConnection();
                    httpURLConnection.setHostnameVerifier(DUMMY_VERIFIER);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);



                    prepareHeader(httpURLConnection);

                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();

                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return MessageBodyManipulator.decryptResponseBody(response);
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;
    }

    //Get with header
    public static String getResponseGET(String url, String actionName, String auth_token) {

        try {
            if (url.contains("http://")) {

                String response = "";
                HttpURLConnection httpURLConnection = null;
                try {
                    URL u = new URL(url);
                    httpURLConnection = (HttpURLConnection) u.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);

                    httpURLConnection.setRequestProperty("Content-Type", "text/plain");
                    httpURLConnection.setRequestProperty("action", actionName);
                    httpURLConnection.setRequestProperty("auth_token", auth_token);
//            httpURLConnection.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        httpURLConnection.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(httpURLConnection);

                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();

                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return MessageBodyManipulator.decryptResponseBody(response);
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            } else if (url.contains("https://")) {

                String response = "";
                HttpsURLConnection httpURLConnection = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            LogMessage("check", "server trusted");
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {

                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }

                    URL u = new URL(url);
                    httpURLConnection = (HttpsURLConnection) u.openConnection();
                    httpURLConnection.setHostnameVerifier(DUMMY_VERIFIER);

                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(AppConstants.timeOut);
                    httpURLConnection.setReadTimeout(AppConstants.timeOut);

                    httpURLConnection.setRequestProperty("Content-Type", "text/plain");
                    httpURLConnection.setRequestProperty("action", actionName);
                    httpURLConnection.setRequestProperty("auth_token", auth_token);
//            httpURLConnection.setRequestProperty("api_key", apiKey);
                    if (isReadDeviceIDFeatureEnabled) {
                        httpURLConnection.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(httpURLConnection);

                    httpURLConnection.connect();
                    int status = httpURLConnection.getResponseCode();

                    switch (status) {
                        case 200:
                        case 201:
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                                response = sb.toString().substring(0, sb.toString().length() - 1);
                                LogMessage("", "response-->" + response);

                            }
                            br.close();
                            return MessageBodyManipulator.decryptResponseBody(response);
                    }
                } catch (IOException ex) {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;

    }


    private static final HostnameVerifier DUMMY_VERIFIER = (hostname, session) -> {
        String ip = TrustMethods.getIp(AppConstants.IP);
     /*   HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
        return hv.verify(AppConstants.IP, session);
*/
        if (hostname.equalsIgnoreCase(ip)) {
            return true;
        } else {
            return false;
        }

    };

    //Used in pin activation
    public static String postWitInstituteAuthHeader(String requestUrl, String postValues,
                                                    String auth_token,String institute) {

        try {
            if (requestUrl.contains("http://")) {

                URL url;
                String response = "";
                HttpURLConnection conn = null;
                try {
                    url = new URL(requestUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.

                    conn.setRequestProperty("auth_token", auth_token);
                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
//                        conn.setRequestProperty("device_info", "abcdc1244545rgfg");
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
//                    response+=line;
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        response = "";
                    }
                } catch (IOException ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            } else if (requestUrl.contains("https://")) {

                URL url;
                String response = "";
                HttpsURLConnection conn = null;
                SSLContext context = null;
                try {
                    // Dummy trust manager that trusts all certificates
                    TrustManager localTrustmanager = new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            LogMessage("check", "server trusted");
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            LogMessage("check", "client trusted");
                        }
                    };

                    // Create SSLContext and set the socket factory as default
                    try {
                        SSLContext sslc = SSLContext.getInstance("TLS");
                        sslc.init(null, new TrustManager[]{localTrustmanager}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                    url = new URL(requestUrl);

                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(AppConstants.timeOut);
                    conn.setConnectTimeout(AppConstants.timeOut);
                    conn.setRequestMethod("POST");
                    conn.setHostnameVerifier(DUMMY_VERIFIER);
                    conn.setDoInput(true);// Specifies whether this URLConnection allows receiving data.
                    conn.setDoOutput(true);// Specifies whether this URLConnection allows sending data.
                    conn.setRequestProperty("auth_token", auth_token);

//                    conn.setRequestProperty("institute", institute);  //Added institute id for VMUCB.

                    if (isReadDeviceIDFeatureEnabled) {
                        conn.setRequestProperty("device_info", TrustMethods.getDeviceID(MBank.getInstance().getApplicationContext()));
                    }

                    prepareHeader(conn);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(MessageBodyManipulator.encryptRequestBody(postValues));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                            response = sb.toString().substring(0, sb.toString().length() - 1);
                        }
                    } else {
                        throw new Exception("Invalid response code received from server. Response code: " + String.valueOf(responseCode));
                    }

                } catch (Exception ex) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                return MessageBodyManipulator.decryptResponseBody(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return null;

    }


}
