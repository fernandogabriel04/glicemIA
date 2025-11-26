package br.com.glicemia.model.exceptions;

public class RiscoEmergenciaException extends Exception {

    private final String nivelRisco;
    private final String protocolo;

    public RiscoEmergenciaException(String mensagem, String nivelRisco, String protocolo) {
        super(mensagem);
        this.nivelRisco = nivelRisco;
        this.protocolo = protocolo;
    }

    public String getNivelRisco() {
        return nivelRisco;
    }

    public String getProtocolo() {
        return protocolo;
    }
}
