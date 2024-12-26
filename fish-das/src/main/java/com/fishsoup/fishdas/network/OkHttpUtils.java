package com.fishsoup.fishdas.network;

import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkHttpUtils {

    public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private final OkHttpClient client;

    public String get(String url, Headers headers, String referer) {
        if (StringUtils.hasText(referer)) {
            url = referer + url;
        }
        try (Response response = client.newCall(new Request.Builder().headers(headers).url(url).get().build()).execute()) {
            if (!response.isSuccessful()) {
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", url, ioException.getMessage(), ioException);
            return "";
        }
    }

    public String post(String url, Headers headers, RequestBody requestBody) {
        try (Response response = client.newCall(new Request.Builder().headers(headers).url(url).post(requestBody).build()).execute()) {
            if (!response.isSuccessful()) {
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", url, ioException.getMessage(), ioException);
            return "";
        }
    }

    public String postForm(String url, Headers headers, String referer, Map<String, String> form) {
        if (StringUtils.hasText(referer)) {
            url = referer + url;
        }
        FormBody.Builder builder = new FormBody.Builder();
        form.forEach(builder::add);
        RequestBody requestBody = builder.build();
        return post(url, headers, requestBody);
    }

    public String postJson(String url, Headers headers, String referer, String json) {
        if (StringUtils.hasText(referer)) {
            url = referer + url;
        }
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        return post(url, headers, requestBody);
    }

    public Response downloadImage(String url, Headers headers, String referer) {
        if (StringUtils.hasText(referer)) {
            url = referer + url;
        }
        try {
            return client.newCall(new Request.Builder().headers(headers).url(url).get().build()).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
