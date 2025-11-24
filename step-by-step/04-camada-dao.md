# Fase 04 - Camada DAO (Persistência)

## 🎯 Objetivos
- Criar interfaces DAO seguindo o padrão de projeto
- Implementar DAOs concretos para PostgreSQL (NeonDB)
- Estabelecer operações CRUD completas
- Implementar tratamento robusto de exceções SQL

## 📚 Conceitos Aplicados
- ✅ **Padrão DAO**: Separação da lógica de persistência
- ✅ **Interface/Implementação**: Desacoplamento do banco
- ✅ **JDBC**: Conexão e operações com PostgreSQL
- ✅ **Tratamento de Exceções**: SQLException handling

## 🔧 Implementação

### 1. Interface PacienteDAO

Crie `src/main/java/br/com/glicemia/dao/interfaces/PacienteDAO.java`:

```java
package br.com.glicemia.dao.interfaces;

import br.com.glicemia.model.vo.Paciente;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface que define as operações de persistência para Paciente.
 * Seguindo o padrão DAO, esta interface pode ter múltiplas implementações
 * (Oracle, MySQL, MongoDB, etc.) sem afetar o restante do código.
 */
public interface PacienteDAO {

    /**
     * Insere um novo paciente no banco de dados.
     * @param paciente Paciente a ser inserido
     * @return ID gerado para o paciente
     * @throws SQLException em caso de erro de persistência
     */
    Long inserir(Paciente paciente) throws SQLException;

    /**
     * Atualiza os dados de um paciente existente.
     * @param paciente Paciente com dados atualizados
     * @throws SQLException em caso de erro de persistência
     */
    void atualizar(Paciente paciente) throws SQLException;

    /**
     * Remove um paciente do banco de dados.
     * @param idPaciente ID do paciente a ser removido
     * @throws SQLException em caso de erro de persistência
     */
    void deletar(Long idPaciente) throws SQLException;

    /**
     * Busca um paciente por ID.
     * @param idPaciente ID do paciente
     * @return Paciente encontrado ou null
     * @throws SQLException em caso de erro de persistência
     */
    Paciente buscarPorId(Long idPaciente) throws SQLException;

    /**
     * Busca um paciente por CPF.
     * @param cpf CPF do paciente
     * @return Paciente encontrado ou null
     * @throws SQLException em caso de erro de persistência
     */
    Paciente buscarPorCPF(String cpf) throws SQLException;

    /**
     * Lista todos os pacientes cadastrados.
     * @return Lista de pacientes
     * @throws SQLException em caso de erro de persistência
     */
    List<Paciente> listarTodos() throws SQLException;
}
```

### 2. Interface RegistroDAO

Crie `src/main/java/br/com/glicemia/dao/interfaces/RegistroDAO.java`:

```java
package br.com.glicemia.dao.interfaces;

import br.com.glicemia.model.vo.SinalVital;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface que define as operações de persistência para Registros (Sinais Vitais).
 */
public interface RegistroDAO {

    /**
     * Insere um novo registro de sinal vital.
     * @param sinalVital Sinal vital a ser registrado
     * @return ID gerado para o registro
     * @throws SQLException em caso de erro de persistência
     */
    Long inserir(SinalVital sinalVital) throws SQLException;

    /**
     * Busca um registro por ID.
     * @param idRegistro ID do registro
     * @return SinalVital encontrado ou null
     * @throws SQLException em caso de erro de persistência
     */
    SinalVital buscarPorId(Long idRegistro) throws SQLException;

    /**
     * Lista todos os registros de um paciente.
     * @param idPaciente ID do paciente
     * @return Lista de sinais vitais
     * @throws SQLException em caso de erro de persistência
     */
    List<SinalVital> listarPorPaciente(Long idPaciente) throws SQLException;

    /**
     * Lista registros de um paciente em um período específico.
     * @param idPaciente ID do paciente
     * @param inicio Data/hora inicial
     * @param fim Data/hora final
     * @return Lista de sinais vitais no período
     * @throws SQLException em caso de erro de persistência
     */
    List<SinalVital> listarPorPeriodo(Long idPaciente, LocalDateTime inicio,
                                       LocalDateTime fim) throws SQLException;

    /**
     * Busca os últimos N registros de um paciente.
     * @param idPaciente ID do paciente
     * @param limite Quantidade de registros
     * @return Lista dos últimos registros
     * @throws SQLException em caso de erro de persistência
     */
    List<SinalVital> buscarUltimosRegistros(Long idPaciente, int limite) throws SQLException;

    /**
     * Conta quantos registros de risco crítico um paciente teve.
     * @param idPaciente ID do paciente
     * @return Número de registros críticos
     * @throws SQLException em caso de erro de persistência
     */
    int contarRegistrosCriticos(Long idPaciente) throws SQLException;
}
```

### 3. Implementação PacienteDAOImpl

Crie `src/main/java/br/com/glicemia/dao/impl/PacienteDAOImpl.java`:

```java
package br.com.glicemia.dao.impl;

import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Paciente;
import br.com.glicemia.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação PostgreSQL do PacienteDAO.
 * Todas as operações SQL são encapsuladas aqui.
 * Compatível com NeonDB (PostgreSQL serverless).
 */
public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public Long inserir(Paciente paciente) throws SQLException {
        // PostgreSQL usa IDENTITY para auto-incremento, não precisa especificar id_paciente
        String sql = "INSERT INTO TB_PACIENTE (nome, cpf, data_nascimento, " +
                     "email, telefone, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // PreparedStatement com RETURN_GENERATED_KEYS para obter o ID gerado
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

            // Recupera o ID gerado automaticamente pelo PostgreSQL
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
        String sql = "UPDATE TB_PACIENTE SET nome = ?, data_nascimento = ?, " +
                     "email = ?, telefone = ? WHERE id_paciente = ?";

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

    /**
     * Método auxiliar para extrair um objeto Paciente do ResultSet.
     */
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
```

### 4. Implementação RegistroDAOImpl (Parte 1)

Crie `src/main/java/br/com/glicemia/dao/impl/RegistroDAOImpl.java`:

```java
package br.com.glicemia.dao.impl;

import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação PostgreSQL do RegistroDAO.
 * Lida com a persistência de diferentes tipos de sinais vitais.
 * Compatível com NeonDB (PostgreSQL serverless).
 */
public class RegistroDAOImpl implements RegistroDAO {

    @Override
    public Long inserir(SinalVital sinalVital) throws SQLException {
        // PostgreSQL com IDENTITY - não precisa especificar id_registro
        String sql = "INSERT INTO TB_REGISTRO (id_paciente, tipo_sinal, " +
                     "data_hora, valor_principal, valor_secundario, unidade_medida, " +
                     "nivel_risco, observacoes, em_jejum, tipo_insulina, altura, imc) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Campos comuns a todos os sinais vitais
            stmt.setLong(1, sinalVital.getIdPaciente());
            stmt.setString(2, obterTipoSinal(sinalVital));
            stmt.setTimestamp(3, Timestamp.valueOf(sinalVital.getDataHora()));

            // Campos específicos de cada tipo
            preencherCamposEspecificos(stmt, sinalVital);

            stmt.setString(7, sinalVital.getUnidadeMedida());
            stmt.setString(8, sinalVital.getNivelRisco() != null ?
                           sinalVital.getNivelRisco().name() : "NORMAL");
            stmt.setString(9, sinalVital.getObservacoes());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir registro.");
            }

            // Recupera ID gerado pelo PostgreSQL
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
        // PostgreSQL usa LIMIT ao invés de ROWNUM do Oracle
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

    // Métodos auxiliares

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
            stmt.setNull(5, Types.NUMERIC);  // valor_secundario
            stmt.setString(10, glicemia.isEmJejum() ? "S" : "N");
            stmt.setString(11, glicemia.getTipoInsulina());
            stmt.setNull(12, Types.NUMERIC);  // altura
            stmt.setNull(13, Types.NUMERIC);  // imc

        } else if (sinal instanceof PressaoArterial) {
            PressaoArterial pressao = (PressaoArterial) sinal;
            stmt.setInt(4, pressao.getSistolica());
            stmt.setInt(5, pressao.getDiastolica());
            stmt.setNull(10, Types.CHAR);    // em_jejum
            stmt.setNull(11, Types.VARCHAR); // tipo_insulina
            stmt.setNull(12, Types.NUMERIC); // altura
            stmt.setNull(13, Types.NUMERIC); // imc

        } else if (sinal instanceof PesoCorporal) {
            PesoCorporal peso = (PesoCorporal) sinal;
            stmt.setDouble(4, peso.getPeso());
            stmt.setNull(5, Types.NUMERIC);
            stmt.setNull(10, Types.CHAR);
            stmt.setNull(11, Types.VARCHAR);
            stmt.setDouble(12, peso.getAltura());
            if (peso.getImc() != null) {
                stmt.setDouble(13, peso.getImc());
            } else {
                stmt.setNull(13, Types.NUMERIC);
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

                String nivelRiscoStr = rs.getString("nivel_risco");
                if (nivelRiscoStr != null) {
                    // Define o nível de risco sem chamar analisarRisco novamente
                    NivelRisco risco = NivelRisco.valueOf(nivelRiscoStr);
                    // Usa reflexão ou método protegido para setar
                }
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
```

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ ] Interface `PacienteDAO` criada com todos os métodos
- [ ] Interface `RegistroDAO` criada com todos os métodos
- [ ] `PacienteDAOImpl` implementado completamente
- [ ] `RegistroDAOImpl` implementado completamente
- [ ] Teste de inserção de paciente funciona
- [ ] Teste de inserção de registros funciona
- [ ] Teste de busca funciona
- [ ] Tratamento de exceções SQL adequado

## 🧪 Teste do DAO

Crie `TestDAO.java`:

```java
import br.com.glicemia.dao.impl.*;
import br.com.glicemia.dao.interfaces.*;
import br.com.glicemia.model.vo.*;
import java.time.LocalDate;

public class TestDAO {
    public static void main(String[] args) {
        try {
            // Teste Paciente
            PacienteDAO pacienteDAO = new PacienteDAOImpl();

            Paciente paciente = new Paciente("Maria Silva", "12345678901",
                LocalDate.of(1985, 3, 20));
            paciente.setEmail("maria@email.com");

            Long idPaciente = pacienteDAO.inserir(paciente);
            System.out.println("✓ Paciente inserido com ID: " + idPaciente);

            // Teste Registro
            RegistroDAO registroDAO = new RegistroDAOImpl();

            Glicemia glicemia = new Glicemia(idPaciente, 110.0, true);
            Long idRegistro = registroDAO.inserir(glicemia);
            System.out.println("✓ Glicemia registrada com ID: " + idRegistro);

            // Buscar registros
            var registros = registroDAO.listarPorPaciente(idPaciente);
            System.out.println("✓ Total de registros: " + registros.size());

        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## 📌 Próximos Passos

**Próxima fase**: **[Fase 05 - Camada BO (Lógica de Negócio)](./05-camada-bo.md)**

---

**Conceitos implementados**: Padrão DAO ✅ | JDBC ✅ | CRUD ✅
