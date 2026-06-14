package com.avaliacao.service;

import com.avaliacao.dao.FatorDAO;
import com.avaliacao.dao.FatorDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Fator;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class FatorService implements Serializable {
    private static final long serialVersionUID = 1L;

    private FatorDAO dao = new FatorDAOImpl();

    public Fator buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do fator é obrigatorio.");
        }
        Fator f = dao.buscarPorId(id);
        if (f == null) {
            throw new NegocioException("Fator nao encontrado.");
        }
        return f;
    }

    public List<Fator> listarTodos() {
        return dao.listarTodos();
    }

    public List<Fator> buscarPorIds(java.util.Set<Long> ids) {
        return dao.buscarPorIds(ids);
    }

    public Long salvar(Fator f) {
        validarFator(f);
        if (f.getId() != null) {
            dao.atualizar(f);
            return f.getId();
        }
        return dao.inserir(f);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do fator é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarFator(Fator f) {
        ValidadorUtil.validarObrigatorio(f.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(f.getNome(), 255, "Nome");
    }
}
