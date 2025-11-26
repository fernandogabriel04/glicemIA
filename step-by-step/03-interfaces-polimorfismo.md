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

### 2. Atualizar SinalVital para implementar Diagnosticavel

Modifique `SinalVital.java` para adicionar a interface:

### 3. Implementar analisarRisco() em Glicemia

Atualize `Glicemia.java`:

### 4. Implementar analisarRisco() em PressaoArterial

Atualize `PressaoArterial.java`:

### 5. Implementar analisarRisco() em PesoCorporal

Atualize `PesoCorporal.java`:

## 🎭 Demonstração do Polimorfismo

Crie `TestPolimorfismo.java` para demonstrar:

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ x ] Interface `Diagnosticavel` criada com 3 métodos
- [ x ] `SinalVital` atualizada para implementar `Diagnosticavel`
- [ x ] `analisarRisco()` implementado em `Glicemia`
- [ x ] `analisarRisco()` implementado em `PressaoArterial`
- [ x ] `analisarRisco()` implementado em `PesoCorporal`
- [ x ] `getRecomendacaoImediata()` implementado em todas as classes
- [ x ] Emergências lançam `RiscoEmergenciaException` corretamente
- [ x ] Teste de polimorfismo executado com sucesso
- [ x ] Diferentes tipos se comportam de forma específica

## 🎯 Validação do Polimorfismo

Execute os testes e verifique:

1. ✅ Uma lista de `Diagnosticavel` aceita diferentes tipos de sinais vitais
2. ✅ O método `analisarRisco()` funciona para todos sem saber o tipo específico
3. ✅ Cada tipo aplica suas próprias regras de classificação
4. ✅ Exceções de emergência são lançadas corretamente
