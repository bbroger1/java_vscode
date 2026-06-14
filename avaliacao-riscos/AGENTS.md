# AGENTS.md — Projeto Avaliação de Riscos e Controles

**Última atualização:** 2026-06-07

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
└── util/            # ConnectionFactory (JNDI), ValidadorUtil, StringEscapeUtil

src/main/webapp/
├── META-INF/context.xml     # DataSource JNDI (jdbc/AvaliacaoRiscosDS)
├── WEB-INF/
│   ├── web.xml, faces-config.xml
│   └── templates/
│       ├── layout.xhtml     # Template principal (Bootstrap, navbar, footer, scripts)
│       └── includes/        # 12 partials (modais CRUD/seleção, campos, navbar, footer, etc.)
├── resources/css/custom.css
├── resources/js/custom.js
├── index.xhtml (redirect → listagem)
├── listagem.xhtml (CRUD listagem)
├── avaliacao.xhtml (CRUD avaliação + 6 abas)
└── error.xhtml
```

---

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

Mapeado para `/protected/*` mas a aplicação não usa esse prefixo. O filtro **não está ativo**. Se precisar ativar, mapear para `*.xhtml` ou adicionar token CSRF nos formulários.

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
