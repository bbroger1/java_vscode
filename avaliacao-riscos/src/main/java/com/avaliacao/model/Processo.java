package com.avaliacao.model;

import java.util.ArrayList;
import java.util.List;

public class Processo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String codigo;
    private List<Avaliacao> avaliacoes;

    public Processo() {
        this.avaliacoes = new ArrayList<>();
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }

    @Override
    public String toString() {
        return (codigo != null ? "[" + codigo + "] " : "") + nome;
    }
}
