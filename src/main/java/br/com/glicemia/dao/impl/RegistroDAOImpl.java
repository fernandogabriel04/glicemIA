package br.com.glicemia.dao.impl;

import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAOImpl implements RegistroDAO {

    @Override
    public Long inserir(SinalVital sinalVital) throws SQLException {
        String sql = "INSERT INTO TB_REGISTRO (id_paciente, tipo_sinal, " +
                     "data_hora, valor_principal, valor_secundario, unidade_medida, " +
                     "nivel_risco, observacoes, em_jejum, tipo_insulina, altura, imc) " +
                     "VALUES (?, ?::tipo_sinal_enum, ?, ?, ?, ?, ?::nivel_risco_enum, ?, ?::sim_nao_enum, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, sinalVital.getIdPaciente());
            stmt.setString(2, obterTipoSinal(sinalVital));
            stmt.setTimestamp(3, Timestamp.valueOf(sinalVital.getDataHora()));

            preencherCamposEspecificos(stmt, sinalVital);

            stmt.setString(6, sinalVital.getUnidadeMedida());
            stmt.setString(7, sinalVital.getNivelRisco() != null ?
                           sinalVital.getNivelRisco().name() : "NORMAL");
            stmt.setString(8, sinalVital.getObservacoes());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir registro.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Long idGerado = rs.getLong(1);
                sinalVital.setIdRegistro(idGerado);
                return idGerado;
            } else {
                throw new SQLException("Falha ao inserir registro, ID não gerado.");
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public SinalVital buscarPorId(Long idRegistro) throws SQLException {
        String sql = "SELECT * FROM TB_REGISTRO WHERE id_registro = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idRegistro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairSinalVitalDoResultSet(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<SinalVital> listarPorPaciente(Long idPaciente) throws SQLException {
        String sql = "SELECT * FROM TB_REGISTRO WHERE id_paciente = ? ORDER BY data_hora DESC";
        return executarConsultaLista(sql, idPaciente);
    }

    @Override
    public List<SinalVital> listarPorPeriodo(Long idPaciente, LocalDateTime inicio,
                                             LocalDateTime fim) throws SQLException {
        String sql = "SELECT * FROM TB_REGISTRO WHERE id_paciente = ? " +
                     "AND data_hora BETWEEN ? AND ? ORDER BY data_hora DESC";

        List<SinalVital> registros = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(extrairSinalVitalDoResultSet(rs));
                }
            }
        }

        return registros;
    }

    @Override
    public List<SinalVital> buscarUltimosRegistros(Long idPaciente, int limite)
            throws SQLException {
        String sql = "SELECT * FROM TB_REGISTRO WHERE id_paciente = ? " +
                     "ORDER BY data_hora DESC LIMIT ?";

        List<SinalVital> registros = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);
            stmt.setInt(2, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(extrairSinalVitalDoResultSet(rs));
                }
            }
        }

        return registros;
    }

    @Override
    public int contarRegistrosCriticos(Long idPaciente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_REGISTRO WHERE id_paciente = ? " +
                     "AND nivel_risco = 'CRITICO'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    private String obterTipoSinal(SinalVital sinal) {
        if (sinal instanceof Glicemia) return "GLICEMIA";
        if (sinal instanceof PressaoArterial) return "PRESSAO";
        if (sinal instanceof PesoCorporal) return "PESO";
        return "DESCONHECIDO";
    }

    private void preencherCamposEspecificos(PreparedStatement stmt, SinalVital sinal)
            throws SQLException {

        if (sinal instanceof Glicemia) {
            Glicemia glicemia = (Glicemia) sinal;
            stmt.setDouble(4, glicemia.getValorGlicemia());
            stmt.setNull(5, Types.NUMERIC);
            stmt.setString(9, glicemia.isEmJejum() ? "S" : "N");
            stmt.setString(10, glicemia.getTipoInsulina());
            stmt.setNull(11, Types.NUMERIC);
            stmt.setNull(12, Types.NUMERIC);

        } else if (sinal instanceof PressaoArterial) {
            PressaoArterial pressao = (PressaoArterial) sinal;
            stmt.setInt(4, pressao.getSistolica());
            stmt.setInt(5, pressao.getDiastolica());
            stmt.setNull(9, Types.CHAR);
            stmt.setNull(10, Types.VARCHAR);
            stmt.setNull(11, Types.NUMERIC);
            stmt.setNull(12, Types.NUMERIC);

        } else if (sinal instanceof PesoCorporal) {
            PesoCorporal peso = (PesoCorporal) sinal;
            stmt.setDouble(4, peso.getPeso());
            stmt.setNull(5, Types.NUMERIC);
            stmt.setNull(9, Types.CHAR);
            stmt.setNull(10, Types.VARCHAR);
            stmt.setDouble(11, peso.getAltura());
            if (peso.getImc() != null) {
                stmt.setDouble(12, peso.getImc());
            } else {
                stmt.setNull(12, Types.NUMERIC);
            }
        }
    }

    private SinalVital extrairSinalVitalDoResultSet(ResultSet rs) throws SQLException {
        try {
            String tipoSinal = rs.getString("tipo_sinal");
            Long idPaciente = rs.getLong("id_paciente");

            SinalVital sinal = null;

            switch (tipoSinal) {
                case "GLICEMIA":
                    double valorGlicemia = rs.getDouble("valor_principal");
                    boolean emJejum = "S".equals(rs.getString("em_jejum"));
                    sinal = new Glicemia(idPaciente, valorGlicemia, emJejum);
                    ((Glicemia) sinal).setTipoInsulina(rs.getString("tipo_insulina"));
                    break;

                case "PRESSAO":
                    int sistolica = rs.getInt("valor_principal");
                    int diastolica = rs.getInt("valor_secundario");
                    sinal = new PressaoArterial(idPaciente, sistolica, diastolica);
                    break;

                case "PESO":
                    double peso = rs.getDouble("valor_principal");
                    double altura = rs.getDouble("altura");
                    sinal = new PesoCorporal(idPaciente, peso, altura);
                    break;
            }

            if (sinal != null) {
                sinal.setIdRegistro(rs.getLong("id_registro"));
                sinal.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                sinal.setObservacoes(rs.getString("observacoes"));
            }

            return sinal;

        } catch (ValorInvalidoException e) {
            throw new SQLException("Dados inválidos no banco: " + e.getMessage(), e);
        }
    }

    private List<SinalVital> executarConsultaLista(String sql, Long idPaciente)
            throws SQLException {
        List<SinalVital> registros = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(extrairSinalVitalDoResultSet(rs));
                }
            }
        }

        return registros;
    }
}
