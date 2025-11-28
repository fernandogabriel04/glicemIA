package br.com.glicemia;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.dao.impl.PacienteDAOImpl;
import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.model.vo.*;
import br.com.glicemia.service.GerenciadorIA;
import br.com.glicemia.service.ContextoIA;
import br.com.glicemia.util.AlertaEmergencia;
import java.time.LocalDate;
import java.util.List;

public class TestIA {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║           Teste do Sistema de IA           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Inicializar componentes
            PacienteDAOImpl pacienteDAO = new PacienteDAOImpl();
            RegistroDAOImpl registroDAO = new RegistroDAOImpl();
            GerenciadorPacienteBO pacienteBO = new GerenciadorPacienteBO(pacienteDAO);
            GerenciadorRegistroBO registroBO = new GerenciadorRegistroBO(registroDAO);
            GerenciadorIA gerenciadorIA = new GerenciadorIA();

            // Busca ou cria paciente
            Paciente paciente = pacienteDAO.buscarPorCPF("12345678901");
            Long idPaciente;

            if (paciente != null) {
                AlertaEmergencia.exibirSucesso("Usando paciente existente: " + paciente.getNome());
                idPaciente = paciente.getIdPaciente();
            } else {
                AlertaEmergencia.exibirAviso("Criando novo paciente para teste...");
                paciente = pacienteBO.cadastrarPaciente(
                    "João da Silva",
                    "12345678901",
                    LocalDate.of(1985, 5, 15),
                    "joao@email.com",
                    "11987654321"
                );
                idPaciente = paciente.getIdPaciente();
                AlertaEmergencia.exibirSucesso("Paciente criado com ID: " + idPaciente);
            }

            System.out.println();

            // TESTE 1: Contexto IA - Construção de Prompt
            AlertaEmergencia.exibirCabecalho("Teste 1: Construção de Contexto para IA");

            // Cria alguns registros de exemplo
            Glicemia g1 = new Glicemia(idPaciente, 110.0, true);
            Glicemia g2 = new Glicemia(idPaciente, 130.0, false);
            PressaoArterial p1 = new PressaoArterial(idPaciente, 130, 85);

            // Registra no banco
            registroBO.registrarSinalVital(g1);
            registroBO.registrarSinalVital(g2);
            registroBO.registrarSinalVital(p1);

            // Busca últimos registros
            List<SinalVital> historico = registroDAO.buscarUltimosRegistros(idPaciente, 5);
            AlertaEmergencia.exibirSucesso("Histórico carregado: " + historico.size() + " registros");

            // Constrói prompt
            String pergunta = "Como posso melhorar meu controle glicêmico?";
            String prompt = ContextoIA.construirPrompt(historico, pergunta);

            System.out.println("\n━━━ PROMPT GERADO ━━━");
            System.out.println(prompt);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━\n");

            // TESTE 2: Resumo Estatístico
            AlertaEmergencia.exibirCabecalho("Teste 2: Resumo Estatístico");

            String resumo = ContextoIA.criarResumoEstatistico(historico);
            System.out.println(resumo);
            System.out.println();

            // TESTE 3: Serviço de IA com Fallback
            AlertaEmergencia.exibirCabecalho("Teste 3: Consulta à IA");

            System.out.println("Provedor de IA ativo: " + gerenciadorIA.getProvedorAtivo());
            System.out.println("Modo Fallback: " + (gerenciadorIA.isFallbackAtivo() ? "Sim" : "Não"));
            System.out.println();

            System.out.println("Pergunta: " + pergunta);
            System.out.println();
            System.out.println("━━━ RESPOSTA DA IA ━━━");

            String resposta = gerenciadorIA.solicitarRecomendacao(historico, pergunta);
            System.out.println(resposta);

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━\n");

            // TESTE 4: Múltiplas Perguntas
            AlertaEmergencia.exibirCabecalho("Teste 4: Múltiplas Consultas");

            String[] perguntas = {
                "Quais alimentos devo evitar?",
                "Com que frequência devo medir minha glicemia?",
                "Que exercícios são recomendados para diabéticos?"
            };

            for (int i = 0; i < perguntas.length; i++) {
                System.out.println("\n► Pergunta " + (i + 1) + ": " + perguntas[i]);
                System.out.println();

                resposta = gerenciadorIA.solicitarRecomendacao(historico, perguntas[i]);
                System.out.println(resposta);
                System.out.println("\n" + "─".repeat(50));
            }

            // TESTE 5: Consulta sem Histórico
            AlertaEmergencia.exibirCabecalho("Teste 5: Consulta Sem Histórico");

            resposta = gerenciadorIA.solicitarRecomendacao(
                null,
                "Quais são os sinais de diabetes?"
            );
            System.out.println(resposta);
            System.out.println();

            // TESTE 6: Integração Completa - Fluxo Real
            AlertaEmergencia.exibirCabecalho("Teste 6: Fluxo Completo de Integração");

            System.out.println("1. Registrando novo sinal vital...");
            Glicemia novaGlicemia = new Glicemia(idPaciente, 115.0, true);
            boolean liberadoParaIA = registroBO.registrarSinalVital(novaGlicemia);

            AlertaEmergencia.exibirSucesso("Registro salvo com sucesso!");
            System.out.println("   Nível de Risco: " + novaGlicemia.getNivelRisco());
            System.out.println("   Liberado para IA: " + liberadoParaIA);

            System.out.println();
            System.out.println("2. Verificando se pode consultar IA...");

            if (liberadoParaIA) {
                AlertaEmergencia.exibirSucesso("✓ Liberado para consulta à IA");
                System.out.println();

                System.out.println("3. Buscando histórico atualizado...");
                List<SinalVital> historicoAtualizado = registroDAO.buscarUltimosRegistros(
                    idPaciente,
                    10
                );
                AlertaEmergencia.exibirSucesso("Histórico carregado: " +
                    historicoAtualizado.size() + " registros");
                System.out.println();

                System.out.println("4. Consultando IA com contexto completo...");
                String perguntaFinal = "Com base no meu histórico, estou controlando bem minha glicemia?";
                System.out.println("   Pergunta: " + perguntaFinal);
                System.out.println();

                resposta = gerenciadorIA.solicitarRecomendacao(
                    historicoAtualizado,
                    perguntaFinal
                );

                System.out.println("━━━ RESPOSTA FINAL ━━━");
                System.out.println(resposta);
                System.out.println("━━━━━━━━━━━━━━━━━━━━━");

            } else {
                AlertaEmergencia.exibirErro("✗ Consulta à IA bloqueada (risco crítico)");
                System.out.println("   Em casos de emergência, o sistema bloqueia a IA");
                System.out.println("   e exibe protocolo médico de emergência.");
            }

            System.out.println();
            AlertaEmergencia.exibirSucesso("\n✅ Todos os testes da Fase 07 foram concluídos!");
            System.out.println();

            System.out.println("📊 Resumo dos Testes:");
            System.out.println("  ✓ Construção de contexto/prompt");
            System.out.println("  ✓ Resumo estatístico do histórico");
            System.out.println("  ✓ Serviço de IA com fallback automático");
            System.out.println("  ✓ Múltiplas consultas sequenciais");
            System.out.println("  ✓ Consulta sem histórico");
            System.out.println("  ✓ Integração completa (BO + DAO + IA)");
            System.out.println();

            System.out.println("🔧 Provedor de IA utilizado: " + gerenciadorIA.getProvedorAtivo());
            System.out.println();

            if (gerenciadorIA.isFallbackAtivo() ||
                gerenciadorIA.getProvedorAtivo().contains("Local")) {
                System.out.println("💡 DICA: Para usar OpenAI:");
                System.out.println("   1. Edite o arquivo .env");
                System.out.println("   2. Altere IA_PROVIDER=local para IA_PROVIDER=openai");
                System.out.println("   3. Adicione sua chave em IA_API_KEY=sua_chave_aqui");
                System.out.println("   4. Execute novamente o teste");
            }

            System.out.println();

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
