package br.com.glicemia.bo;

import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.dao.impl.PacienteDAOImpl;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Paciente;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GerenciadorPacienteBO {

    private final PacienteDAO pacienteDAO;

    public GerenciadorPacienteBO() {
        this.pacienteDAO = new PacienteDAOImpl();
    }

    public GerenciadorPacienteBO(PacienteDAO pacienteDAO) {
        this.pacienteDAO = pacienteDAO;
    }

    public Paciente cadastrarPaciente(String nome, String cpf, LocalDate dataNascimento,
                                     String email, String telefone)
            throws ValorInvalidoException, SQLException {

        Paciente pacienteExistente = pacienteDAO.buscarPorCPF(cpf);
        if (pacienteExistente != null) {
            throw new ValorInvalidoException(
                "Já existe um paciente cadastrado com o CPF: " + cpf
            );
        }

        if (dataNascimento.isAfter(LocalDate.now().minusYears(1))) {
            throw new ValorInvalidoException(
                "Paciente deve ter pelo menos 1 ano de idade"
            );
        }

        Paciente paciente = new Paciente(nome, cpf, dataNascimento);
        paciente.setEmail(email);
        paciente.setTelefone(telefone);

        Long idGerado = pacienteDAO.inserir(paciente);
        paciente.setIdPaciente(idGerado);

        return paciente;
    }

    public void atualizarPaciente(Paciente paciente) throws SQLException {
        Paciente existente = pacienteDAO.buscarPorId(paciente.getIdPaciente());
        if (existente == null) {
            throw new SQLException("Paciente não encontrado: " + paciente.getIdPaciente());
        }

        pacienteDAO.atualizar(paciente);
    }

    public Paciente buscarPacientePorId(Long idPaciente) throws SQLException {
        return pacienteDAO.buscarPorId(idPaciente);
    }

    public Paciente buscarPacientePorCPF(String cpf) throws SQLException {
        return pacienteDAO.buscarPorCPF(cpf);
    }

    public List<Paciente> listarTodosPacientes() throws SQLException {
        return pacienteDAO.listarTodos();
    }

    public void removerPaciente(Long idPaciente) throws SQLException {
        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new SQLException("Paciente não encontrado: " + idPaciente);
        }

        pacienteDAO.deletar(idPaciente);
    }
}
