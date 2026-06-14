package com.avaliacao.service;

import com.avaliacao.dao.TesteDAO;
import com.avaliacao.dao.TesteDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Teste;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class TesteService implements Serializable {
    private static final long serialVersionUID = 1L;

    private TesteDAO dao = new TesteDAOImpl();

    public Teste buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do teste é obrigatorio.");
        }
        Teste t = dao.buscarPorId(id);
        if (t == null) {
            throw new NegocioException("Teste nao encontrado.");
        }
        return t;
    }

    public List<Teste> listarTodos() {
        return dao.listarTodos();
    }

    public List<Teste> buscarPorIds(java.util.Set<Long> ids) {
        return dao.buscarPorIds(ids);
    }

    public Long salvar(Teste t) {
        validarTeste(t);
        if (t.getId() != null) {
            dao.atualizar(t);
            return t.getId();
        }
        return dao.inserir(t);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do teste é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarTeste(Teste t) {
        ValidadorUtil.validarObrigatorio(t.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(t.getNome(), 255, "Nome");
        if (t.getResultado() == null || t.getResultado().trim().isEmpty()) {
            t.setResultado("NAO_EXECUTADO");
        }
    }
}
