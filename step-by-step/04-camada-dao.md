# Fase 04 - Camada DAO (Persistência)

## 🎯 Objetivos
- Criar interfaces DAO seguindo o padrão de projeto
- Implementar DAOs concretos para PostgreSQL (NeonDB)
- Estabelecer operações CRUD completas
- Implementar tratamento robusto de exceções SQL

## 📚 Conceitos Aplicados
- ✅ **Padrão DAO**: Separação da lógica de persistência
- ✅ **Interface/Implementação**: Desacoplamento do banco
- ✅ **JDBC**: Conexão e operações com PostgreSQL
- ✅ **Tratamento de Exceções**: SQLException handling

## 🔧 Implementação

### 1. Interface PacienteDAO

Crie `src/main/java/br/com/glicemia/dao/interfaces/PacienteDAO.java`:

### 2. Interface RegistroDAO

Crie `src/main/java/br/com/glicemia/dao/interfaces/RegistroDAO.java`:

### 3. Implementação PacienteDAOImpl

Crie `src/main/java/br/com/glicemia/dao/impl/PacienteDAOImpl.java`:

### 4. Implementação RegistroDAOImpl (Parte 1)

Crie `src/main/java/br/com/glicemia/dao/impl/RegistroDAOImpl.java`:

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ x ] Interface `PacienteDAO` criada com todos os métodos
- [ x ] Interface `RegistroDAO` criada com todos os métodos
- [ x ] `PacienteDAOImpl` implementado completamente
- [ x ] `RegistroDAOImpl` implementado completamente
- [ x ] Teste de inserção de paciente funciona
- [ x ] Teste de inserção de registros funciona
- [ x ] Teste de busca funciona
- [ x ] Tratamento de exceções SQL adequado

## 🧪 Teste do DAO

Crie `TestDAO.java`:
