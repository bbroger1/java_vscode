package com.avaliacao.dao;

import com.avaliacao.dao.generic.CrudDAO;
import com.avaliacao.dao.generic.SearchableDAO;
import com.avaliacao.model.Avaliacao;

public interface AvaliacaoDAO extends CrudDAO<Avaliacao>, SearchableDAO<Avaliacao> {
}
