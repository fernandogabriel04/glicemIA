package br.com.glicemia;

import br.com.glicemia.dao.impl.*;
import br.com.glicemia.dao.interfaces.*;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.exceptions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDAO {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         Teste da Camada DAO            ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            PacienteDAO pacienteDAO = new PacienteDAOImpl();
            RegistroDAO registroDAO = new RegistroDAOImpl();

            System.out.println("=== Teste 1: Buscar ou Inserir Paciente ===");

            Long idPaciente;
            Paciente paciente = pacienteDAO.buscarPorCPF("12345678901");

            if (paciente != null) {
                System.out.println("✓ Paciente já existe no banco");
                idPaciente = paciente.getIdPaciente();
                System.out.println("  ID: " + idPaciente);
                System.out.println("  Nome: " + paciente.getNome());
                System.out.println("  Idade: " + paciente.getIdade() + " anos\n");
            } else {
                paciente = new Paciente("Maria Silva", "12345678901",
                    LocalDate.of(1985, 3, 20));
                paciente.setEmail("maria@email.com");
                paciente.setTelefone("11987654321");

                idPaciente = pacienteDAO.inserir(paciente);
                System.out.println("✓ Paciente inserido com ID: " + idPaciente);
                System.out.println("  Nome: " + paciente.getNome());
                System.out.println("  Idade: " + paciente.getIdade() + " anos\n");
            }

            System.out.println("=== Teste 2: Buscar Paciente por ID ===");
            Paciente pacienteBuscado = pacienteDAO.buscarPorId(idPaciente);
            if (pacienteBuscado != null) {
                System.out.println("✓ Paciente encontrado: " + pacienteBuscado.getNome());
            }

            System.out.println("\n=== Teste 3: Buscar Paciente por CPF ===");
            Paciente pacienteCPF = pacienteDAO.buscarPorCPF("12345678901");
            if (pacienteCPF != null) {
                System.out.println("✓ Paciente encontrado por CPF: " + pacienteCPF.getNome());
            }

            System.out.println("\n=== Teste 4: Registrar Glicemia ===");
            Glicemia glicemia = new Glicemia(idPaciente, 110.0, true);
            glicemia.analisarRisco();
            Long idGlicemia = registroDAO.inserir(glicemia);
            System.out.println("✓ Glicemia registrada com ID: " + idGlicemia);
            System.out.println("  " + glicemia.getDescricao());
            System.out.println("  Risco: " + glicemia.getNivelRisco().getDescricao());

            System.out.println("\n=== Teste 5: Registrar Pressão Arterial ===");
            PressaoArterial pressao = new PressaoArterial(idPaciente, 130, 85);
            pressao.analisarRisco();
            Long idPressao = registroDAO.inserir(pressao);
            System.out.println("✓ Pressão registrada com ID: " + idPressao);
            System.out.println("  " + pressao.getDescricao());
            System.out.println("  Risco: " + pressao.getNivelRisco().getDescricao());

            System.out.println("\n=== Teste 6: Registrar Peso Corporal ===");
            PesoCorporal peso = new PesoCorporal(idPaciente, 75.0, 1.70);
            peso.analisarRisco();
            Long idPeso = registroDAO.inserir(peso);
            System.out.println("✓ Peso registrado com ID: " + idPeso);
            System.out.println("  " + peso.getDescricao());
            System.out.println("  Risco: " + peso.getNivelRisco().getDescricao());

            System.out.println("\n=== Teste 7: Listar Registros do Paciente ===");
            var registros = registroDAO.listarPorPaciente(idPaciente);
            System.out.println("✓ Total de registros encontrados: " + registros.size());
            for (SinalVital sinal : registros) {
                System.out.println("  - " + sinal.getDescricao());
            }

            System.out.println("\n=== Teste 8: Buscar Últimos 2 Registros ===");
            var ultimosRegistros = registroDAO.buscarUltimosRegistros(idPaciente, 2);
            System.out.println("✓ Últimos 2 registros:");
            for (SinalVital sinal : ultimosRegistros) {
                System.out.println("  - " + sinal.getDescricao());
            }

            System.out.println("\n=== Teste 9: Contar Registros Críticos ===");
            int criticos = registroDAO.contarRegistrosCriticos(idPaciente);
            System.out.println("✓ Total de registros críticos: " + criticos);

            System.out.println("\n=== Teste 10: Listar por Período ===");
            LocalDateTime inicio = LocalDateTime.now().minusHours(1);
            LocalDateTime fim = LocalDateTime.now().plusHours(1);
            var registrosPeriodo = registroDAO.listarPorPeriodo(idPaciente, inicio, fim);
            System.out.println("✓ Registros no período: " + registrosPeriodo.size());

            System.out.println("\n=== Teste 11: Atualizar Paciente ===");
            paciente.setEmail("maria.silva@newemail.com");
            paciente.setTelefone("11999887766");
            pacienteDAO.atualizar(paciente);
            System.out.println("✓ Paciente atualizado com sucesso");

            Paciente pacienteAtualizado = pacienteDAO.buscarPorId(idPaciente);
            System.out.println("  Novo email: " + pacienteAtualizado.getEmail());
            System.out.println("  Novo telefone: " + pacienteAtualizado.getTelefone());

            System.out.println("\n=== Teste 12: Listar Todos os Pacientes ===");
            var todosPacientes = pacienteDAO.listarTodos();
            System.out.println("✓ Total de pacientes cadastrados: " + todosPacientes.size());
            for (Paciente p : todosPacientes) {
                System.out.println("  - " + p.getNome() + " (CPF: " + p.getCpf() + ")");
            }

            System.out.println("\n✅ Todos os testes da fase 04 passaram com sucesso!");

        } catch (RiscoEmergenciaException e) {
            System.err.println("🚨 EMERGÊNCIA: " + e.getMessage());
            System.err.println("Protocolo:\n" + e.getProtocolo());
        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
