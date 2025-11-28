package br.com.glicemia.service;

import java.util.Arrays;
import java.util.List;

// Valida se perguntas estão dentro do escopo permitido: Diabetes e Hipertensão apenas.
public class TopicValidator {

    // Palavras-chave relacionadas ao escopo permitido
    private static final List<String> KEYWORDS_DIABETES = Arrays.asList(
        "diabetes", "diabético", "diabética", "glicemia", "glicose",
        "açúcar", "insulina", "hipoglicemia", "hiperglicemia",
        "hemoglobina glicada", "hba1c", "carboidrato", "carboidratos",
        "glicêmico", "glicêmica"
    );

    private static final List<String> KEYWORDS_HIPERTENSAO = Arrays.asList(
        "pressão", "hipertensão", "hipertenso", "hipertensa",
        "sistólica", "diastólica", "cardiovascular", "coração",
        "sódio", "sal", "pressão arterial", "pressão alta",
        "hipotensão", "hipotenso", "hipotensa"
    );

    private static final List<String> KEYWORDS_RELACIONADOS = Arrays.asList(
        "dieta", "alimentação", "exercício", "exercícios", "atividade física",
        "peso", "imc", "obesidade", "sobrepeso", "nutrição",
        "medicamento", "medicação", "remédio", "tratamento",
        "sintoma", "sintomas", "médico", "consulta", "exame",
        "saúde", "doença", "doente", "colesterol", "triglicerídeos",
        "comer", "beber", "comida", "alimento", "alimentos",
        "jejum", "pós-prandial", "refeição", "refeições",
        "medir", "medição", "controle", "monitoramento",
        "risco", "emergência", "crise", "urgência"
    );

    /**
     * Valida se a pergunta está dentro do escopo permitido.
     *
     * @param pergunta Pergunta do usuário
     * @return true se a pergunta é válida (diabetes/hipertensão)
     */
    public boolean isTopicoValido(String pergunta) {
        if (pergunta == null || pergunta.trim().isEmpty()) {
            return false;
        }

        String perguntaLower = pergunta.toLowerCase();

        // Verifica se contém palavras-chave do escopo
        boolean contemDiabetes = KEYWORDS_DIABETES.stream()
            .anyMatch(perguntaLower::contains);

        boolean contemHipertensao = KEYWORDS_HIPERTENSAO.stream()
            .anyMatch(perguntaLower::contains);

        boolean contemRelacionado = KEYWORDS_RELACIONADOS.stream()
            .anyMatch(perguntaLower::contains);

        return contemDiabetes || contemHipertensao || contemRelacionado;
    }

    // Gera mensagem de rejeição educada para tópicos fora do escopo.
    public String getMensagemRejeicao() {
        return "Desculpe, sou um assistente especializado em diabetes e hipertensão. 🩺\n\n" +
               "Posso ajudar com perguntas sobre:\n" +
               "  • Controle de glicemia e diabetes\n" +
               "  • Pressão arterial e hipertensão\n" +
               "  • Alimentação para essas condições\n" +
               "  • Exercícios recomendados\n" +
               "  • Interpretação dos seus registros\n\n" +
               "Por favor, faça uma pergunta relacionada a esses temas. 😊";
    }

    // Detectar comandos de saída do chat.
    public boolean isComandoSaida(String entrada) {
        if (entrada == null) return false;

        String entradaLower = entrada.trim().toLowerCase();
        return entradaLower.equals("voltar") ||
               entradaLower.equals("sair") ||
               entradaLower.equals("exit") ||
               entradaLower.equals("quit");
    }
}
