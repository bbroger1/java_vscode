# Documentacao Tecnica - Sistema de Avaliacao de Riscos e Controles

## 1. Como renderizar a hierarquia Processo > Risco > Fator > Controle no frontend sem libs de arvore

A hierarquia e renderizada utilizando **tabelas encadeadas com indentacao visual via CSS**, sem dependencia externa de componentes de arvore.

### Estrategia

Para exibir a arvore completa (ex.: na aba "Testes", onde o usuario vincula um teste a um no especifico), utiliza-se uma unica `h:dataTable` com as linhas indentadas conforme o nivel hierarquico:

```xhtml
<h:dataTable value="#{avaliacaoMB.hierarquiaCompleta}" var="item"
             styleClass="table table-sm table-hover">
    <h:column>
        <f:facet name="header">Item</f:facet>
        <h:outputText value="#{item.nome}"
                      style="padding-left: #{item.nivel * 20}px;
                             font-weight: #{item.nivel == 0 ? 'bold' : 'normal'};"/>
    </h:column>
    <h:column>
        <f:facet name="header">Tipo</f:facet>
        #{item.tipo}
    </h:column>
</h:dataTable>
```

### Backend

O `AvaliacaoMB` expoe um metodo `getHierarquiaCompleta()` que monta uma `List<ItemHierarquia>` percorrendo processos, riscos, fatores e controles, populando o campo `nivel` (0-3):

```java
public List<ItemHierarquia> getHierarquiaCompleta() {
    List<ItemHierarquia> lista = new ArrayList<>();
    for (Processo p : processos) {
        lista.add(new ItemHierarquia(p.getNome(), "Processo", 0));
        for (Risco r : riscoService.listarPorProcesso(p.getId())) {
            lista.add(new ItemHierarquia(r.getNome(), "Risco", 1));
            for (Fator f : fatorService.listarPorRisco(r.getId())) {
                lista.add(new ItemHierarquia(f.getNome(), "Fator", 2));
                for (Controle c : controleService.listarPorFator(f.getId())) {
                    lista.add(new ItemHierarquia(c.getNome(), "Controle", 3));
                }
            }
        }
    }
    return lista;
}
```

### Pagina de Testes (vinculacao)

Na aba "Testes", ao criar/editar um teste, o modal exibe a arvore hierarquica em um `h:selectOneMenu` encadeado ou em uma tabela selecionavel, permitindo ao usuario escolher a qual controle (ou ramo) o teste sera vinculado.

---

## 2. Implementacao do AJAX de filtro em `h:dataTable`

### Componentes utilizados

- `h:inputText` para o campo de pesquisa
- `f:ajax` para disparar a requisicao AJAX ao digitar
- Atributo `event="keyup"` para reagir a cada tecla
- Atributo `delay="300"` para aguardar 300ms de inatividade antes de disparar (evita requisicoes excessivas)
- Atributo `render` apontando para o `id` da tabela e paginacao
- Atributo `listener="#{bean.pesquisar}"` para executar a pesquisa no backend

### Exemplo completo

```xhtml
<div class="form-group">
    <label for="filtro">Pesquisar</label>
    <h:inputText id="filtro" value="#{listagemMB.filtro}"
                 styleClass="form-control"
                 placeholder="Digite para pesquisar...">
        <f:ajax event="keyup" render="tabelaAvaliacoes paginacao"
                listener="#{listagemMB.pesquisar}" delay="300"/>
    </h:inputText>
</div>

<h:panelGroup id="tabelaAvaliacoes">
    <h:dataTable value="#{listagemMB.avaliacoes}" var="avaliacao" .../>
</h:panelGroup>

<h:panelGroup id="paginacao" layout="block">
    <!-- paginacao -->
</h:panelGroup>
```

### Explicacao dos atributos

| Atributo | Valor | Funcao |
|---|---|---|
| `event` | `keyup` | Dispara a requisicao ao soltar uma tecla |
| `render` | `"tabelaAvaliacoes paginacao"` | IDs dos componentes a serem atualizados no cliente |
| `execute` | `"@this"` (padrao) | Executa apenas o componente que disparou o evento |
| `delay` | `300` | Aguarda 300ms de pausa na digitacao antes de disparar |
| `listener` | `#{bean.pesquisar}` | Metodo chamado no servidor para processar o filtro |

### Fluxo no servidor

```java
public void pesquisar() {
    pagina = 1; // reseta para a primeira pagina
    carregarDados();
}

private void carregarDados() {
    int total = avaliacaoService.contar(filtro);
    totalPaginas = (int) Math.ceil((double) total / TAMANHO_PAGINA);
    avaliacoes = avaliacaoService.pesquisar(filtro, pagina, TAMANHO_PAGINA);
}
```

No DAO, a pesquisa usa `PreparedStatement` com `LIKE` (case-insensitive) e `LIMIT/OFFSET`:

```sql
SELECT ... FROM avaliacao
WHERE LOWER(titulo) LIKE LOWER(?) OR LOWER(descricao) LIKE LOWER(?)
ORDER BY data_criacao DESC LIMIT ? OFFSET ?
```

---

## 3. Conversacao e estado entre abas (sem perder dados)

### Escopo `@ViewScoped`

Toda a pagina de detalhe da avaliacao utiliza `@ViewScoped`, o que significa que o Managed Bean mantem o estado enquanto o usuario permanecer na mesma view JSF (mesmo URL). As abas sao simnuladas com links `h:commandLink` que alteram uma propriedade `activeTab` e renderizam apenas o conteudo via AJAX.

### Controle de estado

```java
@ManagedBean
@ViewScoped
public class AvaliacaoMB implements Serializable {
    private String activeTab = "dados-basicos";
    private Avaliacao avaliacao;
    private List<Processo> processos; // lazy: carregado sob demanda
    // ...
}
```

### Como funciona

1. O usuario clica em uma aba → `f:ajax` executa `setActiveTab('processos')` e `aoAtivarAba()`
2. O servidor altera o estado do bean e renderiza parcialmente `tabContentInner`
3. Como o bean e `@ViewScoped`, os dados das abas anteriores permanecem na memoria
4. O formulario da aba "Dados Basicos" mantem os valores pois esta dentro do mesmo `h:form` e e atualizado apenas quando o usuario clica em "Salvar"

### Lazy loading de abas

```java
public void aoAtivarAba() {
    switch (activeTab) {
        case "processos":
            if (processos == null) carregarProcessos();
            break;
        case "riscos":
            if (riscos == null) carregarRiscos(); // ou null
            break;
        // ...
    }
}
```

Isso garante que dados de abas nao acessadas nunca sao carregados, otimizando performance.

### `f:viewParam` para receber parametro

```xhtml
<f:metadata>
    <f:viewParam name="Id" value="#{avaliacaoMB.avaliacao.id}"
                 converter="javax.faces.Long" />
</f:metadata>
```

No `@PostConstruct`, o ID e lido diretamente do request parameter para carregar a avaliacao.

### Navegacao entre paginas

- **Salvar**: `return "avaliacao.xhtml?Id=" + id + "&faces-redirect=true"` → redireciona para a mesma pagina com o ID (gera uma nova view, mas o dado foi persistido)
- **Voltar**: `return "listagem.xhtml?faces-redirect=true"` → sai da view atual e volta a listagem

---

## 4. Guia rapido de build, deploy e execucao no Tomcat 9 via VS Code

### Pre-requisitos

- JDK 8+ instalado (`JAVA_HOME` configurado)
- Apache Maven 3.6+ instalado
- Apache Tomcat 9 instalado
- PostgreSQL 14+ rodando
- VS Code com extensoes:
  - "Extension Pack for Java" (Microsoft)
  - "Tomcat for Java" ou "Community Server Connectors"

### 1. Criar o banco de dados PostgreSQL

```sql
CREATE DATABASE avaliacao_riscos;
```

Executar o script `sql/ddl-avaliacao-riscos.sql` no banco criado.

### 2. Configurar DataSource no Tomcat

Editar `$TOMCAT_HOME/conf/server.xml` ou usar o `context.xml` do projeto:

```xml
<!-- Em META-INF/context.xml dentro do WAR -->
<Resource name="jdbc/AvaliacaoRiscosDS"
          auth="Container"
          type="javax.sql.DataSource"
          driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://localhost:5432/avaliacao_riscos"
          username="postgres"
          password="postgres"
          maxTotal="20"
          maxIdle="5"
          maxWaitMillis="10000"
          validationQuery="SELECT 1"
          testOnBorrow="true"/>
```

Copiar o driver JDBC do PostgreSQL (`postgresql-42.7.4.jar`) para `$TOMCAT_HOME/lib/`.

### 3. Build com Maven

```bash
cd avaliacao-riscos
mvn clean package
```

O arquivo `target/avaliacao-riscos.war` sera gerado.

### 4. Deploy no Tomcat pelo VS Code

**Opcao A - Community Server Connectors:**
1. Ctrl+Shift+P → "Community Server Connectors: Create Server" → selecione a pasta do Tomcat 9
2. Clique com direito no servidor → "Add Deployment" → selecione o arquivo WAR ou a pasta alvo
3. Clique com direito no servidor → "Start Server"

**Opcao B - Manual:**
1. Copie `target/avaliacao-riscos.war` para `$TOMCAT_HOME/webapps/`
2. Inicie o Tomcat: `$TOMCAT_HOME/bin/startup.bat`

### 5. Verificar o deploy

Acesse: http://localhost:8080/avaliacao-riscos/

### 6. Logs

```bash
tail -f $TOMCAT_HOME/logs/catalina.out
# ou no Windows:
Get-Content $TOMCAT_HOME\logs\catalina.<data>.log -Wait
```

### Estrutura final do projeto (resumo)

```
avaliacao-riscos/
├── pom.xml
├── sql/ddl-avaliacao-riscos.sql
├── src/main/java/com/avaliacao/
│   ├── model/        (8 entidades)
│   ├── dao/          (interfaces + 7 DAOs + GenericDAOImpl)
│   ├── service/      (7 services)
│   ├── controller/   (3 managed beans)
│   ├── filter/       (2 filtros OWASP)
│   ├── util/         (3 utilitarios)
│   └── exception/    (2 excecoes)
├── src/main/webapp/
│   ├── META-INF/context.xml
│   ├── WEB-INF/{web.xml, faces-config.xml}
│   ├── WEB-INF/templates/
│   │   ├── layout.xhtml
│   │   └── includes/ (10 partials)
│   ├── resources/{css, js}/
│   └── {index, listagem, avaliacao, error}.xhtml
└── DOCS.md
```
