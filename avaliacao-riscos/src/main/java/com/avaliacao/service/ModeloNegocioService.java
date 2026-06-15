package com.avaliacao.service;

import com.avaliacao.dao.ModeloNegocioDAO;
import com.avaliacao.dao.ModeloNegocioDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.ModeloNegocio;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class ModeloNegocioService implements Serializable {
    private static final long serialVersionUID = 1L;

    private ModeloNegocioDAO dao = new ModeloNegocioDAOImpl();

    public ModeloNegocio buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do modelo de negocio é obrigatorio.");
        }
        ModeloNegocio m = dao.buscarPorId(id);
        if (m == null) {
            throw new NegocioException("Modelo de negocio nao encontrado.");
        }
        return m;
    }

    public List<ModeloNegocio> listarTodos() {
        return dao.listarTodos();
    }

    public List<ModeloNegocio> buscarPorIds(java.util.Set<Long> ids) {
        return dao.buscarPorIds(ids);
    }

    public Long salvar(ModeloNegocio m) {
        ValidadorUtil.validarObrigatorio(m.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(m.getNome(), 255, "Nome");
        if (m.getId() != null) {
            dao.atualizar(m);
            return m.getId();
        }
        return dao.inserir(m);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do modelo de negocio é obrigatorio.");
        }
        dao.excluir(id);
    }
}
