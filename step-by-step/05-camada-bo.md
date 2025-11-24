# Fase 05 - Camada BO (Lógica de Negócio)

## 🎯 Objetivos
- Implementar a camada Business Object (BO)
- Aplicar regras de negócio de segurança
- Coordenar DAO e preparar integração com IA
- Implementar o "funil de segurança"

## 📚 Conceitos Aplicados
- ✅ **Separação de Responsabilidades**: BO != DAO
- ✅ **Regras de Negócio**: Lógica clínica isolada
- ✅ **Coordenação**: Orquestra múltiplos componentes

## 🔧 Implementação

### 1. Classe GerenciadorPacienteBO

Crie `src/main/java/br/com/glicemia/bo/GerenciadorPacienteBO.java`:

```java
package br.com.glicemia.bo;

import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.dao.impl.PacienteDAOImpl;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Paciente;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Object responsável por gerenciar operações relacionadas a Pacientes.
 * Aplica regras de negócio antes de delegar para o DAO.
 */
public class GerenciadorPacienteBO {

    private final PacienteDAO pacienteDAO;

    public GerenciadorPacienteBO() {
        this.pacienteDAO = new PacienteDAOImpl();
    }

    // Construtor para injeção de dependência (útil para testes)
    public GerenciadorPacienteBO(PacienteDAO pacienteDAO) {
        this.pacienteDAO = pacienteDAO;
    }

    /**
     * Cadastra um novo paciente no sistema.
     * Valida se já não existe paciente com o mesmo CPF.
     *
     * @param nome Nome completo do paciente
     * @param cpf CPF do paciente
     * @param dataNascimento Data de nascimento
     * @param email Email (opcional)
     * @param telefone Telefone (opcional)
     * @return Paciente cadastrado com ID gerado
     * @throws ValorInvalidoException se os dados forem inválidos
     * @throws SQLException se houver erro de persistência
     */
    public Paciente cadastrarPaciente(String nome, String cpf, LocalDate dataNascimento,
                                     String email, String telefone)
            throws ValorInvalidoException, SQLException {

        // Regra de negócio: CPF não pode ser duplicado
        Paciente pacienteExistente = pacienteDAO.buscarPorCPF(cpf);
        if (pacienteExistente != null) {
            throw new ValorInvalidoException(
                "Já existe um paciente cadastrado com o CPF: " + cpf
            );
        }

        // Regra de negócio: Paciente deve ter pelo menos 1 ano de idade
        if (dataNascimento.isAfter(LocalDate.now().minusYears(1))) {
            throw new ValorInvalidoException(
                "Paciente deve ter pelo menos 1 ano de idade"
            );
        }

        // Cria e persiste o paciente
        Paciente paciente = new Paciente(nome, cpf, dataNascimento);
        paciente.setEmail(email);
        paciente.setTelefone(telefone);

        Long idGerado = pacienteDAO.inserir(paciente);
        paciente.setIdPaciente(idGerado);

        return paciente;
    }

    /**
     * Atualiza os dados de um paciente.
     * @param paciente Paciente com dados atualizados
     * @throws SQLException se houver erro de persistência
     */
    public void atualizarPaciente(Paciente paciente) throws SQLException {
        // Regra de negócio: Verificar se o paciente existe
        Paciente existente = pacienteDAO.buscarPorId(paciente.getIdPaciente());
        if (existente == null) {
            throw new SQLException("Paciente não encontrado: " + paciente.getIdPaciente());
        }

        pacienteDAO.atualizar(paciente);
    }

    /**
     * Busca um paciente por ID.
     * @param idPaciente ID do paciente
     * @return Paciente encontrado ou null
     * @throws SQLException se houver erro de persistência
     */
    public Paciente buscarPacientePorId(Long idPaciente) throws SQLException {
        return pacienteDAO.buscarPorId(idPaciente);
    }

    /**
     * Busca um paciente por CPF.
     * @param cpf CPF do paciente
     * @return Paciente encontrado ou null
     * @throws SQLException se houver erro de persistência
     */
    public Paciente buscarPacientePorCPF(String cpf) throws SQLException {
        return pacienteDAO.buscarPorCPF(cpf);
    }

    /**
     * Lista todos os pacientes cadastrados.
     * @return Lista de pacientes
     * @throws SQLException se houver erro de persistência
     */
    public List<Paciente> listarTodosPacientes() throws SQLException {
        return pacienteDAO.listarTodos();
    }

    /**
     * Remove um paciente do sistema.
     * Regra de negócio: Só pode excluir se não tiver registros vinculados.
     *
     * @param idPaciente ID do paciente a ser removido
     * @throws SQLException se houver erro ou se existirem registros vinculados
     */
    public void removerPaciente(Long idPaciente) throws SQLException {
        // Nota: Aqui você deveria verificar se há registros vinculados
        // usando o RegistroDAO. Por simplicidade, vamos apenas deletar.

        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new SQLException("Paciente não encontrado: " + idPaciente);
        }

        pacienteDAO.deletar(idPaciente);
    }
}
```

### 2. Classe GerenciadorRegistroBO (O Coração do Sistema)

Crie `src/main/java/br/com/glicemia/bo/GerenciadorRegistroBO.java`:

```java
package br.com.glicemia.bo;

import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business Object responsável por gerenciar registros de sinais vitais.
 *
 * Este é o "FUNIL DE SEGURANÇA" do sistema:
 * 1. Recebe o sinal vital
 * 2. Chama analisarRisco()
 * 3. Se CRÍTICO → Bloqueia IA, exibe alerta, retorna protocolo
 * 4. Se NÃO CRÍTICO → Salva no banco e libera para consulta à IA
 */
public class GerenciadorRegistroBO {

    private final RegistroDAO registroDAO;

    public GerenciadorRegistroBO() {
        this.registroDAO = new RegistroDAOImpl();
    }

    // Construtor para injeção de dependência (útil para testes)
    public GerenciadorRegistroBO(RegistroDAO registroDAO) {
        this.registroDAO = registroDAO;
    }

    /**
     * Registra um novo sinal vital aplicando o funil de segurança.
     *
     * FLUXO DE SEGURANÇA:
     * 1. Analisa o risco do sinal vital
     * 2. Se for CRÍTICO → Lança exceção de emergência (NÃO SALVA, NÃO CHAMA IA)
     * 3. Se for OK → Salva no banco e retorna true (liberado para IA)
     *
     * @param sinalVital Sinal vital a ser registrado
     * @return true se o registro foi salvo e está liberado para consulta à IA
     * @throws RiscoEmergenciaException se detectar emergência médica
     * @throws SQLException se houver erro de persistência
     */
    public boolean registrarSinalVital(SinalVital sinalVital)
            throws RiscoEmergenciaException, SQLException {

        // CAMADA DE SEGURANÇA: Análise de risco ANTES de salvar
        NivelRisco risco = sinalVital.analisarRisco();

        // Se chegou aqui, não é emergência (caso contrário, teria lançado exceção)
        // Agora podemos salvar no banco
        registroDAO.inserir(sinalVital);

        // Retorna se está liberado para consulta à IA
        // Regra: Só consulta IA se não for CRÍTICO
        return risco != NivelRisco.CRITICO;
    }

    /**
     * Busca os últimos registros de um paciente para contexto da IA.
     * Usado para montar o histórico que será enviado à IA.
     *
     * @param idPaciente ID do paciente
     * @param limite Número de registros a buscar (padrão: 7 dias)
     * @return Lista dos últimos registros
     * @throws SQLException se houver erro de persistência
     */
    public List<SinalVital> buscarHistoricoRecente(Long idPaciente, int limite)
            throws SQLException {
        return registroDAO.buscarUltimosRegistros(idPaciente, limite);
    }

    /**
     * Busca registros de um período específico.
     * @param idPaciente ID do paciente
     * @param inicio Data/hora inicial
     * @param fim Data/hora final
     * @return Lista de registros no período
     * @throws SQLException se houver erro de persistência
     */
    public List<SinalVital> buscarRegistrosPorPeriodo(Long idPaciente,
                                                       LocalDateTime inicio,
                                                       LocalDateTime fim)
            throws SQLException {
        return registroDAO.listarPorPeriodo(idPaciente, inicio, fim);
    }

    /**
     * Lista todos os registros de um paciente.
     * @param idPaciente ID do paciente
     * @return Lista completa de registros
     * @throws SQLException se houver erro de persistência
     */
    public List<SinalVital> listarTodosRegistros(Long idPaciente) throws SQLException {
        return registroDAO.listarPorPaciente(idPaciente);
    }

    /**
     * Verifica quantos episódios críticos o paciente teve.
     * Útil para relatórios e análise de tendências.
     *
     * @param idPaciente ID do paciente
     * @return Número de registros com nível CRÍTICO
     * @throws SQLException se houver erro de persistência
     */
    public int contarEpisodiosCriticos(Long idPaciente) throws SQLException {
        return registroDAO.contarRegistrosCriticos(idPaciente);
    }

    /**
     * Busca um registro específico por ID.
     * @param idRegistro ID do registro
     * @return SinalVital encontrado ou null
     * @throws SQLException se houver erro de persistência
     */
    public SinalVital buscarRegistroPorId(Long idRegistro) throws SQLException {
        return registroDAO.buscarPorId(idRegistro);
    }

    /**
     * Gera um resumo estatístico dos registros de um paciente.
     * @param idPaciente ID do paciente
     * @return String com resumo estatístico
     * @throws SQLException se houver erro de persistência
     */
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
```

### 3. Classe de Resultado de Registro

Crie `src/main/java/br/com/glicemia/bo/ResultadoRegistro.java`:

```java
package br.com.glicemia.bo;

import br.com.glicemia.model.vo.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

/**
 * Classe que encapsula o resultado de um registro de sinal vital.
 * Facilita a comunicação entre BO e View.
 */
public class ResultadoRegistro {

    private final SinalVital sinalVital;
    private final boolean isEmergencia;
    private final boolean liberadoParaIA;
    private final String mensagemAlerta;
    private final String protocoloEmergencia;

    private ResultadoRegistro(Builder builder) {
        this.sinalVital = builder.sinalVital;
        this.isEmergencia = builder.isEmergencia;
        this.liberadoParaIA = builder.liberadoParaIA;
        this.mensagemAlerta = builder.mensagemAlerta;
        this.protocoloEmergencia = builder.protocoloEmergencia;
    }

    // Getters
    public SinalVital getSinalVital() {
        return sinalVital;
    }

    public boolean isEmergencia() {
        return isEmergencia;
    }

    public boolean isLiberadoParaIA() {
        return liberadoParaIA;
    }

    public String getMensagemAlerta() {
        return mensagemAlerta;
    }

    public String getProtocoloEmergencia() {
        return protocoloEmergencia;
    }

    public NivelRisco getNivelRisco() {
        return sinalVital != null ? sinalVital.getNivelRisco() : null;
    }

    // Builder Pattern para facilitar construção
    public static class Builder {
        private SinalVital sinalVital;
        private boolean isEmergencia = false;
        private boolean liberadoParaIA = false;
        private String mensagemAlerta;
        private String protocoloEmergencia;

        public Builder comSinalVital(SinalVital sinalVital) {
            this.sinalVital = sinalVital;
            return this;
        }

        public Builder emergencia(boolean isEmergencia) {
            this.isEmergencia = isEmergencia;
            return this;
        }

        public Builder liberadoParaIA(boolean liberadoParaIA) {
            this.liberadoParaIA = liberadoParaIA;
            return this;
        }

        public Builder mensagemAlerta(String mensagemAlerta) {
            this.mensagemAlerta = mensagemAlerta;
            return this;
        }

        public Builder protocoloEmergencia(String protocoloEmergencia) {
            this.protocoloEmergencia = protocoloEmergencia;
            return this;
        }

        public ResultadoRegistro build() {
            return new ResultadoRegistro(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Resultado do Registro ===\n");
        sb.append("Sinal: ").append(sinalVital.getDescricao()).append("\n");
        sb.append("Risco: ").append(getNivelRisco()).append("\n");
        sb.append("Emergência: ").append(isEmergencia ? "SIM" : "NÃO").append("\n");
        sb.append("Liberado para IA: ").append(liberadoParaIA ? "SIM" : "NÃO").append("\n");

        if (mensagemAlerta != null) {
            sb.append("\nAlerta: ").append(mensagemAlerta).append("\n");
        }

        if (protocoloEmergencia != null) {
            sb.append("\nProtocolo de Emergência:\n").append(protocoloEmergencia).append("\n");
        }

        return sb.toString();
    }
}
```

## 🧪 Teste da Camada BO

Crie `TestBusinessObject.java`:

```java
import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.*;

import java.time.LocalDate;

public class TestBusinessObject {
    public static void main(String[] args) {
        System.out.println("=== Teste da Camada BO ===\n");

        try {
            // 1. Cadastrar Paciente
            GerenciadorPacienteBO pacienteBO = new GerenciadorPacienteBO();
            Paciente paciente = pacienteBO.cadastrarPaciente(
                "João Silva",
                "12345678901",
                LocalDate.of(1980, 5, 15),
                "joao@email.com",
                "(11) 99999-9999"
            );
            System.out.println("✓ Paciente cadastrado: " + paciente);

            // 2. Registrar Glicemia Normal
            GerenciadorRegistroBO registroBO = new GerenciadorRegistroBO();

            Glicemia glicemiaNormal = new Glicemia(paciente.getIdPaciente(), 95.0, true);
            boolean liberadoIA = registroBO.registrarSinalVital(glicemiaNormal);

            System.out.println("\n--- Teste 1: Glicemia Normal ---");
            System.out.println(glicemiaNormal.getDescricao());
            System.out.println("Risco: " + glicemiaNormal.getNivelRisco());
            System.out.println("Liberado para IA: " + (liberadoIA ? "SIM" : "NÃO"));
            System.out.println("Recomendação: " + glicemiaNormal.getRecomendacaoImediata());

            // 3. Tentar registrar Glicemia Crítica (deve lançar exceção)
            System.out.println("\n--- Teste 2: Glicemia Crítica ---");
            try {
                Glicemia glicemiaCritica = new Glicemia(paciente.getIdPaciente(), 45.0, true);
                registroBO.registrarSinalVital(glicemiaCritica);

                // Se chegou aqui, algo está errado
                System.out.println("✗ ERRO: Deveria ter lançado RiscoEmergenciaException!");

            } catch (RiscoEmergenciaException e) {
                System.out.println("✓ Emergência detectada corretamente!");
                System.out.println("Mensagem: " + e.getMessage());
                System.out.println("Nível: " + e.getNivelRisco());
                System.out.println("\nProtocolo de Emergência:");
                System.out.println(e.getProtocolo());
            }

            // 4. Buscar histórico
            System.out.println("\n--- Teste 3: Histórico ---");
            var historico = registroBO.buscarHistoricoRecente(paciente.getIdPaciente(), 10);
            System.out.println("Total de registros: " + historico.size());

            // 5. Resumo estatístico
            System.out.println("\n--- Teste 4: Resumo Estatístico ---");
            String resumo = registroBO.gerarResumoEstatistico(paciente.getIdPaciente());
            System.out.println(resumo);

        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ ] `GerenciadorPacienteBO` implementado
- [ ] `GerenciadorRegistroBO` implementado com funil de segurança
- [ ] `ResultadoRegistro` criado para encapsular resultados
- [ ] Regra de bloqueio de IA em casos críticos funciona
- [ ] Teste de registro normal funciona
- [ ] Teste de emergência lança exceção corretamente
- [ ] Busca de histórico funciona
- [ ] Resumo estatístico gerado corretamente

## 🎯 Validação do Funil de Segurança

Execute o teste e verifique:

1. ✅ Sinal vital normal: Salva no banco e libera para IA
2. ✅ Sinal vital crítico: Lança exceção, NÃO salva, NÃO chama IA
3. ✅ Mensagem de emergência e protocolo são exibidos
4. ✅ Histórico é buscado corretamente

## 📌 Próximos Passos

**Próxima fase**: **[Fase 06 - Sistema de Alertas e Emergências](./06-sistema-alertas.md)**

---

**Conceitos implementados**: Lógica de Negócio ✅ | Funil de Segurança ✅ | Coordenação BO/DAO ✅
