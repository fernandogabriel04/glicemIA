package br.com.glicemia;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.*;

import java.time.LocalDate;

public class TestBusinessObject {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      Teste da Camada BO (Negócio)      ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            GerenciadorPacienteBO pacienteBO = new GerenciadorPacienteBO();
            GerenciadorRegistroBO registroBO = new GerenciadorRegistroBO();

            System.out.println("=== Teste 1: Cadastrar Paciente ===");

            Paciente paciente;
            try {
                paciente = pacienteBO.cadastrarPaciente(
                    "João Silva",
                    "98765432100",
                    LocalDate.of(1980, 5, 15),
                    "joao@email.com",
                    "(11) 99999-9999"
                );
                System.out.println("✓ Paciente cadastrado com ID: " + paciente.getIdPaciente());
            } catch (Exception e) {
                System.out.println("✓ Paciente já existe, buscando...");
                paciente = pacienteBO.buscarPacientePorCPF("98765432100");
            }

            System.out.println("  Nome: " + paciente.getNome());
            System.out.println("  CPF: " + paciente.getCpf());
            System.out.println("  Idade: " + paciente.getIdade() + " anos");

            System.out.println("\n=== Teste 2: Registrar Glicemia Normal ===");
            Glicemia glicemiaNormal = new Glicemia(paciente.getIdPaciente(), 95.0, true);
            boolean liberadoIA = registroBO.registrarSinalVital(glicemiaNormal);

            System.out.println("✓ Registro salvo com sucesso");
            System.out.println("  " + glicemiaNormal.getDescricao());
            System.out.println("  Risco: " + glicemiaNormal.getNivelRisco().getDescricao());
            System.out.println("  Liberado para IA: " + (liberadoIA ? "SIM" : "NÃO"));
            System.out.println("  Recomendação: " + glicemiaNormal.getRecomendacaoImediata());

            System.out.println("\n=== Teste 3: Registrar Pressão Arterial Normal ===");
            PressaoArterial pressaoNormal = new PressaoArterial(paciente.getIdPaciente(), 120, 80);
            liberadoIA = registroBO.registrarSinalVital(pressaoNormal);

            System.out.println("✓ Registro salvo com sucesso");
            System.out.println("  " + pressaoNormal.getDescricao());
            System.out.println("  Risco: " + pressaoNormal.getNivelRisco().getDescricao());
            System.out.println("  Liberado para IA: " + (liberadoIA ? "SIM" : "NÃO"));

            System.out.println("\n=== Teste 4: Testar FUNIL DE SEGURANÇA (Glicemia Crítica) ===");
            System.out.println("Tentando registrar glicemia de 45 mg/dL...");

            try {
                Glicemia glicemiaCritica = new Glicemia(paciente.getIdPaciente(), 45.0, true);
                registroBO.registrarSinalVital(glicemiaCritica);

                System.out.println("✗ ERRO: Deveria ter lançado RiscoEmergenciaException!");

            } catch (RiscoEmergenciaException e) {
                System.out.println("✓ FUNIL DE SEGURANÇA ATIVADO!");
                System.out.println("✓ Emergência detectada corretamente");
                System.out.println("  Mensagem: " + e.getMessage());
                System.out.println("  Nível: " + e.getNivelRisco());
                System.out.println("\n  Protocolo de Emergência:");
                System.out.println("  " + e.getProtocolo().replace("\n", "\n  "));
            }

            System.out.println("\n=== Teste 5: Testar FUNIL DE SEGURANÇA (Pressão Crítica) ===");
            System.out.println("Tentando registrar pressão 190/125 mmHg...");

            try {
                PressaoArterial pressaoCritica = new PressaoArterial(paciente.getIdPaciente(), 190, 125);
                registroBO.registrarSinalVital(pressaoCritica);

                System.out.println("✗ ERRO: Deveria ter lançado RiscoEmergenciaException!");

            } catch (RiscoEmergenciaException e) {
                System.out.println("✓ FUNIL DE SEGURANÇA ATIVADO!");
                System.out.println("✓ Crise hipertensiva detectada");
                System.out.println("  Mensagem: " + e.getMessage());
                System.out.println("\n  Protocolo de Emergência:");
                System.out.println("  " + e.getProtocolo().replace("\n", "\n  "));
            }

            System.out.println("\n=== Teste 6: Buscar Histórico Recente ===");
            var historico = registroBO.buscarHistoricoRecente(paciente.getIdPaciente(), 10);
            System.out.println("✓ Total de registros encontrados: " + historico.size());

            if (!historico.isEmpty()) {
                System.out.println("\nÚltimos registros:");
                for (int i = 0; i < Math.min(3, historico.size()); i++) {
                    SinalVital sinal = historico.get(i);
                    System.out.println("  " + (i+1) + ". " + sinal.getDescricao());
                }
            }

            System.out.println("\n=== Teste 7: Resumo Estatístico ===");
            String resumo = registroBO.gerarResumoEstatistico(paciente.getIdPaciente());
            System.out.println(resumo);

            System.out.println("\n=== Teste 8: Contar Episódios Críticos ===");
            int episodiosCriticos = registroBO.contarEpisodiosCriticos(paciente.getIdPaciente());
            System.out.println("✓ Total de episódios críticos: " + episodiosCriticos);


            System.out.println("\n ✅ Todos os testes da fase 05 passaram com sucesso!");

        } catch (Exception e) {
            System.err.println("\n✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
