package com.avaliacao.dao;

import java.util.List;

public interface JuncaoDAO {

    // === AVALIACAO <-> PROCESSO ===
    void inserirVinculoAvaliacaoProcesso(Long avaliacaoId, Long processoId);
    void removerVinculoAvaliacaoProcesso(Long avaliacaoId, Long processoId);
    List<Long> listarProcessosPorAvaliacao(Long avaliacaoId);
    List<Long> listarAvaliacoesPorProcesso(Long processoId);

    // === PROCESSO <-> RISCO ===
    void inserirVinculoProcessoRisco(Long avaliacaoId, Long processoId, Long riscoId);
    void removerVinculoProcessoRisco(Long processoId, Long riscoId);
    List<Long> listarRiscosPorProcesso(Long processoId);
    boolean possuiFilhosProcesso(Long processoId);

    // === RISCO <-> FATOR ===
    void inserirVinculoRiscoFator(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId);
    void removerVinculoRiscoFator(Long riscoId, Long fatorId);
    List<Long> listarFatoresPorRisco(Long riscoId);
    boolean possuiFilhosRisco(Long riscoId);

    // === FATOR <-> CONTROLE ===
    void inserirVinculoFatorControle(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId);
    void removerVinculoFatorControle(Long fatorId, Long controleId);
    List<Long> listarControlesPorFator(Long fatorId);
    boolean possuiFilhosFator(Long fatorId);

    // === CONTROLE <-> MODELO_NEGOCIO ===
    void inserirVinculoControleModeloNegocio(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long modeloNegocioId);
    void removerVinculoControleModeloNegocio(Long controleId, Long modeloNegocioId);
    List<Long> listarModelosPorControle(Long controleId);

    // === CONTROLE <-> TESTE (visao controle) ===
    void inserirVinculoControleTeste(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long testeId);
    void removerVinculoControleTeste(Long controleId, Long testeId);
    List<Long> listarTestesPorControle(Long controleId);
    boolean possuiFilhosControle(Long controleId);

    // === TESTE <-> CONTROLE (visao teste, N:M) ===
    void inserirVinculoTesteControle(Long testeId, Long controleId);
    void removerVinculoTesteControle(Long testeId, Long controleId);
    void limparVinculosTeste(Long testeId);
    List<Long> listarControlesPorTeste(Long testeId);

    // === BATCH FETCH METHODS ===
    java.util.Map<Long, java.util.List<Long>> listarRiscosPorProcessos(java.util.Set<Long> processoIds);
    java.util.Map<Long, java.util.List<Long>> listarFatoresPorRiscos(java.util.Set<Long> riscoIds);
    java.util.Map<Long, java.util.List<Long>> listarControlesPorFatores(java.util.Set<Long> fatorIds);
    java.util.Map<Long, java.util.List<Long>> listarTestesPorControles(java.util.Set<Long> controleIds);
    java.util.Map<Long, java.util.List<Long>> listarModelosPorControles(java.util.Set<Long> controleIds);
    java.util.Map<Long, java.util.List<Long>> listarControlesPorTestes(java.util.Set<Long> testeIds);
}
