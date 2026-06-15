# AGENTS.md — Projeto Avaliação de Riscos e Controles

**Última atualização:** 2026-06-14

---

## 1. Visão Geral

Sistema web para registro de avaliações de riscos e controles. Stack: Java 8 + JSF 2.2 (Mojarra) + JDBC puro + PostgreSQL 14+ + Tomcat 9 + Bootstrap 4.

Hierarquia: `Avaliação → Processo → Risco → Fator → Controle → Teste`

---

## 2. Arquitetura

```
src/main/java/com/avaliacao/
├── controller/     # Managed Beans (@ManagedBean @ViewScoped)
│   ├── AvaliacaoMB.java    # CRUD da avaliação + abas (Processo/Risco/Fator/Controle/Teste)
│   ├── ListagemMB.java     # Listagem com paginação e filtro AJAX
│   └── ModalSelecaoMB.java # RequestScoped para modais de seleção
├── dao/
│   ├── generic/            # GenericDAOImpl com JDBC + CrudDAO/SearchableDAO interfaces
│   └── *DAO.java / *DAOImpl.java  # 7 DAOs (Avaliacao, Processo, Risco, Fator, Controle, Teste, ModeloNegocio)
├── service/         # 7 Services com validação via ValidadorUtil
├── model/           # 8 entidades (BaseEntity + Avaliacao, Processo, Risco, Fator, Controle, Teste, ModeloNegocio)
├── exception/       # NegocioException, InfraestruturaException (Runtime)
├── filter/          # CharacterEncodingFilter, CSRFFilter
└── util/            # ConnectionFactory (JNDI), ValidadorUtil, StringEscapeUtil, PhaseListenerUtil

src/main/webapp/
├── META-INF/context.xml.template  # DataSource JNDI template (não comitado)
├── WEB-INF/
│   ├── web.xml, faces-config.xml
│   └── templates/
│       ├── layout.xhtml     # Template principal (Bootstrap, navbar, footer, scripts, meta app-version)
│       └── includes/        # 13 partials (modais seleção/confirmação, campos, navbar, footer, etc.)
├── resources/css/custom.css
├── resources/js/custom.js
├── index.xhtml (redirect → listagem)
├── listagem.xhtml (CRUD listagem)
├── avaliacao.xhtml (CRUD avaliação + 6 abas com forms isolados)
└── error.xhtml
```

---

## 2.1 Estrutura de Forms em avaliacao.xhtml (NOVO)

```
avaliacao.xhtml
├── <h:form id="avaliacaoForm" prependId="false">     # Navegação abas + Dados Básicos
├── <h:form id="processosForm" prependId="false">      # Aba Processos + modal confirmação exclusão
├── <h:form id="riscosForm" prependId="false">          # Aba Riscos + modal confirmação exclusão
├── <h:form id="fatoresForm" prependId="false">         # Aba Fatores + modal confirmação exclusão
├── <h:form id="controlesForm" prependId="false">       # Aba Controles + modal confirmação exclusão
├── <h:form id="testesForm" prependId="false">          # Aba Testes + modal confirmação exclusão
├── <h:form id="modalSelProcessoForm" prependId="false"> # Modal seleção Processo
├── <h:form id="modalSelRiscoForm" prependId="false">    # Modal seleção Risco
├── <h:form id="modalSelFatorForm" prependId="false">    # Modal seleção Fator
├── <h:form id="modalSelControleForm" prependId="false"> # Modal seleção Controle
├── <h:form id="modalSelTesteForm" prependId="false">    # Modal seleção Teste
├── <h:form id="modalConfirmacaoForm" prependId="false"> # Modal confirmação genérica
├── <h:form id="modalVisualizarForm" prependId="false">  # Modal visualizar (read-only)
└── <h:form id="modalVinculoTesteForm" prependId="false"># Modal N:M Teste↔Controle
```

**Princípios:**
- Cada form isolado com `prependId="false"` → IDs previsíveis para `render`/`execute`
- Submit AJAX carrega apenas campos do form ativo (ViewState mínimo)
- Validação isolada por form
- Modais de confirmação exclusão pertencem ao form da aba correspondente

## 3. Decisões Técnicas Importantes

### 3.1. Modais e `onevent`

O `<f:ajax>` padrão do JSF 2.2 **NÃO** suporta `oncomplete`. Use `onevent` com **nome de função** (código inline causa `SyntaxError`).

Variáveis globais `_modalId` e `_isNovo` são setadas via `onclick` no `h:commandLink` e lidas pelo handler:

```xhtml
<h:commandLink action="#{bean.prepararNovoXxx}"
              onclick="_modalId='modalXxx'; _isNovo=true;">
    <f:ajax execute="@this" render="@none" onevent="handleAjaxComplete"/>
</h:commandLink>
```

Handlers disponíveis em `custom.js`:
- `handleAjaxComplete(data)` — abre modal (`_isNovo=true` → `abrirModalNovo`, senão → `abrirModal`)
- `handleSalvarAjax(data)` — fecha modal após salvar se não houver erros (`li.error, li.fatal`)
- `handleFecharModal(data)` — fecha modal incondicionalmente

### 3.2. Botões "Vincular" (antigo "Novo") nas Abas

- **Ação:** Ao clicar em "Vincular [Entidade]", o sistema abre o `modal-selecao.xhtml` (e não o `modal-crud.xhtml`).
- **Action:** `pesquisarXxx()` → carrega a lista de itens disponíveis no banco (`itensSelecao`).
- **Render:** `@none` (modal já está no DOM).
- **Modal open:** `abrirModal('modalSelXxx')` → exibe a tabela de seleção para o usuário escolher um registro existente e vinculá-lo à avaliação atual.

**Não use** `render=":wrapperXxx"` nos botões de vinculação — o modal wrapper pode não ser encontrado pelo `findComponent` por causa do template Facelets.

### 3.3. Botões "Editar" nas Tabelas

Usam `render=":wrapperXxx"` para re-renderizar o modal com os dados da entidade selecionada. Se não funcionar, substituir por `@none` + popular campos via JavaScript.

### 3.4. Navegação entre Abas

As abas usam `h:commandLink` **sem** `f:ajax` (full POST). Isso recarrega a página. Se quiser melhorar UX, adicione `f:ajax` nos links das abas.

### 3.5. Escopo `@ViewScoped`

O estado é mantido via `@ViewScoped`. Toda a página de detalhe (`avaliacao.xhtml`) usa esse escopo. Dados de abas não acessadas são carregados sob demanda (lazy loading).

### 3.6. Conexão com BD

JNDI DataSource configurado em `META-INF/context.xml`:
- JNDI: `java:comp/env/jdbc/AvaliacaoRiscosDS`
- PostgreSQL, database: `avaliacao_riscos`
- Usuário/senha: `postgres / root`

### 3.8. Compatibilidade Java 8

O projeto **requer Java 8** devido às dependências do JSF 2.2/Mojarra. Scripts de deploy devem configurar explicitamente `JAVA_HOME` para apontar para uma instalação do JDK 8.

### 3.9. Correção de Parâmetros SQL Nulos

O método `GenericDAOImpl.contar()` foi corrigido para tratar parâmetros nulos/vazios nas queries de contagem, evitando o erro "Nenhum valor especificado para parâmetro X".

### 3.10. Relacionamento N:M (Teste ↔ Controle)

Um teste pode validar múltiplos controles. Isso é gerenciado pela tabela de junção `teste_controle`.
- **Frontend:** O modal de edição de Teste (`campos-teste.xhtml`) exibe uma tabela com checkboxes (`h:selectBooleanCheckbox`) para seleção múltipla de controles.
- **Backend:** `AvaliacaoMB` mantém um `Map<Long, Boolean> mapSelecaoControles`. Ao salvar, `TesteService.salvar(teste, listaIds)` sincroniza os vínculos na tabela de junção (limpa os antigos e insere os novos via batch processing).

### 3.11. CSRF Filter

Mapeado para `*.xhtml` no `web.xml`. Token CSRF incluído em todos os forms via `csrf-token.xhtml` (`<h:inputHidden id="csrfToken" name="csrfToken" value="#{sessionScope.csrfToken}"/>`). Filtro procura parâmetro terminando em `:csrfToken` (padrão JSF com prependId).

### 3.12. Segurança - Credenciais Externalizadas

`context.xml` removido do git. Template em `context.xml.template` com placeholders `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}`. Configurar DataSource no Tomcat (`$CATALINA_BASE/conf/context.xml`) ou via variáveis de ambiente.

### 3.13. Performance - Batch Fetch (N+1 Eliminado)

`ArvoreService` refatorado para usar batch fetch via `JuncaoDAO`:
- `listarRiscosPorProcessos(Set<Long>)`, `listarFatoresPorRiscos(Set<Long>)`, etc.
- 6 queries fixas vs N×5 anterior
- Services expõem `buscarPorIds(Set<Long>)` usando `CrudDAO.buscarPorIds`
- DAOs implementam `getSqlListarTodos()` para query dinâmica com `IN` clause

### 3.14. PhaseListener para Monitoramento

`PhaseListenerUtil` registrado no `faces-config.xml` loga duração de cada fase JSF (FINE/INFO level). Útil para identificar gargalos no ciclo de vida.

---

## 4. Problemas Conhecidos e Correções

### 4.1. Modal "Novo" não abre (CORRIGIDO)

**Causa:** O botão "Novo Processo" chamava `pesquisarSelecao()` em vez de `prepararNovoProcesso()`.  
**Solução:** Alterar action para `prepararNovoProcesso()`, render para `@none`, e usar `abrirModalNovo()` que limpa os campos.

### 4.2. `fecharModalSeSemErro` não detectava erros (CORRIGIDO)

**Causa:** A função buscava `.alert-danger` mas JSF renderiza `<li class="error">`.  
**Solução:** Alterar seletor para `li.error, li.fatal`.

### 4.3. Optional chaining no custom.js (CORRIGIDO)

**Causa:** `?.` (optional chaining) não é suportado em browsers antigos.  
**Solução:** Substituir por `if (form) form.reset()`.

### 4.4. Botões "Editar" nas tabelas

Se o modal de edição não abrir ou mostrar dados errados, o `render=":wrapperXxx"` pode não estar encontrando o componente. Corrigir igual aos botões "Novo": usar `@none` e JavaScript para popular os campos.

### 4.5. `onevent` inline → named functions (CORRIGIDO)

**Causa:** `onevent="if (event.status === 'success') ..."` causa `SyntaxError: expected expression, got keyword 'if'` porque o código inline é interpretado como expressão, não como corpo de função.

**Solução:** Substituir por funções nomeadas (`handleAjaxComplete`, `handleSalvarAjax`, `handleFecharModal`) com variáveis globais (`_modalId`, `_isNovo`) setadas via `onclick`. Arquivos alterados:
- `avaliacao.xhtml` — 22 ocorrências (5 Novo + 5 Editar + 5 Excluir + 1 confirma exclusão + ...)
- `modal-crud.xhtml` — botão Salvar
- `modal-selecao.xhtml` — botão Selecionar
- `listagem.xhtml` — botões Excluir e confirmar exclusão

### 4.6. CSRF Token Inválido (CORRIGIDO 2026-06-14)

**Causa:** JSF prefixa `name` do `h:inputHidden` com `formId:` (ex: `avaliacaoForm:csrfToken`), filtro buscava apenas `csrfToken`.  
**Solução:** `CSRFFilter` itera todos parâmetros procurando `paramName.endsWith(":csrfToken")`. Adicionado `id="csrfToken"` no `csrf-token.xhtml`.

### 4.7. Modal Seleção sem Form Próprio (CORRIGIDO 2026-06-14)

**Causa:** `modal-selecao.xhtml` tinha componentes JSF mas sem `<h:form>` próprio, causando submit do form principal inteiro.  
**Solução:** Wrapper `<h:form id="#{modalId}Form" prependId="false">` no template. Modais movidos para fora do `avaliacaoForm` em forms isolados.

### 4.8. Estrutura de Forms por Aba (REFACTOR 2026-06-14)

**Causa:** Form único `avaliacaoForm` com 500+ campos causava ViewState grande, validação cruzada, submit pesado.  
**Solução:** Form por aba (`processosForm`, `riscosForm`, `fatoresForm`, `controlesForm`, `testesForm`) + modais de confirmação exclusão por aba. Todos com `prependId="false"`.

### 4.9. Modais de Visualizar Vazios (CORRIGIDO 2026-06-15)
 
**Causa:** O modal de visualizar abria sem os detalhes das entidades porque a view não continha um container JSF (`h:panelGroup`) atualizável via AJAX no corpo do modal. Além disso, a tentativa de usar `f:setPropertyActionListener` sem a execução do datatable pai falhava em resolver o row context no ciclo de vida JSF.
**Solução:**
- Adicionado o wrapper `<h:panelGroup id="modalVisualizarCorpo" layout="block">` ao corpo do modal.
- Criado layout de grid rico condicional em [avaliacao.xhtml](file:///c:/Users/bbrog/OneDrive/Desktop/java_vscode/avaliacao-riscos/src/main/webapp/avaliacao.xhtml) exibindo os campos correspondentes para cada tipo de DTO (`ProcessoDTO`, `RiscoDTO`, `FatorDTO`, `ControleDTO`, `TesteDTO`).
- Adicionado método helper `getTipoItemVisualizar()` e restaurada a passagem do objeto da linha diretamente via método de ação EL (`action="#{avaliacaoMB.visualizarItem(xxx)}"`), que preserva o contexto da linha de maneira robusta.
 
### 4.10. Layout e Contraste na aba Riscos (CORRIGIDO 2026-06-15)
 
**Causa:** O botão "Vincular Risco" tinha baixo contraste (texto branco sobre fundo azul claro) devido à herança de classes do container de cabeçalho `.list-group-item-primary`. Além disso, os dados da tabela de Riscos estavam todos alinhados à esquerda por padrão.
**Solução:**
- Aplicado estilo inline de alta especificidade no link de vinculação do Risco para impor fundo azul escuro (`#0056b3`) e texto branco legível.
- Configurados os atributos `columnClasses` e `headerClasses` na `<h:dataTable>` de riscos para centralizar horizontalmente os dados e cabeçalhos de **Probabilidade**, **Impacto**, **Nível** e **Ações**.
 
### 4.11. Execução do Deploy e Queda do Tomcat (CORRIGIDO 2026-06-15)
 
**Causa:** A execução de `deploy-avaliacao.bat` através do shell do agente de IA era encerrada abruptamente ao término da execução do agente, derrubando o servidor Tomcat.
**Solução:** Executado o deploy no Windows por meio de `Start-Process cmd.exe` de forma independente no PowerShell, o que abre um Prompt de Comando independente que persiste mesmo após a finalização do agente.

---

## 5. Comandos Úteis

```bash
# Build
cd C:\Users\bbrog\OneDrive\Desktop\java_vscode\avaliacao-riscos
mvn clean package

# Deploy no Tomcat (executar como administrador)
C:\Users\bbrog\OneDrive\Desktop\java_vscode\deploy-avaliacao.bat

# Hot reload (compila Java e copia para o Tomcat explodido)
powershell -File C:\Users\bbrog\OneDrive\Desktop\java_vscode\dev-watch.ps1

# Compilar apenas (sem package)
mvn clean compile
```

---

## 6. Estrutura do Banco

```sql
-- Database: avaliacao_riscos
-- Tabelas: avaliacao, modelo_negocio, processo, risco, fator, controle, teste
-- Todas com PK via sequences (seq_*), FKs com ON DELETE CASCADE
-- Script completo em: sql/ddl-avaliacao-riscos.sql
```

---

## ⚠️ REGRA IMPORTANTE

**Este arquivo deve ser atualizado sempre que houver mudanças significativas no projeto** (novas funcionalidades, correções de bugs, alterações na arquitetura, decisões técnicas relevantes). Mantenha-o sincronizado para que agentes de IA possam dar continuidade ao trabalho sem perder contexto.
