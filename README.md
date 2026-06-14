# Avaliação de Riscos e Controles

Sistema web para registro e gestão de avaliações de riscos e controles organizacionais.

## 📋 Sobre o Projeto

Aplicação Java EE (JSF 2.2) para gestão hierárquica de avaliações de risco:
**Avaliação → Processo → Risco → Fator → Controle → Teste**

**Versão atual:** 1.1.0 (ver `<meta name="app-version" content="1.1.0"/>` em `layout.xhtml`)

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
│   ├── dao/            # DAOs com JDBC + GenericDAO (batch fetch)
│   ├── service/        # Regras de negócio e validação
│   ├── model/          # Entidades (BaseEntity + 7 modelos)
│   ├── exception/      # NegocioException, InfraestruturaException
│   ├── filter/         # CharacterEncodingFilter, CSRFFilter
│   └── util/           # ConnectionFactory, ValidadorUtil, StringEscapeUtil, PhaseListenerUtil
├── src/main/webapp/
│   ├── META-INF/context.xml.template   # DataSource JNDI template (não comitado)
│   ├── WEB-INF/
│   │   ├── web.xml, faces-config.xml
│   │   ├── templates/          # layout.xhtml + 13 partials
│   ├── resources/
│   │   ├── css/custom.css
│   │   └── js/custom.js
│   ├── index.xhtml
│   ├── listagem.xhtml
│   ├── avaliacao.xhtml         # Forms isolados por aba + modais
│   └── error.xhtml
├── pom.xml
└── sql/ddl-avaliacao-riscos.sql
```

## 🔒 Segurança (v1.1.0+)

- **Credenciais externalizadas:** `context.xml` não comitado, template com placeholders `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}`
- **CSRF Protection ativo:** Filter mapeado para `*.xhtml`, token em todos os forms via `csrf-token.xhtml`
- **Headers de segurança recomendados:** Configurar no Tomcat/Reverse Proxy (X-Frame-Options, CSP, HSTS)

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

3. Configure o DataSource no Tomcat (`$CATALINA_BASE/conf/context.xml`):
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
          maxWaitMillis="-1"
          validationQuery="SELECT 1"
          testOnBorrow="true"/>
```
> **Nota:** Não use `META-INF/context.xml` do WAR. Use o template `context.xml.template` como referência e configure no Tomcat.

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
- Configurar no Tomcat (`$CATALINA_BASE/conf/context.xml`) - **não usar `META-INF/context.xml` do WAR**

### Filtros
- `CharacterEncodingFilter` - UTF-8 em todas as requisições
- `CSRFFilter` - Mapeado para `*.xhtml` (ativo), token via `csrf-token.xhtml`

### Monitoramento JSF (v1.1.0+)
- `PhaseListenerUtil` registrado em `faces-config.xml`
- Loga duração de cada fase JSF (RESTORE_VIEW → RENDER_RESPONSE)
- Níveis: FINE (início/fim), INFO (duração em ms)

## 🐛 Problemas Conhecidos e Soluções

| Problema | Causa | Solução |
|----------|-------|---------|
| Modal "Novo" não abre | Action incorreta | Usar `prepararNovoXxx()` + `render="@none"` + `abrirModalNovo()` |
| Erros não detectados no modal | Seletor `.alert-danger` incorreto | Usar `li.error, li.fatal` |
| `onevent` inline falha | JSF interpreta como expressão | Usar funções nomeadas (`handleAjaxComplete`, etc.) |
| Optional chaining (`?.`) falha | Não suportado em browsers antigos | Substituir por `if (obj) obj.method()` |
| CSRF Token inválido (403) | JSF prefixa `name` com `formId:` | Filter procura `paramName.endsWith(":csrfToken")` + `id="csrfToken"` no inputHidden |
| Modal seleção não carrega dados | Sem `<h:form>` próprio no modal | Wrapper `<h:form id="#{modalId}Form" prependId="false">` no template |
| ViewState grande / submit lento | Form único com todas as abas | Forms isolados por aba + modais (`prependId="false"`) |

## ⚡ Performance (v1.1.0+)

- **Batch Fetch:** `ArvoreService` usa `JuncaoDAO` batch methods (`listarRiscosPorProcessos`, `listarFatoresPorRiscos`, etc.) — 6 queries fixas vs N×5 anterior
- **CrudDAO.buscarPorIds(Set<Long>):** Implementação genérica com `IN` clause
- **Forms isolados:** Submit AJAX carrega apenas campos do form ativo (ViewState mínimo)
- **Índices recomendados:** FKs nas tabelas de junção (`avaliacao_processo`, `processo_risco`, `risco_fator`, `fator_controle`, `controle_teste`, `teste_controle`)

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

# Hot reload (desenvolvimento)
powershell -File dev-watch.ps1

# Deploy automatizado (Windows - admin)
.\deploy-avaliacao.bat
```

## 📄 Licença

Projeto interno - Avaliação de Riscos e Controles.