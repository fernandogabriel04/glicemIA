package br.com.glicemia.view;

import br.com.glicemia.util.AlertaEmergencia;
import java.util.Scanner;

// Menu principal do sistema. Ponto de entrada da aplicação.
public class MenuPrincipal {

    private final Scanner scanner;
    private final MenuPaciente menuPaciente;
    private final MenuRegistro menuRegistro;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.menuPaciente = new MenuPaciente(scanner);
        this.menuRegistro = new MenuRegistro(scanner);
    }

    public void iniciar() {
        exibirBanner();

        boolean continuar = true;

        while (continuar) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    menuPaciente.exibir();
                    break;
                case 2:
                    menuRegistro.exibir();
                    break;
                case 3:
                    iniciarChatIA();
                    break;
                case 4:
                    exibirRelatorios();
                    break;
                case 0:
                    continuar = false;
                    exibirDespedida();
                    break;
                default:
                    AlertaEmergencia.exibirErro("Opção inválida!");
            }
        }

        scanner.close();
    }

    private void exibirBanner() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║                                                  ║");
        System.out.println("║             🏥 GlicemIA 🏥                      ║");
        System.out.println("║      Monitor Metabólico Inteligente              ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("\nSistema de monitoramento de diabéticos e hipertensos com IA");
        System.out.println("Desenvolvido para ajudar no monitoramento contínuo de pacientes.\n");
        System.out.println("by. Fernando Gabriel e Calebe - 2025\n");
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n════════════ MENU PRINCIPAL ════════════");
        System.out.println("1. Gerenciar Pacientes");
        System.out.println("2. Registrar Sinais Vitais");
        System.out.println("3. Chat IA - Consultas e Dúvidas");
        System.out.println("4. Relatórios e Histórico");
        System.out.println("0. Sair");
        System.out.println("════════════════════════════════════════");
        System.out.print("Escolha uma opção: ");
    }

    private void iniciarChatIA() {
        try {
            System.out.println();
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            ChatIAView chatIA = new ChatIAView();
            chatIA.iniciar(idPaciente);

        } catch (NumberFormatException e) {
            AlertaEmergencia.exibirErro("ID inválido! Digite apenas números.");
            aguardarEnter();
        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro ao iniciar chat: " + e.getMessage());
            aguardarEnter();
        }
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void exibirRelatorios() {
        AlertaEmergencia.exibirCabecalho("Relatórios");
        System.out.println("Funcionalidade em desenvolvimento...");
        aguardarEnter();
    }

    private void exibirDespedida() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║     Obrigado por usar o sistema GlicemIA!        ║");
        System.out.println("║          Monitore sua saúde! 💚                 ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
    }

    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal();
        menu.iniciar();
    }
}
