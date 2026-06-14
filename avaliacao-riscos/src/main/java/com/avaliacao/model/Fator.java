package com.avaliacao.model;

import java.util.ArrayList;
import java.util.List;

public class Fator extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String tipo;
    private List<Risco> riscos;
    private List<Controle> controles;

    public Fator() {
        this.riscos = new ArrayList<>();
        this.controles = new ArrayList<>();
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

    public List<Risco> getRiscos() { return riscos; }
    public void setRiscos(List<Risco> riscos) { this.riscos = riscos; }

    public List<Controle> getControles() { return controles; }
    public void setControles(List<Controle> controles) { this.controles = controles; }

    @Override
    public String toString() {
        return nome + (tipo != null ? " (" + tipo + ")" : "");
    }
}
