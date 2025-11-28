package br.com.glicemia.view;

import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.ConversationSession;
import br.com.glicemia.service.GerenciadorIA;
import br.com.glicemia.service.TopicValidator;
import br.com.glicemia.util.AlertaEmergencia;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// Interface de chat com a IA. Permite conversas contínuas com a IA sobre diabetes e hipertensão.
public class ChatIAView {

    private final Scanner scanner;
    private final RegistroDAOImpl registroDAO;
    private final GerenciadorIA gerenciadorIA;
    private final TopicValidator validator;

    public ChatIAView() {
        this.scanner = new Scanner(System.in);
        this.registroDAO = new RegistroDAOImpl();
        this.gerenciadorIA = new GerenciadorIA();
        this.validator = new TopicValidator();
    }

    // Inicia o chat para um paciente.
    public void iniciar(Long idPaciente) {
        try {
            exibirCabecalho();

            // Carrega histórico do paciente
            System.out.println("Carregando seu histórico médico...\n");
            List<SinalVital> historico = registroDAO.buscarUltimosRegistros(idPaciente, 10);

            if (historico.isEmpty()) {
                System.out.println("⚠ Nenhum registro encontrado. Registre seus sinais vitais primeiro!");
                System.out.println();
                aguardarEnter();
                return;
            }

            // Security Funnel: Bloqueia se último registro é CRÍTICO
            SinalVital ultimoRegistro = historico.get(0);
            if (ultimoRegistro.getNivelRisco() == NivelRisco.CRITICO) {
                bloquearPorEmergencia(ultimoRegistro);
                return;
            }

            // Exibe status do histórico
            exibirStatusHistorico(historico);

            // Cria sessão conversacional
            ConversationSession session = new ConversationSession(idPaciente, historico);

            // Exibe informações da sessão
            exibirInfoSessao(session);

            // Loop conversacional
            executarLoopConversacional(session);

            // Resumo da sessão ao sair
            exibirResumoSessao(session);

        } catch (SQLException e) {
            AlertaEmergencia.exibirErro("Erro ao acessar banco de dados: " + e.getMessage());
            aguardarEnter();
        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            aguardarEnter();
        }
    }

    /**
     * Loop principal da conversa.
     */
    private void executarLoopConversacional(ConversationSession session) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  💬 CHAT INICIADO 💬                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Você pode fazer perguntas sobre:");
        System.out.println("  • Controle de glicemia e diabetes");
        System.out.println("  • Pressão arterial e hipertensão");
        System.out.println("  • Alimentação e exercícios");
        System.out.println("  • Interpretação dos seus registros");
        System.out.println();
        System.out.println("Digite 'voltar' ou 'sair' para encerrar a conversa.");
        System.out.println("────────────────────────────────────────────────────────────────");
        System.out.println();

        while (!session.atingiuLimite()) {
            // Prompt do usuário
            System.out.print("💬 Você: ");
            String pergunta = scanner.nextLine().trim();

            // Verifica comando de saída
            if (validator.isComandoSaida(pergunta)) {
                System.out.println("\n👋 Encerrando consulta...\n");
                break;
            }

            // Verifica se não está vazio
            if (pergunta.isEmpty()) {
                continue;
            }

            // Processa pergunta
            System.out.println();
            System.out.print("🤖 IA está pensando");
            exibirAnimacaoCarregamento();
            System.out.println();

            String resposta = gerenciadorIA.solicitarRecomendacaoConversacional(session, pergunta);

            // Exibe resposta
            System.out.println("🤖 Assistente de Saúde:");
            System.out.println();
            exibirRespostaFormatada(resposta);
            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────────");
            System.out.println();
        }

        // Verifica se atingiu limite
        if (session.atingiuLimite()) {
            System.out.println("⚠ Você atingiu o limite de perguntas desta sessão.");
            System.out.println("Por favor, inicie uma nova consulta.\n");
        }
    }

    /**
     * Exibe cabeçalho do chat.
     */
    private void exibirCabecalho() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     💬 CHAT IA 💬                              ║");
        System.out.println("║         Assistente Virtual para Diabetes e Hipertensão        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Bloqueia acesso quando último registro é CRÍTICO.
     */
    private void bloquearPorEmergencia(SinalVital registroCritico) {
        System.out.println();
        AlertaEmergencia.exibirSistemaBloqueado();
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("         ⚠ ACESSO AO CHAT BLOQUEADO ⚠");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Seu último registro indica um nível de risco CRÍTICO:");
        System.out.println();
        System.out.println("  " + registroCritico.getDescricao());
        System.out.println("  Data: " + registroCritico.getDataHora());
        System.out.println("  Nível de Risco: " + registroCritico.getNivelRisco().getDescricao());
        System.out.println();
        System.out.println("🚨 VOCÊ PRECISA DE ATENDIMENTO MÉDICO IMEDIATO!");
        System.out.println();
        System.out.println("O chat está temporariamente bloqueado para sua");
        System.out.println("segurança. Por favor, siga o protocolo de emergência exibido");
        System.out.println("no momento do registro.");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");

        aguardarEnter();
    }

    /**
     * Exibe status do histórico médico.
     */
    private void exibirStatusHistorico(List<SinalVital> historico) {
        System.out.println("📊 Status do seu histórico:");
        System.out.println("  • Total de registros: " + historico.size());

        SinalVital ultimoRegistro = historico.get(0);
        System.out.println("  • Último registro: " + ultimoRegistro.getDescricao());
        System.out.println("  • Nível de Risco: " + ultimoRegistro.getNivelRisco().getDescricao());
        System.out.println();
    }

    /**
     * Exibe informações da sessão.
     */
    private void exibirInfoSessao(ConversationSession session) {
        System.out.println("🔧 Configuração da IA:");
        System.out.println("  • Provedor: " + gerenciadorIA.getProvedorAtivo());
        System.out.println("  • Limite de perguntas: " + ConversationSession.LIMITE_TURNOS + " por sessão");
        System.out.println("  • Data de início: " + session.getDataInicio());

        if (gerenciadorIA.isFallbackAtivo()) {
            System.out.println();
            System.out.println("ℹ Usando IA Local (respostas baseadas em regras)");
        }

        System.out.println();
    }

    /**
     * Exibe resumo da sessão ao encerrar.
     */
    private void exibirResumoSessao(ConversationSession session) {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📋 RESUMO DO CHAT                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Quantidade de perguntas: " + session.getQuantidadeTurnos());
        System.out.println("Duração da sessão: " + session.getDataInicio() + " - " +
                          java.time.LocalDateTime.now().format(
                              java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println();

        if (session.getQuantidadeTurnos() > 0) {
            System.out.println("Tópicos discutidos:");
            System.out.println(session.getResumoConversa());
            System.out.println();
        }

        System.out.println("Obrigado por usar o Chat IA! 💙");
        System.out.println();
        System.out.println("⚠ LEMBRETE IMPORTANTE:");
        System.out.println("As informações fornecidas são apenas orientações gerais.");
        System.out.println("Em caso de dúvidas ou sintomas graves, consulte um médico.");
        System.out.println();

        aguardarEnter();
    }

    /**
     * Exibe resposta formatada com quebras de linha adequadas.
     */
    private void exibirRespostaFormatada(String resposta) {
        String[] linhas = resposta.split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty()) {
                System.out.println();
            } else {
                System.out.println("  " + linha);
            }
        }
    }

    /**
     * Animação simples de carregamento.
     */
    private void exibirAnimacaoCarregamento() {
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();
        } catch (InterruptedException e) {
            // Ignora
        }
    }

    /**
     * Aguarda usuário pressionar ENTER.
     */
    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
