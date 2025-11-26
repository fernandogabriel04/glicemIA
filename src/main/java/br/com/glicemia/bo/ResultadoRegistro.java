package br.com.glicemia.bo;

import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

public class ResultadoRegistro {

    private final SinalVital sinalVital;
    private final boolean isEmergencia;
    private final boolean liberadoParaIA;
    private final String mensagemAlerta;
    private final String protocoloEmergencia;

    private ResultadoRegistro(Builder builder) {
        this.sinalVital = builder.sinalVital;
        this.isEmergencia = builder.isEmergencia;
        this.liberadoParaIA = builder.liberadoParaIA;
        this.mensagemAlerta = builder.mensagemAlerta;
        this.protocoloEmergencia = builder.protocoloEmergencia;
    }

    public SinalVital getSinalVital() {
        return sinalVital;
    }

    public boolean isEmergencia() {
        return isEmergencia;
    }

    public boolean isLiberadoParaIA() {
        return liberadoParaIA;
    }

    public String getMensagemAlerta() {
        return mensagemAlerta;
    }

    public String getProtocoloEmergencia() {
        return protocoloEmergencia;
    }

    public NivelRisco getNivelRisco() {
        return sinalVital != null ? sinalVital.getNivelRisco() : null;
    }

    public static class Builder {
        private SinalVital sinalVital;
        private boolean isEmergencia = false;
        private boolean liberadoParaIA = false;
        private String mensagemAlerta;
        private String protocoloEmergencia;

        public Builder comSinalVital(SinalVital sinalVital) {
            this.sinalVital = sinalVital;
            return this;
        }

        public Builder emergencia(boolean isEmergencia) {
            this.isEmergencia = isEmergencia;
            return this;
        }

        public Builder liberadoParaIA(boolean liberadoParaIA) {
            this.liberadoParaIA = liberadoParaIA;
            return this;
        }

        public Builder mensagemAlerta(String mensagemAlerta) {
            this.mensagemAlerta = mensagemAlerta;
            return this;
        }

        public Builder protocoloEmergencia(String protocoloEmergencia) {
            this.protocoloEmergencia = protocoloEmergencia;
            return this;
        }

        public ResultadoRegistro build() {
            return new ResultadoRegistro(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Resultado do Registro ===\n");
        sb.append("Sinal: ").append(sinalVital.getDescricao()).append("\n");
        sb.append("Risco: ").append(getNivelRisco()).append("\n");
        sb.append("Emergência: ").append(isEmergencia ? "SIM" : "NÃO").append("\n");
        sb.append("Liberado para IA: ").append(liberadoParaIA ? "SIM" : "NÃO").append("\n");

        if (mensagemAlerta != null) {
            sb.append("\nAlerta: ").append(mensagemAlerta).append("\n");
        }

        if (protocoloEmergencia != null) {
            sb.append("\nProtocolo de Emergência:\n").append(protocoloEmergencia).append("\n");
        }

        return sb.toString();
    }
}
