package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.NivelRisco;

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
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        NivelRisco risco;

        if (sistolica >= 180 || diastolica >= 120) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "CRISE HIPERTENSIVA detectada: " + sistolica + "/" + diastolica + " mmHg",
                "CRITICO",
                "1. Sentar-se confortavelmente e respirar calmamente\n" +
                "2. NÃO dirigir\n" +
                "3. Procurar emergência IMEDIATAMENTE\n" +
                "4. Risco de AVC ou infarto"
            );
        }

        if (sistolica < 90 || diastolica < 60) {
            risco = NivelRisco.ALTO;
        } else if (sistolica < 120 && diastolica < 80) {
            risco = NivelRisco.NORMAL;
        } else if (sistolica < 140 && diastolica < 90) {
            risco = NivelRisco.ATENCAO;
        } else if (sistolica < 160 && diastolica < 100) {
            risco = NivelRisco.ALTO;
        } else {
            risco = NivelRisco.ALTO;
        }

        setNivelRisco(risco);
        return risco;
    }

    @Override
    public String getRecomendacaoImediata() {
        if (getNivelRisco() == null) {
            return "Análise de risco não realizada";
        }

        switch (getNivelRisco()) {
            case NORMAL:
                return "Pressão arterial dentro dos valores normais.";

            case ATENCAO:
                return "Pré-hipertensão detectada. Adote hábitos saudáveis e monitore regularmente.";

            case ALTO:
                if (sistolica < 90) {
                    return "Hipotensão detectada. Hidrate-se e evite levantar-se bruscamente. Consulte médico.";
                } else {
                    return "Hipertensão detectada. Reduza sal, estresse e consulte seu médico.";
                }

            case CRITICO:
                return "EMERGÊNCIA MÉDICA. Procure atendimento imediato.";

            default:
                return "Status desconhecido";
        }
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
