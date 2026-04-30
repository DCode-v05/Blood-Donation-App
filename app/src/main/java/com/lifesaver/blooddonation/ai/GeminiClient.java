package com.lifesaver.blooddonation.ai;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.lifesaver.blooddonation.BuildConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Java port of config/gemini.js — strictly scoped to blood-donation topics
 * via the system prompt below.
 */
public class GeminiClient {

    public interface ChatCallback {
        void onResponse(String text);
        void onError(String message);
    }

    private static final String SYSTEM_PROMPT = ""
            + "You are LifeSaver AI, a specialized assistant for the LifeSaver "
            + "blood-donation app.\n\n"
            + "STRICT RULES:\n"
            + "1. ONLY answer questions related to blood donation and "
            + "transfusion, hospitals and medical facilities, blood types and "
            + "compatibility, donation eligibility, app features, and health "
            + "topics related to blood donation.\n"
            + "2. For ANY unrelated questions (sports, weather, politics, "
            + "general knowledge, etc.), politely decline and redirect to "
            + "blood / hospital topics. DO NOT answer the question.\n"
            + "3. Be helpful, concise, and accurate. Use medical facts and "
            + "WHO guidelines. Prioritise user safety.";

    private static GeminiClient INSTANCE;
    public static synchronized GeminiClient get() {
        if (INSTANCE == null) INSTANCE = new GeminiClient();
        return INSTANCE;
    }

    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private GeminiClient() {
        GenerationConfig.Builder cfg = new GenerationConfig.Builder();
        cfg.temperature     = 0.7f;
        cfg.topK            = 40;
        cfg.topP            = 0.95f;
        cfg.maxOutputTokens = 1024;

        GenerativeModel raw = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.GEMINI_API_KEY,
                cfg.build());
        model = GenerativeModelFutures.from(raw);
    }

    public void ask(@NonNull String userMessage, @NonNull ChatCallback cb) {
        Content content = new Content.Builder()
                .addText(SYSTEM_PROMPT + "\n\nUser: " + userMessage)
                .build();
        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                cb.onResponse(text == null ? "" : text);
            }
            @Override public void onFailure(@NonNull Throwable t) {
                cb.onError(t.getMessage());
            }
        }, executor);
    }
}
