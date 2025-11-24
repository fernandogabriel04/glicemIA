package br.com.glicemia.model;

public enum NivelRisco {

    NORMAL("Normal", "Valores dentro da faixa esperada", 0),
    ATENCAO("Atenção", "Valores fora da faixa ideal, requer monitoramento", 1),
    ALTO("Alto", "Valores preocupantes, consulte um médico", 2),
    CRITICO("Crítico", "Emergência médica - procure atendimento imediato", 3);

    private final String descricao;
    private final String mensagem;
    private final int gravidade;

    NivelRisco(String descricao, String mensagem, int gravidade) {
        this.descricao = descricao;
        this.mensagem = mensagem;
        this.gravidade = gravidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getGravidade() {
        return gravidade;
    }

    public boolean isCritico() {
        return this == CRITICO;
    }

    public boolean requerAtencaoMedica() {
        return this == ALTO || this == CRITICO;
    }

    public boolean isAceitavel() {
        return this == NORMAL || this == ATENCAO;
    }

    public static NivelRisco fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Nível de risco não pode ser nulo ou vazio");
        }

        try {
            return NivelRisco.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nível de risco inválido: " + valor +
                ". Valores válidos: NORMAL, ATENCAO, ALTO, CRITICO");
        }
    }
}
