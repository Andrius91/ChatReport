package icu.yogurt.chatreport.common;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static icu.yogurt.chatreport.common.ConfigKeys.API_HOST;
import static icu.yogurt.chatreport.common.ConfigKeys.API_KEY;

public class API {
    private final OkHttpClient client;
    private final String baseUrl;

    public API() {

        String apiKey = API_KEY.get();
        this.baseUrl = API_HOST.get();

        if(apiKey.isEmpty() || baseUrl.isEmpty()){
            throw new RuntimeException("Invalid api host: " + baseUrl + " and api key: " + apiKey);
        }

        this.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "Api " + apiKey)
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    public CompletableFuture<String> getAsync(String endpoint) {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .build();

        return getStringCompletableFuture(request);
    }

    public CompletableFuture<String> postAsync(String endpoint, String json) {
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .post(body)
                .build();

        return getStringCompletableFuture(request);
    }

    public CompletableFuture<String> updateAsync(String endpoint, String json) {
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .method("PATCH", body)
                .build();

        return getStringCompletableFuture(request);
    }

    @NotNull
    private CompletableFuture<String> getStringCompletableFuture(Request request) {
        return CompletableFuture.supplyAsync(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println(response.body().string());
                    throw new IOException("Unexpected code " + response);
                }

                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}