package br.com.glicemia.service;

import br.com.glicemia.model.vo.SinalVital;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

 //Constroi uma sessão de chat com a IA. Mantém histórico de perguntas e respostas durante uma sessão de chat.
public class ConversationSession {

    public static final int LIMITE_TURNOS = 20; // Limite público de turnos por sessão

    private final Long idPaciente;
    private final LocalDateTime dataInicio;
    private final List<ConversationTurn> turnos;
    private List<SinalVital> historicoPaciente;
    private int limiteMaximoTurnos = LIMITE_TURNOS; // Evita sessões infinitas

    public ConversationSession(Long idPaciente, List<SinalVital> historico) {
        this.idPaciente = idPaciente;
        this.dataInicio = LocalDateTime.now();
        this.turnos = new ArrayList<>();
        this.historicoPaciente = historico != null ? historico : new ArrayList<>();
    }

    public void adicionarTurno(String pergunta, String resposta) {
        turnos.add(new ConversationTurn(pergunta, resposta));
    }

    public boolean atingiuLimite() {
        return turnos.size() >= limiteMaximoTurnos;
    }

    public List<ConversationTurn> getTurnos() {
        return new ArrayList<>(turnos);
    }

    public int getQuantidadeTurnos() {
        return turnos.size();
    }

    public String getResumoConversa() {
        if (turnos.isEmpty()) {
            return "Nenhuma conversa anterior.";
        }

        StringBuilder resumo = new StringBuilder();
        resumo.append("=== HISTÓRICO DA CONVERSA ===\n");

        // Limita a últimas 5 interações para não sobrecarregar prompt
        int inicio = Math.max(0, turnos.size() - 5);
        for (int i = inicio; i < turnos.size(); i++) {
            ConversationTurn turno = turnos.get(i);
            resumo.append(String.format("\nPaciente: %s\n", turno.getPergunta()));
            resumo.append(String.format("IA: %s\n", turno.getResposta()));
        }

        return resumo.toString();
    }

    // Getters
    public Long getIdPaciente() { return idPaciente; }
    public List<SinalVital> getHistoricoPaciente() { return historicoPaciente; }
    public LocalDateTime getDataInicio() { return dataInicio; }

    // Classe para representar um turno de conversa.
    public static class ConversationTurn {
        private final String pergunta;
        private final String resposta;
        private final LocalDateTime timestamp;

        public ConversationTurn(String pergunta, String resposta) {
            this.pergunta = pergunta;
            this.resposta = resposta;
            this.timestamp = LocalDateTime.now();
        }

        public String getPergunta() { return pergunta; }
        public String getResposta() { return resposta; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
