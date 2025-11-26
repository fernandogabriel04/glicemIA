package br.com.glicemia.dao.interfaces;

import br.com.glicemia.model.vo.Paciente;
import java.sql.SQLException;
import java.util.List;

public interface PacienteDAO {

    Long inserir(Paciente paciente) throws SQLException;

    void atualizar(Paciente paciente) throws SQLException;

    void deletar(Long idPaciente) throws SQLException;

    Paciente buscarPorId(Long idPaciente) throws SQLException;

    Paciente buscarPorCPF(String cpf) throws SQLException;

    List<Paciente> listarTodos() throws SQLException;
}
