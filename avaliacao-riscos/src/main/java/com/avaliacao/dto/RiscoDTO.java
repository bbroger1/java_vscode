package com.avaliacao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RiscoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private String probabilidade;
    private String impacto;
    private String nivel;
    private List<FatorDTO> fatores;

    public RiscoDTO() {
        this.fatores = new ArrayList<>();
    }

    public RiscoDTO(Long id, String nome, String descricao, String probabilidade,
                    String impacto, String nivel) {
        this();
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.probabilidade = probabilidade;
        this.impacto = impacto;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getProbabilidade() { return probabilidade; }
    public void setProbabilidade(String probabilidade) { this.probabilidade = probabilidade; }

    public String getImpacto() { return impacto; }
    public void setImpacto(String impacto) { this.impacto = impacto; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public List<FatorDTO> getFatores() { return fatores; }
    public void setFatores(List<FatorDTO> fatores) { this.fatores = fatores; }

    @Override
    public String toString() {
        return nome + (nivel != null ? " (" + nivel + ")" : "");
    }
}
