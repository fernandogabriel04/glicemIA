package br.com.glicemia.model;

public enum TipoSinal {

    GLICEMIA("Glicemia", "mg/dL"),
    PRESSAO("Pressão Arterial", "mmHg"),
    PESO("Peso Corporal", "kg");

    private final String descricao;
    private final String unidadePadrao;

    TipoSinal(String descricao, String unidadePadrao) {
        this.descricao = descricao;
        this.unidadePadrao = unidadePadrao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidadePadrao() {
        return unidadePadrao;
    }

    public static TipoSinal fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de sinal não pode ser nulo ou vazio");
        }

        try {
            return TipoSinal.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de sinal inválido: " + valor +
                ". Valores válidos: GLICEMIA, PRESSAO, PESO");
        }
    }
}
