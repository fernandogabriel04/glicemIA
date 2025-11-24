package br.com.glicemia.model;

public enum SimNao {

    SIM('S', true),
    NAO('N', false);

    private final char codigo;
    private final boolean valorBooleano;

    SimNao(char codigo, boolean valorBooleano) {
        this.codigo = codigo;
        this.valorBooleano = valorBooleano;
    }

    public char getCodigo() {
        return codigo;
    }

    public boolean getValorBooleano() {
        return valorBooleano;
    }

    public String getDescricao() {
        return this == SIM ? "Sim" : "Não";
    }

    public static SimNao fromCodigo(char codigo) {
        char codigoUpper = Character.toUpperCase(codigo);

        if (codigoUpper == 'S') {
            return SIM;
        } else if (codigoUpper == 'N') {
            return NAO;
        } else {
            throw new IllegalArgumentException("Código inválido: " + codigo +
                ". Valores válidos: 'S' ou 'N'");
        }
    }

    public static SimNao fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor não pode ser nulo ou vazio");
        }

        String valorUpper = valor.trim().toUpperCase();

        if (valorUpper.equals("S") || valorUpper.equals("SIM")) {
            return SIM;
        } else if (valorUpper.equals("N") || valorUpper.equals("NAO") || valorUpper.equals("NÃO")) {
            return NAO;
        } else {
            throw new IllegalArgumentException("Valor inválido: " + valor +
                ". Valores válidos: 'S', 'N', 'SIM', 'NAO'");
        }
    }

    public static SimNao fromBoolean(boolean valor) {
        return valor ? SIM : NAO;
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
