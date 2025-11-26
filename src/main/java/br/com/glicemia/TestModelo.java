package br.com.glicemia;

import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import java.time.LocalDate;

public class TestModelo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║  Teste do Modelo de Domínio - Fase 02 ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        try {
            System.out.println("=== Teste 1: Glicemia válida ===");
            Glicemia g1 = new Glicemia(1L, 95.0, true);
            System.out.println("✓ " + g1.getDescricao());

            System.out.println("\n=== Teste 2: Glicemia inválida (deve lançar exceção) ===");
            try {
                Glicemia g2 = new Glicemia(1L, -10.0, true);
            } catch (ValorInvalidoException e) {
                System.out.println("✓ Exceção capturada: " + e.getMessage());
            }

            System.out.println("\n=== Teste 3: Pressão válida ===");
            PressaoArterial p1 = new PressaoArterial(1L, 120, 80);
            System.out.println("✓ " + p1.getDescricao());

            System.out.println("\n=== Teste 4: Pressão inválida (sistólica menor que diastólica) ===");
            try {
                PressaoArterial p2 = new PressaoArterial(1L, 80, 120);
            } catch (ValorInvalidoException e) {
                System.out.println("✓ Exceção capturada: " + e.getMessage());
            }

            System.out.println("\n=== Teste 5: Peso e IMC ===");
            PesoCorporal peso = new PesoCorporal(1L, 70.0, 1.75);
            System.out.println("✓ " + peso.getDescricao());

            System.out.println("\n=== Teste 6: Paciente ===");
            Paciente paciente = new Paciente("João Silva", "12345678901",
                LocalDate.of(1990, 5, 15));
            System.out.println("✓ " + paciente);

            System.out.println("\n=== Teste 7: Paciente com CPF inválido ===");
            try {
                Paciente p3 = new Paciente("Maria", "123", LocalDate.of(1995, 1, 1));
            } catch (ValorInvalidoException e) {
                System.out.println("✓ Exceção capturada: " + e.getMessage());
            }

            System.out.println("\n✅ Todos os testes da fase 02 passaram com sucesso!");

        } catch (Exception e) {
            System.err.println("✗ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
