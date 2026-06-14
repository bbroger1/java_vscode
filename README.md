# Avaliação de Riscos e Controles

Sistema web para registro e gestão de avaliações de riscos e controles organizacionais.

## 📋 Sobre o Projeto

Aplicação Java EE (JSF 2.2) para gestão hierárquica de avaliações de risco:
**Avaliação → Processo → Risco → Fator → Controle → Teste**

## 🛠️ Stack Tecnológica

| Tecnologia | Versão |
|------------|--------|
| Java | 8 |
| JSF (Mojarra) | 2.2.20 |
| Build | Maven 3.x |
| Banco de Dados | PostgreSQL 14+ |
| Servidor | Apache Tomcat 9 |
| Frontend | Bootstrap 4 + jQuery |
| Connection Pool | Apache DBCP2 |
| Persistência | JDBC puro (sem JPA/Hibernate) |

## 📁 Estrutura do Projeto

```
avaliacao-riscos/
├── src/main/java/com/avaliacao/
│   ├── controller/     # Managed Beans (@ViewScoped)
│   ├── dao/            # DAOs com JDBC + GenericDAO
│   ├── service/        # Regras de negócio e validação
│   ├── model/          # Entidades (BaseEntity + 7 modelos)
│   ├── exception/      # NegocioException, InfraestruturaException
│   ├── filter/         # CharacterEncodingFilter, CSRFFilter
│   └── util/           # ConnectionFactory, ValidadorUtil, StringEscapeUtil
├── src/main/webapp/
│   ├── META-INF/context.xml    # DataSource JNDI
│   ├── WEB-INF/
│   │   ├── web.xml, faces-config.xml
│   │   ├── templates/          # layout.xhtml + 12 partials
│   ├── resources/
│   │   ├── css/custom.css
│   │   └── js/custom.js
│   ├── index.xhtml
│   ├── listagem.xhtml
│   ├── avaliacao.xhtml
│   └── error.xhtml
├── pom.xml
└── sql/ddl-avaliacao-riscos.sql
```

## 🚀 Como Executar

### Pré-requisitos
- JDK 8 (obrigatório - JSF 2.2 não funciona em versões superiores)
- Maven 3.6+
- PostgreSQL 14+
- Apache Tomcat 9

### Configuração do Banco de Dados

1. Crie o database e usuário:
```sql
CREATE DATABASE avaliacao_riscos;
CREATE USER postgres WITH PASSWORD 'root';
GRANT ALL PRIVILEGES ON DATABASE avaliacao_riscos TO postgres;
```

2. Execute o script DDL:
```bash
psql -U postgres -d avaliacao_riscos -f sql/ddl-avaliacao-riscos.sql
```

3. Configure o DataSource no Tomcat (`conf/server.xml` ou `META-INF/context.xml`):
```xml
<Resource name="jdbc/AvaliacaoRiscosDS"
          auth="Container"
          type="javax.sql.DataSource"
          driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://localhost:5432/avaliacao_riscos"
          username="postgres"
          password="root"
          maxTotal="20"
          maxIdle="10"
          maxWaitMillis="-1"/>
```

### Build e Deploy

```bash
# Compilar e gerar WAR
cd avaliacao-riscos
mvn clean package

# Deploy manual: copiar target/avaliacao-riscos.war para $TOMCAT_HOME/webapps/

# Deploy automatizado (Windows - executar como administrador)
.\deploy-avaliacao.bat
```

### Desenvolvimento com Hot Reload

```bash
# Compila Java e copia classes para Tomcat explodido
powershell -File dev-watch.ps1
```

## 📦 Funcionalidades

- **CRUD completo** para todas as entidades da hierarquia
- **Listagem paginada** com filtros AJAX
- **Modais** para criação/edição/seleção/vinculação
- **Validação** server-side com mensagens JSF
- **Tratamento de exceções** com pages de erro dedicadas
- **CSRF protection** (filter configurado, não ativo por padrão)
- **Encoding UTF-8** forçado via filter

## 🗄️ Modelo de Dados

Tabelas principais (todas com PK via sequences `seq_*`, FKs com `ON DELETE CASCADE`):
- `avaliacao` - Avaliação principal
- `modelo_negocio` - Modelos de negócio
- `processo` - Processos da avaliação
- `risco` - Riscos por processo
- `fator` - Fatores de risco
- `controle` - Controles mitigatórios
- `teste` - Testes de efetividade
- `teste_controle` - Tabela N:M (junção teste ↔ controle)

## ⚙️ Configurações Importantes

### Java 8 Obrigatório
O projeto **requer JDK 8** devido às dependências do JSF 2.2/Mojarra. Configure `JAVA_HOME` explicitamente.

### JNDI DataSource
- Nome: `java:comp/env/jdbc/AvaliacaoRiscosDS`
- Configurado em `src/main/webapp/META-INF/context.xml`

### Filtros
- `CharacterEncodingFilter` - UTF-8 em todas as requisições
- `CSRFFilter` - Mapeado para `/protected/*` (não ativo na configuração atual)

## 🐛 Problemas Conhecidos e Soluções

| Problema | Causa | Solução |
|----------|-------|---------|
| Modal "Novo" não abre | Action incorreta | Usar `prepararNovoXxx()` + `render="@none"` + `abrirModalNovo()` |
| Erros não detectados no modal | Seletor `.alert-danger` incorreto | Usar `li.error, li.fatal` |
| `onevent` inline falha | JSF interpreta como expressão | Usar funções nomeadas (`handleAjaxComplete`, etc.) |
| Optional chaining (`?.`) falha | Não suportado em browsers antigos | Substituir por `if (obj) obj.method()` |

## 📝 Scripts Úteis

```bash
# Build completo
mvn clean package

# Apenas compilar
mvn clean compile

# Rodar testes (se existirem)
mvn test

# Limpar target
mvn clean
```

## 📄 Licença

Projeto interno - Avaliação de Riscos e Controles.