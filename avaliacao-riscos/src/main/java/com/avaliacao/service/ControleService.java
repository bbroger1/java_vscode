package com.avaliacao.service;

import com.avaliacao.dao.ControleDAO;
import com.avaliacao.dao.ControleDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Controle;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class ControleService implements Serializable {
    private static final long serialVersionUID = 1L;

    private ControleDAO dao = new ControleDAOImpl();

    public Controle buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do controle é obrigatorio.");
        }
        Controle c = dao.buscarPorId(id);
        if (c == null) {
            throw new NegocioException("Controle nao encontrado.");
        }
        return c;
    }

    public List<Controle> listarTodos() {
        return dao.listarTodos();
    }

    public List<Controle> buscarPorIds(java.util.Set<Long> ids) {
        return dao.buscarPorIds(ids);
    }

    public Long salvar(Controle c) {
        validarControle(c);
        if (c.getId() != null) {
            dao.atualizar(c);
            return c.getId();
        }
        return dao.inserir(c);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do controle é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarControle(Controle c) {
        ValidadorUtil.validarObrigatorio(c.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(c.getNome(), 255, "Nome");
        if (c.getStatus() == null || c.getStatus().trim().isEmpty()) {
            c.setStatus("ATIVO");
        }
    }
}
