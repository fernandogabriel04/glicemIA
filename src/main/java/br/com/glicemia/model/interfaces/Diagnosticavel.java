package br.com.glicemia.model.interfaces;

import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;

public interface Diagnosticavel {

    NivelRisco analisarRisco() throws RiscoEmergenciaException;

    String getRecomendacaoImediata();

    boolean isEmergencia();
}
