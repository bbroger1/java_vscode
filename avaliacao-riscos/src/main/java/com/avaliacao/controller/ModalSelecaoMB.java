package com.avaliacao.controller;

import com.avaliacao.service.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@RequestScoped
public class ModalSelecaoMB implements Serializable {
    private static final long serialVersionUID = 1L;

    private ProcessoService processoService = new ProcessoService();
    private RiscoService riscoService = new RiscoService();
    private FatorService fatorService = new FatorService();
    private ControleService controleService = new ControleService();
    private TesteService testeService = new TesteService();
    private ModeloNegocioService modeloNegocioService = new ModeloNegocioService();

    private String tipoEntidade;
    private Long parentId;
    private String filtro;

    public List<?> listarEntidades() {
        if (tipoEntidade == null) return null;
        try {
            switch (tipoEntidade) {
                case "processo":
                    return processoService.listarTodos();
                case "risco":
                    return riscoService.listarTodos();
                case "fator":
                    return fatorService.listarTodos();
                case "controle":
                    return controleService.listarTodos();
                case "teste":
                    return testeService.listarTodos();
                case "modelo_negocio":
                    return modeloNegocioService.listarTodos();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(String tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}
