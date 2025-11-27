package br.com.glicemia.util;

public class ProtocoloEmergencia {

    public static final String HIPOGLICEMIA_SEVERA =
        "1. INGERIR IMEDIATAMENTE 15g de carboidrato simples:\n" +
        "   • 3 balas/jujubas\n" +
        "   • 1 colher de sopa de açúcar ou mel\n" +
        "   • 150ml de suco de laranja\n" +
        "   • 1/2 copo de refrigerante comum (não diet)\n" +
        "\n" +
        "2. AGUARDAR 15 MINUTOS e medir novamente\n" +
        "\n" +
        "3. Se ainda < 70 mg/dL:\n" +
        "   • Repetir o item 1\n" +
        "   • CHAMAR EMERGÊNCIA: SAMU 192\n" +
        "\n" +
        "4. Se inconsciente:\n" +
        "   • NÃO dar nada pela boca\n" +
        "   • Deitar de lado\n" +
        "   • CHAMAR 192 IMEDIATAMENTE\n" +
        "\n" +
        "⚠ RISCO: Convulsões, perda de consciência, coma";

    public static final String HIPERGLICEMIA_SEVERA =
        "1. BEBER ÁGUA IMEDIATAMENTE (pelo menos 500ml)\n" +
        "\n" +
        "2. NÃO SE EXERCITAR (pode piorar)\n" +
        "\n" +
        "3. Verificar presença de sintomas de cetoacidose:\n" +
        "   • Náusea ou vômito\n" +
        "   • Dor abdominal\n" +
        "   • Respiração rápida e profunda\n" +
        "   • Hálito com cheiro de frutas (cetônico)\n" +
        "   • Confusão mental\n" +
        "\n" +
        "4. PROCURAR EMERGÊNCIA IMEDIATAMENTE se:\n" +
        "   • Glicemia > 300 mg/dL por mais de 2 horas\n" +
        "   • Presença de sintomas acima\n" +
        "   • Incapaz de baixar a glicemia\n" +
        "\n" +
        "5. Chamar SAMU 192 se houver vômito persistente\n" +
        "\n" +
        "⚠ RISCO: Cetoacidose diabética (potencialmente fatal)";

    public static final String CRISE_HIPERTENSIVA =
        "1. SENTAR-SE CONFORTAVELMENTE\n" +
        "\n" +
        "2. RESPIRAR CALMAMENTE:\n" +
        "   • Inspire pelo nariz (4 segundos)\n" +
        "   • Prenda (2 segundos)\n" +
        "   • Expire pela boca (6 segundos)\n" +
        "\n" +
        "3. NÃO DIRIGIR\n" +
        "\n" +
        "4. Se tiver medicação de emergência prescrita, tomar agora\n" +
        "\n" +
        "5. PROCURAR EMERGÊNCIA IMEDIATAMENTE\n" +
        "\n" +
        "6. Chamar SAMU 192 se apresentar:\n" +
        "   • Dor no peito\n" +
        "   • Falta de ar severa\n" +
        "   • Dor de cabeça intensa\n" +
        "   • Visão turva\n" +
        "   • Confusão mental\n" +
        "   • Formigamento ou fraqueza em um lado do corpo\n" +
        "\n" +
        "⚠ RISCO: AVC (derrame), infarto, edema pulmonar";

    public static final String HIPOTENSAO_SEVERA =
        "1. DEITAR IMEDIATAMENTE com pernas elevadas\n" +
        "\n" +
        "2. Se possível, beber líquidos (água ou isotônico)\n" +
        "\n" +
        "3. NÃO levantar-se bruscamente\n" +
        "\n" +
        "4. Procurar ajuda se apresentar:\n" +
        "   • Tontura intensa\n" +
        "   • Visão escurecida\n" +
        "   • Sudorese fria\n" +
        "   • Náusea\n" +
        "   • Confusão mental\n" +
        "\n" +
        "5. CHAMAR 192 se:\n" +
        "   • Desmaio\n" +
        "   • Dor no peito\n" +
        "   • Respiração curta\n" +
        "\n" +
        "⚠ RISCO: Choque, queda com trauma, arritmias";

    public static String obterProtocolo(String tipoEmergencia) {
        switch (tipoEmergencia.toUpperCase()) {
            case "HIPOGLICEMIA":
                return HIPOGLICEMIA_SEVERA;
            case "HIPERGLICEMIA":
                return HIPERGLICEMIA_SEVERA;
            case "HIPERTENSAO":
                return CRISE_HIPERTENSIVA;
            case "HIPOTENSAO":
                return HIPOTENSAO_SEVERA;
            default:
                return "Protocolo não disponível. PROCURE ATENDIMENTO MÉDICO IMEDIATAMENTE.";
        }
    }
}
