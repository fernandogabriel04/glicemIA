package br.com.glicemia.dao.interfaces;

import br.com.glicemia.model.vo.SinalVital;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistroDAO {

    Long inserir(SinalVital sinalVital) throws SQLException;

    SinalVital buscarPorId(Long idRegistro) throws SQLException;

    List<SinalVital> listarPorPaciente(Long idPaciente) throws SQLException;

    List<SinalVital> listarPorPeriodo(Long idPaciente, LocalDateTime inicio, LocalDateTime fim) throws SQLException;

    List<SinalVital> buscarUltimosRegistros(Long idPaciente, int limite) throws SQLException;

    int contarRegistrosCriticos(Long idPaciente) throws SQLException;
}
