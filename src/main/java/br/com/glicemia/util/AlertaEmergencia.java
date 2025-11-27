package br.com.glicemia.util;

import br.com.glicemia.model.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

public class AlertaEmergencia {

    private static final String RESET = "\u001B[0m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARELO = "\u001B[33m";
    private static final String LARANJA = "\u001B[38;5;208m";
    private static final String VERMELHO = "\u001B[31m";
    private static final String VERMELHO_BG = "\u001B[41m";
    private static final String BRANCO = "\u001B[37m";
    private static final String NEGRITO = "\u001B[1m";

    public static void exibirAlerta(SinalVital sinalVital) {
        NivelRisco risco = sinalVital.getNivelRisco();

        if (risco == null) {
            System.out.println("Sinal vital não analisado.");
            return;
        }

        switch (risco) {
            case NORMAL:
                exibirAlertaNormal(sinalVital);
                break;
            case ATENCAO:
                exibirAlertaAtencao(sinalVital);
                break;
            case ALTO:
                exibirAlertaAlto(sinalVital);
                break;
            case CRITICO:
                exibirAlertaCritico(sinalVital);
                break;
        }
    }

    private static void exibirAlertaNormal(SinalVital sinal) {
        System.out.println("\n" + VERDE + "┌────────────────────────────────────────────┐");
        System.out.println("│         ✓ RESULTADO NORMAL                 │");
        System.out.println("└────────────────────────────────────────────┘" + RESET);
        System.out.println(sinal.getDescricao());
        System.out.println(VERDE + "Recomendação: " + RESET + sinal.getRecomendacaoImediata());
        System.out.println();
    }

    private static void exibirAlertaAtencao(SinalVital sinal) {
        System.out.println("\n" + AMARELO + "┌────────────────────────────────────────────┐");
        System.out.println("│         ⚠ ATENÇÃO NECESSÁRIA               │");
        System.out.println("└────────────────────────────────────────────┘" + RESET);
        System.out.println(sinal.getDescricao());
        System.out.println(AMARELO + "Recomendação: " + RESET + sinal.getRecomendacaoImediata());
        System.out.println();
    }

    private static void exibirAlertaAlto(SinalVital sinal) {
        System.out.println("\n" + LARANJA + "┌────────────────────────────────────────────┐");
        System.out.println("│         ⚠⚠ RISCO ALTO DETECTADO           │");
        System.out.println("└────────────────────────────────────────────┘" + RESET);
        System.out.println(NEGRITO + sinal.getDescricao() + RESET);
        System.out.println(LARANJA + "Recomendação: " + RESET + sinal.getRecomendacaoImediata());
        System.out.println(LARANJA + "\nConsulte um médico em breve." + RESET);
        System.out.println();
    }

    public static void exibirAlertaCritico(SinalVital sinal) {
        System.out.println("\n" + VERMELHO_BG + BRANCO + NEGRITO);
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║                                            ║");
        System.out.println("║      🚨 EMERGÊNCIA MÉDICA DETECTADA 🚨     ║");
        System.out.println("║                                            ║");
        System.out.println("╚════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(VERMELHO + NEGRITO + "SITUAÇÃO CRÍTICA:" + RESET);
        System.out.println("  " + sinal.getDescricao());
        System.out.println();
        System.out.println(VERMELHO + NEGRITO + "AÇÃO IMEDIATA NECESSÁRIA:" + RESET);
        System.out.println("  " + sinal.getRecomendacaoImediata());
        System.out.println();
    }

    public static void exibirProtocoloEmergencia(String protocolo) {
        System.out.println(VERMELHO + NEGRITO + "╔════════════════════════════════════════════╗");
        System.out.println("║        PROTOCOLO DE EMERGÊNCIA             ║");
        System.out.println("╚════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(protocolo);
        System.out.println();
        System.out.println(VERMELHO_BG + BRANCO + " IMPORTANTE: NÃO AGUARDE. PROCURE ATENDIMENTO MÉDICO IMEDIATAMENTE. " + RESET);
        System.out.println();
    }

    public static void exibirSistemaBloqueado() {
        System.out.println(VERMELHO + "┌────────────────────────────────────────────┐");
        System.out.println("│  🚫 SISTEMA DE IA BLOQUEADO                │");
        System.out.println("│                                            │");
        System.out.println("│  A consulta à IA foi bloqueada devido à   │");
        System.out.println("│  detecção de risco de vida.               │");
        System.out.println("│                                            │");
        System.out.println("│  PRIORIDADE: Atendimento médico imediato  │");
        System.out.println("└────────────────────────────────────────────┘" + RESET);
        System.out.println();
    }

    public static void exibirCabecalho(String titulo) {
        String linha = "═".repeat(titulo.length() + 4);
        System.out.println("\n" + NEGRITO + "╔" + linha + "╗");
        System.out.println("║  " + titulo + "  ║");
        System.out.println("╚" + linha + "╝" + RESET + "\n");
    }

    public static void exibirSucesso(String mensagem) {
        System.out.println(VERDE + "✓ " + mensagem + RESET);
    }

    public static void exibirErro(String mensagem) {
        System.out.println(VERMELHO + "✗ ERRO: " + mensagem + RESET);
    }

    public static void exibirAviso(String mensagem) {
        System.out.println(AMARELO + "⚠ " + mensagem + RESET);
    }
}
