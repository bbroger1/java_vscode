package com.avaliacao.dao.generic;

import java.util.List;
import java.util.Set;

public interface CrudDAO<T> {
    T buscarPorId(Long id);
    List<T> listarTodos();
    Long inserir(T entidade);
    void atualizar(T entidade);
    void excluir(Long id);
    List<T> buscarPorIds(Set<Long> ids);
}
