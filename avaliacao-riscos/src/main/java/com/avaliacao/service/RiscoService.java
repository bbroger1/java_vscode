package com.avaliacao.service;

import com.avaliacao.dao.RiscoDAO;
import com.avaliacao.dao.RiscoDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Risco;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class RiscoService implements Serializable {
    private static final long serialVersionUID = 1L;

    private RiscoDAO dao = new RiscoDAOImpl();

    public Risco buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do risco é obrigatorio.");
        }
        Risco r = dao.buscarPorId(id);
        if (r == null) {
            throw new NegocioException("Risco nao encontrado.");
        }
        return r;
    }

    public List<Risco> listarTodos() {
        return dao.listarTodos();
    }

    public List<Risco> buscarPorIds(java.util.Set<Long> ids) {
        return dao.buscarPorIds(ids);
    }

    public Long salvar(Risco r) {
        validarRisco(r);
        if (r.getId() != null) {
            dao.atualizar(r);
            return r.getId();
        }
        return dao.inserir(r);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do risco é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarRisco(Risco r) {
        ValidadorUtil.validarObrigatorio(r.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(r.getNome(), 255, "Nome");
    }
}
