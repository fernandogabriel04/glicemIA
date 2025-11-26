package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.NivelRisco;

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
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        NivelRisco risco;

        if (valorGlicemia < 50) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "HIPOGLICEMIA SEVERA detectada: " + valorGlicemia + " mg/dL",
                "CRITICO",
                "1. Ingerir 15g de carboidrato simples\n" +
                "2. Aguardar 15 minutos e medir novamente\n" +
                "3. Se < 70 mg/dL, procure emergência IMEDIATAMENTE"
            );
        }

        if (valorGlicemia >= 300) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "HIPERGLICEMIA SEVERA detectada: " + valorGlicemia + " mg/dL",
                "CRITICO",
                "1. Beber água imediatamente\n" +
                "2. NÃO se exercitar\n" +
                "3. Procurar atendimento médico URGENTE"
            );
        }

        if (emJejum) {
            if (valorGlicemia < 70) {
                risco = NivelRisco.ALTO;
            } else if (valorGlicemia <= 99) {
                risco = NivelRisco.NORMAL;
            } else if (valorGlicemia <= 125) {
                risco = NivelRisco.ATENCAO;
            } else {
                risco = NivelRisco.ALTO;
            }
        } else {
            if (valorGlicemia < 70) {
                risco = NivelRisco.ALTO;
            } else if (valorGlicemia <= 140) {
                risco = NivelRisco.NORMAL;
            } else if (valorGlicemia <= 199) {
                risco = NivelRisco.ATENCAO;
            } else {
                risco = NivelRisco.ALTO;
            }
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
                return "Glicemia dentro dos valores normais. Continue monitorando.";

            case ATENCAO:
                if (emJejum) {
                    return "Glicemia de jejum elevada. Consulte seu médico para avaliação.";
                } else {
                    return "Glicemia pós-prandial elevada. Revise sua alimentação.";
                }

            case ALTO:
                if (valorGlicemia < 70) {
                    return "Hipoglicemia detectada. Ingira carboidratos simples imediatamente.";
                } else {
                    return "Hiperglicemia detectada. Evite doces, beba água e consulte seu médico.";
                }

            case CRITICO:
                return "EMERGÊNCIA MÉDICA. Siga o protocolo de emergência.";

            default:
                return "Status desconhecido";
        }
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
