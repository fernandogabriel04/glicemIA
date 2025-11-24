# Fase 08 - Camada View (Interface Console)

## 🎯 Objetivos
- Criar interface interativa no console
- Implementar menus navegáveis
- Integrar todas as camadas (VO, BO, DAO, Serviços)
- Proporcionar experiência de usuário fluida

## 📚 Conceitos Aplicados
- ✅ **MVC Pattern**: View separada da lógica de negócio
- ✅ **Scanner**: Captura de entrada do usuário
- ✅ **Loops e Validações**: Interação contínua

## 🔧 Implementação

### 1. Classe MenuPrincipal

Crie `src/main/java/br/com/glicemia/view/MenuPrincipal.java`:

```java
package br.com.glicemia.view;

import br.com.glicemia.util.AlertaEmergencia;

import java.util.Scanner;

/**
 * Menu principal do sistema GlicemIA.
 * Ponto de entrada da aplicação.
 */
public class MenuPrincipal {

    private final Scanner scanner;
    private final MenuPaciente menuPaciente;
    private final MenuRegistro menuRegistro;
    private final MenuConsultaIA menuConsultaIA;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.menuPaciente = new MenuPaciente(scanner);
        this.menuRegistro = new MenuRegistro(scanner);
        this.menuConsultaIA = new MenuConsultaIA(scanner);
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
                    menuConsultaIA.exibir();
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
        System.out.println("║            🏥 GlicemIA System 1.0 🏥            ║");
        System.out.println("║      Monitor Metabólico Inteligente              ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("\nSistema de monitoramento de saúde com IA");
        System.out.println("Desenvolvido para ajudar no controle metabólico\n");
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n════════════ MENU PRINCIPAL ════════════");
        System.out.println("1. Gerenciar Pacientes");
        System.out.println("2. Registrar Sinais Vitais");
        System.out.println("3. Consultar IA para Recomendações");
        System.out.println("4. Relatórios e Histórico");
        System.out.println("0. Sair");
        System.out.println("════════════════════════════════════════");
        System.out.print("Escolha uma opção: ");
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
        System.out.println("║     Obrigado por usar o GlicemIA System!        ║");
        System.out.println("║        Cuide bem da sua saúde! 💚               ║");
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
```

### 2. Classe MenuPaciente

Crie `src/main/java/br/com/glicemia/view/MenuPaciente.java`:

```java
package br.com.glicemia.view;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.model.vo.Paciente;
import br.com.glicemia.util.AlertaEmergencia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Menu para gerenciamento de pacientes.
 */
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
```

### 3. Classe MenuRegistro

Crie `src/main/java/br/com/glicemia/view/MenuRegistro.java`:

```java
package br.com.glicemia.view;

import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.util.AlertaEmergencia;
import br.com.glicemia.util.ProtocoloEmergencia;

import java.util.Scanner;

/**
 * Menu para registro de sinais vitais.
 * Implementa o FUNIL DE SEGURANÇA do sistema.
 */
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

            // Cria o sinal vital
            Glicemia glicemia = new Glicemia(idPaciente, valor, emJejum);
            if (!obs.isEmpty()) {
                glicemia.setObservacoes(obs);
            }

            // FUNIL DE SEGURANÇA: registra via BO
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
```

### 4. Classe MenuConsultaIA

Crie `src/main/java/br/com/glicemia/view/MenuConsultaIA.java`:

```java
package br.com.glicemia.view;

import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.service.GerenciadorIA;
import br.com.glicemia.util.AlertaEmergencia;

import java.util.Scanner;

/**
 * Menu para consultas à IA.
 */
public class MenuConsultaIA {

    private final Scanner scanner;
    private final GerenciadorRegistroBO registroBO;
    private final GerenciadorIA gerenciadorIA;

    public MenuConsultaIA(Scanner scanner) {
        this.scanner = scanner;
        this.registroBO = new GerenciadorRegistroBO();
        this.gerenciadorIA = new GerenciadorIA();
    }

    public void exibir() {
        AlertaEmergencia.exibirCabecalho("Consulta à IA");

        try {
            System.out.print("ID do Paciente: ");
            Long idPaciente = Long.parseLong(scanner.nextLine());

            System.out.print("\nQual sua pergunta para a IA?\n");
            System.out.println("Exemplos:");
            System.out.println("- O que devo comer no jantar?");
            System.out.println("- Como melhorar minha glicemia?");
            System.out.println("- Dicas de exercícios?\n");
            System.out.print("Sua pergunta: ");
            String pergunta = scanner.nextLine();

            // Busca histórico recente (últimos 10 registros)
            var historico = registroBO.buscarHistoricoRecente(idPaciente, 10);

            System.out.println("\n🤖 Consultando IA... (pode levar alguns segundos)");

            // Solicita recomendação
            String resposta = gerenciadorIA.solicitarRecomendacao(historico, pergunta);

            // Exibe resultado
            AlertaEmergencia.exibirCabecalho("Resposta da IA");
            System.out.println("Provedor: " + gerenciadorIA.getProvedorAtivo());

            if (gerenciadorIA.isFallbackAtivo()) {
                AlertaEmergencia.exibirAviso(
                    "IA online indisponível. Usando recomendações locais."
                );
            }

            System.out.println("\n" + resposta);

            System.out.println("\n⚕ LEMBRE-SE: Esta é uma recomendação geral.");
            System.out.println("Para orientações específicas, consulte seu médico.");

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro na consulta: " + e.getMessage());
        }

        aguardarEnter();
    }

    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
```

## ✅ Checklist de Validação

- [ ] `MenuPrincipal` criado com navegação funcional
- [ ] `MenuPaciente` permite cadastrar e buscar pacientes
- [ ] `MenuRegistro` implementa funil de segurança
- [ ] `MenuConsultaIA` integra com o serviço de IA
- [ ] Alertas coloridos funcionam corretamente
- [ ] Emergências bloqueiam a IA
- [ ] Navegação entre menus é fluida
- [ ] Tratamento de erros de entrada do usuário

## 🧪 Teste Completo

Execute `MenuPrincipal.main()` e teste:

1. ✅ Cadastrar um paciente
2. ✅ Registrar glicemia normal
3. ✅ Registrar glicemia crítica (deve bloquear)
4. ✅ Ver histórico
5. ✅ Consultar IA

## 📌 Próximos Passos

**Próxima fase**: **[Fase 09 - Testes Unitários](./09-testes-unitarios.md)**

---

**Conceitos implementados**: Interface Console ✅ | Integração Completa ✅ | MVC ✅
