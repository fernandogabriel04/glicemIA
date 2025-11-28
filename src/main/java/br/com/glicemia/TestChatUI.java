package br.com.glicemia;

import br.com.glicemia.view.ChatIAView;
import br.com.glicemia.util.AlertaEmergencia;

/**
 * Teste App.
 *
 * Para testar:
 * 1. Execute este arquivo
 * 2. Experimente fazer perguntas válidas sobre diabetes/hipertensão
 * 3. Teste perguntas fora do escopo (futebol, clima, etc)
 * 4. Teste comandos de saída (voltar, sair)
 * 5. Verifique se o contexto é mantido entre perguntas
 */
public class TestChatUI {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        Teste Manual: Interface do Chat IA                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // ID de paciente de teste (usar 1L se existir no banco)
            Long idPaciente = 1L;

            System.out.println("Iniciando chat para paciente ID: " + idPaciente);
            System.out.println();

            // Inicia interface
            ChatIAView chat = new ChatIAView();
            chat.iniciar(idPaciente);

            System.out.println();
            AlertaEmergencia.exibirSucesso("✅ Teste concluído com sucesso!");
            System.out.println();

        } catch (Exception e) {
            AlertaEmergencia.exibirErro("Erro durante o teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
