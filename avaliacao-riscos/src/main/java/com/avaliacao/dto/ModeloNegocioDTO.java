package com.avaliacao.dto;

import java.io.Serializable;

public class ModeloNegocioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private String versao;

    public ModeloNegocioDTO() {
    }

    public ModeloNegocioDTO(Long id, String nome, String descricao, String versao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.versao = versao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }

    @Override
    public String toString() {
        return nome + (versao != null ? " v" + versao : "");
    }
}
