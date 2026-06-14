package com.avaliacao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FatorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private String tipo;
    private List<ControleDTO> controles;

    public FatorDTO() {
        this.controles = new ArrayList<>();
    }

    public FatorDTO(Long id, String nome, String descricao, String tipo) {
        this();
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<ControleDTO> getControles() { return controles; }
    public void setControles(List<ControleDTO> controles) { this.controles = controles; }

    @Override
    public String toString() {
        return nome + (tipo != null ? " (" + tipo + ")" : "");
    }
}
