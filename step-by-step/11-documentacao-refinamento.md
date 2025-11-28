# Fase 11 - Documentação e Refinamento

## 🎯 Objetivos
- Criar documentação JavaDoc completa
- Gerar diagramas UML
- Escrever manual de uso
- Preparar apresentação do projeto
- Revisão final de código

## 📚 Conceitos Aplicados
- ✅ **JavaDoc**: Documentação de API
- ✅ **UML**: Modelagem visual
- ✅ **Boas Práticas**: Code review
- ✅ **Apresentação**: Defesa do projeto

## 🔧 Implementação

### 1. Documentação JavaDoc

#### Padrão de Documentação

```java
/**
 * Descrição breve da classe em uma linha.
 * <p>
 * Descrição mais detalhada da classe, explicando seu propósito,
 * responsabilidades e como ela se encaixa na arquitetura.
 * </p>
 *
 * <h2>Exemplo de Uso:</h2>
 * <pre>{@code
 * Glicemia glicemia = new Glicemia(1L, 95.0, true);
 * NivelRisco risco = glicemia.analisarRisco();
 * }</pre>
 *
 * @author Seu Nome
 * @version 1.0
 * @since 2025-01-24
 * @see SinalVital
 * @see Diagnosticavel
 */
public class Glicemia extends SinalVital {

    /**
     * Valor da glicemia medida em mg/dL.
     * Deve estar entre 1 e 600 mg/dL.
     */
    private double valorGlicemia;

    /**
     * Indica se a medição foi realizada em jejum (8h sem alimentos).
     */
    private boolean emJejum;

    /**
     * Constrói uma nova medição de glicemia.
     *
     * @param idPaciente      ID do paciente que realizou a medição
     * @param valorGlicemia   Valor da glicemia em mg/dL
     * @param emJejum         true se a medição foi em jejum
     * @throws ValorInvalidoException se o valor for inválido (≤0 ou >600)
     */
    public Glicemia(Long idPaciente, double valorGlicemia, boolean emJejum)
            throws ValorInvalidoException {
        // Implementação...
    }

    /**
     * Analisa o risco da glicemia baseado em diretrizes da Sociedade
     * Brasileira de Diabetes (SBD).
     * <p>
     * Classificação para jejum:
     * <ul>
     *   <li>Normal: 70-99 mg/dL</li>
     *   <li>Pré-diabetes: 100-125 mg/dL</li>
     *   <li>Diabetes: ≥126 mg/dL</li>
     *   <li>Hipoglicemia severa: &lt;50 mg/dL (EMERGÊNCIA)</li>
     *   <li>Hiperglicemia severa: ≥300 mg/dL (EMERGÊNCIA)</li>
     * </ul>
     * </p>
     *
     * @return NivelRisco classificado
     * @throws RiscoEmergenciaException se detectar emergência médica
     */
    @Override
    public NivelRisco analisarRisco() throws RiscoEmergenciaException {
        // Implementação...
    }
}
```

#### Gerar JavaDoc

```bash
# Via Maven
mvn javadoc:javadoc

# Via linha de comando
javadoc -d docs/javadoc -sourcepath src/main/java -subpackages br.com.glicemia
```

### 2. Diagramas UML

#### Diagrama de Classes (Principais)

Crie `docs/diagrams/class-diagram.md`:

```markdown
# Diagrama de Classes - GlicemIA

## Hierarquia de Classes

```
                    ┌─────────────────┐
                    │  <<interface>>   │
                    │  Diagnosticavel  │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  <<abstract>>     │
                    │   SinalVital      │
                    └────────┬─────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼─────────┐  ┌──────▼──────────┐
│   Glicemia     │  │ PressaoArterial  │  │  PesoCorporal   │
└────────────────┘  └──────────────────┘  └─────────────────┘

## Camada DAO

┌──────────────────┐         ┌──────────────────┐
│  <<interface>>    │         │  <<interface>>    │
│   PacienteDAO    │         │   RegistroDAO    │
└────────┬─────────┘         └────────┬─────────┘
         │                            │
         │                            │
┌────────▼─────────┐         ┌────────▼─────────┐
│ PacienteDAOImpl  │         │ RegistroDAOImpl  │
└──────────────────┘         └──────────────────┘

## Camada BO

┌─────────────────────────┐    ┌─────────────────────────┐
│ GerenciadorPacienteBO   │    │ GerenciadorRegistroBO   │
└─────────────────────────┘    └─────────────────────────┘
            │                              │
            │                              │
            ▼                              ▼
    [PacienteDAO]                   [RegistroDAO]

## Serviços

┌──────────────────┐
│  <<interface>>    │
│    ServicoIA     │
└────────┬─────────┘
         │
    ┌────┴─────────────────┐
    │                      │
┌───▼─────────┐    ┌───────▼────────┐
│ OpenAIService│    │ IALocalService │
└──────────────┘    └────────────────┘
         │                  │
         └────────┬─────────┘
                  │
         ┌────────▼─────────┐
         │  GerenciadorIA   │
         └──────────────────┘
```

#### Diagrama de Sequência: Registro com Emergência

```
Usuário      View         BO              VO            DAO
  │           │            │               │             │
  │──entrada─>│            │               │             │
  │           │──registrar─>│              │             │
  │           │            │──new Glicemia─>│            │
  │           │            │<──glicemia────│             │
  │           │            │──analisarRisco()            │
  │           │            │<──CRITICO─────│             │
  │           │            │ (EXCEPTION)                 │
  │           │<─exception─│                             │
  │<─alerta──│                                           │
  │ vermelho                                             │
  │                        │                             │
  │        (NÃO SALVA NO BANCO)                         │
  │        (NÃO CHAMA IA)                               │
```
```

### 3. Manual de Uso

Crie `docs/MANUAL_USUARIO.md`:

```markdown
# Manual do Usuário - GlicemIA

## 1. Introdução

O **GlicemIA** é um sistema de monitoramento metabólico que combina:
- Regras clínicas rígidas (Java) para segurança
- Inteligência Artificial para recomendações personalizadas
- Sistema de alertas por nível de risco

## 2. Primeiros Passos

### 2.1. Configuração Inicial

1. Configure o banco de dados Oracle em `database.properties`
2. Execute o script SQL de criação de tabelas
3. (Opcional) Configure sua chave de API da OpenAI

### 2.2. Executar o Sistema

```bash
java -cp target/classes br.com.glicemia.view.MenuPrincipal
```

## 3. Funcionalidades

### 3.1. Cadastro de Paciente

1. Menu Principal → Opção 1
2. Escolha "Cadastrar Novo Paciente"
3. Preencha os dados:
   - Nome completo
   - CPF (11 dígitos)
   - Data de nascimento (dd/MM/yyyy)
   - Email (opcional)
   - Telefone (opcional)

### 3.2. Registro de Sinais Vitais

#### Glicemia

1. Menu Principal → Opção 2
2. Escolha "Registrar Glicemia"
3. Informe:
   - ID do paciente
   - Valor em mg/dL
   - Se estava em jejum (S/N)
   - Observações (opcional)

**Interpretação dos Resultados:**

| Nível | Cor | Significado | Ação |
|-------|-----|-------------|------|
| NORMAL | Verde | Valores dentro do esperado | Continue assim |
| ATENÇÃO | Amarelo | Necessita monitoramento | Revise hábitos |
| ALTO | Laranja | Fora da faixa ideal | Consulte médico |
| CRÍTICO | Vermelho | Emergência médica | PROCURE AJUDA IMEDIATA |

### 3.3. Consultar IA

1. Menu Principal → Opção 3
2. Informe o ID do paciente
3. Faça sua pergunta (ex: "O que devo comer no jantar?")
4. Aguarde a resposta

**Dicas de Perguntas:**
- "Como melhorar minha glicemia?"
- "Que exercícios são recomendados?"
- "O que evitar na alimentação?"

### 3.4. Ver Histórico

1. Menu Principal → Opção 2
2. Escolha "Ver Histórico"
3. Informe o ID do paciente
4. Visualize:
   - Todos os registros ordenados por data
   - Resumo estatístico

## 4. Sistema de Segurança

### 4.1. Funil de Segurança

O sistema aplica um **funil de segurança** em cada registro:

```
Entrada de Dados
      ↓
Validação de Valores
      ↓
Análise de Risco (VO)
      ↓
┌─────────────┐
│ É CRÍTICO?  │
└─────┬───────┘
      │
      ├─ SIM → Alerta Vermelho + Protocolo
      │         NÃO salva no banco
      │         NÃO consulta IA
      │
      └─ NÃO → Salva no banco
                Libera consulta à IA
```

### 4.2. Protocolos de Emergência

Quando detectada emergência, o sistema exibe:
1. Alerta visual destacado
2. Protocolo de primeiros socorros
3. Orientação para buscar ajuda médica

## 5. Limitações e Avisos

⚠️ **IMPORTANTE:**
- Este sistema NÃO substitui consulta médica
- As recomendações da IA são gerais
- Sempre consulte profissionais de saúde
- Em caso de emergência, procure atendimento IMEDIATAMENTE

## 6. Solução de Problemas

### Erro de Conexão com Banco

```
Verifique:
- Banco de dados está rodando
- Credenciais em database.properties estão corretas
- Porta do banco de dados está acessível
```

### IA Não Responde

```
O sistema usa fallback automático:
- Se OpenAI falhar, usa IA local
- Recomendações locais são baseadas em regras
- Funciona sem internet
```

## 7. Suporte

Para dúvidas ou problemas:
- Consulte a documentação técnica (JavaDoc)
- Revise o README.me do projeto
```

### 4. Checklist de Apresentação

Crie `docs/APRESENTACAO.md`:

```markdown
# Checklist de Apresentação - GlicemIA

## 1. Introdução (2 min)

- [ ] Nome do projeto: **GlicemIA - Monitor Metabólico Inteligente**
- [ ] Problema que resolve: Monitoramento de saúde metabólica com segurança
- [ ] Diferencial: Funil de segurança + IA generativa

## 2. Conceitos OO Aplicados (3 min)

### Abstração
- [ ] Demonstrar classe abstrata `SinalVital`
- [ ] Explicar por que "sinal vital" é abstrato

### Herança
- [ ] Mostrar `Glicemia`, `PressaoArterial`, `PesoCorporal`
- [ ] Explicar especialização de comportamentos

### Interface e Polimorfismo
- [ ] Demonstrar interface `Diagnosticavel`
- [ ] Mostrar código onde `analisarRisco()` funciona para todos os tipos
- [ ] Executar exemplo prático

### Encapsulamento
- [ ] Mostrar atributos privados
- [ ] Demonstrar validações nos setters

### Exceções Customizadas
- [ ] `ValorInvalidoException` para validações
- [ ] `RiscoEmergenciaException` para emergências

## 3. Arquitetura MVC/DAO (3 min)

- [ ] Explicar separação de camadas
- [ ] Mostrar diagrama de arquitetura
- [ ] Demonstrar fluxo: View → BO → DAO → Oracle

## 4. Demonstração Prática (5 min)

### Cenário 1: Registro Normal
- [ ] Cadastrar paciente
- [ ] Registrar glicemia normal (95 mg/dL)
- [ ] Mostrar alerta verde
- [ ] Consultar IA

### Cenário 2: Emergência
- [ ] Registrar glicemia crítica (45 mg/dL)
- [ ] Mostrar alerta vermelho
- [ ] Demonstrar que NÃO salvou no banco
- [ ] Mostrar protocolo de emergência

### Cenário 3: Histórico
- [ ] Ver histórico do paciente
- [ ] Mostrar resumo estatístico

## 5. Testes (2 min)

- [ ] Mostrar cobertura de testes (>80%)
- [ ] Executar suite de testes
- [ ] Demonstrar teste de emergência

## 6. Diferenciais Técnicos (2 min)

- [ ] Integração com IA (OpenAI/Gemini)
- [ ] Sistema de fallback automático
- [ ] Protocolos de emergência baseados em diretrizes médicas
- [ ] Interface console com alertas coloridos

## 7. Conclusão (1 min)

- [ ] Recapitular conceitos OO aplicados
- [ ] Destacar importância da segurança clínica
- [ ] Agradecer a atenção

## Tempo Total: 18 minutos + 2 min de perguntas

## Materiais Necessários

- [ ] Projeto compilado e funcionando
- [ ] Banco de dados Oracle configurado
- [ ] Apresentação de slides (opcional)
- [ ] Diagramas UML impressos ou em PDF
- [ ] Código fonte comentado e organizado

## Possíveis Perguntas

**P: Por que não salvar registros críticos?**
R: Prioridade é a segurança do paciente. Em emergência, o foco deve ser buscar ajuda, não documentar.

**P: Como a IA funciona?**
R: Usamos API da OpenAI para análise de histórico. Em caso de falha, temos IA local baseada em regras.

**P: O sistema poderia ser usado em produção?**
R: Com algumas melhorias sim: validação completa de CPF, criptografia de dados, auditoria de acessos, e validação médica dos protocolos.

**P: Como garantem a segurança dos dados?**
R: Encapsulamento impede alterações diretas. Validações impedem dados inválidos. Para produção, seria necessário criptografia.
```

### 5. README Principal Atualizado

Atualizar o `README.me` do projeto:

```markdown
# GlicemIA - Monitor Metabólico Inteligente

## 📋 Sobre o Projeto

Sistema de monitoramento de diabetes e hipertensos que combina regras determinísticas (Java) para segurança clínica com IA generativa para recomendações personalizadas.

## 🎯 Funcionalidades

- ✅ Cadastro de pacientes
- ✅ Registro de sinais vitais (glicemia, pressão, peso)
- ✅ Análise de risco automática
- ✅ Sistema de alertas por cor
- ✅ Protocolos de emergência
- ✅ Bloqueio de IA em emergências
- ✅ Consulta à IA para recomendações
- ✅ Histórico completo com estatísticas

## 🏗️ Arquitetura

- **Modelo**: Value Objects com OO puro
- **Persistência**: Banco de dados relacional via JDBC
- **Lógica de Negócio**: Camada BO
- **Interface**: Console interativo
- **IA**: OpenAI com fallback local

## 🚀 Como Executar

1. Configurar `database.properties`
2. Executar script SQL de criação de tabelas
3. Compile: `mvn clean package`
4. Execute: `java -jar target/glicemia.jar`

## 📊 Testes

```bash
mvn test              # Testes unitários
mvn integration-test  # Testes de integração
mvn verify            # Todos os testes
```

## 📚 Documentação
criar documentação

- [Manual do Usuário](docs/MANUAL_USUARIO.md)
- [JavaDoc](docs/javadoc/index.html)
- [Guia de Apresentação](docs/APRESENTACAO.md)
- [Plano de Desenvolvimento](step-by-step/README.md)

## 🎓 Conceitos OO Implementados

✅ Abstração | ✅ Herança | ✅ Interface | ✅ Polimorfismo
✅ Encapsulamento | ✅ Exceções | ✅ MVC/DAO | ✅ Testes

## 📄 Licença

Projeto acadêmico - POO 2025
```

## ✅ Checklist Final

### Código
- [ ] Todas as classes têm JavaDoc
- [ ] Código formatado consistentemente
- [ ] Sem warnings do compilador
- [ ] Sem código comentado desnecessário
- [ ] Constantes em maiúsculas
- [ ] Nomes de variáveis descritivos

### Documentação
- [ ] README.me completo
- [ ] Manual do usuário criado
- [ ] JavaDoc gerado
- [ ] Diagramas UML criados
- [ ] Guia de apresentação pronto

### Testes
- [ ] Cobertura > 80%
- [ ] Todos os testes passam
- [ ] Testes de integração funcionam
- [ ] Cenários de emergência testados

### Apresentação
- [ ] Demo funciona perfeitamente
- [ ] Banco de dados configurado
- [ ] Exemplos de uso preparados
- [ ] Resposta para perguntas frequentes
