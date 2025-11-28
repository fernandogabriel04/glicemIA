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
            System.out.println("  ← Resposta HTTP: " + response.code() + " " + response.message());

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "sem corpo";
                System.err.println("  ✗ Erro na API: " + errorBody);
                throw new IOException("Erro na API OpenAI: " + response.code() +
                                    " - " + response.message() + " | " + errorBody);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // Extrai a resposta
            String conteudo = jsonResponse
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

            System.out.println("  ✓ Resposta extraída com sucesso (" + conteudo.length() + " caracteres)");
            return conteudo;
        }
    }

    @Override
    public boolean isDisponivel() {
        // DEBUG
        System.out.println("  → Verificando disponibilidade da OpenAI...");
        System.out.println("  API Key presente: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("  Modelo: " + modelo);

        // A API da OpenAI não suporta HEAD requests no endpoint de chat.
        // Verificamos apenas se a API key está configurada.
        // Erros de conectividade/autenticação serão capturados no método solicitarRecomendacao().
        boolean disponivel = apiKey != null && !apiKey.isEmpty();

        System.out.println("  " + (disponivel ? "✓" : "✗") + " API disponível: " + disponivel);

        return disponivel;
    }

    @Override
    public String getNomeProvedor() {
        return "OpenAI " + modelo;
    }
}
