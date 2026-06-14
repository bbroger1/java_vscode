package com.avaliacao.dao.generic;

import java.util.List;

public interface CrudDAO<T> {
    T buscarPorId(Long id);
    List<T> listarTodos();
    Long inserir(T entidade);
    void atualizar(T entidade);
    void excluir(Long id);
}
