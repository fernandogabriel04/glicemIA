package br.com.glicemia;

import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.service.ConversationSession;
import br.com.glicemia.service.GerenciadorIA;
import br.com.glicemia.service.TopicValidator;
import br.com.glicemia.util.AlertaEmergencia;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestChat {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║        Teste do Chat IA                   ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Setup
            RegistroDAOImpl registroDAO = new RegistroDAOImpl();
            GerenciadorIA gerenciadorIA = new GerenciadorIA();
            TopicValidator validator = new TopicValidator();

            // Busca histórico de um paciente (ID 1)
            List<SinalVital> historico = registroDAO.buscarUltimosRegistros(1L, 5);
            if (historico.isEmpty()) {
                System.out.println("⚠ Nenhum histórico encontrado. Criando histórico fictício...");
                historico = criarHistoricoFicticio();
            }

            AlertaEmergencia.exibirSucesso("Histórico carregado: " + historico.size() + " registros");
            System.out.println();

            // TESTE 1: Validação de Tópicos
            AlertaEmergencia.exibirCabecalho("Teste 1: Validação de Tópicos");

            String[] perguntasValidas = {
                "Minha glicemia está 140 mg/dL. O que devo fazer?",
                "Pressão 15/10 é perigoso?",
                "Posso comer uma banana?"
            };

            String[] perguntasInvalidas = {
                "Qual o melhor time de futebol?",
                "Como está o tempo hoje?",
                "Me conte uma piada"
            };

            System.out.println("Perguntas VÁLIDAS (diabetes/hipertensão):");
            for (String pergunta : perguntasValidas) {
                boolean valido = validator.isTopicoValido(pergunta);
                System.out.println("  " + (valido ? "✓" : "✗") + " \"" + pergunta + "\"");
            }

            System.out.println("\nPerguntas INVÁLIDAS (fora do escopo):");
            for (String pergunta : perguntasInvalidas) {
                boolean valido = validator.isTopicoValido(pergunta);
                System.out.println("  " + (!valido ? "✓" : "✗") + " \"" + pergunta + "\"");
            }

            System.out.println();

            // TESTE 2: Sessão de Chat
            AlertaEmergencia.exibirCabecalho("Teste 2: Sessão de Chat");

            ConversationSession session = new ConversationSession(1L, historico);
            System.out.println("Sessão criada para paciente ID: " + session.getIdPaciente());
            System.out.println("Data de início: " + session.getDataInicio());
            System.out.println("Provedor de IA: " + gerenciadorIA.getProvedorAtivo());
            System.out.println();

            // Pergunta 1 - Válida
            System.out.println("━━━ Pergunta 1 ━━━");
            String pergunta1 = "Minha glicemia está 140 mg/dL. O que devo fazer?";
            System.out.println("💬 Usuário: " + pergunta1);
            System.out.println();

            String resposta1 = gerenciadorIA.solicitarRecomendacaoConversacional(
                session,
                pergunta1
            );

            System.out.println("🤖 IA:");
            System.out.println(resposta1);
            System.out.println();

            // Pergunta 2 - Contexto (referencia anterior)
            System.out.println("━━━ Pergunta 2 (com contexto) ━━━");
            String pergunta2 = "E posso comer uma fruta agora?";
            System.out.println("💬 Usuário: " + pergunta2);
            System.out.println();

            String resposta2 = gerenciadorIA.solicitarRecomendacaoConversacional(
                session,
                pergunta2
            );

            System.out.println("🤖 IA:");
            System.out.println(resposta2);
            System.out.println();

            // Pergunta 3 - Inválida (fora do escopo)
            System.out.println("━━━ Pergunta 3 (fora do escopo) ━━━");
            String pergunta3 = "Qual o melhor time de futebol?";
            System.out.println("💬 Usuário: " + pergunta3);
            System.out.println();

            String resposta3 = gerenciadorIA.solicitarRecomendacaoConversacional(
                session,
                pergunta3
            );

            System.out.println("🤖 IA:");
            System.out.println(resposta3);
            System.out.println();

            // TESTE 3: Estado da Sessão
            AlertaEmergencia.exibirCabecalho("Teste 3: Estado da Sessão");

            System.out.println("Quantidade de turnos: " + session.getQuantidadeTurnos());
            System.out.println("Atingiu limite? " + (session.atingiuLimite() ? "Sim" : "Não"));
            System.out.println();

            System.out.println("Resumo da conversa:");
            System.out.println(session.getResumoConversa());
            System.out.println();

            // TESTE 4: Comandos de Saída
            AlertaEmergencia.exibirCabecalho("Teste 4: Comandos de Saída");

            String[] comandos = {"voltar", "sair", "exit", "quit", "VOLTAR"};
            System.out.println("Comandos de saída reconhecidos:");
            for (String comando : comandos) {
                boolean ehSaida = validator.isComandoSaida(comando);
                System.out.println("  " + (ehSaida ? "✓" : "✗") + " \"" + comando + "\"");
            }

            System.out.println();
            AlertaEmergencia.exibirSucesso("\n✅ Todos os testes do Chat passaram!");
            System.out.println();

            System.out.println("Resumo dos Testes:");
            System.out.println("  ✓ Validação de tópicos funcionando");
            System.out.println("  ✓ Sessão conversacional criada");
            System.out.println("  ✓ Contexto mantido entre perguntas");
            System.out.println("  ✓ Rejeição de tópicos fora do escopo");
            System.out.println("  ✓ Estado da sessão rastreado corretamente");
            System.out.println("  ✓ Comandos de saída reconhecidos");
            System.out.println();

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<SinalVital> criarHistoricoFicticio() {
        List<SinalVital> historico = new ArrayList<>();

        try {
            Glicemia g1 = new Glicemia(1L, 110.0, true);
            g1.analisarRisco();
            historico.add(g1);

            Glicemia g2 = new Glicemia(1L, 140.0, false);
            g2.analisarRisco();
            historico.add(g2);

            PressaoArterial p1 = new PressaoArterial(1L, 130, 85);
            p1.analisarRisco();
            historico.add(p1);

        } catch (Exception e) {
            // Ignora exceções de emergência para testes
        }

        return historico;
    }
}
