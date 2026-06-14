package com.avaliacao.service;

import com.avaliacao.dao.AvaliacaoDAO;
import com.avaliacao.dao.AvaliacaoDAOImpl;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.Avaliacao;
import com.avaliacao.util.ValidadorUtil;

import java.io.Serializable;
import java.util.List;

public class AvaliacaoService implements Serializable {
    private static final long serialVersionUID = 1L;

    private AvaliacaoDAO dao = new AvaliacaoDAOImpl();

    public Avaliacao buscarPorId(Long id) {
        if (id == null) {
            throw new NegocioException("ID da avaliacao é obrigatorio.");
        }
        Avaliacao a = dao.buscarPorId(id);
        if (a == null) {
            throw new NegocioException("Avaliacao nao encontrada.");
        }
        return a;
    }

    public List<Avaliacao> listarTodos() {
        return dao.listarTodos();
    }

    public List<Avaliacao> pesquisar(String filtro, int pagina, int tamanhoPagina) {
        int offset = (pagina - 1) * tamanhoPagina;
        return dao.pesquisar(filtro, offset, tamanhoPagina);
    }

    public int contar(String filtro) {
        return dao.contar(filtro);
    }

    public Long salvar(Avaliacao a) {
        validarAvaliacao(a);
        if (a.getId() != null) {
            dao.atualizar(a);
            return a.getId();
        }
        Long id = dao.inserir(a);
        a.setId(id);
        return id;
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new NegocioException("ID da avaliacao é obrigatorio.");
        }
        dao.excluir(id);
    }

    private void validarAvaliacao(Avaliacao a) {
        ValidadorUtil.validarObrigatorio(a.getTitulo(), "Titulo");
        ValidadorUtil.validarTamanhoMaximo(a.getTitulo(), 255, "Titulo");
        if (a.getStatus() == null || a.getStatus().trim().isEmpty()) {
            a.setStatus("EM_ANDAMENTO");
        }
    }
}
