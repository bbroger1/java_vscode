package com.avaliacao.model;

import java.time.LocalDateTime;

public class Avaliacao extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String descricao;
    private String status;

    public Avaliacao() {
        this.status = "EM_ANDAMENTO";
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusFormatado() {
        if (status == null) return "";
        return status.replace("_", " ");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return titulo + " (" + status + ")";
    }
}
