# Fase 01 - Setup e Estrutura Inicial

## 🎯 Objetivos
- Configurar o ambiente de desenvolvimento Java
- Criar a estrutura de pastas do projeto seguindo MVC/DAO
- Configurar o banco de dados PostgreSQL (NeonDB)
- Preparar as dependências necessárias

## 📋 Pré-requisitos
- JDK 11 ou superior
- Conta gratuita no NeonDB (PostgreSQL serverless na nuvem)
- IDE Java (Eclipse, IntelliJ IDEA ou NetBeans)
- Maven ou gerenciamento manual de dependências

## 🗂️ Estrutura de Pastas

Crie a seguinte estrutura no seu projeto:

```
glicemIA/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/
│   │   │       └── com/
│   │   │           └── glicemia/
│   │   │               ├── model/          # Value Objects (VO)
│   │   │               │   ├── vo/
│   │   │               │   ├── interfaces/
│   │   │               │   └── exceptions/
│   │   │               ├── dao/            # Data Access Objects
│   │   │               │   ├── interfaces/
│   │   │               │   └── impl/
│   │   │               ├── bo/             # Business Objects
│   │   │               ├── service/        # Serviços (IA, etc)
│   │   │               ├── view/           # Interface Console
│   │   │               └── util/           # Utilitários
│   │   └── resources/
│   │       └── database.properties
│   └── test/
│       └── java/
│           └── br/
│               └── com/
│                   └── glicemia/
│                       ├── model/
│                       ├── dao/
│                       └── bo/
├── lib/                                     # JARs externos (se não usar Maven)
├── docs/                                    # Documentação e diagramas
└── README.me
```

## 💾 Configuração do Banco de Dados PostgreSQL (NeonDB)

### 1. Criar Conta no NeonDB ✅

1. **Acesse**: https://neon.tech
2. **Sign Up** (pode usar GitHub, Google ou email)
3. **Crie um novo projeto**:
   - Nome: `glicemIA`
   - Região: US East - Ohio
   - PostgreSQL version: 17
4. **Copie a Connection String**:
   ```
   postgresql://neondb_owner:npg_HwVr5dQsB9Ng@ep-quiet-hill-adk5tqx5-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
   ```

### 2. Script de Criação das Tabelas✅

Criar arquivo `database-setup.sql` com toda estrutura de criação de tabelas.

### 3. Executar o Script no NeonDB✅

**Via Console Web do NeonDB**
1. No dashboard do NeonDB, clique em **"SQL Editor"**
2. Cole o conteúdo completo do `database-setup.sql`
3. Clique em **"Run"** ou pressione `Ctrl+Enter`

## 📦 Dependências Necessárias

### Utilizar Maven (`pom.xml`):
1. Criar o arquivo `pom.xml`
2. Abrir o arquivo `pom.xml` na IDE
3. Adicionar as dependências necessárias no `<dependencies>`
4. Salvar o arquivo `pom.xml`

### Instalar Maven no Windows:
## Opção 1: Download Manual
1. Acesse: https://maven.apache.org/download.cgi
2. Baixe o arquivo: apache-maven-3.9.6-bin.zip (ou versão mais recente)
3. Extraia para: C:\Program Files\Apache\maven
4. O caminho final deve ser: C:\Program Files\Apache\maven\bin\mvn.cmd
5. Configurar Variáveis de Ambiente.
6. Abra: Painel de Controle → Sistema → Configurações avançadas do sistema
7. Clique em "Variáveis de Ambiente"
8. Em "Variáveis do sistema", clique em "Novo"
9. Nome da variável: MAVEN_HOME
10. Valor: C:\Program Files\Apache\maven
11. Ainda em "Variáveis do sistema", selecione Path e clique em "Editar"
12. Clique em "Novo" e adicione: %MAVEN_HOME%\bin
13. Clique OK em todas as janelas
Verificar Instalação:
Abra um novo terminal/cmd
Execute: mvn --version
## Opção 2: Via Chocolatey (Se já tiver instalado)
choco install maven

## 🛠️ Classe Utilitária de Conexão (Inicial)

Criar `src/main/java/br/com/glicemia/util/DatabaseConnection.java`:

Para conectar ao banco de dados PostgreSQL, criar classe `DatabaseConnection`:

## ✅ Checklist de Validação

Marque cada item ao concluir:

- [ x ] Estrutura de pastas criada corretamente
- [ x ] Conta NeonDB criada e projeto configurado
- [ x ] Script SQL executado no NeonDB e tabelas criadas
- [ x ] Connection string do NeonDB copiada
- [ x ] Arquivo `database.properties` configurado com suas credenciais NeonDB
- [ x ] Dependências PostgreSQL baixadas (Maven)
- [ x ] Classe `DatabaseConnection` criada
- [ x ] Teste de conexão executado com sucesso (com SSL)

## 🧪 Teste Inicial

Crie um arquivo de teste `TestConnection.java`:

**Execute** e verifique se a conexão é bem-sucedida.

### Possíveis Erros e Soluções:

**Erro: "No suitable driver found"**
```
Solução: Adicione a dependência postgresql-42.7.1.jar no classpath
```

**Erro: "Connection refused"**
```
Solução: Verifique a connection string do NeonDB
Certifique-se de que ?sslmode=require está no final da URL
```

**Erro: "Authentication failed"**
```
Solução: Verifique username e password no database.properties
Eles devem corresponder aos dados do NeonDB
```

## 📌 Próximos Passos

Após concluir esta fase com sucesso:

1. Commit inicial do código (se estiver usando Git)
2. Vá para **[Fase 02 - Modelo de Domínio (Value Objects)](./02-modelo-dominio-vo.md)**

---

**Dúvidas?** Revise os pré-requisitos e certifique-se de que todas as ferramentas estão instaladas corretamente.
