package br.com.glicemia;

import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.exceptions.*;
import br.com.glicemia.util.AlertaEmergencia;

public class TestAlertas {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     Teste do Sistema de Alertas            ║");
        System.out.println("╚════════════════════════════════════════════╝");

        try {
            AlertaEmergencia.exibirCabecalho("Teste 1: Glicemia Normal");
            Glicemia normal = new Glicemia(1L, 95.0, true);
            normal.analisarRisco();
            AlertaEmergencia.exibirAlerta(normal);

            Thread.sleep(2000);

            AlertaEmergencia.exibirCabecalho("Teste 2: Glicemia em Atenção");
            Glicemia atencao = new Glicemia(1L, 110.0, true);
            atencao.analisarRisco();
            AlertaEmergencia.exibirAlerta(atencao);

            Thread.sleep(2000);

            AlertaEmergencia.exibirCabecalho("Teste 3: Hipertensão Estágio 1");
            PressaoArterial alto = new PressaoArterial(1L, 150, 95);
            alto.analisarRisco();
            AlertaEmergencia.exibirAlerta(alto);

            Thread.sleep(2000);

            AlertaEmergencia.exibirCabecalho("Teste 4: Hipoglicemia Severa");
            try {
                Glicemia critico = new Glicemia(1L, 45.0, true);
                critico.analisarRisco();

            } catch (RiscoEmergenciaException e) {
                Glicemia critico = new Glicemia(1L, 45.0, true);
                critico.setNivelRiscoFromDB(br.com.glicemia.model.NivelRisco.CRITICO);
                AlertaEmergencia.exibirAlertaCritico(critico);
                AlertaEmergencia.exibirSistemaBloqueado();
                AlertaEmergencia.exibirProtocoloEmergencia(e.getProtocolo());
            }

            Thread.sleep(2000);

            AlertaEmergencia.exibirCabecalho("Teste 5: Crise Hipertensiva");
            try {
                PressaoArterial criticoPressao = new PressaoArterial(1L, 190, 125);
                criticoPressao.analisarRisco();

            } catch (RiscoEmergenciaException e) {
                PressaoArterial criticoPressao = new PressaoArterial(1L, 190, 125);
                criticoPressao.setNivelRiscoFromDB(br.com.glicemia.model.NivelRisco.CRITICO);
                AlertaEmergencia.exibirAlertaCritico(criticoPressao);
                AlertaEmergencia.exibirSistemaBloqueado();
                AlertaEmergencia.exibirProtocoloEmergencia(e.getProtocolo());
            }

            Thread.sleep(2000);

            AlertaEmergencia.exibirCabecalho("Teste 6: Mensagens Utilitárias");
            AlertaEmergencia.exibirSucesso("Registro salvo com sucesso!");
            AlertaEmergencia.exibirAviso("Este é um aviso de teste");
            AlertaEmergencia.exibirErro("Este é um erro de teste");

            System.out.println("\n ✅ Todos os testes da fase 06 passaram com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
