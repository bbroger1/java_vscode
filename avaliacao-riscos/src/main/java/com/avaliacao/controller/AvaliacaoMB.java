package com.avaliacao.controller;

import com.avaliacao.dao.JuncaoDAO;
import com.avaliacao.dao.JuncaoDAOImpl;
import com.avaliacao.dto.*;
import com.avaliacao.model.*;
import com.avaliacao.service.*;
import com.avaliacao.util.DebugLog;
import com.avaliacao.exception.NegocioException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class AvaliacaoMB implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AvaliacaoMB.class.getName());

    private AvaliacaoService avaliacaoService = new AvaliacaoService();
    private ArvoreService arvoreService = new ArvoreService();
    private ProcessoService processoService = new ProcessoService();
    private RiscoService riscoService = new RiscoService();
    private FatorService fatorService = new FatorService();
    private ControleService controleService = new ControleService();
    private TesteService testeService = new TesteService();
    private ModeloNegocioService modeloNegocioService = new ModeloNegocioService();
    private JuncaoDAO juncaoDAO = new JuncaoDAOImpl();

    private Avaliacao avaliacao;
    private String activeTab = "dados-basicos";

    // Arvore em DTOs
    private List<ProcessoDTO> arvoreProcessos;
    private List<ProcessoDTO> arvoreRiscos;
    private List<ProcessoDTO> arvoreFatores;
    private List<ProcessoDTO> arvoreControles;
    private List<ProcessoDTO> arvoreTestes;

    // Modal de seleção
    private List<?> itensSelecao;
    private String filtroSelecao;
    private Long parentIdSelecao;

    // Preparação para abertura de modal de seleção no contexto da árvore
    private String tipoVinculo;

    // Vinculação de Teste (N:M com Controle via teste_controle)
    private Map<Long, Boolean> mapSelecaoControles = new HashMap<>();

    // Exclusão
    private String entidadeExcluir;
    private Long idEntidadeExcluir;
    private String mensagemExclusao;

    // Visualização
    private Object itemVisualizar;

    @PostConstruct
    public void init() {
        System.out.println(">>> CONSTRUTOR AvaliacaoBean chamado! <<<");
        System.out.flush();
        String id = FacesContext.getCurrentInstance()
            .getExternalContext().getRequestParameterMap().get("Id");
        if (id != null && !id.isEmpty()) {
            Long avaliacaoId = Long.valueOf(id);
            avaliacao = avaliacaoService.buscarPorId(avaliacaoId);
        } else {
            avaliacao = new Avaliacao();
        }
    }

    public void teste() {
        System.out.println("ACTION LISTENER FUNCIONOU");
    }

    // =========================================================
    // NAVEGACAO ENTRE ABAS (LAZY LOADING)
    // =========================================================

    public void setActiveTab(String activeTab) {
        if (avaliacao.getId() == null && !"dados-basicos".equals(activeTab)) {
            return;
        }
        this.activeTab = activeTab;
        if (avaliacao.getId() == null) return;
        switch (activeTab) {
            case "processos":
                if (arvoreProcessos == null) arvoreProcessos = arvoreService.carregarProcessos(avaliacao.getId());
                break;
            case "riscos":
                if (arvoreRiscos == null) arvoreRiscos = arvoreService.carregarRiscos(avaliacao.getId());
                break;
            case "fatores":
                if (arvoreFatores == null) arvoreFatores = arvoreService.carregarFatores(avaliacao.getId());
                break;
            case "controles":
                if (arvoreControles == null) arvoreControles = arvoreService.carregarControles(avaliacao.getId());
                break;
            case "testes":
                if (arvoreTestes == null) arvoreTestes = arvoreService.carregarTestes(avaliacao.getId());
                break;
        }
    }

    // =========================================================
    // MODAL DE SELECAO (Vincular entidade existente)
    // =========================================================

    private void executarPesquisaSelecao() {
        String tipo = (tipoVinculo != null && !tipoVinculo.isEmpty()) ? tipoVinculo : activeTab;
        System.out.println(">>> ENTROU EM executarPesquisaSelecao! tipo=" + tipo + ", filtro=" + filtroSelecao);
        DebugLog.log("executarPesquisaSelecao() chamado. tipo=" + tipo + ", filtro=" + filtroSelecao);
        try {
            switch (tipo) {
                case "processos":
                case "processo":
                    List<Processo> processos = arvoreService.listarProcessosDisponiveis(avaliacao.getId());
                    if (filtroSelecao != null && !filtroSelecao.trim().isEmpty()) {
                        String f = filtroSelecao.trim().toLowerCase();
                        List<Processo> filtrados = new ArrayList<>();
                        for (Processo p : processos) {
                            if ((p.getNome() != null && p.getNome().toLowerCase().contains(f))
                                || (p.getCodigo() != null && p.getCodigo().toLowerCase().contains(f))) {
                                filtrados.add(p);
                            }
                        }
                        itensSelecao = filtrados;
                    } else {
                        itensSelecao = processos;
                    }
                    break;
                case "riscos":
                case "risco":
                    List<Risco> riscos = riscoService.listarTodos();
                    if (filtroSelecao != null && !filtroSelecao.trim().isEmpty()) {
                        String f = filtroSelecao.trim().toLowerCase();
                        List<Risco> filtrados = new ArrayList<>();
                        for (Risco r : riscos) {
                            if (r.getNome() != null && r.getNome().toLowerCase().contains(f)) {
                                filtrados.add(r);
                            }
                        }
                        itensSelecao = filtrados;
                    } else {
                        itensSelecao = riscos;
                    }
                    break;
                case "fatores":
                case "fator":
                    List<Fator> fatores = fatorService.listarTodos();
                    if (filtroSelecao != null && !filtroSelecao.trim().isEmpty()) {
                        String f = filtroSelecao.trim().toLowerCase();
                        List<Fator> filtrados = new ArrayList<>();
                        for (Fator fa : fatores) {
                            if (fa.getNome() != null && fa.getNome().toLowerCase().contains(f)) {
                                filtrados.add(fa);
                            }
                        }
                        itensSelecao = filtrados;
                    } else {
                        itensSelecao = fatores;
                    }
                    break;
                case "controles":
                case "controle":
                    List<Controle> controles = controleService.listarTodos();
                    if (filtroSelecao != null && !filtroSelecao.trim().isEmpty()) {
                        String f = filtroSelecao.trim().toLowerCase();
                        List<Controle> filtrados = new ArrayList<>();
                        for (Controle c : controles) {
                            if (c.getNome() != null && c.getNome().toLowerCase().contains(f)) {
                                filtrados.add(c);
                            }
                        }
                        itensSelecao = filtrados;
                    } else {
                        itensSelecao = controles;
                    }
                    break;
                case "testes":
                case "teste":
                    List<Teste> testes = testeService.listarTodos();
                    if (filtroSelecao != null && !filtroSelecao.trim().isEmpty()) {
                        String f = filtroSelecao.trim().toLowerCase();
                        List<Teste> filtrados = new ArrayList<>();
                        for (Teste t : testes) {
                            if (t.getNome() != null && t.getNome().toLowerCase().contains(f)) {
                                filtrados.add(t);
                            }
                        }
                        itensSelecao = filtrados;
                    } else {
                        itensSelecao = testes;
                    }
                    break;
                default:
                    itensSelecao = null;
            }
            DebugLog.log("itensSelecao=" + (itensSelecao == null ? "null" : itensSelecao.size() + " itens"));
        } catch (Exception e) {
            itensSelecao = null;
            DebugLog.log("ERRO em executarPesquisaSelecao: " + e.getMessage(), e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    public void pesquisarSelecao(javax.faces.event.AjaxBehaviorEvent event) {
        executarPesquisaSelecao();
    }

    public void pesquisarSelecao(ActionEvent event) {
        executarPesquisaSelecao();
    }

    public void pesquisarSelecao() {
        executarPesquisaSelecao();
    }

    public void selecionarItem(Object item) {
        if (avaliacao.getId() == null) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR,
                "Salve a avaliação antes de vincular.");
            return;
        }
        try {
            if (item instanceof Processo) {
                arvoreService.vincularProcessoAvaliacao(avaliacao.getId(), ((Processo) item).getId());
                arvoreProcessos = arvoreService.carregarProcessos(avaliacao.getId());
            } else if (item instanceof Risco) {
                if (parentIdSelecao == null) {
                    throw new NegocioException("O processo pai não foi selecionado.");
                }
                arvoreService.vincularRiscoProcesso(avaliacao.getId(), parentIdSelecao, ((Risco) item).getId());
                arvoreRiscos = arvoreService.carregarRiscos(avaliacao.getId());
            } else if (item instanceof Fator) {
                if (parentIdSelecao == null) {
                    throw new NegocioException("O risco pai não foi selecionado.");
                }
                Long processoIdRf = obterProcessoIdPorRisco(avaliacao.getId(), parentIdSelecao);
                arvoreService.vincularFatorRisco(avaliacao.getId(), processoIdRf, parentIdSelecao, ((Fator) item).getId());
                arvoreFatores = arvoreService.carregarFatores(avaliacao.getId());
            } else if (item instanceof Controle) {
                if (parentIdSelecao == null) {
                    throw new NegocioException("O fator pai não foi selecionado.");
                }
                Long processoIdFc = obterProcessoIdPorFator(avaliacao.getId(), parentIdSelecao);
                Long riscoIdFc = obterRiscoIdPorFator(avaliacao.getId(), parentIdSelecao);
                arvoreService.vincularControleFator(avaliacao.getId(), processoIdFc, riscoIdFc, parentIdSelecao, ((Controle) item).getId());
                arvoreControles = arvoreService.carregarControles(avaliacao.getId());
            } else if (item instanceof Teste) {
                if (parentIdSelecao == null) {
                    throw new NegocioException("O controle pai não foi selecionado.");
                }
                Long processoIdCt = obterProcessoIdPorControle(avaliacao.getId(), parentIdSelecao);
                Long riscoIdCt = obterRiscoIdPorControle(avaliacao.getId(), parentIdSelecao);
                Long fatorIdCt = obterFatorIdPorControle(avaliacao.getId(), parentIdSelecao);
                arvoreService.vincularTesteControle(avaliacao.getId(), processoIdCt, riscoIdCt, fatorIdCt, parentIdSelecao, ((Teste) item).getId());
                arvoreTestes = arvoreService.carregarTestes(avaliacao.getId());
            }
            parentIdSelecao = null;
            tipoVinculo = null;
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Vinculo realizado com sucesso!");
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao vincular: " + e.getMessage());
        }
    }

    // =========================================================
    // DESVINCULAR
    // =========================================================

    public void prepararDesvincular(String entidade, Long id) {
        this.entidadeExcluir = entidade;
        this.idEntidadeExcluir = id;
        this.mensagemExclusao = "Deseja realmente desvincular este item?";
    }

    public void confirmarDesvinculacao() {
        if (idEntidadeExcluir == null) return;
        try {
            switch (entidadeExcluir) {
                case "processo":
                    arvoreService.desvincularProcessoAvaliacao(avaliacao.getId(), idEntidadeExcluir);
                    arvoreProcessos = arvoreService.carregarProcessos(avaliacao.getId());
                    break;
                case "risco":
                    Long processoIdR = obterProcessoIdPorRisco(avaliacao.getId(), idEntidadeExcluir);
                    arvoreService.desvincularRiscoProcesso(processoIdR, idEntidadeExcluir);
                    arvoreRiscos = arvoreService.carregarRiscos(avaliacao.getId());
                    break;
                case "fator":
                    Long riscoIdF = obterRiscoIdPorFator(avaliacao.getId(), idEntidadeExcluir);
                    arvoreService.desvincularFatorRisco(riscoIdF, idEntidadeExcluir);
                    arvoreFatores = arvoreService.carregarFatores(avaliacao.getId());
                    break;
                case "controle":
                    Long fatorIdC = obterFatorIdPorControle(avaliacao.getId(), idEntidadeExcluir);
                    arvoreService.desvincularControleFator(fatorIdC, idEntidadeExcluir);
                    arvoreControles = arvoreService.carregarControles(avaliacao.getId());
                    break;
                case "teste":
                    Long controleIdT = obterControleIdPorTeste(avaliacao.getId(), idEntidadeExcluir);
                    arvoreService.desvincularTesteControle(controleIdT, idEntidadeExcluir);
                    arvoreTestes = arvoreService.carregarTestes(avaliacao.getId());
                    break;
            }
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Desvinculado com sucesso!");
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        idEntidadeExcluir = null;
        entidadeExcluir = null;
    }

    // =========================================================
    // VISUALIZAR (read-only)
    // =========================================================

    public void visualizarItem(Object item) {
        System.out.println(">>> visualizarItem chamado com: " + (item != null ? item.getClass().getName() + " -> " + item.toString() : "null"));
        this.itemVisualizar = item;
    }

    public void prepararVisualizar() {
        System.out.println(">>> prepararVisualizar chamado. itemVisualizar: " + (itemVisualizar != null ? itemVisualizar.getClass().getName() + " -> " + itemVisualizar.toString() : "null"));
    }

    public String getTipoItemVisualizar() {
        if (itemVisualizar == null) {
            return "";
        }
        String name = itemVisualizar.getClass().getSimpleName();
        if (name.endsWith("DTO")) {
            return name.substring(0, name.length() - 3).toLowerCase();
        }
        return name.toLowerCase();
    }

    // =========================================================
    // PREPARAR VINCULO (definir pai no contexto da arvore)
    // =========================================================

    public void prepararVinculo(String tipoVinculo, Long parentId) {
        this.tipoVinculo = tipoVinculo;
        this.parentIdSelecao = parentId;
        this.filtroSelecao = "";
        executarPesquisaSelecao();
    }

    // =========================================================
    // MODAIS CRUD (NOVO) - criar nova entidade
    // =========================================================

    public void prepararNovoProcesso() {
        Processo p = new Processo();
        Long id = processoService.salvar(p);
        arvoreService.vincularProcessoAvaliacao(avaliacao.getId(), id);
        arvoreProcessos = arvoreService.carregarProcessos(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Processo criado e vinculado com sucesso!");
    }

    public void prepararNovoRisco() {
        Risco r = new Risco();
        Long id = riscoService.salvar(r);
        arvoreService.vincularRiscoProcesso(avaliacao.getId(), parentIdSelecao, id);
        arvoreRiscos = arvoreService.carregarRiscos(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Risco criado e vinculado com sucesso!");
    }

    public void prepararNovoFator() {
        Fator f = new Fator();
        Long id = fatorService.salvar(f);
        Long processoId = obterProcessoIdPorRisco(avaliacao.getId(), parentIdSelecao);
        arvoreService.vincularFatorRisco(avaliacao.getId(), processoId, parentIdSelecao, id);
        arvoreFatores = arvoreService.carregarFatores(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Fator criado e vinculado com sucesso!");
    }

    public void prepararNovoControle() {
        Controle c = new Controle();
        Long id = controleService.salvar(c);
        Long processoId = obterProcessoIdPorFator(avaliacao.getId(), parentIdSelecao);
        Long riscoId = obterRiscoIdPorFator(avaliacao.getId(), parentIdSelecao);
        arvoreService.vincularControleFator(avaliacao.getId(), processoId, riscoId, parentIdSelecao, id);
        arvoreControles = arvoreService.carregarControles(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Controle criado e vinculado com sucesso!");
    }

    public void prepararNovoTeste() {
        Teste t = new Teste();
        Long id = testeService.salvar(t);
        Long processoId = obterProcessoIdPorControle(avaliacao.getId(), parentIdSelecao);
        Long riscoId = obterRiscoIdPorControle(avaliacao.getId(), parentIdSelecao);
        Long fatorId = obterFatorIdPorControle(avaliacao.getId(), parentIdSelecao);
        arvoreService.vincularTesteControle(avaliacao.getId(), processoId, riscoId, fatorId, parentIdSelecao, id);
        arvoreTestes = arvoreService.carregarTestes(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Teste criado e vinculado com sucesso!");
    }

    // =========================================================
    // CRUD AVALIACAO
    // =========================================================

    public String salvarAvaliacao() {
        try {
            avaliacaoService.salvar(avaliacao);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Avaliacao salva com sucesso!");
            FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().setKeepMessages(true);
            return "avaliacao.xhtml?Id=" + avaliacao.getId() + "&faces-redirect=true";
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar avaliacao: " + e.getMessage());
            return null;
        }
    }

    public String voltar() {
        return "listagem.xhtml?faces-redirect=true";
    }

    // =========================================================
    // N:M TESTE <-> CONTROLE (teste_controle)
    // =========================================================

    public void prepararVinculoTesteControle(TesteDTO teste) {
        mapSelecaoControles.clear();
        List<Long> vinculados = juncaoDAO.listarControlesPorTeste(teste.getId());
        List<Controle> todosControles = controleService.listarTodos();
        for (Controle c : todosControles) {
            mapSelecaoControles.put(c.getId(), vinculados.contains(c.getId()));
        }
        this.itemVisualizar = teste;
    }

    public void salvarVinculosTeste() {
        TesteDTO teste = (TesteDTO) itemVisualizar;
        if (teste == null) return;
        juncaoDAO.limparVinculosTeste(teste.getId());
        for (Map.Entry<Long, Boolean> entry : mapSelecaoControles.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                juncaoDAO.inserirVinculoTesteControle(teste.getId(), entry.getKey());
            }
        }
        arvoreTestes = arvoreService.carregarTestes(avaliacao.getId());
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Vinculos do teste atualizados!");
    }

    // =========================================================
    // METODOS AUXILIARES (navegar pela arvore)
    // =========================================================

    private Long obterProcessoIdPorRisco(Long avaliacaoId, Long riscoId) {
        for (ProcessoDTO p : arvoreRiscos) {
            for (RiscoDTO r : p.getRiscos()) {
                if (r.getId().equals(riscoId)) return p.getId();
            }
        }
        return null;
    }

    private Long obterProcessoIdPorFator(Long avaliacaoId, Long fatorId) {
        for (ProcessoDTO p : arvoreFatores) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    if (f.getId().equals(fatorId)) return p.getId();
                }
            }
        }
        return null;
    }

    private Long obterRiscoIdPorFator(Long avaliacaoId, Long fatorId) {
        for (ProcessoDTO p : arvoreFatores) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    if (f.getId().equals(fatorId)) return r.getId();
                }
            }
        }
        return null;
    }

    private Long obterProcessoIdPorControle(Long avaliacaoId, Long controleId) {
        for (ProcessoDTO p : arvoreControles) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    for (ControleDTO c : f.getControles()) {
                        if (c.getId().equals(controleId)) return p.getId();
                    }
                }
            }
        }
        return null;
    }

    private Long obterRiscoIdPorControle(Long avaliacaoId, Long controleId) {
        for (ProcessoDTO p : arvoreControles) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    for (ControleDTO c : f.getControles()) {
                        if (c.getId().equals(controleId)) return r.getId();
                    }
                }
            }
        }
        return null;
    }

    private Long obterFatorIdPorControle(Long avaliacaoId, Long controleId) {
        for (ProcessoDTO p : arvoreControles) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    for (ControleDTO c : f.getControles()) {
                        if (c.getId().equals(controleId)) return f.getId();
                    }
                }
            }
        }
        return null;
    }

    private Long obterControleIdPorTeste(Long avaliacaoId, Long testeId) {
        for (ProcessoDTO p : arvoreTestes) {
            for (RiscoDTO r : p.getRiscos()) {
                for (FatorDTO f : r.getFatores()) {
                    for (ControleDTO c : f.getControles()) {
                        for (TesteDTO t : c.getTestes()) {
                            if (t.getId().equals(testeId)) return c.getId();
                        }
                    }
                }
            }
        }
        return null;
    }

    // =========================================================
    // UTIL
    // =========================================================

    private void adicionarMensagem(FacesMessage.Severity severity, String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, ""));
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Avaliacao getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }
    public String getActiveTab() { return activeTab; }

    public List<ProcessoDTO> getArvoreProcessos() { return arvoreProcessos; }
    public List<ProcessoDTO> getArvoreRiscos() { return arvoreRiscos; }
    public List<ProcessoDTO> getArvoreFatores() { return arvoreFatores; }
    public List<ProcessoDTO> getArvoreControles() { return arvoreControles; }
    public List<ProcessoDTO> getArvoreTestes() { return arvoreTestes; }

    public String getFiltroSelecao() { return filtroSelecao; }
    public void setFiltroSelecao(String filtroSelecao) { this.filtroSelecao = filtroSelecao; }

    public List<?> getItensSelecao() { return itensSelecao; }

    public Long getParentIdSelecao() { return parentIdSelecao; }
    public void setParentIdSelecao(Long parentIdSelecao) { this.parentIdSelecao = parentIdSelecao; }

    public String getEntidadeExcluir() { return entidadeExcluir; }
    public Long getIdEntidadeExcluir() { return idEntidadeExcluir; }
    public String getMensagemExclusao() { return mensagemExclusao; }

    public Object getItemVisualizar() { return itemVisualizar; }

    public Map<Long, Boolean> getMapSelecaoControles() { return mapSelecaoControles; }

    public String getTipoVinculo() { return tipoVinculo; }
    public void setTipoVinculo(String tipoVinculo) { this.tipoVinculo = tipoVinculo; }
}
