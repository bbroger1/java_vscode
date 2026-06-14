package com.avaliacao.model;

import java.util.ArrayList;
import java.util.List;

public class Risco extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String probabilidade;
    private String impacto;
    private String nivel;
    private List<Processo> processos;
    private List<Fator> fatores;

    public Risco() {
        this.processos = new ArrayList<>();
        this.fatores = new ArrayList<>();
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

    public String getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(String probabilidade) {
        this.probabilidade = probabilidade;
    }

    public String getImpacto() {
        return impacto;
    }

    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public List<Processo> getProcessos() { return processos; }
    public void setProcessos(List<Processo> processos) { this.processos = processos; }

    public List<Fator> getFatores() { return fatores; }
    public void setFatores(List<Fator> fatores) { this.fatores = fatores; }

    @Override
    public String toString() {
        return nome + (nivel != null ? " (" + nivel + ")" : "");
    }
}
