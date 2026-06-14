package com.avaliacao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ControleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private String tipo;
    private String status;
    private List<ModeloNegocioDTO> modelosNegocio;
    private List<TesteDTO> testes;

    public ControleDTO() {
        this.modelosNegocio = new ArrayList<>();
        this.testes = new ArrayList<>();
    }

    public ControleDTO(Long id, String nome, String descricao, String tipo, String status) {
        this();
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ModeloNegocioDTO> getModelosNegocio() { return modelosNegocio; }
    public void setModelosNegocio(List<ModeloNegocioDTO> modelosNegocio) { this.modelosNegocio = modelosNegocio; }

    public List<TesteDTO> getTestes() { return testes; }
    public void setTestes(List<TesteDTO> testes) { this.testes = testes; }

    @Override
    public String toString() {
        return nome + " (" + status + ")";
    }
}
