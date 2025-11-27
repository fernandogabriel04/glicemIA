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

### 2. Classe ProtocoloEmergencia

Crie `src/main/java/br/com/glicemia/util/ProtocoloEmergencia.java`:

## 🧪 Teste do Sistema de Alertas

Crie `TestAlertas.java`:

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ x ] Classe `AlertaEmergencia` criada com formatação colorida
- [ x ] Classe `ProtocoloEmergencia` com todos os protocolos
- [ x ] Alerta NORMAL exibido em verde
- [ x ] Alerta ATENÇÃO exibido em amarelo
- [ x ] Alerta ALTO exibido em laranja
- [ x ] Alerta CRÍTICO exibido com destaque vermelho
- [ x ] Protocolo de emergência formatado corretamente
- [ x ] Sistema de bloqueio da IA é visível
- [ x ] Mensagens utilitárias funcionam

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