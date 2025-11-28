package br.com.glicemia.service;

import br.com.glicemia.model.vo.*;
import java.time.format.DateTimeFormatter;
import java.util.List;


 // Constrói prompts para IA com contexto de chat. Estende a funcionalidade do ContextoIA para incluir histórico do chat.
public class ConversationContextBuilder {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    
    // Constrói prompt conversacional incluindo histórico da conversa.
    public static String construirPromptConversacional(
            ConversationSession session,
            String novaPergunta) {

        StringBuilder prompt = new StringBuilder();

        // Instruções do sistema com restrição de tópico
        prompt.append("Você é um assistente de saúde especializado EXCLUSIVAMENTE em ");
        prompt.append("DIABETES e HIPERTENSÃO. Você NÃO deve responder perguntas sobre ");
        prompt.append("outros assuntos médicos ou não médicos.\n\n");

        prompt.append("REGRAS IMPORTANTES:\n");
        prompt.append("- Você NÃO é um médico e NÃO pode diagnosticar doenças\n");
        prompt.append("- Sempre incentive consulta médica para casos sérios\n");
        prompt.append("- Forneça dicas práticas de alimentação e hábitos\n");
        prompt.append("- Seja específico e baseado em evidências\n");
        prompt.append("- Responda APENAS sobre diabetes e hipertensão\n");
        prompt.append("- Se a pergunta for fora do escopo, redirecione educadamente\n");
        prompt.append("- Mantenha tom conversacional e amigável\n\n");

        // Contexto do histórico de sinais vitais
        List<SinalVital> historico = session.getHistoricoPaciente();
        prompt.append("═══ HISTÓRICO CLÍNICO DO PACIENTE ═══\n\n");

        if (historico == null || historico.isEmpty()) {
            prompt.append("Nenhum registro de sinais vitais disponível.\n\n");
        } else {
            // Mostra últimos 5 registros
            int limite = Math.min(5, historico.size());
            for (int i = 0; i < limite; i++) {
                SinalVital sinal = historico.get(i);
                prompt.append(String.format("Registro %d:\n", i + 1));
                prompt.append("  Data: ")
                      .append(sinal.getDataHora().format(FORMATTER))
                      .append("\n");
                prompt.append("  ").append(formatarSinalVital(sinal)).append("\n");
                prompt.append("  Nível de Risco: ")
                      .append(sinal.getNivelRisco() != null ?
                             sinal.getNivelRisco().getDescricao() : "Não analisado")
                      .append("\n\n");
            }
        }

        // Histórico da conversa (últimas interações)
        if (session.getQuantidadeTurnos() > 0) {
            prompt.append("═══ CONVERSA ANTERIOR NESTA SESSÃO ═══\n\n");
            prompt.append(session.getResumoConversa()).append("\n");
        }

        // Pergunta atual
        prompt.append("═══ NOVA PERGUNTA DO PACIENTE ═══\n");
        prompt.append(novaPergunta).append("\n\n");

        prompt.append("Por favor, responda em português, de forma clara e prática, ");
        prompt.append("com no máximo 200 palavras.");

        return prompt.toString();
    }

    // Formata um sinal vital de forma legível.
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
}
