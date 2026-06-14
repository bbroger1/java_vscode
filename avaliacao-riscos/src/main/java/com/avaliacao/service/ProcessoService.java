package com.avaliacao.service;

import com.avaliacao.dao.ProcessoDAO;
import com.avaliacao.dao.ProcessoDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Processo;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class ProcessoService implements Serializable {
    private static final long serialVersionUID = 1L;

    private ProcessoDAO dao = new ProcessoDAOImpl();

    public Processo buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID do processo é obrigatorio.");
        }
        Processo p = dao.buscarPorId(id);
        if (p == null) {
            throw new NegocioException("Processo nao encontrado.");
        }
        return p;
    }

    public List<Processo> listarTodos() {
        return dao.listarTodos();
    }

    public Long salvar(Processo p) {
        validarProcesso(p);
        if (p.getId() != null) {
            dao.atualizar(p);
            return p.getId();
        }
        return dao.inserir(p);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID do processo é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarProcesso(Processo p) {
        ValidadorUtil.validarObrigatorio(p.getNome(), "Nome");
        ValidadorUtil.validarTamanhoMaximo(p.getNome(), 255, "Nome");
    }
}
