package com.avaliacao.service;

import com.avaliacao.dao.*;
import com.avaliacao.dto.*;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArvoreService implements Serializable {
    private static final long serialVersionUID = 1L;

    private JuncaoDAO juncaoDAO = new JuncaoDAOImpl();
    private ProcessoService processoService = new ProcessoService();
    private RiscoService riscoService = new RiscoService();
    private FatorService fatorService = new FatorService();
    private ControleService controleService = new ControleService();
    private TesteService testeService = new TesteService();
    private ModeloNegocioService modeloNegocioService = new ModeloNegocioService();

    // =========================================================
    // CARREGAMENTO POR ABA (LAZY) - OTIMIZADO COM BATCH FETCH
    // =========================================================

    public List<ProcessoDTO> carregarProcessos(Long avaliacaoId) {
        List<Long> ids = juncaoDAO.listarProcessosPorAvaliacao(avaliacaoId);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, Processo> processoMap = processoService.buscarPorIds(new HashSet<>(ids))
            .stream().collect(Collectors.toMap(Processo::getId, p -> p));
        
        List<ProcessoDTO> dtos = new ArrayList<>();
        for (Long id : ids) {
            Processo p = processoMap.get(id);
            if (p != null) {
                dtos.add(new ProcessoDTO(p.getId(), p.getNome(), p.getDescricao(), p.getCodigo()));
            }
        }
        return dtos;
    }

    public List<ProcessoDTO> carregarRiscos(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarProcessos(avaliacaoId);
        if (processos.isEmpty()) {
            return processos;
        }
        
        Set<Long> processoIds = processos.stream().map(ProcessoDTO::getId).collect(Collectors.toSet());
        Map<Long, List<Long>> riscosPorProcesso = juncaoDAO.listarRiscosPorProcessos(processoIds);
        
        Set<Long> todosRiscoIds = riscosPorProcesso.values().stream()
            .flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, Risco> riscoMap = riscoService.buscarPorIds(todosRiscoIds)
            .stream().collect(Collectors.toMap(Risco::getId, r -> r));
        
        for (ProcessoDTO pDto : processos) {
            List<Long> riscoIds = riscosPorProcesso.get(pDto.getId());
            if (riscoIds != null) {
                for (Long riscoId : riscoIds) {
                    Risco r = riscoMap.get(riscoId);
                    if (r != null) {
                        pDto.getRiscos().add(new RiscoDTO(r.getId(), r.getNome(), r.getDescricao(),
                            r.getProbabilidade(), r.getImpacto(), r.getNivel()));
                    }
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarFatores(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarRiscos(avaliacaoId);
        if (processos.isEmpty()) {
            return processos;
        }
        
        Set<Long> todosRiscoIds = processos.stream()
            .flatMap(p -> p.getRiscos().stream())
            .map(RiscoDTO::getId)
            .collect(Collectors.toSet());
        
        if (todosRiscoIds.isEmpty()) {
            return processos;
        }
        
        Map<Long, List<Long>> fatoresPorRisco = juncaoDAO.listarFatoresPorRiscos(todosRiscoIds);
        Set<Long> todosFatorIds = fatoresPorRisco.values().stream()
            .flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, Fator> fatorMap = fatorService.buscarPorIds(todosFatorIds)
            .stream().collect(Collectors.toMap(Fator::getId, f -> f));
        
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                List<Long> fatorIds = fatoresPorRisco.get(rDto.getId());
                if (fatorIds != null) {
                    for (Long fatorId : fatorIds) {
                        Fator f = fatorMap.get(fatorId);
                        if (f != null) {
                            rDto.getFatores().add(new FatorDTO(f.getId(), f.getNome(), f.getDescricao(), f.getTipo()));
                        }
                    }
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarControles(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarFatores(avaliacaoId);
        if (processos.isEmpty()) {
            return processos;
        }
        
        Set<Long> todosFatorIds = processos.stream()
            .flatMap(p -> p.getRiscos().stream())
            .flatMap(r -> r.getFatores().stream())
            .map(FatorDTO::getId)
            .collect(Collectors.toSet());
        
        if (todosFatorIds.isEmpty()) {
            return processos;
        }
        
        Map<Long, List<Long>> controlesPorFator = juncaoDAO.listarControlesPorFatores(todosFatorIds);
        Set<Long> todosControleIds = controlesPorFator.values().stream()
            .flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, Controle> controleMap = controleService.buscarPorIds(todosControleIds)
            .stream().collect(Collectors.toMap(Controle::getId, c -> c));
        
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                for (FatorDTO fDto : rDto.getFatores()) {
                    List<Long> controleIds = controlesPorFator.get(fDto.getId());
                    if (controleIds != null) {
                        for (Long controleId : controleIds) {
                            Controle c = controleMap.get(controleId);
                            if (c != null) {
                                fDto.getControles().add(new ControleDTO(c.getId(), c.getNome(),
                                    c.getDescricao(), c.getTipo(), c.getStatus()));
                            }
                        }
                    }
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarTestes(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarControles(avaliacaoId);
        if (processos.isEmpty()) {
            return processos;
        }
        
        Set<Long> todosControleIds = processos.stream()
            .flatMap(p -> p.getRiscos().stream())
            .flatMap(r -> r.getFatores().stream())
            .flatMap(f -> f.getControles().stream())
            .map(ControleDTO::getId)
            .collect(Collectors.toSet());
        
        if (todosControleIds.isEmpty()) {
            return processos;
        }
        
        Map<Long, List<Long>> testesPorControle = juncaoDAO.listarTestesPorControles(todosControleIds);
        Map<Long, List<Long>> modelosPorControle = juncaoDAO.listarModelosPorControles(todosControleIds);
        
        Set<Long> todosTesteIds = testesPorControle.values().stream()
            .flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, Teste> testeMap = testeService.buscarPorIds(todosTesteIds)
            .stream().collect(Collectors.toMap(Teste::getId, t -> t));
        
        Set<Long> todosModeloIds = modelosPorControle.values().stream()
            .flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, ModeloNegocio> modeloMap = modeloNegocioService.buscarPorIds(todosModeloIds)
            .stream().collect(Collectors.toMap(ModeloNegocio::getId, m -> m));
        
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                for (FatorDTO fDto : rDto.getFatores()) {
                    for (ControleDTO cDto : fDto.getControles()) {
                        List<Long> testeIds = testesPorControle.get(cDto.getId());
                        if (testeIds != null) {
                            for (Long testeId : testeIds) {
                                Teste t = testeMap.get(testeId);
                                if (t != null) {
                                    cDto.getTestes().add(new TesteDTO(t.getId(), t.getNome(),
                                        t.getDescricao(), t.getTipo(), t.getResultado(),
                                        t.getDataExecucao() != null ? t.getDataExecucao().toString() : null));
                                }
                            }
                        }
                        List<Long> modeloIds = modelosPorControle.get(cDto.getId());
                        if (modeloIds != null) {
                            for (Long modeloId : modeloIds) {
                                ModeloNegocio m = modeloMap.get(modeloId);
                                if (m != null) {
                                    cDto.getModelosNegocio().add(new ModeloNegocioDTO(m.getId(),
                                        m.getNome(), m.getDescricao(), m.getVersao()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return processos;
    }

    // =========================================================
    // OPERACOES DE VINCULACAO
    // =========================================================

    public void vincularProcessoAvaliacao(Long avaliacaoId, Long processoId) {
        juncaoDAO.inserirVinculoAvaliacaoProcesso(avaliacaoId, processoId);
    }

    public void desvincularProcessoAvaliacao(Long avaliacaoId, Long processoId) {
        if (juncaoDAO.possuiFilhosProcesso(processoId)) {
            throw new NegocioException(
                "Exclua os riscos vinculados antes de remover este processo.");
        }
        juncaoDAO.removerVinculoAvaliacaoProcesso(avaliacaoId, processoId);
    }

    public void vincularRiscoProcesso(Long avaliacaoId, Long processoId, Long riscoId) {
        juncaoDAO.inserirVinculoProcessoRisco(avaliacaoId, processoId, riscoId);
    }

    public void desvincularRiscoProcesso(Long processoId, Long riscoId) {
        if (juncaoDAO.possuiFilhosRisco(riscoId)) {
            throw new NegocioException(
                "Exclua os fatores vinculados antes de remover este risco.");
        }
        juncaoDAO.removerVinculoProcessoRisco(processoId, riscoId);
    }

    public void vincularFatorRisco(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId) {
        juncaoDAO.inserirVinculoRiscoFator(avaliacaoId, processoId, riscoId, fatorId);
    }

    public void desvincularFatorRisco(Long riscoId, Long fatorId) {
        if (juncaoDAO.possuiFilhosFator(fatorId)) {
            throw new NegocioException(
                "Exclua os controles vinculados antes de remover este fator.");
        }
        juncaoDAO.removerVinculoRiscoFator(riscoId, fatorId);
    }

    public void vincularControleFator(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId) {
        juncaoDAO.inserirVinculoFatorControle(avaliacaoId, processoId, riscoId, fatorId, controleId);
    }

    public void desvincularControleFator(Long fatorId, Long controleId) {
        if (juncaoDAO.possuiFilhosControle(controleId)) {
            throw new NegocioException(
                "Exclua os testes vinculados antes de remover este controle.");
        }
        juncaoDAO.removerVinculoFatorControle(fatorId, controleId);
    }

    public void vincularModeloNegocioControle(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long modeloNegocioId) {
        juncaoDAO.inserirVinculoControleModeloNegocio(avaliacaoId, processoId, riscoId, fatorId, controleId, modeloNegocioId);
    }

    public void desvincularModeloNegocioControle(Long controleId, Long modeloNegocioId) {
        juncaoDAO.removerVinculoControleModeloNegocio(controleId, modeloNegocioId);
    }

    public void vincularTesteControle(Long avaliacaoId, Long processoId, Long riscoId, Long fatorId, Long controleId, Long testeId) {
        juncaoDAO.inserirVinculoControleTeste(avaliacaoId, processoId, riscoId, fatorId, controleId, testeId);
    }

    public void desvincularTesteControle(Long controleId, Long testeId) {
        juncaoDAO.removerVinculoControleTeste(controleId, testeId);
    }

    // =========================================================
    // SELECAO (listas para modais)
    // =========================================================

    public List<Long> listarIdsProcessosPorAvaliacao(Long avaliacaoId) {
        return juncaoDAO.listarProcessosPorAvaliacao(avaliacaoId);
    }

    public List<Processo> listarProcessosDisponiveis(Long avaliacaoId) {
        List<Long> idsVinculados = juncaoDAO.listarProcessosPorAvaliacao(avaliacaoId);
        return processoService.listarTodos().stream()
            .filter(p -> !idsVinculados.contains(p.getId()))
            .collect(Collectors.toList());
    }
}