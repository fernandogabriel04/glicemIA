# Fase 03 - Interfaces e Polimorfismo

## 🎯 Objetivos
- Criar a interface `Diagnosticavel`
- Implementar o método polimórfico `analisarRisco()`
- Aplicar classificação de risco específica para cada sinal vital
- Demonstrar polimorfismo na prática

## 📚 Conceitos OO Aplicados
- ✅ **Interface**: Contrato que obriga implementação de métodos
- ✅ **Polimorfismo**: Mesmo método, comportamentos diferentes
- ✅ **Composição**: SinalVital implementa Diagnosticavel

## 🔧 Implementação

### 1. Interface Diagnosticavel

Crie `src/main/java/br/com/glicemia/model/interfaces/Diagnosticavel.java`:

```java
package br.com.glicemia.model.interfaces;

import br.com.glicemia.model.vo.NivelRisco;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;

/**
 * Interface que define o contrato para qualquer entidade
 * que possa ser diagnosticada quanto ao nível de risco à saúde.
 *
 * Esta interface é o coração do polimorfismo no sistema:
 * - Cada sinal vital implementa sua própria lógica de análise
 * - O sistema não precisa saber qual tipo de sinal está analisando
 * - Basta chamar analisarRisco() e deixar o objeto se autoavaliar
 */
public interface Diagnosticavel {

    /**
     * Analisa o risco do sinal vital com base em tabelas clínicas.
     *
     * @return NivelRisco classificado (NORMAL, ATENCAO, ALTO, CRITICO)
     * @throws RiscoEmergenciaException se detectar risco de vida imediato
     */
    NivelRisco analisarRisco() throws RiscoEmergenciaException;

    /**
     * Retorna uma recomendação imediata baseada no nível de risco.
     *
     * @return String com a recomendação
     */
    String getRecomendacaoImediata();

    /**
     * Verifica se o caso requer intervenção de emergência.
     *
     * @return true se for uma emergência médica
     */
    boolean isEmergencia();
}
```

### 2. Atualizar SinalVital para implementar Diagnosticavel

Modifique `SinalVital.java` para adicionar a interface:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.interfaces.Diagnosticavel;
import java.time.LocalDateTime;

/**
 * Classe abstrata que representa um sinal vital genérico.
 * Implementa Diagnosticavel, obrigando todas as filhas a
 * implementarem a lógica de análise de risco.
 */
public abstract class SinalVital implements Diagnosticavel {

    private Long idRegistro;
    private Long idPaciente;
    private LocalDateTime dataHora;
    private String unidadeMedida;
    private String observacoes;
    private NivelRisco nivelRisco;  // Armazena o último risco calculado

    public SinalVital(Long idPaciente, String unidadeMedida) {
        this.idPaciente = idPaciente;
        this.dataHora = LocalDateTime.now();
        this.unidadeMedida = unidadeMedida;
    }

    // Getters e Setters (mantém os existentes e adiciona)

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    protected void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    // Métodos existentes...
    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }
    public Long getIdPaciente() { return idPaciente; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    protected abstract void validar() throws ValorInvalidoException;
    public abstract String getDescricao();

    // Implementação padrão de isEmergencia()
    @Override
    public boolean isEmergencia() {
        return nivelRisco != null && nivelRisco.isEmergencia();
    }
}
```

### 3. Implementar analisarRisco() em Glicemia

Atualize `Glicemia.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class Glicemia extends SinalVital {

    private double valorGlicemia;
    private boolean emJejum;
    private String tipoInsulina;

    // Construtor existente...
    public Glicemia(Long idPaciente, double valorGlicemia, boolean emJejum)
            throws ValorInvalidoException {
        super(idPaciente, "mg/dL");
        this.valorGlicemia = valorGlicemia;
        this.emJejum = emJejum;
        validar();
    }

    /**
     * Analisa o risco da glicemia com base em diretrizes da SBD.
     *
     * Referências (Jejum):
     * - Normal: 70-99 mg/dL
     * - Pré-diabetes: 100-125 mg/dL
     * - Diabetes: >= 126 mg/dL
     * - Hipoglicemia severa: < 50 mg/dL (EMERGÊNCIA)
     * - Hiperglicemia severa: >= 300 mg/dL (EMERGÊNCIA)
     */
    @Override
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        NivelRisco risco;

        // EMERGÊNCIAS (Hipoglicemia severa)
        if (valorGlicemia < 50) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "HIPOGLICEMIA SEVERA detectada: " + valorGlicemia + " mg/dL",
                "CRITICO",
                "1. Ingerir 15g de carboidrato simples (3 balas, 1 colher de açúcar)\n" +
                "2. Aguardar 15 minutos e medir novamente\n" +
                "3. Se < 70 mg/dL após 15min, procure emergência IMEDIATAMENTE"
            );
        }

        // EMERGÊNCIAS (Hiperglicemia severa)
        if (valorGlicemia >= 300) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "HIPERGLICEMIA SEVERA detectada: " + valorGlicemia + " mg/dL",
                "CRITICO",
                "1. Beber água imediatamente\n" +
                "2. NÃO se exercitar\n" +
                "3. Procurar atendimento médico URGENTE\n" +
                "4. Risco de cetoacidose diabética"
            );
        }

        // Análise para glicemia em jejum
        if (emJejum) {
            if (valorGlicemia < 70) {
                risco = NivelRisco.ALTO;
            } else if (valorGlicemia <= 99) {
                risco = NivelRisco.NORMAL;
            } else if (valorGlicemia <= 125) {
                risco = NivelRisco.ATENCAO;
            } else {
                risco = NivelRisco.ALTO;
            }
        }
        // Análise para glicemia pós-prandial (2h após refeição)
        else {
            if (valorGlicemia < 70) {
                risco = NivelRisco.ALTO;
            } else if (valorGlicemia <= 140) {
                risco = NivelRisco.NORMAL;
            } else if (valorGlicemia <= 199) {
                risco = NivelRisco.ATENCAO;
            } else {
                risco = NivelRisco.ALTO;
            }
        }

        setNivelRisco(risco);
        return risco;
    }

    @Override
    public String getRecomendacaoImediata() {
        if (getNivelRisco() == null) {
            return "Análise de risco não realizada";
        }

        switch (getNivelRisco()) {
            case NORMAL:
                return "Glicemia dentro dos valores normais. Continue monitorando.";

            case ATENCAO:
                if (emJejum) {
                    return "Glicemia de jejum elevada. Consulte seu médico para avaliação.";
                } else {
                    return "Glicemia pós-prandial elevada. Revise sua alimentação.";
                }

            case ALTO:
                if (valorGlicemia < 70) {
                    return "Hipoglicemia detectada. Ingira carboidratos simples imediatamente.";
                } else {
                    return "Hiperglicemia detectada. Evite doces, beba água e consulte seu médico.";
                }

            case CRITICO:
                return "EMERGÊNCIA MÉDICA. Siga o protocolo de emergência.";

            default:
                return "Status desconhecido";
        }
    }

    // Métodos existentes (getters, setters, validar, getDescricao)...
    @Override
    protected void validar() throws ValorInvalidoException {
        if (valorGlicemia <= 0) {
            throw new ValorInvalidoException(
                "Glicemia deve ser um valor positivo. Valor fornecido: " + valorGlicemia
            );
        }
        if (valorGlicemia > 600) {
            throw new ValorInvalidoException(
                "Valor de glicemia extremamente alto (>600 mg/dL). Verifique a medição."
            );
        }
    }

    public double getValorGlicemia() { return valorGlicemia; }
    public void setValorGlicemia(double valorGlicemia) throws ValorInvalidoException {
        this.valorGlicemia = valorGlicemia;
        validar();
    }
    public boolean isEmJejum() { return emJejum; }
    public void setEmJejum(boolean emJejum) { this.emJejum = emJejum; }
    public String getTipoInsulina() { return tipoInsulina; }
    public void setTipoInsulina(String tipoInsulina) { this.tipoInsulina = tipoInsulina; }

    @Override
    public String getDescricao() {
        return String.format("Glicemia: %.1f %s (%s)",
            valorGlicemia, getUnidadeMedida(), emJejum ? "Jejum" : "Pós-prandial"
        );
    }
}
```

### 4. Implementar analisarRisco() em PressaoArterial

Atualize `PressaoArterial.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class PressaoArterial extends SinalVital {

    private int sistolica;
    private int diastolica;

    // Construtor existente...
    public PressaoArterial(Long idPaciente, int sistolica, int diastolica)
            throws ValorInvalidoException {
        super(idPaciente, "mmHg");
        this.sistolica = sistolica;
        this.diastolica = diastolica;
        validar();
    }

    /**
     * Analisa o risco da pressão arterial baseado nas diretrizes
     * da Sociedade Brasileira de Cardiologia.
     *
     * Classificação:
     * - Normal: < 120/80 mmHg
     * - Pré-hipertensão: 120-139 / 80-89 mmHg
     * - Hipertensão Estágio 1: 140-159 / 90-99 mmHg
     * - Hipertensão Estágio 2: >= 160/100 mmHg
     * - Crise Hipertensiva: >= 180/120 mmHg (EMERGÊNCIA)
     */
    @Override
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        NivelRisco risco;

        // EMERGÊNCIA: Crise Hipertensiva
        if (sistolica >= 180 || diastolica >= 120) {
            risco = NivelRisco.CRITICO;
            setNivelRisco(risco);
            throw new RiscoEmergenciaException(
                "CRISE HIPERTENSIVA detectada: " + sistolica + "/" + diastolica + " mmHg",
                "CRITICO",
                "1. Sentar-se confortavelmente e respirar calmamente\n" +
                "2. NÃO dirigir\n" +
                "3. Procurar emergência IMEDIATAMENTE\n" +
                "4. Risco de AVC ou infarto"
            );
        }

        // Hipotensão severa
        if (sistolica < 90 || diastolica < 60) {
            risco = NivelRisco.ALTO;
        }
        // Normal
        else if (sistolica < 120 && diastolica < 80) {
            risco = NivelRisco.NORMAL;
        }
        // Pré-hipertensão
        else if (sistolica < 140 && diastolica < 90) {
            risco = NivelRisco.ATENCAO;
        }
        // Hipertensão Estágio 1
        else if (sistolica < 160 && diastolica < 100) {
            risco = NivelRisco.ALTO;
        }
        // Hipertensão Estágio 2
        else {
            risco = NivelRisco.ALTO;
        }

        setNivelRisco(risco);
        return risco;
    }

    @Override
    public String getRecomendacaoImediata() {
        if (getNivelRisco() == null) {
            return "Análise de risco não realizada";
        }

        switch (getNivelRisco()) {
            case NORMAL:
                return "Pressão arterial dentro dos valores normais.";

            case ATENCAO:
                return "Pré-hipertensão detectada. Adote hábitos saudáveis e monitore regularmente.";

            case ALTO:
                if (sistolica < 90) {
                    return "Hipotensão detectada. Hidrate-se e evite levantar-se bruscamente. Consulte médico.";
                } else {
                    return "Hipertensão detectada. Reduza sal, estresse e consulte seu médico.";
                }

            case CRITICO:
                return "EMERGÊNCIA MÉDICA. Procure atendimento imediato.";

            default:
                return "Status desconhecido";
        }
    }

    // Métodos existentes...
    @Override
    protected void validar() throws ValorInvalidoException {
        if (sistolica <= 0 || diastolica <= 0) {
            throw new ValorInvalidoException(
                "Pressão arterial deve ter valores positivos. Fornecido: " +
                sistolica + "/" + diastolica
            );
        }
        if (sistolica <= diastolica) {
            throw new ValorInvalidoException(
                "Pressão sistólica deve ser maior que diastólica. Fornecido: " +
                sistolica + "/" + diastolica
            );
        }
        if (sistolica > 300 || diastolica > 200) {
            throw new ValorInvalidoException(
                "Valores de pressão arterial extremamente altos. Verifique a medição."
            );
        }
    }

    public int getSistolica() { return sistolica; }
    public void setSistolica(int sistolica) throws ValorInvalidoException {
        this.sistolica = sistolica;
        validar();
    }
    public int getDiastolica() { return diastolica; }
    public void setDiastolica(int diastolica) throws ValorInvalidoException {
        this.diastolica = diastolica;
        validar();
    }

    @Override
    public String getDescricao() {
        return String.format("Pressão Arterial: %d/%d %s",
            sistolica, diastolica, getUnidadeMedida()
        );
    }
}
```

### 5. Implementar analisarRisco() em PesoCorporal

Atualize `PesoCorporal.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.exceptions.ValorInvalidoException;

public class PesoCorporal extends SinalVital {

    private double peso;
    private double altura;
    private Double imc;

    // Construtor e métodos existentes...
    public PesoCorporal(Long idPaciente, double peso, double altura)
            throws ValorInvalidoException {
        super(idPaciente, "kg");
        this.peso = peso;
        this.altura = altura;
        validar();
        calcularIMC();
    }

    /**
     * Analisa o risco baseado no IMC (Índice de Massa Corporal).
     *
     * Classificação OMS:
     * - Abaixo do peso: < 18.5 (risco de desnutrição)
     * - Normal: 18.5 - 24.9
     * - Sobrepeso: 25 - 29.9
     * - Obesidade Grau I: 30 - 34.9
     * - Obesidade Grau II: 35 - 39.9
     * - Obesidade Grau III: >= 40 (risco muito alto)
     */
    @Override
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        if (imc == null) {
            setNivelRisco(NivelRisco.NORMAL);
            return NivelRisco.NORMAL;
        }

        NivelRisco risco;

        if (imc < 16) {
            // Desnutrição severa - pode ser emergência
            risco = NivelRisco.ALTO;
        } else if (imc < 18.5) {
            // Abaixo do peso
            risco = NivelRisco.ATENCAO;
        } else if (imc < 25) {
            // Peso normal
            risco = NivelRisco.NORMAL;
        } else if (imc < 30) {
            // Sobrepeso
            risco = NivelRisco.ATENCAO;
        } else if (imc < 35) {
            // Obesidade Grau I
            risco = NivelRisco.ALTO;
        } else if (imc < 40) {
            // Obesidade Grau II
            risco = NivelRisco.ALTO;
        } else {
            // Obesidade Grau III (mórbida)
            risco = NivelRisco.ALTO;
        }

        setNivelRisco(risco);
        return risco;
    }

    @Override
    public String getRecomendacaoImediata() {
        if (getNivelRisco() == null || imc == null) {
            return "Análise de risco não realizada";
        }

        switch (getNivelRisco()) {
            case NORMAL:
                return "IMC dentro da faixa saudável. Mantenha hábitos saudáveis.";

            case ATENCAO:
                if (imc < 18.5) {
                    return "IMC abaixo do ideal. Consulte nutricionista para ganho de peso saudável.";
                } else {
                    return "Sobrepeso detectado. Adote alimentação balanceada e pratique exercícios.";
                }

            case ALTO:
                if (imc < 16) {
                    return "Desnutrição severa. Consulte médico URGENTEMENTE.";
                } else {
                    return "Obesidade detectada. Procure acompanhamento médico e nutricional.";
                }

            default:
                return "Status desconhecido";
        }
    }

    private void calcularIMC() {
        if (altura > 0) {
            this.imc = peso / (altura * altura);
        }
    }

    public String getClassificacaoIMC() {
        if (imc == null) return "Não disponível";
        if (imc < 18.5) return "Abaixo do peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidade Grau I";
        if (imc < 40) return "Obesidade Grau II";
        return "Obesidade Grau III";
    }

    // Métodos existentes...
    @Override
    protected void validar() throws ValorInvalidoException {
        if (peso <= 0) {
            throw new ValorInvalidoException("Peso deve ser um valor positivo. Valor fornecido: " + peso);
        }
        if (peso < 20 || peso > 300) {
            throw new ValorInvalidoException("Peso fora da faixa esperada (20-300 kg). Verifique a medição.");
        }
        if (altura <= 0) {
            throw new ValorInvalidoException("Altura deve ser um valor positivo. Valor fornecido: " + altura);
        }
        if (altura < 0.5 || altura > 2.5) {
            throw new ValorInvalidoException("Altura fora da faixa esperada (0.5-2.5 m). Verifique a medição.");
        }
    }

    public double getPeso() { return peso; }
    public void setPeso(double peso) throws ValorInvalidoException {
        this.peso = peso;
        validar();
        calcularIMC();
    }
    public double getAltura() { return altura; }
    public void setAltura(double altura) throws ValorInvalidoException {
        this.altura = altura;
        validar();
        calcularIMC();
    }
    public Double getImc() { return imc; }

    @Override
    public String getDescricao() {
        return String.format("Peso: %.1f %s | Altura: %.2f m | IMC: %.1f (%s)",
            peso, getUnidadeMedida(), altura,
            imc != null ? imc : 0.0, getClassificacaoIMC()
        );
    }
}
```

## 🎭 Demonstração do Polimorfismo

Crie `TestPolimorfismo.java` para demonstrar:

```java
import br.com.glicemia.model.vo.*;
import br.com.glicemia.model.interfaces.Diagnosticavel;
import br.com.glicemia.model.exceptions.*;
import java.util.ArrayList;
import java.util.List;

public class TestPolimorfismo {
    public static void main(String[] args) {
        System.out.println("=== Demonstração de Polimorfismo ===\n");

        // Lista de sinais vitais (POLIMORFISMO!)
        List<Diagnosticavel> sinaisVitais = new ArrayList<>();

        try {
            // Adicionando diferentes tipos de sinais vitais
            sinaisVitais.add(new Glicemia(1L, 95.0, true));
            sinaisVitais.add(new PressaoArterial(1L, 130, 85));
            sinaisVitais.add(new PesoCorporal(1L, 75.0, 1.70));

            // O MESMO código funciona para TODOS os tipos!
            for (Diagnosticavel sinal : sinaisVitais) {
                System.out.println("----------------------------------------");
                System.out.println("Sinal: " + ((SinalVital) sinal).getDescricao());

                try {
                    NivelRisco risco = sinal.analisarRisco();
                    System.out.println("Nível de Risco: " + risco.getDescricao());
                    System.out.println("Cor: " + risco.getCor());
                    System.out.println("Recomendação: " + sinal.getRecomendacaoImediata());

                } catch (RiscoEmergenciaException e) {
                    System.out.println("🚨 EMERGÊNCIA DETECTADA! 🚨");
                    System.out.println("Mensagem: " + e.getMessage());
                    System.out.println("Protocolo:\n" + e.getProtocolo());
                }
            }

            // Teste de emergência
            System.out.println("\n=== Testando Emergência ===");
            Glicemia emergencia = new Glicemia(1L, 45.0, true);
            emergencia.analisarRisco(); // Deve lançar exceção

        } catch (Exception e) {
            System.out.println("🚨 " + e.getMessage());
        }
    }
}
```

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ ] Interface `Diagnosticavel` criada com 3 métodos
- [ ] `SinalVital` atualizada para implementar `Diagnosticavel`
- [ ] `analisarRisco()` implementado em `Glicemia`
- [ ] `analisarRisco()` implementado em `PressaoArterial`
- [ ] `analisarRisco()` implementado em `PesoCorporal`
- [ ] `getRecomendacaoImediata()` implementado em todas as classes
- [ ] Emergências lançam `RiscoEmergenciaException` corretamente
- [ ] Teste de polimorfismo executado com sucesso
- [ ] Diferentes tipos se comportam de forma específica

## 🎯 Validação do Polimorfismo

Execute os testes e verifique:

1. ✅ Uma lista de `Diagnosticavel` aceita diferentes tipos de sinais vitais
2. ✅ O método `analisarRisco()` funciona para todos sem saber o tipo específico
3. ✅ Cada tipo aplica suas próprias regras de classificação
4. ✅ Exceções de emergência são lançadas corretamente

## 📌 Próximos Passos

Após validar todos os itens:

**Próxima fase**: **[Fase 04 - Camada DAO (Persistência)](./04-camada-dao.md)**

---

**Conceitos implementados**: Interface ✅ | Polimorfismo ✅ | Tratamento de Exceções ✅
