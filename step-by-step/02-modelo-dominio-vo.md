# Fase 02 - Modelo de Domínio (Value Objects)

## 🎯 Objetivos
- Criar a classe abstrata `SinalVital`
- Implementar classes filhas (Glicemia, PressaoArterial, PesoCorporal)
- Criar a classe `Paciente`
- Aplicar encapsulamento e validações
- Criar exceções customizadas

## 📚 Conceitos OO Aplicados
- ✅ **Abstração**: SinalVital como conceito genérico
- ✅ **Herança**: Classes especializadas herdam de SinalVital
- ✅ **Encapsulamento**: Atributos privados com getters/setters
- ✅ **Exceções Customizadas**: Tratamento de erros de domínio

## 🔧 Implementação

### 1. Exceções Customizadas

Crie em `src/main/java/br/com/glicemia/model/exceptions/`:

#### ValorInvalidoException.java
```java
package br.com.glicemia.model.exceptions;

/**
 * Exceção lançada quando um valor inválido é fornecido
 * para um sinal vital (ex: pressão negativa, glicemia zero).
 */
public class ValorInvalidoException extends Exception {

    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }

    public ValorInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
```

#### RiscoEmergenciaException.java
```java
package br.com.glicemia.model.exceptions;

/**
 * Exceção lançada quando um sinal vital indica risco de vida imediato.
 * Esta exceção força o sistema a bloquear a chamada de IA e exibir alerta.
 */
public class RiscoEmergenciaException extends Exception {

    private final String nivelRisco;
    private final String protocolo;

    public RiscoEmergenciaException(String mensagem, String nivelRisco, String protocolo) {
        super(mensagem);
        this.nivelRisco = nivelRisco;
        this.protocolo = protocolo;
    }

    public String getNivelRisco() {
        return nivelRisco;
    }

    public String getProtocolo() {
        return protocolo;
    }
}
```

### 2. Enum de Nível de Risco

Crie `src/main/java/br/com/glicemia/model/vo/NivelRisco.java`:

```java
package br.com.glicemia.model.vo;

/**
 * Enumeração dos níveis de risco para sinais vitais.
 */
public enum NivelRisco {
    NORMAL("Normal", "Verde"),
    ATENCAO("Atenção", "Amarelo"),
    ALTO("Alto", "Laranja"),
    CRITICO("Crítico - Emergência", "Vermelho");

    private final String descricao;
    private final String cor;

    NivelRisco(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }

    public boolean isEmergencia() {
        return this == CRITICO;
    }
}
```

### 3. Classe Abstrata SinalVital

Crie `src/main/java/br/com/glicemia/model/vo/SinalVital.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import java.time.LocalDateTime;

/**
 * Classe abstrata que representa um sinal vital genérico.
 * Nenhuma instância direta pode ser criada, pois "sinal vital"
 * é um conceito abstrato - você sempre mede algo específico.
 */
public abstract class SinalVital {

    private Long idRegistro;
    private Long idPaciente;
    private LocalDateTime dataHora;
    private String unidadeMedida;
    private String observacoes;

    /**
     * Construtor base para todos os sinais vitais.
     * @param idPaciente ID do paciente que realizou a medição
     * @param unidadeMedida Unidade de medida (mg/dL, mmHg, kg, etc)
     */
    public SinalVital(Long idPaciente, String unidadeMedida) {
        this.idPaciente = idPaciente;
        this.dataHora = LocalDateTime.now();
        this.unidadeMedida = unidadeMedida;
    }

    // Getters e Setters com validação

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    /**
     * Método abstrato que força cada sinal vital a implementar
     * sua própria lógica de validação.
     * @throws ValorInvalidoException se o valor não for válido
     */
    protected abstract void validar() throws ValorInvalidoException;

    /**
     * Retorna uma descrição legível do sinal vital.
     * @return String formatada com os dados da medição
     */
    public abstract String getDescricao();
}
```

### 4. Classe Glicemia

Crie `src/main/java/br/com/glicemia/model/vo/Glicemia.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

/**
 * Representa uma medição de glicemia (açúcar no sangue).
 * Valores de referência:
 * - Jejum: Normal < 100 mg/dL
 * - Jejum: Pré-diabetes 100-125 mg/dL
 * - Jejum: Diabetes >= 126 mg/dL
 * - Hipoglicemia severa: < 50 mg/dL (EMERGÊNCIA)
 */
public class Glicemia extends SinalVital {

    private double valorGlicemia;
    private boolean emJejum;
    private String tipoInsulina;

    public Glicemia(Long idPaciente, double valorGlicemia, boolean emJejum)
            throws ValorInvalidoException {
        super(idPaciente, "mg/dL");
        this.valorGlicemia = valorGlicemia;
        this.emJejum = emJejum;
        validar();
    }

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

    // Getters e Setters

    public double getValorGlicemia() {
        return valorGlicemia;
    }

    public void setValorGlicemia(double valorGlicemia) throws ValorInvalidoException {
        this.valorGlicemia = valorGlicemia;
        validar();
    }

    public boolean isEmJejum() {
        return emJejum;
    }

    public void setEmJejum(boolean emJejum) {
        this.emJejum = emJejum;
    }

    public String getTipoInsulina() {
        return tipoInsulina;
    }

    public void setTipoInsulina(String tipoInsulina) {
        this.tipoInsulina = tipoInsulina;
    }

    @Override
    public String getDescricao() {
        return String.format("Glicemia: %.1f %s (%s)",
            valorGlicemia,
            getUnidadeMedida(),
            emJejum ? "Jejum" : "Pós-prandial"
        );
    }
}
```

### 5. Classe PressaoArterial

Crie `src/main/java/br/com/glicemia/model/vo/PressaoArterial.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

/**
 * Representa uma medição de pressão arterial.
 * Valores de referência (adultos):
 * - Normal: < 120/80 mmHg
 * - Pré-hipertensão: 120-139 / 80-89 mmHg
 * - Hipertensão Estágio 1: 140-159 / 90-99 mmHg
 * - Hipertensão Estágio 2: >= 160/100 mmHg
 * - Crise Hipertensiva: >= 180/120 mmHg (EMERGÊNCIA)
 */
public class PressaoArterial extends SinalVital {

    private int sistolica;  // Pressão máxima
    private int diastolica; // Pressão mínima

    public PressaoArterial(Long idPaciente, int sistolica, int diastolica)
            throws ValorInvalidoException {
        super(idPaciente, "mmHg");
        this.sistolica = sistolica;
        this.diastolica = diastolica;
        validar();
    }

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

    // Getters e Setters

    public int getSistolica() {
        return sistolica;
    }

    public void setSistolica(int sistolica) throws ValorInvalidoException {
        this.sistolica = sistolica;
        validar();
    }

    public int getDiastolica() {
        return diastolica;
    }

    public void setDiastolica(int diastolica) throws ValorInvalidoException {
        this.diastolica = diastolica;
        validar();
    }

    @Override
    public String getDescricao() {
        return String.format("Pressão Arterial: %d/%d %s",
            sistolica,
            diastolica,
            getUnidadeMedida()
        );
    }
}
```

### 6. Classe PesoCorporal

Crie `src/main/java/br/com/glicemia/model/vo/PesoCorporal.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;

/**
 * Representa uma medição de peso corporal.
 * Calcula automaticamente o IMC se a altura estiver disponível.
 * Classificação IMC:
 * - Abaixo do peso: < 18.5
 * - Normal: 18.5 - 24.9
 * - Sobrepeso: 25 - 29.9
 * - Obesidade Grau I: 30 - 34.9
 * - Obesidade Grau II: 35 - 39.9
 * - Obesidade Grau III: >= 40
 */
public class PesoCorporal extends SinalVital {

    private double peso;      // em kg
    private double altura;    // em metros
    private Double imc;       // calculado automaticamente

    public PesoCorporal(Long idPaciente, double peso, double altura)
            throws ValorInvalidoException {
        super(idPaciente, "kg");
        this.peso = peso;
        this.altura = altura;
        validar();
        calcularIMC();
    }

    @Override
    protected void validar() throws ValorInvalidoException {
        if (peso <= 0) {
            throw new ValorInvalidoException(
                "Peso deve ser um valor positivo. Valor fornecido: " + peso
            );
        }

        if (peso < 20 || peso > 300) {
            throw new ValorInvalidoException(
                "Peso fora da faixa esperada (20-300 kg). Verifique a medição."
            );
        }

        if (altura <= 0) {
            throw new ValorInvalidoException(
                "Altura deve ser um valor positivo. Valor fornecido: " + altura
            );
        }

        if (altura < 0.5 || altura > 2.5) {
            throw new ValorInvalidoException(
                "Altura fora da faixa esperada (0.5-2.5 m). Verifique a medição."
            );
        }
    }

    /**
     * Calcula o Índice de Massa Corporal (IMC).
     * Fórmula: IMC = peso / (altura²)
     */
    private void calcularIMC() {
        if (altura > 0) {
            this.imc = peso / (altura * altura);
        }
    }

    /**
     * Retorna a classificação do IMC.
     * @return String com a classificação
     */
    public String getClassificacaoIMC() {
        if (imc == null) return "Não disponível";

        if (imc < 18.5) return "Abaixo do peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidade Grau I";
        if (imc < 40) return "Obesidade Grau II";
        return "Obesidade Grau III";
    }

    // Getters e Setters

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) throws ValorInvalidoException {
        this.peso = peso;
        validar();
        calcularIMC();
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) throws ValorInvalidoException {
        this.altura = altura;
        validar();
        calcularIMC();
    }

    public Double getImc() {
        return imc;
    }

    @Override
    public String getDescricao() {
        return String.format("Peso: %.1f %s | Altura: %.2f m | IMC: %.1f (%s)",
            peso,
            getUnidadeMedida(),
            altura,
            imc != null ? imc : 0.0,
            getClassificacaoIMC()
        );
    }
}
```

### 7. Classe Paciente

Crie `src/main/java/br/com/glicemia/model/vo/Paciente.java`:

```java
package br.com.glicemia.model.vo;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import java.time.LocalDate;
import java.time.Period;

/**
 * Representa um paciente no sistema GlicemIA.
 */
public class Paciente {

    private Long idPaciente;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;
    private LocalDate dataCadastro;

    public Paciente(String nome, String cpf, LocalDate dataNascimento)
            throws ValorInvalidoException {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = LocalDate.now();
        validar();
    }

    /**
     * Valida os dados do paciente.
     * @throws ValorInvalidoException se algum dado for inválido
     */
    private void validar() throws ValorInvalidoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValorInvalidoException("Nome do paciente é obrigatório");
        }

        if (cpf == null || !validarCPF(cpf)) {
            throw new ValorInvalidoException("CPF inválido: " + cpf);
        }

        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new ValorInvalidoException("Data de nascimento inválida");
        }
    }

    /**
     * Valida o formato básico do CPF (apenas dígitos e tamanho).
     * Nota: Para produção, implemente validação completa com dígitos verificadores.
     * @param cpf CPF a ser validado
     * @return true se o formato é válido
     */
    private boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return cpfLimpo.length() == 11;
    }

    /**
     * Calcula a idade do paciente.
     * @return idade em anos
     */
    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    // Getters e Setters

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws ValorInvalidoException {
        this.nome = nome;
        validar();
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) throws ValorInvalidoException {
        this.dataNascimento = dataNascimento;
        validar();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    @Override
    public String toString() {
        return String.format("Paciente: %s | CPF: %s | Idade: %d anos",
            nome,
            cpf,
            getIdade()
        );
    }
}
```

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ ] Exceção `ValorInvalidoException` criada
- [ ] Exceção `RiscoEmergenciaException` criada
- [ ] Enum `NivelRisco` criado
- [ ] Classe abstrata `SinalVital` criada
- [ ] Classe `Glicemia` implementada e testada
- [ ] Classe `PressaoArterial` implementada e testada
- [ ] Classe `PesoCorporal` implementada e testada
- [ ] Classe `Paciente` implementada e testada
- [ ] Todas as classes compilam sem erros
- [ ] Validações funcionam corretamente

## 🧪 Testes Manuais Rápidos

Crie `TestModelo.java` para testar:

```java
public class TestModelo {
    public static void main(String[] args) {
        try {
            // Teste 1: Glicemia válida
            Glicemia g1 = new Glicemia(1L, 95.0, true);
            System.out.println("✓ " + g1.getDescricao());

            // Teste 2: Glicemia inválida (deve lançar exceção)
            try {
                Glicemia g2 = new Glicemia(1L, -10.0, true);
            } catch (ValorInvalidoException e) {
                System.out.println("✓ Exceção capturada: " + e.getMessage());
            }

            // Teste 3: Pressão válida
            PressaoArterial p1 = new PressaoArterial(1L, 120, 80);
            System.out.println("✓ " + p1.getDescricao());

            // Teste 4: Peso e IMC
            PesoCorporal peso = new PesoCorporal(1L, 70.0, 1.75);
            System.out.println("✓ " + peso.getDescricao());

            // Teste 5: Paciente
            Paciente paciente = new Paciente("João Silva", "12345678901",
                LocalDate.of(1990, 5, 15));
            System.out.println("✓ " + paciente);

        } catch (Exception e) {
            System.err.println("✗ Erro: " + e.getMessage());
        }
    }
}
```

## 📌 Próximos Passos

Após validar todos os itens:

1. Próxima fase: **[Fase 03 - Interfaces e Polimorfismo](./03-interfaces-polimorfismo.md)**
2. Nela, adicionaremos a interface `Diagnosticavel` e o método `analisarRisco()`

---

**Conceitos implementados**: Abstração ✅ | Herança ✅ | Encapsulamento ✅ | Exceções ✅
