package br.com.glicemia.service.impl;

import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.ContextoIA;
import br.com.glicemia.service.ServicoIA;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpenAIService implements ServicoIA {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String modelo;
    private final OkHttpClient client;
    private final Gson gson;

    public OpenAIService(String apiKey, String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo != null ? modelo : "gpt-3.5-turbo";
        this.gson = new Gson();

        // Configura cliente HTTP com timeout
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    @Override
    public String solicitarRecomendacao(List<SinalVital> historicoRecente, String pergunta)
            throws IOException {

        // Verifica se é um prompt customizado (para chat)
        String promptCompleto;
        if (pergunta.startsWith("__CUSTOM_PROMPT__:")) {
            // Usa o prompt customizado fornecido
            promptCompleto = pergunta.substring("__CUSTOM_PROMPT__:".length());
        } else {
            // Constrói o prompt com contexto padrão
            promptCompleto = ContextoIA.construirPrompt(historicoRecente, pergunta);
        }

        // Monta o JSON da requisição
        JsonObject mensagem = new JsonObject();
        mensagem.addProperty("role", "user");
        mensagem.addProperty("content", promptCompleto);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", modelo);
        requestBody.add("messages", gson.toJsonTree(new JsonObject[]{
            gson.fromJson(mensagem, JsonObject.class)
        }));
        requestBody.addProperty("max_tokens", 500);
        requestBody.addProperty("temperature", 0.7);

        // Cria a requisição HTTP
        Request request = new Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(gson.toJson(requestBody), JSON))
            .build();

        // Executa a requisição
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro na API OpenAI: " + response.code() +
                                    " - " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // Extrai a resposta
            return jsonResponse
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        }
    }

    @Override
    public boolean isDisponivel() {
        try {
            // Tenta fazer uma requisição simples para verificar conectividade
            Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .head()
                .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful() || response.code() == 401;
                // (apenas chave inválida)
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getNomeProvedor() {
        return "OpenAI " + modelo;
    }
}
