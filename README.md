# GlicemIA - Monitor Metabólico Inteligente

Sistema de monitoramento de sinais vitais (glicemia, pressão arterial e peso) com análise de risco e integração com IA generativa.

## 🎯 Características

- **Monitoramento de Sinais Vitais**: Glicemia, Pressão Arterial e Peso Corporal
- **Análise de Risco Automática**: Classificação em NORMAL, ATENÇÃO, ALTO e CRÍTICO
- **IA Generativa**: Recomendações personalizadas via OpenAI/Gemini
- **Banco de Dados**: PostgreSQL com NeonDB (cloud serverless)
- **Arquitetura**: MVC/DAO em Java puro (sem frameworks)

## 🛠️ Tecnologias

- Java 11+
- PostgreSQL 17 (NeonDB)
- JDBC
- JUnit 5 + Mockito
- OkHttp + Gson

## 📋 Pré-requisitos

- JDK 11 ou superior
- Maven 3.6+
- Conta NeonDB (gratuita)

## ⚙️ Configuração

1. Clone o repositório
2. Configure o arquivo `src/main/resources/database.properties` com as credenciais NeonDB
3. Execute o script `database-setup.sql` no NeonDB
4. Compile o projeto: `mvn clean install`
5. Execute o teste de conexão: `mvn exec:java -Dexec.mainClass="br.com.glicemia.TestConnection"`

## 📂 Estrutura do Projeto

```
glicemIA/
├── src/
│   ├── main/
│   │   ├── java/br/com/glicemia/
│   │   │   ├── model/      # Value Objects e Enums
│   │   │   ├── dao/        # Data Access Objects
│   │   │   ├── bo/         # Business Objects
│   │   │   ├── service/    # Serviços (IA)
│   │   │   ├── view/       # Interface Console
│   │   │   └── util/       # Utilitários
│   │   └── resources/
│   └── test/               # Testes unitários
├── database-setup.sql      # Script de criação do BD
├── pom.xml
└── README.md
```

## 🎓 Conceitos de OO Aplicados

- ✅ Abstração (SinalVital)
- ✅ Herança (Glicemia, PressaoArterial, PesoCorporal)
- ✅ Polimorfismo (analisarRisco())
- ✅ Encapsulamento (atributos privados)
- ✅ Interfaces (Diagnosticavel)
- ✅ Exceções Customizadas
- ✅ Padrão DAO
- ✅ Padrão BO/Service

## 📝 Licença

Projeto acadêmico para fins educacionais.