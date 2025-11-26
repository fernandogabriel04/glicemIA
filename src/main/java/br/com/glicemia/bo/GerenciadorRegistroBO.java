package br.com.glicemia.bo;

import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class GerenciadorRegistroBO {

    private final RegistroDAO registroDAO;

    public GerenciadorRegistroBO() {
        this.registroDAO = new RegistroDAOImpl();
    }

    public GerenciadorRegistroBO(RegistroDAO registroDAO) {
        this.registroDAO = registroDAO;
    }

    public boolean registrarSinalVital(SinalVital sinalVital)
            throws RiscoEmergenciaException, SQLException {

        NivelRisco risco = sinalVital.analisarRisco();

        registroDAO.inserir(sinalVital);

        return risco != NivelRisco.CRITICO;
    }

    public List<SinalVital> buscarHistoricoRecente(Long idPaciente, int limite)
            throws SQLException {
        return registroDAO.buscarUltimosRegistros(idPaciente, limite);
    }

    public List<SinalVital> buscarRegistrosPorPeriodo(Long idPaciente,
                                                       LocalDateTime inicio,
                                                       LocalDateTime fim)
            throws SQLException {
        return registroDAO.listarPorPeriodo(idPaciente, inicio, fim);
    }

    public List<SinalVital> listarTodosRegistros(Long idPaciente) throws SQLException {
        return registroDAO.listarPorPaciente(idPaciente);
    }

    public int contarEpisodiosCriticos(Long idPaciente) throws SQLException {
        return registroDAO.contarRegistrosCriticos(idPaciente);
    }

    public SinalVital buscarRegistroPorId(Long idRegistro) throws SQLException {
        return registroDAO.buscarPorId(idRegistro);
    }

    public String gerarResumoEstatistico(Long idPaciente) throws SQLException {
        List<SinalVital> registros = registroDAO.listarPorPaciente(idPaciente);

        if (registros.isEmpty()) {
            return "Nenhum registro encontrado para este paciente.";
        }

        int totalRegistros = registros.size();
        int countNormal = 0;
        int countAtencao = 0;
        int countAlto = 0;
        int countCritico = 0;

        for (SinalVital sinal : registros) {
            if (sinal.getNivelRisco() != null) {
                switch (sinal.getNivelRisco()) {
                    case NORMAL:
                        countNormal++;
                        break;
                    case ATENCAO:
                        countAtencao++;
                        break;
                    case ALTO:
                        countAlto++;
                        break;
                    case CRITICO:
                        countCritico++;
                        break;
                }
            }
        }

        StringBuilder resumo = new StringBuilder();
        resumo.append("=== Resumo Estatístico ===\n");
        resumo.append("Total de Registros: ").append(totalRegistros).append("\n");
        resumo.append("Normal: ").append(countNormal).append(" (")
              .append(String.format("%.1f%%", (countNormal * 100.0 / totalRegistros)))
              .append(")\n");
        resumo.append("Atenção: ").append(countAtencao).append(" (")
              .append(String.format("%.1f%%", (countAtencao * 100.0 / totalRegistros)))
              .append(")\n");
        resumo.append("Alto: ").append(countAlto).append(" (")
              .append(String.format("%.1f%%", (countAlto * 100.0 / totalRegistros)))
              .append(")\n");
        resumo.append("Crítico: ").append(countCritico).append(" (")
              .append(String.format("%.1f%%", (countCritico * 100.0 / totalRegistros)))
              .append(")\n");

        return resumo.toString();
    }
}
