package com.avaliacao.model;

public class ModeloNegocio extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String versao;

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

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    @Override
    public String toString() {
        return nome + (versao != null ? " v" + versao : "");
    }
}
