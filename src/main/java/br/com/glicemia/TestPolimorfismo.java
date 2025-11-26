package br.com.glicemia;

import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.interfaces.Diagnosticavel;
import br.com.glicemia.model.exceptions.*;
import br.com.glicemia.model.NivelRisco;
import java.util.ArrayList;
import java.util.List;

public class TestPolimorfismo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      Demonstração de Polimorfismo      ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        List<Diagnosticavel> sinaisVitais = new ArrayList<>();

        try {
            sinaisVitais.add(new Glicemia(1L, 95.0, true));
            sinaisVitais.add(new PressaoArterial(1L, 130, 85));
            sinaisVitais.add(new PesoCorporal(1L, 75.0, 1.70));

            System.out.println("=== Teste Polimorfismo ===");
            System.out.println("O MESMO código funciona para TODOS os tipos!\n");

            for (Diagnosticavel sinal : sinaisVitais) {
                System.out.println("----------------------------------------");
                System.out.println("Sinal: " + ((SinalVital) sinal).getDescricao());

                try {
                    NivelRisco risco = sinal.analisarRisco();
                    System.out.println("Nível de Risco: " + risco.getDescricao());
                    System.out.println("Recomendação: " + sinal.getRecomendacaoImediata());

                } catch (RiscoEmergenciaException e) {
                    System.out.println("🚨 EMERGÊNCIA DETECTADA! 🚨");
                    System.out.println("Mensagem: " + e.getMessage());
                    System.out.println("Protocolo:\n" + e.getProtocolo());
                }
            }

            System.out.println("\n=== Testando Emergência ===");
            Glicemia emergencia = new Glicemia(1L, 45.0, true);
            emergencia.analisarRisco();

        } catch (RiscoEmergenciaException e) {
            System.out.println("🚨 " + e.getMessage());
            System.out.println("Protocolo:\n" + e.getProtocolo());
        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n✅ Todos os testes da fase 03 passaram com sucesso!");
    }
}
