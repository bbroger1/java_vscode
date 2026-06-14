package com.avaliacao.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Teste extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String descricao;
    private String tipo;
    private String resultado;
    private LocalDate dataExecucao;
    private List<Controle> controles;

    public Teste() {
        this.resultado = "NAO_EXECUTADO";
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

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDate dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public List<Controle> getControles() { return controles; }
    public void setControles(List<Controle> controles) { this.controles = controles; }

    @Override
    public String toString() {
        return nome + " (" + resultado + ")";
    }
}
