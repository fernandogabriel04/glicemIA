package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.interfaces.Diagnosticavel;
import br.com.glicemia.model.NivelRisco;
import java.time.LocalDateTime;

public abstract class SinalVital implements Diagnosticavel {

    private Long idRegistro;
    private Long idPaciente;
    private LocalDateTime dataHora;
    private String unidadeMedida;
    private String observacoes;
    private NivelRisco nivelRisco;

    public SinalVital(Long idPaciente, String unidadeMedida) {
        this.idPaciente = idPaciente;
        this.dataHora = LocalDateTime.now();
        this.unidadeMedida = unidadeMedida;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    protected void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public void setNivelRiscoFromDB(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    protected abstract void validar() throws ValorInvalidoException;

    public abstract String getDescricao();

    @Override
    public boolean isEmergencia() {
        return nivelRisco != null && nivelRisco.isCritico();
    }
}
