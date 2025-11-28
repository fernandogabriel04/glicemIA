package br.com.glicemia.service.impl;

import br.com.glicemia.model.vo.*;
import br.com.glicemia.service.ServicoIA;

import java.util.List;

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
        } else {
            return gerarRecomendacaoGenerica(pergunta);
        }
    }

    @Override
    public boolean isDisponivel() {
        return true;
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
