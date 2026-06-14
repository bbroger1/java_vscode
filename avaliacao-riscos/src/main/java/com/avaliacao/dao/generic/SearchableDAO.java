package com.avaliacao.dao.generic;

import java.util.List;

public interface SearchableDAO<T> {
    List<T> pesquisar(String filtro, int offset, int limit);
    int contar(String filtro);
}
