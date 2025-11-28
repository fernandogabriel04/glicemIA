package br.com.glicemia.view;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.model.vo.Paciente;
import br.com.glicemia.util.AlertaEmergencia;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

// Menu para gerenciamento de pacientes.
public class MenuPaciente {

    private final Scanner scanner;
    private final GerenciadorPacienteBO pacienteBO;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MenuPaciente(Scanner scanner) {
        this.scanner = scanner;
        this.pacienteBO = new GerenciadorPacienteBO();
    }

    public void exibir() {
        boolean voltar = false;

        while (!voltar) {
            AlertaEmergencia.exibirCabecalho("Gerenciamento de Pacientes");
            System.out.println("1. Cadastrar Novo Paciente");
            System.out.println("2. Buscar Paciente por CPF");
            System.out.println("3. Listar Todos os Pacientes");
            System.out.println("0. Voltar");
            System.out.print("\nEscolha uma opção: ");

            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    cadastrarPaciente();
                    break;
                case 2:
                    buscarPacientePorCPF();
                    break;
                case 3:
                    listarPacientes();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    AlertaEmergencia.exibirErro("Opção inválida!");
            }
        }
    }

    private void cadastrarPaciente() {
        AlertaEmergencia.exibirCabecalho("Cadastro de Paciente");

        try {
            System.out.print("Nome completo: ");
            String nome = scanner.nextLine();

            System.out.print("CPF (apenas números): ");
            String cpf = scanner.nextLine();

            System.out.print("Data de nascimento (dd/MM/yyyy): ");
            String dataStr = scanner.nextLine();
            LocalDate dataNascimento = LocalDate.parse(dataStr, formatoData);

            System.out.print("Email (opcional): ");
            String email = scanner.nextLine();

            System.out.print("Telefone (opcional): ");
            String telefone = scanner.nextLine();

            // Cadastra via BO
            Paciente paciente = pacienteBO.cadastrarPaciente(
                nome, cpf, dataNascimento, email, telefone
            );

            AlertaEmergencia.exibirSucesso("Paciente cadastrado com sucesso!");
            System.out.println("\nID gerado: " + paciente.getIdPaciente());
            System.out.println(paciente);

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Falha no cadastro: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void buscarPacientePorCPF() {
        AlertaEmergencia.exibirCabecalho("Buscar Paciente");

        try {
            System.out.print("Digite o CPF: ");
            String cpf = scanner.nextLine();

            Paciente paciente = pacienteBO.buscarPacientePorCPF(cpf);

            if (paciente != null) {
                AlertaEmergencia.exibirSucesso("Paciente encontrado!");
                exibirDetalhesPaciente(paciente);
            } else {
                AlertaEmergencia.exibirAviso("Paciente não encontrado com o CPF: " + cpf);
            }

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro na busca: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void listarPacientes() {
        AlertaEmergencia.exibirCabecalho("Lista de Pacientes");

        try {
            List<Paciente> pacientes = pacienteBO.listarTodosPacientes();

            if (pacientes.isEmpty()) {
                AlertaEmergencia.exibirAviso("Nenhum paciente cadastrado.");
            } else {
                System.out.println("Total de pacientes: " + pacientes.size() + "\n");
                for (Paciente p : pacientes) {
                    System.out.println("─────────────────────────────────────");
                    exibirDetalhesPaciente(p);
                }
                System.out.println("─────────────────────────────────────");
            }

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro ao listar: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void exibirDetalhesPaciente(Paciente p) {
        System.out.println("ID: " + p.getIdPaciente());
        System.out.println("Nome: " + p.getNome());
        System.out.println("CPF: " + p.getCpf());
        System.out.println("Idade: " + p.getIdade() + " anos");
        System.out.println("Data Nascimento: " +
            p.getDataNascimento().format(formatoData));
        if (p.getEmail() != null) {
            System.out.println("Email: " + p.getEmail());
        }
        if (p.getTelefone() != null) {
            System.out.println("Telefone: " + p.getTelefone());
        }
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
