# Fase 09 - Testes Unitários

## 🎯 Objetivos
- Criar testes unitários com JUnit 5
- Alcançar cobertura mínima de 80%
- Testar lógica de negócio isoladamente
- Validar regras de classificação de risco

## 📚 Conceitos Aplicados
- ✅ **JUnit 5**: Framework de testes
- ✅ **Assertions**: Verificações de comportamento
- ✅ **Test Driven**: Validação sistemática
- ✅ **Exceções**: Testes de fluxos alternativos

## 🔧 Implementação - Com modelos base para ideação de desenvolvimento

### 1. Testes do Modelo - GlicemiaTest

Crie `src/test/java/br/com/glicemia/model/GlicemiaTest.java`:

```java
package br.com.glicemia.model;

import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Glicemia;
import br.com.glicemia.model.vo.NivelRisco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Glicemia.
 */
class GlicemiaTest {

    @Test
    @DisplayName("Deve criar glicemia válida em jejum")
    void deveCriarGlicemiaValidaEmJejum() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 95.0, true);

        assertNotNull(glicemia);
        assertEquals(95.0, glicemia.getValorGlicemia());
        assertTrue(glicemia.isEmJejum());
        assertEquals("mg/dL", glicemia.getUnidadeMedida());
    }

    @Test
    @DisplayName("Deve lançar exceção para glicemia negativa")
    void deveLancarExcecaoParaGlicemiaNegativa() {
        assertThrows(ValorInvalidoException.class, () -> {
            new Glicemia(1L, -10.0, true);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para glicemia zero")
    void deveLancarExcecaoParaGlicemiaZero() {
        assertThrows(ValorInvalidoException.class, () -> {
            new Glicemia(1L, 0.0, true);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para glicemia extremamente alta")
    void deveLancarExcecaoParaGlicemiaExtremamenteAlta() {
        assertThrows(ValorInvalidoException.class, () -> {
            new Glicemia(1L, 650.0, true);
        });
    }

    @Test
    @DisplayName("Deve classificar glicemia normal em jejum como NORMAL")
    void deveClassificarGlicemiaNormalEmJejum() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 90.0, true);
        NivelRisco risco = glicemia.analisarRisco();

        assertEquals(NivelRisco.NORMAL, risco);
        assertFalse(glicemia.isEmergencia());
    }

    @Test
    @DisplayName("Deve classificar glicemia pré-diabetes como ATENCAO")
    void deveClassificarGlicemiaPreDiabetes() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 110.0, true);
        NivelRisco risco = glicemia.analisarRisco();

        assertEquals(NivelRisco.ATENCAO, risco);
        assertFalse(glicemia.isEmergencia());
    }

    @Test
    @DisplayName("Deve classificar glicemia diabetes como ALTO")
    void deveClassificarGlicemiaDiabetes() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 130.0, true);
        NivelRisco risco = glicemia.analisarRisco();

        assertEquals(NivelRisco.ALTO, risco);
        assertFalse(glicemia.isEmergencia());
    }

    @Test
    @DisplayName("Deve lançar RiscoEmergenciaException para hipoglicemia severa")
    void deveLancarExcecaoParaHipoglicemiaSevera() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 45.0, true);

        RiscoEmergenciaException exception = assertThrows(
            RiscoEmergenciaException.class,
            () -> glicemia.analisarRisco()
        );

        assertNotNull(exception.getProtocolo());
        assertTrue(exception.getMessage().contains("HIPOGLICEMIA SEVERA"));
        assertEquals("CRITICO", exception.getNivelRisco());
    }

    @Test
    @DisplayName("Deve lançar RiscoEmergenciaException para hiperglicemia severa")
    void deveLancarExcecaoParaHiperglicemiaSevera() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 350.0, true);

        RiscoEmergenciaException exception = assertThrows(
            RiscoEmergenciaException.class,
            () -> glicemia.analisarRisco()
        );

        assertNotNull(exception.getProtocolo());
        assertTrue(exception.getMessage().contains("HIPERGLICEMIA SEVERA"));
    }

    @Test
    @DisplayName("Deve retornar recomendação imediata para cada nível de risco")
    void deveRetornarRecomendacaoImediata() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 95.0, true);
        glicemia.analisarRisco();

        String recomendacao = glicemia.getRecomendacaoImediata();

        assertNotNull(recomendacao);
        assertFalse(recomendacao.isEmpty());
    }

    @Test
    @DisplayName("Deve formatar descrição corretamente")
    void deveFormatarDescricaoCorretamente() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 95.5, true);
        String descricao = glicemia.getDescricao();

        assertTrue(descricao.contains("95.5"));
        assertTrue(descricao.contains("mg/dL"));
        assertTrue(descricao.contains("Jejum"));
    }

    @Test
    @DisplayName("Deve permitir adicionar tipo de insulina")
    void devePermitirAdicionarTipoInsulina() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 100.0, true);
        glicemia.setTipoInsulina("NPH");

        assertEquals("NPH", glicemia.getTipoInsulina());
    }

    @Test
    @DisplayName("Deve permitir adicionar observações")
    void devePermitirAdicionarObservacoes() throws ValorInvalidoException {
        Glicemia glicemia = new Glicemia(1L, 100.0, true);
        glicemia.setObservacoes("Após exercício físico");

        assertEquals("Após exercício físico", glicemia.getObservacoes());
    }
}
```

### 2. Testes do Modelo - PressaoArterialTest

Crie `src/test/java/br/com/glicemia/model/PressaoArterialTest.java`:

```java
package br.com.glicemia.model;

import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.NivelRisco;
import br.com.glicemia.model.vo.PressaoArterial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PressaoArterialTest {

    @Test
    @DisplayName("Deve criar pressão arterial válida")
    void deveCriarPressaoArterialValida() throws ValorInvalidoException {
        PressaoArterial pressao = new PressaoArterial(1L, 120, 80);

        assertNotNull(pressao);
        assertEquals(120, pressao.getSistolica());
        assertEquals(80, pressao.getDiastolica());
    }

    @Test
    @DisplayName("Deve lançar exceção para pressão negativa")
    void deveLancarExcecaoParaPressaoNegativa() {
        assertThrows(ValorInvalidoException.class, () -> {
            new PressaoArterial(1L, -10, 80);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando sistólica <= diastólica")
    void deveLancarExcecaoQuandoSistolicaMenorOuIgualDiastolica() {
        assertThrows(ValorInvalidoException.class, () -> {
            new PressaoArterial(1L, 80, 120);
        });

        assertThrows(ValorInvalidoException.class, () -> {
            new PressaoArterial(1L, 100, 100);
        });
    }

    @Test
    @DisplayName("Deve classificar pressão normal como NORMAL")
    void deveClassificarPressaoNormal() throws Exception {
        PressaoArterial pressao = new PressaoArterial(1L, 110, 70);
        NivelRisco risco = pressao.analisarRisco();

        assertEquals(NivelRisco.NORMAL, risco);
    }

    @Test
    @DisplayName("Deve classificar pré-hipertensão como ATENCAO")
    void deveClassificarPreHipertensao() throws Exception {
        PressaoArterial pressao = new PressaoArterial(1L, 130, 85);
        NivelRisco risco = pressao.analisarRisco();

        assertEquals(NivelRisco.ATENCAO, risco);
    }

    @Test
    @DisplayName("Deve classificar hipertensão como ALTO")
    void deveClassificarHipertensao() throws Exception {
        PressaoArterial pressao = new PressaoArterial(1L, 150, 95);
        NivelRisco risco = pressao.analisarRisco();

        assertEquals(NivelRisco.ALTO, risco);
    }

    @Test
    @DisplayName("Deve lançar exceção para crise hipertensiva")
    void deveLancarExcecaoParaCriseHipertensiva() throws ValorInvalidoException {
        PressaoArterial pressao = new PressaoArterial(1L, 190, 125);

        assertThrows(RiscoEmergenciaException.class, () -> {
            pressao.analisarRisco();
        });
    }

    @Test
    @DisplayName("Deve detectar hipotensão como ALTO risco")
    void deveDetectarHipotensao() throws Exception {
        PressaoArterial pressao = new PressaoArterial(1L, 85, 55);
        NivelRisco risco = pressao.analisarRisco();

        assertEquals(NivelRisco.ALTO, risco);
    }
}
```

### 3. Testes do BO - GerenciadorRegistroBOTest

Crie `src/test/java/br/com/glicemia/bo/GerenciadorRegistroBOTest.java`:

```java
package br.com.glicemia.bo;

import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.Glicemia;
import br.com.glicemia.model.vo.NivelRisco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes do Business Object com Mock do DAO.
 */
class GerenciadorRegistroBOTest {

    private GerenciadorRegistroBO registroBO;
    private RegistroDAO mockDAO;

    @BeforeEach
    void setUp() {
        // Cria um mock do DAO
        mockDAO = Mockito.mock(RegistroDAO.class);
        registroBO = new GerenciadorRegistroBO(mockDAO);
    }

    @Test
    @DisplayName("Deve salvar glicemia normal e liberar para IA")
    void deveSalvarGlicemiaNormalELiberarIA() throws Exception {
        // Configura o mock para retornar ID
        when(mockDAO.inserir(any())).thenReturn(1L);

        Glicemia glicemia = new Glicemia(1L, 95.0, true);
        boolean liberadoIA = registroBO.registrarSinalVital(glicemia);

        // Verifica se salvou
        verify(mockDAO, times(1)).inserir(glicemia);

        // Verifica se liberou para IA
        assertTrue(liberadoIA);
        assertEquals(NivelRisco.NORMAL, glicemia.getNivelRisco());
    }

    @Test
    @DisplayName("Deve bloquear IA para glicemia crítica e NÃO salvar")
    void deveBLoquearIAParaGlicemiaCritica() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 45.0, true);

        // Deve lançar exceção de emergência
        assertThrows(RiscoEmergenciaException.class, () -> {
            registroBO.registrarSinalVital(glicemia);
        });

        // Verifica que NÃO salvou no banco
        verify(mockDAO, never()).inserir(any());
    }

    @Test
    @DisplayName("Deve retornar histórico vazio quando não há registros")
    void deveRetornarHistoricoVazio() throws Exception {
        when(mockDAO.buscarUltimosRegistros(1L, 10))
            .thenReturn(java.util.Collections.emptyList());

        var historico = registroBO.buscarHistoricoRecente(1L, 10);

        assertTrue(historico.isEmpty());
    }
}
```

### 4. Testes de Validação - PacienteTest

Crie `src/test/java/br/com/glicemia/model/PacienteTest.java`:

```java
package br.com.glicemia.model;

import br.com.glicemia.model.exceptions.ValorInvalidoException;
import br.com.glicemia.model.vo.Paciente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    @DisplayName("Deve criar paciente válido")
    void deveCriarPacienteValido() throws ValorInvalidoException {
        Paciente paciente = new Paciente(
            "João Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15)
        );

        assertNotNull(paciente);
        assertEquals("João Silva", paciente.getNome());
        assertEquals("12345678901", paciente.getCpf());
    }

    @Test
    @DisplayName("Deve calcular idade corretamente")
    void deveCalcularIdadeCorretamente() throws ValorInvalidoException {
        LocalDate nascimento = LocalDate.now().minusYears(30);
        Paciente paciente = new Paciente("Teste", "12345678901", nascimento);

        assertEquals(30, paciente.getIdade());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio")
    void deveLancarExcecaoParaNomeVazio() {
        assertThrows(ValorInvalidoException.class, () -> {
            new Paciente("", "12345678901", LocalDate.of(1990, 1, 1));
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF inválido")
    void deveLancarExcecaoParaCPFInvalido() {
        assertThrows(ValorInvalidoException.class, () -> {
            new Paciente("João", "123", LocalDate.of(1990, 1, 1));
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para data de nascimento futura")
    void deveLancarExcecaoParaDataFutura() {
        LocalDate futuro = LocalDate.now().plusDays(1);

        assertThrows(ValorInvalidoException.class, () -> {
            new Paciente("João", "12345678901", futuro);
        });
    }

    @Test
    @DisplayName("Deve aceitar CPF formatado")
    void deveAceitarCPFFormatado() throws ValorInvalidoException {
        Paciente paciente = new Paciente(
            "Maria",
            "123.456.789-01",
            LocalDate.of(1985, 3, 20)
        );

        assertNotNull(paciente);
    }
}
```

### 5. Suite de Testes

Crie `src/test/java/br/com/glicemia/AllTests.java`:

```java
package br.com.glicemia;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Suite que executa todos os testes do projeto.
 */
@Suite
@SelectPackages({"br.com.glicemia.model", "br.com.glicemia.bo"})
public class AllTests {
    // Classe vazia - anotações definem o comportamento
}
```

## 🧪 Executar os Testes

### Via Maven:
```bash
mvn test
```

### Via IDE:
- Eclipse: Botão direito no projeto → Run As → JUnit Test
- IntelliJ: Botão direito na pasta test → Run Tests

## ✅ Checklist de Validação

- [ ] Testes de Glicemia implementados (10+ testes)
- [ ] Testes de PressaoArterial implementados (7+ testes)
- [ ] Testes de Paciente implementados (6+ testes)
- [ ] Testes de BO com mocks implementados
- [ ] Todos os testes passam
- [ ] Cobertura de testes > 80%
- [ ] Testes de exceções validados
- [ ] Testes de validação de entrada funcionam

## 📊 Exemplo de Saída

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running br.com.glicemia.model.GlicemiaTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running br.com.glicemia.model.PressaoArterialTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running br.com.glicemia.model.PacienteTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running br.com.glicemia.bo.GerenciadorRegistroBOTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] -------------------------------------------------------
```

