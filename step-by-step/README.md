# GlicemIA - Plano de Desenvolvimento Step-by-Step

Este diretório contém o planejamento detalhado para o desenvolvimento do **GlicemIA - Monitor Metabólico Inteligente**.

🆕 **Atualizado para PostgreSQL + NeonDB** (banco de dados serverless na nuvem)

## 📋 Índice de Fases

Cada fase possui um arquivo detalhado com instruções, critérios de validação e código de exemplo:

1. **[Fase 01 - Setup e Estrutura Inicial](./01-setup-estrutura-inicial.md)**
   - Configuração do ambiente
   - Estrutura de pastas MVC/DAO
   - Configuração do PostgreSQL (NeonDB cloud)

2. **[Fase 02 - Modelo de Domínio (Value Objects)](./02-modelo-dominio-vo.md)**
   - Classe abstrata SinalVital
   - Classes filhas (Glicemia, PressaoArterial, PesoCorporal)
   - Classe Paciente
   - Validações e Encapsulamento

3. **[Fase 03 - Interfaces e Polimorfismo](./03-interfaces-polimorfismo.md)**
   - Interface Diagnosticavel
   - Implementação de analisarRisco()
   - Sistema de classificação de risco

4. **[Fase 04 - Camada DAO (Persistência)](./04-camada-dao.md)**
   - Interfaces DAO
   - Implementação com PostgreSQL (NeonDB)
   - Conexão JDBC com SSL
   - CRUD completo

5. **[Fase 05 - Camada BO (Lógica de Negócio)](./05-camada-bo.md)**
   - Gerenciamento de regras de segurança
   - Validação de risco antes de salvar
   - Integração DAO e preparação para IA

6. **[Fase 06 - Sistema de Alertas e Emergências](./06-sistema-alertas.md)**
   - Classe de AlertaEmergencia
   - Lógica de bloqueio de IA em casos críticos
   - Protocolos de emergência

7. **[Fase 07 - Integração com IA Generativa](./07-integracao-ia.md)**
   - Serviço de IA (OpenAI/Gemini)
   - Contexto histórico para IA
   - Fallback local (IA simulada)
   - Tratamento de erros de rede

8. **[Fase 08 - Camada View (Interface Console)](./08-camada-view.md)**
   - Menus interativos
   - Captura de dados do usuário
   - Exibição de alertas e recomendações

9. **[Fase 09 - Testes Unitários](./09-testes-unitarios.md)**
   - Testes de lógica de negócio
   - Testes de validação
   - Testes de exceções
   - Cobertura mínima de 80%

10. **[Fase 10 - Testes de Integração](./10-testes-integracao.md)**
    - Mock de banco de dados
    - Mock de serviço de IA
    - Testes de fluxo completo

11. **[Fase 11 - Documentação e Refinamento](./11-documentacao-refinamento.md)**
    - JavaDoc completo
    - Diagramas UML
    - Manual de uso
    - Preparação para apresentação

## 🎯 Critérios de Avaliação Atendidos

| Critério | Onde está implementado |
|----------|------------------------|
| **Abstração** | Fase 02 - Classe abstrata `SinalVital` |
| **Herança** | Fase 02 - Classes `Glicemia`, `PressaoArterial`, `PesoCorporal` |
| **Interface** | Fase 03 - Interface `Diagnosticavel` |
| **Polimorfismo** | Fase 03 - Método `analisarRisco()` com comportamentos diferentes |
| **Encapsulamento** | Fase 02 - Atributos privados com getters/setters validados |
| **Exceções Customizadas** | Fases 02, 04, 05 - `ValorInvalidoException`, `RiscoEmergenciaException` |
| **Arquitetura MVC/DAO** | Fases 04, 05, 08 - Separação clara de camadas |
| **Conexão com BD PostgreSQL** | Fase 04 - Implementação JDBC com NeonDB |
| **Testes Unitários** | Fase 09 - JUnit com cobertura >80% |
| **Testes Integração** | Fase 10 - Mocks e cenários de fluxo |

## ⚡ Ordem de Execução Recomendada

### Sprint 1 - Fundação (Fases 1-3)
Estabelece a estrutura e o modelo de domínio com OO puro.

### Sprint 2 - Persistência e Lógica (Fases 4-6)
Implementa banco de dados e regras de negócio de segurança.

### Sprint 3 - Inteligência e Interface (Fases 7-8)
Adiciona IA e interface com usuário.

### Sprint 4 - Qualidade (Fases 9-11)
Testes, documentação e preparação final.

## 🚀 Início Rápido

```bash
# 1. Comece pela Fase 01
cd step-by-step
# Abra o arquivo 01-setup-estrutura-inicial.md

# 2. Siga os arquivos em ordem numérica
# Cada fase possui:
# - Objetivos claros
# - Código de exemplo
# - Checklist de validação
# - Próximos passos

# 3. Marque cada fase como concluída ao terminar
```

## 📊 Diagrama de Arquitetura

```
┌─────────────────────────────────────────────────┐
│                    VIEW                          │
│              (Console/Menu)                      │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│              BUSINESS OBJECT (BO)                │
│  ┌──────────────────────────────────────────┐   │
│  │  1. Recebe VO                            │   │
│  │  2. Chama analisarRisco()                │   │
│  │  3. Se CRÍTICO → Alerta, bloqueia IA     │   │
│  │  4. Se OK → Salva via DAO                │   │
│  │  5. Se OK → Consulta IA para dicas       │   │
│  └──────────────────────────────────────────┘   │
└────────┬───────────────────────────────┬────────┘
         │                               │
┌────────▼─────────┐          ┌──────────▼─────────┐
│   DAO LAYER      │          │   IA SERVICE       │
│ (PostgreSQL JDBC)│          │  (OpenAI/Gemini)   │
└──────────────────┘          └────────────────────┘
         │
┌────────▼──────────────┐
│  PostgreSQL (NeonDB)  │
│   Cloud Serverless    │
└───────────────────────┘
```

## 🎓 Conceitos OO Aplicados por Fase

- **Abstração**: SinalVital (Fase 02)
- **Herança**: Glicemia extends SinalVital (Fase 02)
- **Interface**: Diagnosticavel (Fase 03)
- **Polimorfismo**: analisarRisco() (Fase 03)
- **Encapsulamento**: private + getters/setters (Fase 02)
- **Exceções**: ValorInvalidoException (Fase 02)
- **Padrão DAO**: Separação persistência (Fase 04)
- **Padrão BO**: Lógica de negócio isolada (Fase 05)

---
