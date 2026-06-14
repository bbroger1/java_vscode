package com.avaliacao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private List<RiscoDTO> riscos;

    public ProcessoDTO() {
        this.riscos = new ArrayList<>();
    }

    public ProcessoDTO(Long id, String nome, String descricao, String codigo) {
        this();
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public List<RiscoDTO> getRiscos() { return riscos; }
    public void setRiscos(List<RiscoDTO> riscos) { this.riscos = riscos; }

    @Override
    public String toString() {
        return (codigo != null ? "[" + codigo + "] " : "") + nome;
    }
}
