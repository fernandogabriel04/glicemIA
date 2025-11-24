# Fase 07 - Integração com IA Generativa

## 🎯 Objetivos
- Integrar com API de IA (OpenAI/Gemini)
- Implementar construção de contexto histórico
- Criar sistema de fallback local
- Garantir resiliência em caso de falha de rede

## 📚 Conceitos Aplicados
- ✅ **API REST**: Comunicação HTTP com serviços externos
- ✅ **Padrão Fallback**: Graceful degradation
- ✅ **JSON**: Serialização e desserialização
- ✅ **Tratamento de Exceções**: Resiliência

## 🔧 Implementação

### 1. Interface ServicoIA

Crie `src/main/java/br/com/glicemia/service/ServicoIA.java`:

```java
package br.com.glicemia.service;

import br.com.glicemia.model.vo.SinalVital;
import java.util.List;

/**
 * Interface para serviços de Inteligência Artificial.
 * Permite trocar a implementação (OpenAI, Gemini, local) sem afetar o código.
 */
public interface ServicoIA {

    /**
     * Solicita uma recomendação de saúde baseada no histórico.
     *
     * @param historicoRecente Lista dos últimos sinais vitais
     * @param pergunta Pergunta específica do usuário
     * @return Resposta da IA
     * @throws Exception se houver erro na comunicação
     */
    String solicitarRecomendacao(List<SinalVital> historicoRecente, String pergunta)
            throws Exception;

    /**
     * Verifica se o serviço de IA está disponível.
     * @return true se a IA está acessível
     */
    boolean isDisponivel();

    /**
     * Retorna o nome do provedor de IA.
     * @return Nome do provedor (ex: "OpenAI GPT-4", "Gemini Pro", "Local")
     */
    String getNomeProvedor();
}
```

### 2. Classe ContextoIA

Crie `src/main/java/br/com/glicemia/service/ContextoIA.java`:

```java
package br.com.glicemia.service;

import br.com.glicemia.model.vo.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe responsável por construir o contexto que será enviado à IA.
 * Formata o histórico de sinais vitais de forma legível para a IA.
 */
public class ContextoIA {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constrói o prompt completo para a IA com contexto do paciente.
     *
     * @param historico Lista de sinais vitais recentes
     * @param pergunta Pergunta específica do usuário
     * @return Prompt formatado para envio à IA
     */
    public static String construirPrompt(List<SinalVital> historico, String pergunta) {
        StringBuilder prompt = new StringBuilder();

        // Instruções de sistema para a IA
        prompt.append("Você é um assistente de saúde especializado em diabetes e ");
        prompt.append("doenças metabólicas. Sua função é analisar históricos de sinais ");
        prompt.append("vitais e fornecer recomendações de estilo de vida.\n\n");

        prompt.append("IMPORTANTE:\n");
        prompt.append("- Você NÃO é um médico e NÃO pode diagnosticar doenças\n");
        prompt.append("- Sempre incentive consulta médica para casos sérios\n");
        prompt.append("- Forneça dicas práticas de alimentação e hábitos\n");
        prompt.append("- Seja específico e baseado em evidências\n\n");

        // Contexto do histórico
        prompt.append("═══ HISTÓRICO DO PACIENTE (últimos registros) ═══\n\n");

        if (historico == null || historico.isEmpty()) {
            prompt.append("Nenhum registro anterior encontrado.\n\n");
        } else {
            for (int i = 0; i < historico.size(); i++) {
                SinalVital sinal = historico.get(i);
                prompt.append(String.format("Registro %d:\n", i + 1));
                prompt.append("  Data: ")
                      .append(sinal.getDataHora().format(FORMATTER))
                      .append("\n");
                prompt.append("  ").append(formatarSinalVital(sinal)).append("\n");
                prompt.append("  Nível de Risco: ")
                      .append(sinal.getNivelRisco() != null ?
                             sinal.getNivelRisco().getDescricao() : "Não analisado")
                      .append("\n");

                if (sinal.getObservacoes() != null && !sinal.getObservacoes().isEmpty()) {
                    prompt.append("  Observações: ")
                          .append(sinal.getObservacoes())
                          .append("\n");
                }
                prompt.append("\n");
            }
        }

        // Pergunta do usuário
        prompt.append("═══ PERGUNTA DO USUÁRIO ═══\n");
        prompt.append(pergunta).append("\n\n");

        prompt.append("Por favor, forneça uma resposta em português, ");
        prompt.append("clara e prática, com no máximo 200 palavras.");

        return prompt.toString();
    }

    /**
     * Formata um sinal vital de forma legível para a IA.
     */
    private static String formatarSinalVital(SinalVital sinal) {
        if (sinal instanceof Glicemia) {
            Glicemia g = (Glicemia) sinal;
            return String.format("Glicemia: %.1f mg/dL (%s)",
                g.getValorGlicemia(),
                g.isEmJejum() ? "Jejum" : "Pós-prandial");

        } else if (sinal instanceof PressaoArterial) {
            PressaoArterial p = (PressaoArterial) sinal;
            return String.format("Pressão Arterial: %d/%d mmHg",
                p.getSistolica(),
                p.getDiastolica());

        } else if (sinal instanceof PesoCorporal) {
            PesoCorporal p = (PesoCorporal) sinal;
            return String.format("Peso: %.1f kg | IMC: %.1f (%s)",
                p.getPeso(),
                p.getImc() != null ? p.getImc() : 0.0,
                p.getClassificacaoIMC());
        }

        return sinal.getDescricao();
    }

    /**
     * Cria um resumo estatístico do histórico para inclusão no prompt.
     */
    public static String criarResumoEstatistico(List<SinalVital> historico) {
        if (historico == null || historico.isEmpty()) {
            return "Sem dados estatísticos disponíveis.";
        }

        StringBuilder resumo = new StringBuilder();
        resumo.append("═══ RESUMO ESTATÍSTICO ═══\n");

        // Conta tipos de registros
        long countGlicemia = historico.stream()
            .filter(s -> s instanceof Glicemia)
            .count();
        long countPressao = historico.stream()
            .filter(s -> s instanceof PressaoArterial)
            .count();
        long countPeso = historico.stream()
            .filter(s -> s instanceof PesoCorporal)
            .count();

        resumo.append(String.format("Total de registros: %d\n", historico.size()));
        resumo.append(String.format("  - Glicemia: %d\n", countGlicemia));
        resumo.append(String.format("  - Pressão: %d\n", countPressao));
        resumo.append(String.format("  - Peso: %d\n", countPeso));

        return resumo.toString();
    }
}
```

### 3. Implementação OpenAI

Crie `src/main/java/br/com/glicemia/service/impl/OpenAIService.java`:

```java
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

/**
 * Implementação do serviço de IA usando OpenAI (GPT).
 */
public class OpenAIService implements ServicoIA {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String modelo;
    private final OkHttpClient client;
    private final Gson gson;

    public OpenAIService(String apiKey, String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo != null ? modelo : "gpt-4";
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

        // Constrói o prompt com contexto
        String promptCompleto = ContextoIA.construirPrompt(historicoRecente, pergunta);

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
                // 401 significa que a URL está acessível (apenas chave inválida)
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
```

### 4. Implementação Fallback Local

Crie `src/main/java/br/com/glicemia/service/impl/IALocalService.java`:

```java
package br.com.glicemia.service.impl;

import br.com.glicemia.model.vo.*;
import br.com.glicemia.service.ServicoIA;

import java.util.List;

/**
 * Implementação local (fallback) do serviço de IA.
 * Usado quando não há conexão com internet ou a API falha.
 * Fornece recomendações baseadas em regras pré-programadas.
 */
public class IALocalService implements ServicoIA {

    @Override
    public String solicitarRecomendacao(List<SinalVital> historicoRecente, String pergunta) {
        // Analisa o histórico e gera recomendação baseada em regras
        if (historicoRecente == null || historicoRecente.isEmpty()) {
            return gerarRecomendacaoSemHistorico(pergunta);
        }

        // Identifica o tipo predominante de registro
        SinalVital ultimoRegistro = historicoRecente.get(0);

        if (ultimoRegistro instanceof Glicemia) {
            return gerarRecomendacaoGlicemia(historicoRecente, pergunta);
        } else if (ultimoRegistro instanceof PressaoArterial) {
            return gerarRecomendacaoPressao(historicoRecente, pergunta);
        } else if (ultimoRegistro instanceof PesoCorporal) {
            return gerarRecomendacaoPeso(historicoRecente, pergunta);
        }

        return gerarRecomendacaoGenerica(pergunta);
    }

    @Override
    public boolean isDisponivel() {
        return true; // Sempre disponível
    }

    @Override
    public String getNomeProvedor() {
        return "IA Local (Fallback)";
    }

    // Métodos privados para gerar recomendações específicas

    private String gerarRecomendacaoGlicemia(List<SinalVital> historico, String pergunta) {
        Glicemia ultima = (Glicemia) historico.get(0);
        double valor = ultima.getValorGlicemia();

        StringBuilder recomendacao = new StringBuilder();
        recomendacao.append("📊 Análise Local - Glicemia\n\n");

        if (valor < 70) {
            recomendacao.append("Sua glicemia está baixa. Recomendações:\n\n");
            recomendacao.append("🍎 Alimentação:\n");
            recomendacao.append("- Faça refeições regulares (3-3 horas)\n");
            recomendacao.append("- Inclua carboidratos complexos (arroz integral, aveia)\n");
            recomendacao.append("- Tenha sempre um lanche de emergência\n\n");
            recomendacao.append("⚠ Evite jejum prolongado");

        } else if (valor > 126) {
            recomendacao.append("Sua glicemia está elevada. Recomendações:\n\n");
            recomendacao.append("🥗 Alimentação:\n");
            recomendacao.append("- Reduza carboidratos simples (açúcar, doces, pão branco)\n");
            recomendacao.append("- Aumente fibras (verduras, legumes, grãos integrais)\n");
            recomendacao.append("- Beba bastante água (2L/dia)\n");
            recomendacao.append("- Evite refrigerantes e sucos industrializados\n\n");
            recomendacao.append("🏃 Atividade:\n");
            recomendacao.append("- Caminhada 30min/dia ajuda a controlar a glicemia");

        } else {
            recomendacao.append("Sua glicemia está controlada! Continue assim:\n\n");
            recomendacao.append("✓ Mantenha alimentação equilibrada\n");
            recomendacao.append("✓ Pratique atividade física regular\n");
            recomendacao.append("✓ Monitore sua glicemia regularmente");
        }

        recomendacao.append("\n\n⚕ IMPORTANTE: Consulte seu médico regularmente.");

        return recomendacao.toString();
    }

    private String gerarRecomendacaoPressao(List<SinalVital> historico, String pergunta) {
        PressaoArterial ultima = (PressaoArterial) historico.get(0);

        StringBuilder recomendacao = new StringBuilder();
        recomendacao.append("📊 Análise Local - Pressão Arterial\n\n");
        recomendacao.append("Recomendações gerais para controle da pressão:\n\n");
        recomendacao.append("🧂 Alimentação:\n");
        recomendacao.append("- REDUZA o sal (máx. 5g/dia = 1 colher de chá)\n");
        recomendacao.append("- Evite alimentos processados e embutidos\n");
        recomendacao.append("- Aumente potássio (banana, abacate, vegetais verdes)\n\n");
        recomendacao.append("🧘 Estilo de Vida:\n");
        recomendacao.append("- Pratique técnicas de relaxamento\n");
        recomendacao.append("- Durma 7-8 horas por noite\n");
        recomendacao.append("- Limite álcool e evite cigarro\n");
        recomendacao.append("- Exercite-se 150min/semana\n\n");
        recomendacao.append("⚕ Consulte cardiologista regularmente");

        return recomendacao.toString();
    }

    private String gerarRecomendacaoPeso(List<SinalVital> historico, String pergunta) {
        PesoCorporal ultimo = (PesoCorporal) historico.get(0);

        StringBuilder recomendacao = new StringBuilder();
        recomendacao.append("📊 Análise Local - Peso Corporal\n\n");
        recomendacao.append("Seu IMC atual: ")
                     .append(String.format("%.1f", ultimo.getImc()))
                     .append(" (").append(ultimo.getClassificacaoIMC()).append(")\n\n");
        recomendacao.append("Recomendações:\n\n");
        recomendacao.append("🍽 Alimentação Balanceada:\n");
        recomendacao.append("- Controle porções\n");
        recomendacao.append("- Coma devagar e mastigue bem\n");
        recomendacao.append("- Priorize alimentos naturais\n\n");
        recomendacao.append("🏃 Atividade Física:\n");
        recomendacao.append("- Combine aeróbico com musculação\n");
        recomendacao.append("- Comece gradualmente\n\n");
        recomendacao.append("⚕ Procure nutricionista para plano personalizado");

        return recomendacao.toString();
    }

    private String gerarRecomendacaoSemHistorico(String pergunta) {
        return "📊 IA Local - Sem Histórico\n\n" +
               "Não há registros anteriores para análise.\n\n" +
               "Recomendação Geral:\n" +
               "- Mantenha registros regulares dos seus sinais vitais\n" +
               "- Consulte seu médico regularmente\n" +
               "- Adote hábitos saudáveis: alimentação equilibrada, " +
               "exercícios e sono adequado\n\n" +
               "⚕ Para orientações específicas, consulte um profissional de saúde.";
    }

    private String gerarRecomendacaoGenerica(String pergunta) {
        return "📊 IA Local\n\n" +
               "Para recomendações personalizadas, é necessário:\n" +
               "- Manter histórico de medições regulares\n" +
               "- Consultar profissionais de saúde\n" +
               "- Fazer exames médicos periódicos\n\n" +
               "⚠ Esta é uma resposta genérica. Para análise detalhada, " +
               "é necessária conexão com a IA online.";
    }
}
```

### 5. Gerenciador de Serviços de IA

Crie `src/main/java/br/com/glicemia/service/GerenciadorIA.java`:

```java
package br.com.glicemia.service;

import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.impl.IALocalService;
import br.com.glicemia.service.impl.OpenAIService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Gerenciador que coordena os serviços de IA com fallback automático.
 */
public class GerenciadorIA {

    private ServicoIA servicoPrincipal;
    private final ServicoIA servicoFallback;
    private boolean fallbackAtivo = false;

    public GerenciadorIA() {
        // Carrega configurações
        Properties props = carregarConfiguracoes();

        String provider = props.getProperty("ia.provider", "local");
        String apiKey = props.getProperty("ia.api.key");
        String modelo = props.getProperty("ia.model", "gpt-4");

        // Inicializa serviço principal
        if ("openai".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isEmpty()) {
            this.servicoPrincipal = new OpenAIService(apiKey, modelo);
        } else {
            this.servicoPrincipal = null;
        }

        // Fallback sempre disponível
        this.servicoFallback = new IALocalService();
    }

    /**
     * Solicita recomendação com fallback automático.
     */
    public String solicitarRecomendacao(List<SinalVital> historico, String pergunta) {
        // Tenta usar serviço principal primeiro
        if (servicoPrincipal != null && servicoPrincipal.isDisponivel()) {
            try {
                fallbackAtivo = false;
                return servicoPrincipal.solicitarRecomendacao(historico, pergunta);

            } catch (Exception e) {
                System.err.println("⚠ Falha no serviço principal de IA: " + e.getMessage());
                System.out.println("Usando IA local como fallback...\n");
                fallbackAtivo = true;
            }
        }

        // Usa fallback local
        try {
            return servicoFallback.solicitarRecomendacao(historico, pergunta);
        } catch (Exception e) {
            return "Erro ao gerar recomendação: " + e.getMessage();
        }
    }

    public boolean isFallbackAtivo() {
        return fallbackAtivo;
    }

    public String getProvedorAtivo() {
        if (fallbackAtivo || servicoPrincipal == null) {
            return servicoFallback.getNomeProvedor();
        }
        return servicoPrincipal.getNomeProvedor();
    }

    private Properties carregarConfiguracoes() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações: " + e.getMessage());
        }
        return props;
    }
}
```

## ✅ Checklist de Validação

- [ ] Interface `ServicoIA` criada
- [ ] Classe `ContextoIA` implementada
- [ ] `OpenAIService` implementado (ou outro provedor)
- [ ] `IALocalService` implementado como fallback
- [ ] `GerenciadorIA` coordena com fallback automático
- [ ] Teste com API real (se tiver chave)
- [ ] Teste de fallback funciona sem internet
- [ ] Contexto histórico é formatado corretamente

## 📌 Próximos Passos

**Próxima fase**: **[Fase 08 - Camada View (Interface Console)](./08-camada-view.md)**

---

**Conceitos implementados**: Integração API ✅ | Fallback ✅ | Resiliência ✅
