package com.avaliacao.controller;

import com.avaliacao.model.Avaliacao;
import com.avaliacao.service.AvaliacaoService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class ListagemMB implements Serializable {
    private static final long serialVersionUID = 1L;

    private AvaliacaoService avaliacaoService = new AvaliacaoService();

    private List<Avaliacao> avaliacoes;
    private String filtro = "";
    private int pagina = 1;
    private int totalPaginas = 1;
    private static final int TAMANHO_PAGINA = 10;

    private Long idParaExcluir;

    @PostConstruct
    public void init() {
        pesquisar();
    }

    public void pesquisar() {
        pagina = 1;
        carregarDados();
    }

    public void avancarPagina() {
        if (pagina < totalPaginas) {
            pagina++;
            carregarDados();
        }
    }

    public void retrocederPagina() {
        if (pagina > 1) {
            pagina--;
            carregarDados();
        }
    }

    public void irParaPagina(int p) {
        if (p >= 1 && p <= totalPaginas) {
            pagina = p;
            carregarDados();
        }
    }

    private void carregarDados() {
        int total = avaliacaoService.contar(filtro);
        totalPaginas = (int) Math.ceil((double) total / TAMANHO_PAGINA);
        if (totalPaginas < 1) totalPaginas = 1;
        avaliacoes = avaliacaoService.pesquisar(filtro, pagina, TAMANHO_PAGINA);
    }

    public String navegarParaAvaliacao(Long id) {
        return "avaliacao.xhtml?Id=" + id + "&faces-redirect=true";
    }

    public String novaAvaliacao() {
        return "avaliacao.xhtml?faces-redirect=true";
    }

    public void prepararExcluir(Long id) {
        this.idParaExcluir = id;
    }

    public void excluir() {
        try {
            avaliacaoService.excluir(idParaExcluir);
            carregarDados();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Avaliação excluída com sucesso!", ""));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao excluir avaliação: " + e.getMessage(), ""));
        }
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public Long getIdParaExcluir() {
        return idParaExcluir;
    }

    public void setIdParaExcluir(Long idParaExcluir) {
        this.idParaExcluir = idParaExcluir;
    }
}
