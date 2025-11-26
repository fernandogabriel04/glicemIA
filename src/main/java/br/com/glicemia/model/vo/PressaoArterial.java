package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class PressaoArterial extends SinalVital {

    private int sistolica;
    private int diastolica;

    public PressaoArterial(Long idPaciente, int sistolica, int diastolica)
            throws ValorInvalidoException {
        super(idPaciente, "mmHg");
        this.sistolica = sistolica;
        this.diastolica = diastolica;
        validar();
    }

    @Override
    protected void validar() throws ValorInvalidoException {
        if (sistolica <= 0 || diastolica <= 0) {
            throw new ValorInvalidoException(
                "Pressão arterial deve ter valores positivos. Fornecido: " +
                sistolica + "/" + diastolica
            );
        }

        if (sistolica <= diastolica) {
            throw new ValorInvalidoException(
                "Pressão sistólica deve ser maior que diastólica. Fornecido: " +
                sistolica + "/" + diastolica
            );
        }

        if (sistolica > 300 || diastolica > 200) {
            throw new ValorInvalidoException(
                "Valores de pressão arterial extremamente altos. Verifique a medição."
            );
        }
    }

    public int getSistolica() {
        return sistolica;
    }

    public void setSistolica(int sistolica) throws ValorInvalidoException {
        this.sistolica = sistolica;
        validar();
    }

    public int getDiastolica() {
        return diastolica;
    }

    public void setDiastolica(int diastolica) throws ValorInvalidoException {
        this.diastolica = diastolica;
        validar();
    }

    @Override
    public String getDescricao() {
        return String.format("Pressão Arterial: %d/%d %s",
            sistolica,
            diastolica,
            getUnidadeMedida()
        );
    }
}
