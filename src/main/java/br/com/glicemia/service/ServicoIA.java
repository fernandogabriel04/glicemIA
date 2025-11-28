package br.com.glicemia.service;

import br.com.glicemia.model.vo.SinalVital;
import java.util.List;


// Interface para serviços de Inteligência Artificial. Permite trocar a implementação (OpenAI, Gemini, local) sem afetar o código.

public interface ServicoIA {

    /**
     * Solicita uma recomendação de saúde baseada no histórico e pergunta do usuário.
     *
     * @param historicoRecente Lista dos últimos sinais vitais
     * @param pergunta Pergunta específica do usuário
     * @return Resposta da IA
     * @throws Exception se houver erro na comunicação
     */
    String solicitarRecomendacao(List<SinalVital> historicoRecente, String pergunta)
            throws Exception;

    /**
     * Verifica se o serviço de IA está disponível.
     *
     * @return true se a IA está acessível
     */
    boolean isDisponivel();

    /**
     * Retorna o nome do provedor de IA.
     *
     * @return Nome do provedor (ex: "OpenAI GPT-4", "Gemini Pro", "Local")
     */
    String getNomeProvedor();
}
