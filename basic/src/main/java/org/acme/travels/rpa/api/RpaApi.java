/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.travels.rpa.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.acme.travels.rpa.api.parameters.BotSignature;
import org.acme.travels.rpa.json.JsonUtils;
import org.apache.commons.codec.binary.Base64;

public class RpaApi {

    public static boolean debug = true;

    public static void ignoreSSL() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        } };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier validHosts = new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        // All hosts will be valid
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
    }

    public static String getBearerToken(String baseURL, String tenantId, String username, String password)
            throws RpaApiException {

        String token = null;

        HashMap<String, String> headerMap = new HashMap<String, String>();

        String body = "";

        headerMap.put("grant_type", "password");
        headerMap.put("username", username);
        headerMap.put("password", password);
        headerMap.put("culture", "en-US");

        String url = baseURL + "/v1.0/token";
        try {
            String result = postEncoded(url, body, tenantId, headerMap);

            token = JsonUtils.getBearerToken(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

    public static String getProcessIdByName(String baseUrl, String tenantId, String bearerToken, String processName) throws Exception {

        String processId = null;

        String getProcessUrl = baseUrl
                + "/v2.0/workspace/" + tenantId + "/process?lang=en-US";

        HashMap<String, String> headerMap = new HashMap<String, String>();

        headerMap.put("Authorization", "Bearer " + bearerToken);
        headerMap.put("Content-Type", "application/json");

        String result = doRest("GET", getProcessUrl, null, headerMap, null, null);

        processId = JsonUtils.findProcessId(result, processName);

        System.out.println(result);

        return processId;
    }

    public static BotSignature getProcessDetails(String baseUrl, String tenantId, String bearerToken,
            String processId) throws Exception {

        BotSignature botSignature = null;

        String startProcessUrl = baseUrl
                + "/v2.0/workspace/" + tenantId + "/process/" + processId;

        HashMap<String, String> headerMap = new HashMap<String, String>();

        headerMap.put("Authorization", "Bearer " + bearerToken);
        headerMap.put("Content-Type", "application/json");

        String response = doRest("GET", startProcessUrl, null, headerMap, null, null);

        botSignature = JsonUtils.getScriptSchema(response);

        return botSignature;
    }

    public static RpaResponse startProcessAndWait(String baseUrl, String tenantId, String username, String password,
            String processName, String payload, Integer waitSeconds)
            throws RpaApiException, InterruptedException {

        final int RETRY_COUNT = 10;
        final int RETRY_IN_MILISECS = 3000;

        System.out.println("Start process and wait...");

        String bearerToken = getBearerToken(baseUrl, tenantId, username, password);

        if (bearerToken == null) {
            throw new RpaApiException(
                    "Failed to login  to tenant +  " + tenantId + " with username " + username);
        }

        String processId = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                processId = getProcessIdByName(baseUrl, tenantId, bearerToken, processName);
                break;
            } catch (Exception e) {
                //e.printStackTrace();
                Thread.sleep(RETRY_IN_MILISECS);
                System.out.println("Retry getProcessIdByName " + i);
            }
        }
        if (processId == null) {
            throw new RpaApiException("Process " + processName + " not found");
        }

        String processInstanceId = "";
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                processInstanceId = startProcess(baseUrl, tenantId, bearerToken, processId, payload);
                break;
            } catch (Exception e) {
                // e.printStackTrace();
                Thread.sleep(RETRY_IN_MILISECS);
                System.out.println("Retry startProcess " + i);
            }
        }

        if (processInstanceId == null) {
            throw new RpaApiException("Process instance not started after " + RETRY_COUNT + " retries");
        }

        // Wait for a response for a designated number of secs
        Thread.sleep(waitSeconds * 1000);

        RpaResponse result = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                result = getResult(baseUrl, tenantId, bearerToken, processId, processInstanceId);
                break;
            } catch (Exception e) {
                //e.printStackTrace();
                Thread.sleep(RETRY_IN_MILISECS);
                System.out.println("Retry getResult " + i);
            }
        }

        if (result == null) {
            throw new RpaApiException("Failed to retreive process result after " + RETRY_COUNT + " retries");
        }

        return result;
    }

    private static RpaResponse getResult(String baseUrl, String tenantId, String bearerToken, String processId,
            String processInstanceId) throws Exception {

        String result = "ERROR";

        String getProcessUrl = baseUrl
                + "/v2.0/workspace/" + tenantId + "/process/" + processId + "/instance/" + processInstanceId;

        HashMap<String, String> headerMap = new HashMap<String, String>();

        headerMap.put("Authorization", "Bearer " + bearerToken);
        headerMap.put("Content-Type", "application/json");

        result = doRest("GET", getProcessUrl, null, headerMap, null, null);

        return new RpaResponse(result);
    }

    public static String doRest(String command, String urlString, String content, HashMap<String, String> headerMap,
            String userid, String password) throws Exception {
        if (debug) {
            System.out.println(">> doRest: command=" + command + ", urlString=" + urlString + ", content=" + content
                    + ", userid=" + userid + ", password=" + password);
        }

        if ((!command.equals("GET")) && (!command.equals("POST")) && (!command.equals("PUT"))) {
            throw new RpaApiException("Unsupported command: " + command + ".  Supported commands are GET, POST, PUT");
        }

        URL url = new URL(urlString);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod(command);

        if (headerMap != null) {
            Set keySet = headerMap.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                httpUrlConnection.addRequestProperty(key, headerMap.get(key));
            }
        }

        if ((userid != null) && (!userid.isEmpty())) {
            String authorization = "Basic " + new String(Base64.encodeBase64(
                    new String(new StringBuilder(String.valueOf(userid)).append(":").append(password).toString())
                            .getBytes()));
            httpUrlConnection.setRequestProperty("Authorization", authorization);
        }

        if ((content != null) && ((command.equals("POST")) || (command.equals("PUT")))) {
            httpUrlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(httpUrlConnection.getOutputStream());
            wr.write(content);
            wr.flush();
            wr.close();
        }

        InputStreamReader inReader = new InputStreamReader(httpUrlConnection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inReader);
        StringBuffer sb = new StringBuffer();
        String line = bufferedReader.readLine();
        while (line != null) {
            sb.append(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        httpUrlConnection.disconnect();

        if (debug) {
            System.out.println("doRest: result is " + sb.toString());
        }
        return sb.toString();
    }

    public static class RpaApiException extends Exception {
        private static final long serialVersionUID = 8768678L;

        public RpaApiException(String arg0) {
            super();
        }
    }

    private static String postEncoded(String urlString, String content, String tenantId, HashMap<String, String> params)
            throws Exception {

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("POST");

            String urlParameters = getDataString(params);

            httpUrlConnection.addRequestProperty("tenantId", tenantId);

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setInstanceFollowRedirects(false);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestProperty("charset", "utf-8");
            httpUrlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httpUrlConnection.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();

            InputStreamReader inReader = new InputStreamReader(httpUrlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inReader);
            StringBuffer sb = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                sb.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            httpUrlConnection.disconnect();

            if (debug) {
                System.out.println("doRest: result is " + sb.toString());
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RpaApiException("doRest exception: " + e.toString());
        }
    }

    private static String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private static String startProcess(String baseUrl, String tenantId, String bearerToken, String processId,
            String payload) throws Exception {

        String processInstanceId = null;

        String startProcessUrl = baseUrl
                + "/v2.0/workspace/" + tenantId + "/process/" + processId + "/instance?lang=en-US";

        HashMap<String, String> headerMap = new HashMap<String, String>();

        headerMap.put("Authorization", "Bearer " + bearerToken);
        headerMap.put("Content-Type", "application/json");

        String response = doRest("POST", startProcessUrl, payload, headerMap, null, null);

        processInstanceId = JsonUtils.getProcessId(response);

        // System.out.println(result);

        return processInstanceId;
    }

    public static void main(String args[]) {

        String baseURL = "https://uk1api.wdgautomation.com";
        String tenantId = "e780ec1f-e62f-4148-8335-2f3ac251373e";
        String username = "ncrowther@uk.ibm.com";
        String password = "Porker01!";
        String processName = "TravelAdvisoryProcess";
        String payload = "{ \"payload\": { \"in_destination\": \"Barbados\" }}";
        final int WAIT_SECS = 30;

        try {
            RpaResponse result = startProcessAndWait(baseURL, tenantId, username, password, processName, payload,
                    WAIT_SECS);

            String status = result.getResponseStatus();
            String responsePayload = result.getResponsePayload();

            if (status.equals("done")) {
                Object outputVar = JsonUtils.getResultVar(responsePayload, "out_travelAdvice");
                System.out.println(outputVar);
            } else {
                System.out.println("Failed: " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
