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
#### RiscoEmergenciaException.java


### 2. Enum de Nível de Risco

Crie `src/main/java/br/com/glicemia/model/vo/NivelRisco.java`:

### 3. Classe Abstrata SinalVital

Crie `src/main/java/br/com/glicemia/model/vo/SinalVital.java`:

### 4. Classe Glicemia

Crie `src/main/java/br/com/glicemia/model/vo/Glicemia.java`:

### 5. Classe PressaoArterial

Crie `src/main/java/br/com/glicemia/model/vo/PressaoArterial.java`:

### 6. Classe PesoCorporal

Crie `src/main/java/br/com/glicemia/model/vo/PesoCorporal.java`:

### 7. Classe Paciente

Crie `src/main/java/br/com/glicemia/model/vo/Paciente.java`:

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ x ] Exceção `ValorInvalidoException` criada
- [ x ] Exceção `RiscoEmergenciaException` criada
- [ x ] Enum `NivelRisco` criado
- [ x ] Classe abstrata `SinalVital` criada
- [ x ] Classe `Glicemia` implementada e testada
- [ x ] Classe `PressaoArterial` implementada e testada
- [ x ] Classe `PesoCorporal` implementada e testada
- [ x ] Classe `Paciente` implementada e testada
- [ x ] Todas as classes compilam sem erros
- [ x ] Validações funcionam corretamente

## 🧪 Testes Manuais Rápidos

Crie `TestModelo.java` para testar:

**Conceitos implementados**: Abstração ✅ | Herança ✅ | Encapsulamento ✅ | Exceções ✅
