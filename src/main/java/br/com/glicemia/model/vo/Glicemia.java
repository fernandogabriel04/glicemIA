package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class Glicemia extends SinalVital {

    private double valorGlicemia;
    private boolean emJejum;
    private String tipoInsulina;

    public Glicemia(Long idPaciente, double valorGlicemia, boolean emJejum)
            throws ValorInvalidoException {
        super(idPaciente, "mg/dL");
        this.valorGlicemia = valorGlicemia;
        this.emJejum = emJejum;
        validar();
    }

    @Override
    protected void validar() throws ValorInvalidoException {
        if (valorGlicemia <= 0) {
            throw new ValorInvalidoException(
                "Glicemia deve ser um valor positivo. Valor fornecido: " + valorGlicemia
            );
        }

        if (valorGlicemia > 600) {
            throw new ValorInvalidoException(
                "Valor de glicemia extremamente alto (>600 mg/dL). Verifique a medição."
            );
        }
    }

    public double getValorGlicemia() {
        return valorGlicemia;
    }

    public void setValorGlicemia(double valorGlicemia) throws ValorInvalidoException {
        this.valorGlicemia = valorGlicemia;
        validar();
    }

    public boolean isEmJejum() {
        return emJejum;
    }

    public void setEmJejum(boolean emJejum) {
        this.emJejum = emJejum;
    }

    public String getTipoInsulina() {
        return tipoInsulina;
    }

    public void setTipoInsulina(String tipoInsulina) {
        this.tipoInsulina = tipoInsulina;
    }

    @Override
    public String getDescricao() {
        return String.format("Glicemia: %.1f %s (%s)",
            valorGlicemia,
            getUnidadeMedida(),
            emJejum ? "Jejum" : "Pós-prandial"
        );
    }
}
