package br.com.glicemia.dao.impl;

import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Paciente;
import br.com.glicemia.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public Long inserir(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO TB_PACIENTE (nome, cpf, data_nascimento, email, telefone, data_cadastro) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(4, paciente.getEmail());
            stmt.setString(5, paciente.getTelefone());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir paciente, nenhuma linha afetada.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Long idGerado = rs.getLong(1);
                paciente.setIdPaciente(idGerado);
                return idGerado;
            } else {
                throw new SQLException("Falha ao inserir paciente, ID não gerado.");
            }

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    @Override
    public void atualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE TB_PACIENTE SET nome = ?, data_nascimento = ?, email = ?, telefone = ? WHERE id_paciente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setDate(2, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(3, paciente.getEmail());
            stmt.setString(4, paciente.getTelefone());
            stmt.setLong(5, paciente.getIdPaciente());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Paciente não encontrado para atualização: " +
                                     paciente.getIdPaciente());
            }
        }
    }

    @Override
    public void deletar(Long idPaciente) throws SQLException {
        String sql = "DELETE FROM TB_PACIENTE WHERE id_paciente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Paciente não encontrado para exclusão: " + idPaciente);
            }
        }
    }

    @Override
    public Paciente buscarPorId(Long idPaciente) throws SQLException {
        String sql = "SELECT * FROM TB_PACIENTE WHERE id_paciente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaciente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPacienteDoResultSet(rs);
                }
                return null;
            }
        }
    }

    @Override
    public Paciente buscarPorCPF(String cpf) throws SQLException {
        String sql = "SELECT * FROM TB_PACIENTE WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPacienteDoResultSet(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<Paciente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM TB_PACIENTE ORDER BY nome";
        List<Paciente> pacientes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pacientes.add(extrairPacienteDoResultSet(rs));
            }
        }

        return pacientes;
    }

    private Paciente extrairPacienteDoResultSet(ResultSet rs) throws SQLException {
        try {
            Long id = rs.getLong("id_paciente");
            String nome = rs.getString("nome");
            String cpf = rs.getString("cpf");
            LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
            String email = rs.getString("email");
            String telefone = rs.getString("telefone");

            Paciente paciente = new Paciente(nome, cpf, dataNascimento);
            paciente.setIdPaciente(id);
            paciente.setEmail(email);
            paciente.setTelefone(telefone);

            return paciente;

        } catch (ValorInvalidoException e) {
            throw new SQLException("Dados inválidos no banco de dados: " + e.getMessage(), e);
        }
    }
}
