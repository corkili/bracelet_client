package org.client.bracelet.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class HttpUtils {

    private static int HTTP_OK = 200;

    public static String JSESSIONID = null;

    public static String postRequest(String url, String jsonString) {
        try {
            System.out.println(url);
            System.out.println(jsonString);
            System.out.println(JSESSIONID);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(jsonString, "UTF-8"));
            post.addHeader("User-Agent", "");
            if (JSESSIONID != null) {
                post.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
            }
            HttpParams httpParams = post.getParams();
            httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                String result = EntityUtils.toString(httpEntity, "UTF-8");
                CookieStore cookieStore = ((DefaultHttpClient)httpClient).getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();
                for (Cookie cookie : cookies) {
                    if ("JSESSIONID".equals(cookie.getName())) {
                        JSESSIONID = cookie.getValue();
                        break;
                    }
                }
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
