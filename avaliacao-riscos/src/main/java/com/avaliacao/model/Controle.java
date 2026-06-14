package com.avaliacao.model;

import java.util.ArrayList;
import java.util.List;

public class Controle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String tipo;
    private String status;
    private List<Fator> fatores;
    private List<ModeloNegocio> modelosNegocio;
    private List<Teste> testes;

    public Controle() {
        this.status = "ATIVO";
        this.fatores = new ArrayList<>();
        this.modelosNegocio = new ArrayList<>();
        this.testes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Fator> getFatores() { return fatores; }
    public void setFatores(List<Fator> fatores) { this.fatores = fatores; }

    public List<ModeloNegocio> getModelosNegocio() { return modelosNegocio; }
    public void setModelosNegocio(List<ModeloNegocio> modelosNegocio) { this.modelosNegocio = modelosNegocio; }

    public List<Teste> getTestes() { return testes; }
    public void setTestes(List<Teste> testes) { this.testes = testes; }

    @Override
    public String toString() {
        return nome + " (" + status + ")";
    }
}
