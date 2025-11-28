package br.com.glicemia.view;

import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.util.AlertaEmergencia;
import java.util.Scanner;

// Menu para registro de sinais vitais. Implementa verificação passando pela camada de BO do sistema.
public class MenuRegistro {

    private final Scanner scanner;
    private final GerenciadorRegistroBO registroBO;

    public MenuRegistro(Scanner scanner) {
        this.scanner = scanner;
        this.registroBO = new GerenciadorRegistroBO();
    }

    public void exibir() {
        boolean voltar = false;

        while (!voltar) {
            AlertaEmergencia.exibirCabecalho("Registro de Sinais Vitais");
            System.out.println("1. Registrar Glicemia");
            System.out.println("2. Registrar Pressão Arterial");
            System.out.println("3. Registrar Peso Corporal");
            System.out.println("4. Ver Histórico");
            System.out.println("0. Voltar");
            System.out.print("\nEscolha uma opção: ");

            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    registrarGlicemia();
                    break;
                case 2:
                    registrarPressao();
                    break;
                case 3:
                    registrarPeso();
                    break;
                case 4:
                    verHistorico();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    AlertaEmergencia.exibirErro("Opção inválida!");
            }
        }
    }

    private void registrarGlicemia() {
        AlertaEmergencia.exibirCabecalho("Registro de Glicemia");

        try {
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            System.out.print("Valor da glicemia (mg/dL): ");
            double valor = Double.parseDouble(scanner.nextLine());

            System.out.print("Estava em jejum? (S/N): ");
            boolean emJejum = scanner.nextLine().trim().toUpperCase().equals("S");

            System.out.print("Observações (opcional): ");
            String obs = scanner.nextLine();

            // Registra
            Glicemia glicemia = new Glicemia(idPaciente, valor, emJejum);
            if (!obs.isEmpty()) {
                glicemia.setObservacoes(obs);
            }

            // registra via BO
            boolean liberadoIA = registroBO.registrarSinalVital(glicemia);

            // Exibe alerta baseado no risco
            AlertaEmergencia.exibirAlerta(glicemia);

            if (liberadoIA) {
                AlertaEmergencia.exibirSucesso("Registro salvo! Consulte a IA para dicas.");
            } else {
                AlertaEmergencia.exibirAviso("Registro salvo, mas consulta à IA não recomendada.");
            }

        } catch (RiscoEmergenciaException e) {
            // EMERGÊNCIA DETECTADA!
            AlertaEmergencia.exibirSistemaBloqueado();
            AlertaEmergencia.exibirProtocoloEmergencia(e.getProtocolo());

            System.out.println("\n⚠ O REGISTRO NÃO FOI SALVO devido à emergência.");
            System.out.println("PROCURE ATENDIMENTO MÉDICO IMEDIATAMENTE.");

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro no registro: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void registrarPressao() {
        AlertaEmergencia.exibirCabecalho("Registro de Pressão Arterial");

        try {
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            System.out.print("Pressão sistólica (máxima): ");
            int sistolica = Integer.parseInt(scanner.nextLine());

            System.out.print("Pressão diastólica (mínima): ");
            int diastolica = Integer.parseInt(scanner.nextLine());

            System.out.print("Observações (opcional): ");
            String obs = scanner.nextLine();

            PressaoArterial pressao = new PressaoArterial(idPaciente, sistolica, diastolica);
            if (!obs.isEmpty()) {
                pressao.setObservacoes(obs);
            }

            boolean liberadoIA = registroBO.registrarSinalVital(pressao);

            AlertaEmergencia.exibirAlerta(pressao);

            if (liberadoIA) {
                AlertaEmergencia.exibirSucesso("Registro salvo com sucesso!");
            }

        } catch (RiscoEmergenciaException e) {
            AlertaEmergencia.exibirSistemaBloqueado();
            AlertaEmergencia.exibirProtocoloEmergencia(e.getProtocolo());

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro no registro: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void registrarPeso() {
        AlertaEmergencia.exibirCabecalho("Registro de Peso Corporal");

        try {
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            System.out.print("Peso (kg): ");
            double peso = Double.parseDouble(scanner.nextLine());

            System.out.print("Altura (m): ");
            double altura = Double.parseDouble(scanner.nextLine());

            System.out.print("Observações (opcional): ");
            String obs = scanner.nextLine();

            PesoCorporal pesoCorporal = new PesoCorporal(idPaciente, peso, altura);
            if (!obs.isEmpty()) {
                pesoCorporal.setObservacoes(obs);
            }

            registroBO.registrarSinalVital(pesoCorporal);

            AlertaEmergencia.exibirAlerta(pesoCorporal);
            AlertaEmergencia.exibirSucesso("Registro salvo com sucesso!");

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro no registro: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void verHistorico() {
        AlertaEmergencia.exibirCabecalho("Histórico de Registros");

        try {
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            var registros = registroBO.listarTodosRegistros(idPaciente);

            if (registros.isEmpty()) {
                AlertaEmergencia.exibirAviso("Nenhum registro encontrado para este paciente.");
            } else {
                System.out.println("\nTotal de registros: " + registros.size() + "\n");
                for (SinalVital sinal : registros) {
                    System.out.println("─────────────────────────────────────");
                    System.out.println("Data: " + sinal.getDataHora());
                    System.out.println(sinal.getDescricao());
                    System.out.println("Risco: " + sinal.getNivelRisco());
                }
                System.out.println("─────────────────────────────────────");

                // Exibe resumo estatístico
                System.out.println("\n" + registroBO.gerarResumoEstatistico(idPaciente));
            }

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro ao buscar histórico: " + e.getMessage());
        }

        aguardarEnter();
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
