package br.com.glicemia.service;

import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.impl.IALocalService;
import br.com.glicemia.service.impl.OpenAIService;
import br.com.glicemia.util.EnvLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class GerenciadorIA {
    private ServicoIA servicoPrincipal;
    private final ServicoIA servicoFallback;
    private boolean fallbackAtivo = false;

    public GerenciadorIA() {
        // Carrega configurações
        Properties props = carregarConfiguracoes();

        String provider = props.getProperty("ia.provider", "local");
        String apiKey = props.getProperty("ia.api.key");
        String modelo = props.getProperty("ia.model", "gpt-3.5-turbo");

        // Inicializa serviço principal
        if ("openai".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isEmpty()) {
            this.servicoPrincipal = new OpenAIService(apiKey, modelo);
        } else {
            this.servicoPrincipal = null;
        }

        // Fallback local
        this.servicoFallback = new IALocalService();
    }

    public String solicitarRecomendacao(List<SinalVital> historico, String pergunta) {
        // Tentar usar serviço principal primeiro
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

        // Fallback local
        try {
            return servicoFallback.solicitarRecomendacao(historico, pergunta);
        } catch (Exception e) {
            return "Erro ao gerar recomendação: " + e.getMessage();
        }
    }

    /**
     * Solicitar recomendação via chat com validação do tópico.
     *
     * @param session Sessão de chat com histórico
     * @param pergunta Nova pergunta do usuário
     * @return Resposta da IA ou mensagem de rejeição de tópico
     */
    public String solicitarRecomendacaoConversacional(
            ConversationSession session,
            String pergunta) {

        // Validação de tópico
        TopicValidator validator = new TopicValidator();
        if (!validator.isTopicoValido(pergunta)) {
            return validator.getMensagemRejeicao();
        }

        // Constrói prompt conversacional
        String promptConversacional = ConversationContextBuilder
            .construirPromptConversacional(session, pergunta);

        // Adiciona tag para identificar prompt customizado
        String perguntaComTag = "__CUSTOM_PROMPT__:" + promptConversacional;

        // Usa serviço de IA com fallback
        String resposta;
        if (servicoPrincipal != null && servicoPrincipal.isDisponivel()) {
            try {

                fallbackAtivo = false;

                // Chama IA com prompt conversacional
                resposta = servicoPrincipal.solicitarRecomendacao(
                    session.getHistoricoPaciente(),
                    perguntaComTag
                );

            } catch (Exception e) {
                System.err.println("⚠ Falha no serviço principal de IA: " + e.getMessage());
                System.out.println("Usando IA local como fallback...\n");
                fallbackAtivo = true;

                try {

                    resposta = servicoFallback.solicitarRecomendacao(
                        session.getHistoricoPaciente(),
                        pergunta
                    );
                } catch (Exception ex) {
                    return "Erro ao gerar recomendação: " + ex.getMessage();
                }
            }
        } else {
            // Usa fallback local
            try {

                fallbackAtivo = true;

                resposta = servicoFallback.solicitarRecomendacao(
                    session.getHistoricoPaciente(),
                    pergunta
                );
            } catch (Exception e) {
                return "Erro ao gerar recomendação: " + e.getMessage();
            }
        }

        // Adiciona turno à sessão
        session.adicionarTurno(pergunta, resposta);

        return resposta;
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

        // Tenta carregar do EnvLoader (.env) primeiro
        if (EnvLoader.isLoaded()) {
            String provider = EnvLoader.get("IA_PROVIDER");
            String apiKey = EnvLoader.get("IA_API_KEY");
            String model = EnvLoader.get("IA_MODEL");

            if (provider != null) props.setProperty("ia.provider", provider);
            if (apiKey != null) props.setProperty("ia.api.key", apiKey);
            if (model != null) props.setProperty("ia.model", model);

            return props;
        }

        // Fallback: Carregar database.properties e resolve placeholders
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                Properties rawProps = new Properties();
                rawProps.load(input);

                String provider = resolveProperty(rawProps.getProperty("ia.provider"));
                String apiKey = resolveProperty(rawProps.getProperty("ia.api.key"));
                String model = resolveProperty(rawProps.getProperty("ia.model"));

                if (provider != null) props.setProperty("ia.provider", provider);
                if (apiKey != null) props.setProperty("ia.api.key", apiKey);
                if (model != null) props.setProperty("ia.model", model);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações: " + e.getMessage());
        }

        return props;
    }

    // Resolve placeholders ${VARIABLE} usando EnvLoader ou System.getenv().
    private String resolveProperty(String value) {
        if (value == null) {
            return null;
        }

        if (value.startsWith("${") && value.endsWith("}")) {
            String envKey = value.substring(2, value.length() - 1);
            String envValue = EnvLoader.get(envKey);

            if (envValue == null) {
                envValue = System.getenv(envKey);
            }

            return envValue != null ? envValue : value;
        }

        return value;
    }
}
