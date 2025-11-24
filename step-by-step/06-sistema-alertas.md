# Fase 06 - Sistema de Alertas e Emergências

## 🎯 Objetivos
- Criar sistema visual de alertas por nível de risco
- Implementar protocolos de emergência
- Desenvolver formatação de saída para console
- Garantir visibilidade de situações críticas

## 📚 Conceitos Aplicados
- ✅ **Padrão Strategy**: Diferentes estratégias de alerta
- ✅ **Formatação de Saída**: Apresentação clara de informações críticas
- ✅ **UX de Segurança**: Destaque visual para emergências

## 🔧 Implementação

### 1. Classe AlertaEmergencia

Crie `src/main/java/br/com/glicemia/util/AlertaEmergencia.java`:

```java
package br.com.glicemia.util;

import br.com.glicemia.model.vo.NivelRisco;
import br.com.glicemia.model.vo.SinalVital;

/**
 * Classe responsável por formatar e exibir alertas de emergência.
 * Usa códigos ANSI para cores no console.
 */
public class AlertaEmergencia {

    // Códigos ANSI para cores
    private static final String RESET = "\u001B[0m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARELO = "\u001B[33m";
    private static final String LARANJA = "\u001B[38;5;208m";
    private static final String VERMELHO = "\u001B[31m";
    private static final String VERMELHO_BG = "\u001B[41m";
    private static final String BRANCO = "\u001B[37m";
    private static final String NEGRITO = "\u001B[1m";

    /**
     * Exibe um alerta formatado com base no nível de risco.
     * @param sinalVital Sinal vital a ser exibido
     */
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

    private static void exibirAlertaCritico(SinalVital sinal) {
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

    /**
     * Exibe o protocolo de emergência de forma destacada.
     * @param protocolo Texto do protocolo de emergência
     */
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

    /**
     * Exibe um banner de sistema bloqueado (quando IA não pode ser consultada).
     */
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

    /**
     * Exibe um cabeçalho formatado para seções.
     * @param titulo Título da seção
     */
    public static void exibirCabecalho(String titulo) {
        String linha = "═".repeat(titulo.length() + 4);
        System.out.println("\n" + NEGRITO + "╔" + linha + "╗");
        System.out.println("║  " + titulo + "  ║");
        System.out.println("╚" + linha + "╝" + RESET + "\n");
    }

    /**
     * Exibe uma mensagem de sucesso.
     * @param mensagem Mensagem a ser exibida
     */
    public static void exibirSucesso(String mensagem) {
        System.out.println(VERDE + "✓ " + mensagem + RESET);
    }

    /**
     * Exibe uma mensagem de erro.
     * @param mensagem Mensagem de erro
     */
    public static void exibirErro(String mensagem) {
        System.out.println(VERMELHO + "✗ ERRO: " + mensagem + RESET);
    }

    /**
     * Exibe uma mensagem de aviso.
     * @param mensagem Mensagem de aviso
     */
    public static void exibirAviso(String mensagem) {
        System.out.println(AMARELO + "⚠ " + mensagem + RESET);
    }
}
```

### 2. Classe ProtocoloEmergencia

Crie `src/main/java/br/com/glicemia/util/ProtocoloEmergencia.java`:

```java
package br.com.glicemia.util;

/**
 * Biblioteca de protocolos de emergência baseados em diretrizes médicas.
 */
public class ProtocoloEmergencia {

    public static final String HIPOGLICEMIA_SEVERA =
        "1. INGERIR IMEDIATAMENTE 15g de carboidrato simples:\n" +
        "   • 3 balas/jujubas\n" +
        "   • 1 colher de sopa de açúcar ou mel\n" +
        "   • 150ml de suco de laranja\n" +
        "   • 1/2 copo de refrigerante comum (não diet)\n" +
        "\n" +
        "2. AGUARDAR 15 MINUTOS e medir novamente\n" +
        "\n" +
        "3. Se ainda < 70 mg/dL:\n" +
        "   • Repetir o item 1\n" +
        "   • CHAMAR EMERGÊNCIA: SAMU 192\n" +
        "\n" +
        "4. Se inconsciente:\n" +
        "   • NÃO dar nada pela boca\n" +
        "   • Deitar de lado\n" +
        "   • CHAMAR 192 IMEDIATAMENTE\n" +
        "\n" +
        "⚠ RISCO: Convulsões, perda de consciência, coma";

    public static final String HIPERGLICEMIA_SEVERA =
        "1. BEBER ÁGUA IMEDIATAMENTE (pelo menos 500ml)\n" +
        "\n" +
        "2. NÃO SE EXERCITAR (pode piorar)\n" +
        "\n" +
        "3. Verificar presença de sintomas de cetoacidose:\n" +
        "   • Náusea ou vômito\n" +
        "   • Dor abdominal\n" +
        "   • Respiração rápida e profunda\n" +
        "   • Hálito com cheiro de frutas (cetônico)\n" +
        "   • Confusão mental\n" +
        "\n" +
        "4. PROCURAR EMERGÊNCIA IMEDIATAMENTE se:\n" +
        "   • Glicemia > 300 mg/dL por mais de 2 horas\n" +
        "   • Presença de sintomas acima\n" +
        "   • Incapaz de baixar a glicemia\n" +
        "\n" +
        "5. Chamar SAMU 192 se houver vômito persistente\n" +
        "\n" +
        "⚠ RISCO: Cetoacidose diabética (potencialmente fatal)";

    public static final String CRISE_HIPERTENSIVA =
        "1. SENTAR-SE CONFORTAVELMENTE\n" +
        "\n" +
        "2. RESPIRAR CALMAMENTE:\n" +
        "   • Inspire pelo nariz (4 segundos)\n" +
        "   • Prenda (2 segundos)\n" +
        "   • Expire pela boca (6 segundos)\n" +
        "\n" +
        "3. NÃO DIRIGIR\n" +
        "\n" +
        "4. Se tiver medicação de emergência prescrita, tomar agora\n" +
        "\n" +
        "5. PROCURAR EMERGÊNCIA IMEDIATAMENTE\n" +
        "\n" +
        "6. Chamar SAMU 192 se apresentar:\n" +
        "   • Dor no peito\n" +
        "   • Falta de ar severa\n" +
        "   • Dor de cabeça intensa\n" +
        "   • Visão turva\n" +
        "   • Confusão mental\n" +
        "   • Formigamento ou fraqueza em um lado do corpo\n" +
        "\n" +
        "⚠ RISCO: AVC (derrame), infarto, edema pulmonar";

    public static final String HIPOTENSAO_SEVERA =
        "1. DEITAR IMEDIATAMENTE com pernas elevadas\n" +
        "\n" +
        "2. Se possível, beber líquidos (água ou isotônico)\n" +
        "\n" +
        "3. NÃO levantar-se bruscamente\n" +
        "\n" +
        "4. Procurar ajuda se apresentar:\n" +
        "   • Tontura intensa\n" +
        "   • Visão escurecida\n" +
        "   • Sudorese fria\n" +
        "   • Náusea\n" +
        "   • Confusão mental\n" +
        "\n" +
        "5. CHAMAR 192 se:\n" +
        "   • Desmaio\n" +
        "   • Dor no peito\n" +
        "   • Respiração curta\n" +
        "\n" +
        "⚠ RISCO: Choque, queda com trauma, arritmias";

    /**
     * Retorna o protocolo apropriado com base no tipo de emergência.
     * @param tipoEmergencia Tipo de emergência detectada
     * @return Protocolo de emergência formatado
     */
    public static String obterProtocolo(String tipoEmergencia) {
        switch (tipoEmergencia.toUpperCase()) {
            case "HIPOGLICEMIA":
                return HIPOGLICEMIA_SEVERA;
            case "HIPERGLICEMIA":
                return HIPERGLICEMIA_SEVERA;
            case "HIPERTENSAO":
                return CRISE_HIPERTENSIVA;
            case "HIPOTENSAO":
                return HIPOTENSAO_SEVERA;
            default:
                return "Protocolo não disponível. PROCURE ATENDIMENTO MÉDICO IMEDIATAMENTE.";
        }
    }
}
```

## 🧪 Teste do Sistema de Alertas

Crie `TestAlertas.java`:

```java
import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.exceptions.*;
import br.com.glicemia.util.AlertaEmergencia;
import br.com.glicemia.util.ProtocoloEmergencia;

public class TestAlertas {
    public static void main(String[] args) {
        System.out.println("=== Teste do Sistema de Alertas ===");

        try {
            // Teste 1: Alerta Normal
            AlertaEmergencia.exibirCabecalho("Teste 1: Glicemia Normal");
            Glicemia normal = new Glicemia(1L, 95.0, true);
            normal.analisarRisco();
            AlertaEmergencia.exibirAlerta(normal);

            Thread.sleep(2000);

            // Teste 2: Alerta de Atenção
            AlertaEmergencia.exibirCabecalho("Teste 2: Glicemia em Atenção");
            Glicemia atencao = new Glicemia(1L, 110.0, true);
            atencao.analisarRisco();
            AlertaEmergencia.exibirAlerta(atencao);

            Thread.sleep(2000);

            // Teste 3: Alerta Alto
            AlertaEmergencia.exibirCabecalho("Teste 3: Hipertensão Estágio 1");
            PressaoArterial alto = new PressaoArterial(1L, 150, 95);
            alto.analisarRisco();
            AlertaEmergencia.exibirAlerta(alto);

            Thread.sleep(2000);

            // Teste 4: Emergência Crítica
            AlertaEmergencia.exibirCabecalho("Teste 4: Hipoglicemia Severa");
            try {
                Glicemia critico = new Glicemia(1L, 45.0, true);
                critico.analisarRisco();

            } catch (RiscoEmergenciaException e) {
                // Simula o que seria exibido na View
                Glicemia critico = new Glicemia(1L, 45.0, true);
                AlertaEmergencia.exibirAlertaCritico(critico);
                AlertaEmergencia.exibirSistemaBloqueado();
                AlertaEmergencia.exibirProtocoloEmergencia(e.getProtocolo());
            }

            // Teste 5: Mensagens utilitárias
            AlertaEmergencia.exibirCabecalho("Teste 5: Mensagens Utilitárias");
            AlertaEmergencia.exibirSucesso("Registro salvo com sucesso!");
            AlertaEmergencia.exibirAviso("Este é um aviso de teste");
            AlertaEmergencia.exibirErro("Este é um erro de teste");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ ] Classe `AlertaEmergencia` criada com formatação colorida
- [ ] Classe `ProtocoloEmergencia` com todos os protocolos
- [ ] Alerta NORMAL exibido em verde
- [ ] Alerta ATENÇÃO exibido em amarelo
- [ ] Alerta ALTO exibido em laranja
- [ ] Alerta CRÍTICO exibido com destaque vermelho
- [ ] Protocolo de emergência formatado corretamente
- [ ] Sistema de bloqueio da IA é visível
- [ ] Mensagens utilitárias funcionam

## 🎨 Exemplo Visual

Quando executar o teste, você deverá ver:

```
╔════════════════════════════════════════════╗
║                                            ║
║      🚨 EMERGÊNCIA MÉDICA DETECTADA 🚨     ║
║                                            ║
╚════════════════════════════════════════════╝

SITUAÇÃO CRÍTICA:
  Glicemia: 45.0 mg/dL (Jejum)

AÇÃO IMEDIATA NECESSÁRIA:
  [Recomendação específica]

╔════════════════════════════════════════════╗
║        PROTOCOLO DE EMERGÊNCIA             ║
╚════════════════════════════════════════════╝

[Protocolo detalhado com passos numerados]
```

## 📌 Próximos Passos

**Próxima fase**: **[Fase 07 - Integração com IA Generativa](./07-integracao-ia.md)**

---

**Conceitos implementados**: Sistema de Alertas ✅ | Protocolos de Segurança ✅ | UX ✅
