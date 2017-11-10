package org.client.bracelet.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    private static int HTTP_OK = 200;


    public static String postRequest(String url, String jsonString) {
        try {
            System.out.println(url);
            System.out.println(jsonString);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(jsonString, "UTF-8"));
            post.addHeader("User-Agent", "");
            HttpParams httpParams = post.getParams();
            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            System.out.println(httpEntity);
            if (httpEntity != null) {
                String result = EntityUtils.toString(httpEntity, "UTF-8");
                System.out.println(result);
                return result;
            } else {
                return "error";
            }
        } catch (IOException e) {
            return "error";
        }
    }

//    public static String postRequest(String url, String jsonString){
//        StringBuilder sb = new StringBuilder();
//        try {
//            byte[] data = jsonString.getBytes();
//            URL httpUrl = new URL(url);
//            System.out.println(url);
//            /*获取网络连接*/
//            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
//            /*设置请求方法为POST方法*/
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "text/json;charset=UTF-8");
//            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setInstanceFollowRedirects(true);
//            conn.setConnectTimeout(5000);
//            conn.setReadTimeout(5000);
//
//            OutputStream outputStream = conn.getOutputStream();
//            BufferedOutputStream writer = new BufferedOutputStream(outputStream);
//            System.out.println(jsonString);
//            writer.write(data);
//            writer.flush();
//            writer.close();
//            outputStream.close();
//
//            System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
//
//            if (conn.getResponseCode() == HTTP_OK) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String temp;
//                while ((temp = reader.readLine()) != null) {
//                    sb.append(temp);
//                }
//                reader.close();
//            } else {
//                sb.append("connection error: ").append(conn.getResponseCode())
//                        .append(" ").append(conn.getResponseMessage());
//            }
//
//        } catch (IOException e) {
//            sb.append("network error!");
//        }
//        return sb.toString();
//    }

}
