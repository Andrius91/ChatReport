package icu.yogurt.chatreport.common;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class API {
    private final OkHttpClient client;
    private final String baseUrl;

    public API(String baseUrl, String apiKey) {
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
        this.baseUrl = baseUrl;
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