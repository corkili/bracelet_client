package org.client.bracelet.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    private static int HTTP_OK = 200;


    public String postRequest(String url, String jsonString){
        StringBuilder sb = new StringBuilder();
        try {
            URL httpUrl = new URL(url);
            System.out.println(url);
            /*获取网络连接*/
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            /*设置请求方法为POST方法*/
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(true);
            conn.setReadTimeout(5000);

            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonString);
            writer.flush();
            writer.close();
            outputStream.close();

            if (conn.getResponseCode() == HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
                }
                reader.close();
            } else {
                sb.append("connection error: ").append(conn.getResponseCode())
                        .append(" ").append(conn.getResponseMessage());
            }

        } catch (IOException e) {
            sb.append("network error!");
        }
        return sb.toString();
    }

}
