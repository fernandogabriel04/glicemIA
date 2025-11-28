# Fase 10 - Testes de Integração

## 🎯 Objetivos
- Criar testes de integração entre camadas
- Testar fluxo completo do sistema
- Validar integração DAO + BO + IA
- Usar mocks para dependências externas

## 📚 Conceitos Aplicados
- ✅ **Testes de Integração**: Múltiplas camadas
- ✅ **Mocks**: Simular banco e IA
- ✅ **Cenários Completos**: Fluxos reais de uso

## 🔧 Implementação

### 1. Teste de Integração Completo - Com modelos base para ideação de desenvolvimento

Crie `src/test/java/br/com/glicemia/integracao/FluxoCompletoTest.java`:

```java
package br.com.glicemia.integracao;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.dao.impl.PacienteDAOImpl;
import br.com.glicemia.dao.impl.RegistroDAOImpl;
import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.exceptions.RiscoEmergenciaException;
import br.com.glicemia.model.vo.Glicemia;
import br.com.glicemia.model.vo.Paciente;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes de integração do fluxo completo:
 * Cadastro de Paciente → Registro de Sinal Vital → Análise → Salvamento
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoCompletoTest {

    private GerenciadorPacienteBO pacienteBO;
    private GerenciadorRegistroBO registroBO;
    private PacienteDAO mockPacienteDAO;
    private RegistroDAO mockRegistroDAO;

    @BeforeEach
    void setUp() {
        // Cria mocks dos DAOs
        mockPacienteDAO = Mockito.mock(PacienteDAO.class);
        mockRegistroDAO = Mockito.mock(RegistroDAO.class);

        // Injeta mocks nos BOs
        pacienteBO = new GerenciadorPacienteBO(mockPacienteDAO);
        registroBO = new GerenciadorRegistroBO(mockRegistroDAO);
    }

    @Test
    @Order(1)
    @DisplayName("Fluxo 1: Cadastro de paciente + Registro normal")
    void fluxoCadastroPacienteERegistroNormal() throws Exception {
        // Simula que o CPF não existe
        when(mockPacienteDAO.buscarPorCPF(anyString())).thenReturn(null);

        // Simula inserção de paciente retornando ID 1
        when(mockPacienteDAO.inserir(any(Paciente.class))).thenReturn(1L);

        // Simula inserção de registro retornando ID 100
        when(mockRegistroDAO.inserir(any())).thenReturn(100L);

        // 1. Cadastra paciente
        Paciente paciente = pacienteBO.cadastrarPaciente(
            "João Teste",
            "12345678901",
            LocalDate.of(1985, 6, 10),
            "joao@teste.com",
            "(11) 99999-9999"
        );

        assertNotNull(paciente);
        assertEquals(1L, paciente.getIdPaciente());

        // 2. Registra glicemia normal
        Glicemia glicemia = new Glicemia(paciente.getIdPaciente(), 95.0, true);
        boolean liberadoIA = registroBO.registrarSinalVital(glicemia);

        // 3. Verifica que foi salvo e liberado para IA
        verify(mockPacienteDAO, times(1)).inserir(any(Paciente.class));
        verify(mockRegistroDAO, times(1)).inserir(any(Glicemia.class));
        assertTrue(liberadoIA);
    }

    @Test
    @Order(2)
    @DisplayName("Fluxo 2: Tentativa de cadastro com CPF duplicado")
    void fluxoCPFDuplicado() throws Exception {
        // Simula que já existe um paciente com o CPF
        Paciente pacienteExistente = new Paciente(
            "Maria Existente",
            "11111111111",
            LocalDate.of(1990, 1, 1)
        );
        when(mockPacienteDAO.buscarPorCPF("11111111111"))
            .thenReturn(pacienteExistente);

        // Tenta cadastrar com CPF duplicado
        Exception exception = assertThrows(Exception.class, () -> {
            pacienteBO.cadastrarPaciente(
                "João Novo",
                "11111111111",
                LocalDate.of(1995, 5, 5),
                null,
                null
            );
        });

        assertTrue(exception.getMessage().contains("CPF"));

        // Verifica que NÃO tentou inserir
        verify(mockPacienteDAO, never()).inserir(any());
    }

    @Test
    @Order(3)
    @DisplayName("Fluxo 3: Registro de emergência NÃO salva no banco")
    void fluxoEmergenciaNaoSalva() throws Exception {
        // Tenta registrar glicemia crítica
        Glicemia glicemiaCritica = new Glicemia(1L, 40.0, true);

        assertThrows(RiscoEmergenciaException.class, () -> {
            registroBO.registrarSinalVital(glicemiaCritica);
        });

        // Verifica que NÃO salvou no banco
        verify(mockRegistroDAO, never()).inserir(any());
    }

    @Test
    @Order(4)
    @DisplayName("Fluxo 4: Busca de histórico vazio")
    void fluxoHistoricoVazio() throws Exception {
        when(mockRegistroDAO.listarPorPaciente(1L))
            .thenReturn(java.util.Collections.emptyList());

        var historico = registroBO.listarTodosRegistros(1L);

        assertTrue(historico.isEmpty());
        verify(mockRegistroDAO, times(1)).listarPorPaciente(1L);
    }

    @Test
    @Order(5)
    @DisplayName("Fluxo 5: Múltiplos registros para mesmo paciente")
    void fluxoMultiplosRegistros() throws Exception {
        when(mockRegistroDAO.inserir(any())).thenReturn(100L, 101L, 102L);

        // Registra 3 glicemias
        Glicemia g1 = new Glicemia(1L, 90.0, true);
        Glicemia g2 = new Glicemia(1L, 105.0, true);
        Glicemia g3 = new Glicemia(1L, 98.0, true);

        registroBO.registrarSinalVital(g1);
        registroBO.registrarSinalVital(g2);
        registroBO.registrarSinalVital(g3);

        // Verifica que os 3 foram salvos
        verify(mockRegistroDAO, times(3)).inserir(any());
    }

    @Test
    @Order(6)
    @DisplayName("Fluxo 6: Tratamento de erro no banco de dados")
    void fluxoErroNoBanco() throws Exception {
        // Simula erro no banco
        when(mockRegistroDAO.inserir(any()))
            .thenThrow(new SQLException("Conexão perdida"));

        Glicemia glicemia = new Glicemia(1L, 95.0, true);

        assertThrows(SQLException.class, () -> {
            registroBO.registrarSinalVital(glicemia);
        });
    }
}
```

### 2. Teste de Integração com IA

Crie `src/test/java/br/com/glicemia/integracao/IntegracaoIATest.java`:

```java
package br.com.glicemia.integracao;

import br.com.glicemia.model.vo.Glicemia;
import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.ServicoIA;
import br.com.glicemia.service.impl.IALocalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração com o serviço de IA.
 */
class IntegracaoIATest {

    private ServicoIA servicoIA;

    @BeforeEach
    void setUp() {
        // Usa IA local para testes (não depende de API externa)
        servicoIA = new IALocalService();
    }

    @Test
    @DisplayName("Deve gerar recomendação com histórico vazio")
    void deveGerarRecomendacaoComHistoricoVazio() throws Exception {
        List<SinalVital> historicoVazio = new ArrayList<>();

        String resposta = servicoIA.solicitarRecomendacao(
            historicoVazio,
            "O que devo comer no jantar?"
        );

        assertNotNull(resposta);
        assertFalse(resposta.isEmpty());
        assertTrue(resposta.contains("histórico") || resposta.contains("registros"));
    }

    @Test
    @DisplayName("Deve gerar recomendação baseada em glicemia elevada")
    void deveGerarRecomendacaoParaGlicemiaElevada() throws Exception {
        List<SinalVital> historico = new ArrayList<>();

        // Adiciona glicemias elevadas
        Glicemia g1 = new Glicemia(1L, 140.0, true);
        Glicemia g2 = new Glicemia(1L, 155.0, true);
        historico.add(g2);
        historico.add(g1);

        String resposta = servicoIA.solicitarRecomendacao(
            historico,
            "Como melhorar minha glicemia?"
        );

        assertNotNull(resposta);
        assertTrue(resposta.toLowerCase().contains("glicemia") ||
                  resposta.toLowerCase().contains("alimentação") ||
                  resposta.toLowerCase().contains("carboidrato"));
    }

    @Test
    @DisplayName("Serviço IA local deve estar sempre disponível")
    void servicoLocalDeveEstarDisponivel() {
        assertTrue(servicoIA.isDisponivel());
    }

    @Test
    @DisplayName("Deve retornar nome do provedor")
    void deveRetornarNomeProvedor() {
        String provedor = servicoIA.getNomeProvedor();

        assertNotNull(provedor);
        assertFalse(provedor.isEmpty());
    }
}
```

### 3. Teste de Integração End-to-End (Simulado)

Crie `src/test/java/br/com/glicemia/integracao/EndToEndTest.java`:

```java
package br.com.glicemia.integracao;

import br.com.glicemia.bo.GerenciadorPacienteBO;
import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.dao.interfaces.PacienteDAO;
import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.vo.Glicemia;
import br.com.glicemia.model.vo.Paciente;
import br.com.glicemia.model.vo.SinalVital;
import br.com.glicemia.service.GerenciadorIA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Teste End-to-End simulado:
 * Paciente → Registro → Histórico → Consulta IA
 */
class EndToEndTest {

    private GerenciadorPacienteBO pacienteBO;
    private GerenciadorRegistroBO registroBO;
    private GerenciadorIA gerenciadorIA;
    private PacienteDAO mockPacienteDAO;
    private RegistroDAO mockRegistroDAO;

    @BeforeEach
    void setUp() {
        mockPacienteDAO = Mockito.mock(PacienteDAO.class);
        mockRegistroDAO = Mockito.mock(RegistroDAO.class);

        pacienteBO = new GerenciadorPacienteBO(mockPacienteDAO);
        registroBO = new GerenciadorRegistroBO(mockRegistroDAO);
        gerenciadorIA = new GerenciadorIA();
    }

    @Test
    @DisplayName("Cenário completo: Novo paciente diabético")
    void cenarioCompletoPacienteDiabetico() throws Exception {
        // ===== PASSO 1: Cadastrar paciente =====
        when(mockPacienteDAO.buscarPorCPF(anyString())).thenReturn(null);
        when(mockPacienteDAO.inserir(any(Paciente.class))).thenReturn(100L);

        Paciente paciente = pacienteBO.cadastrarPaciente(
            "Carlos Diabetes",
            "99988877766",
            LocalDate.of(1975, 8, 20),
            "carlos@email.com",
            null
        );

        assertEquals(100L, paciente.getIdPaciente());
        System.out.println("✓ Paciente cadastrado: " + paciente.getNome());

        // ===== PASSO 2: Registrar 3 medições de glicemia =====
        when(mockRegistroDAO.inserir(any())).thenReturn(1L, 2L, 3L);

        Glicemia g1 = new Glicemia(paciente.getIdPaciente(), 130.0, true);
        Glicemia g2 = new Glicemia(paciente.getIdPaciente(), 145.0, true);
        Glicemia g3 = new Glicemia(paciente.getIdPaciente(), 138.0, true);

        registroBO.registrarSinalVital(g1);
        registroBO.registrarSinalVital(g2);
        registroBO.registrarSinalVital(g3);

        verify(mockRegistroDAO, times(3)).inserir(any());
        System.out.println("✓ 3 registros de glicemia salvos");

        // ===== PASSO 3: Buscar histórico =====
        when(mockRegistroDAO.buscarUltimosRegistros(paciente.getIdPaciente(), 10))
            .thenReturn(List.of(g3, g2, g1));

        List<SinalVital> historico = registroBO.buscarHistoricoRecente(
            paciente.getIdPaciente(),
            10
        );

        assertEquals(3, historico.size());
        System.out.println("✓ Histórico recuperado: " + historico.size() + " registros");

        // ===== PASSO 4: Consultar IA =====
        String resposta = gerenciadorIA.solicitarRecomendacao(
            historico,
            "Como melhorar minha glicemia?"
        );

        assertNotNull(resposta);
        assertFalse(resposta.isEmpty());
        System.out.println("✓ IA consultada: " + resposta.substring(0, 50) + "...");

        // ===== PASSO 5: Gerar resumo estatístico =====
        when(mockRegistroDAO.listarPorPaciente(paciente.getIdPaciente()))
            .thenReturn(List.of(g1, g2, g3));

        String resumo = registroBO.gerarResumoEstatistico(paciente.getIdPaciente());

        assertTrue(resumo.contains("Total de Registros: 3"));
        System.out.println("✓ Resumo estatístico gerado");

        System.out.println("\n✅ Cenário End-to-End completado com sucesso!");
    }

    @Test
    @DisplayName("Cenário com falha: Sistema sem internet")
    void cenarioSemInternet() throws Exception {
        // Sistema usa fallback local automaticamente
        GerenciadorIA gerenciador = new GerenciadorIA();

        String resposta = gerenciador.solicitarRecomendacao(
            List.of(),
            "Dicas de alimentação?"
        );

        assertNotNull(resposta);
        System.out.println("✓ Fallback local funcionou: " + gerenciador.getProvedorAtivo());
    }
}
```

### 4. Teste de Performance Simples

Crie `src/test/java/br/com/glicemia/integracao/PerformanceTest.java`:

```java
package br.com.glicemia.integracao;

import br.com.glicemia.bo.GerenciadorRegistroBO;
import br.com.glicemia.dao.interfaces.RegistroDAO;
import br.com.glicemia.model.vo.Glicemia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes básicos de performance.
 */
class PerformanceTest {

    private GerenciadorRegistroBO registroBO;
    private RegistroDAO mockRegistroDAO;

    @BeforeEach
    void setUp() {
        mockRegistroDAO = Mockito.mock(RegistroDAO.class);
        registroBO = new GerenciadorRegistroBO(mockRegistroDAO);
    }

    @Test
    @DisplayName("Deve processar 100 registros em menos de 1 segundo")
    void deveProcessarMuitosRegistrosRapidamente() throws Exception {
        when(mockRegistroDAO.inserir(any())).thenReturn(1L);

        long inicio = System.currentTimeMillis();

        // Registra 100 glicemias
        for (int i = 0; i < 100; i++) {
            Glicemia glicemia = new Glicemia(1L, 90.0 + i % 50, true);
            registroBO.registrarSinalVital(glicemia);
        }

        long duracao = System.currentTimeMillis() - inicio;

        assertTrue(duracao < 1000, "Demorou " + duracao + "ms para processar 100 registros");
        System.out.println("✓ 100 registros processados em " + duracao + "ms");
    }

    @Test
    @DisplayName("Análise de risco deve ser instantânea (< 10ms)")
    void analiseDeRiscoDeveSerRapida() throws Exception {
        Glicemia glicemia = new Glicemia(1L, 95.0, true);

        long inicio = System.nanoTime();
        glicemia.analisarRisco();
        long duracao = (System.nanoTime() - inicio) / 1_000_000; // Convert to ms

        assertTrue(duracao < 10, "Análise de risco demorou " + duracao + "ms");
        System.out.println("✓ Análise de risco executada em " + duracao + "ms");
    }
}
```

## 🧪 Executar Testes de Integração

```bash
# Via Maven
mvn integration-test

# Ou todos os testes
mvn verify
```

## ✅ Checklist de Validação

- [ ] Teste de fluxo completo implementado
- [ ] Teste de integração com IA implementado
- [ ] Teste End-to-End simulado funciona
- [ ] Teste de CPF duplicado valida regra de negócio
- [ ] Teste de emergência não salva no banco
- [ ] Teste de erro no banco tratado
- [ ] Teste de performance básico implementado
- [ ] Todos os testes de integração passam

## 📊 Cobertura Esperada

Após todas as fases de testes:
- **Cobertura de Código**: > 80%
- **Cenários Testados**: > 30
- **Fluxos Principais**: 100%
