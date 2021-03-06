package net.minecraft.launcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.Charsets;

public class Http
{
  public static String buildQuery(Map<String, Object> query)
  {
    StringBuilder builder = new StringBuilder();

    for (Map.Entry entry : query.entrySet()) {
      if (builder.length() > 0) {
        builder.append('&');
      }
      try
      {
        builder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        Launcher.getInstance().println("Unexpected exception building query", e);
      }

      if (entry.getValue() != null) {
        builder.append('=');
        try {
          builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          Launcher.getInstance().println("Unexpected exception building query", e);
        }
      }
    }

    return builder.toString();
  }

  public static String performPost(URL url, Map<String, Object> query, Proxy proxy) throws IOException {
    return performPost(url, buildQuery(query), proxy, "application/x-www-form-urlencoded", false);
  }

  public static String performPost(URL url, String parameters, Proxy proxy, String contentType, boolean returnErrorPage) throws IOException {
    HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
    byte[] paramAsBytes = parameters.getBytes(Charsets.UTF_8);

    connection.setConnectTimeout(15000);
    connection.setReadTimeout(15000);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");

    connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
    connection.setRequestProperty("Content-Language", "en-US");

    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);

    DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
    writer.write(paramAsBytes);
    writer.flush();
    writer.close();
    try
    {
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }
    catch (IOException e)
    {
      BufferedReader reader;
      BufferedReader reader;
      if (returnErrorPage) {
        InputStream stream = connection.getErrorStream();

        if (stream != null)
          reader = new BufferedReader(new InputStreamReader(stream));
        else
          throw e;
      }
      else {
        throw e;
      }
    }
    BufferedReader reader;
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }

    reader.close();
    return response.toString();
  }

  public static String performGet(URL url, Proxy proxy) throws IOException {
    HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
    connection.setRequestMethod("GET");

    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }

    reader.close();
    return response.toString();
  }

  public static URL concatenateURL(URL url, String args) throws MalformedURLException {
    if ((url.getQuery() != null) && (url.getQuery().length() > 0)) {
      return new URL(url.getProtocol(), url.getHost(), url.getFile() + "?" + args);
    }
    return new URL(url.getProtocol(), url.getHost(), url.getFile() + "&" + args);
  }
}