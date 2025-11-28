package br.com.glicemia.service;

import br.com.glicemia.model.vo.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ContextoIA {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constroi prompt completo para a IA com contexto do paciente.
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

    // Formata um sinal vital de forma legível para a IA.
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

    // Cria um resumo estaitstico do historico para inclusão no prompt.
    public static String criarResumoEstatistico(List<SinalVital> historico) {
        if (historico == null || historico.isEmpty()) {
            return "Sem dados estatísticos disponíveis.";
        }

        StringBuilder resumo = new StringBuilder();
        resumo.append("═══ RESUMO ESTATÍSTICO ═══\n");

        // Conta tipos de registro
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
