package com.avaliacao.service;

import com.avaliacao.dao.*;
import com.avaliacao.dto.*;
import com.avaliacao.exception.NegocioException;
import com.avaliacao.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    // CARREGAMENTO POR ABA (LAZY)
    // =========================================================

    public List<ProcessoDTO> carregarProcessos(Long avaliacaoId) {
        List<Long> ids = juncaoDAO.listarProcessosPorAvaliacao(avaliacaoId);
        List<ProcessoDTO> dtos = new ArrayList<>();
        for (Long id : ids) {
            Processo p = processoService.buscarPorId(id);
            if (p != null) {
                dtos.add(new ProcessoDTO(p.getId(), p.getNome(), p.getDescricao(), p.getCodigo()));
            }
        }
        return dtos;
    }

    public List<ProcessoDTO> carregarRiscos(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarProcessos(avaliacaoId);
        for (ProcessoDTO pDto : processos) {
            List<Long> riscoIds = juncaoDAO.listarRiscosPorProcesso(pDto.getId());
            for (Long riscoId : riscoIds) {
                Risco r = riscoService.buscarPorId(riscoId);
                if (r != null) {
                    pDto.getRiscos().add(new RiscoDTO(r.getId(), r.getNome(), r.getDescricao(),
                        r.getProbabilidade(), r.getImpacto(), r.getNivel()));
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarFatores(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarRiscos(avaliacaoId);
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                List<Long> fatorIds = juncaoDAO.listarFatoresPorRisco(rDto.getId());
                for (Long fatorId : fatorIds) {
                    Fator f = fatorService.buscarPorId(fatorId);
                    if (f != null) {
                        rDto.getFatores().add(new FatorDTO(f.getId(), f.getNome(), f.getDescricao(), f.getTipo()));
                    }
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarControles(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarFatores(avaliacaoId);
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                for (FatorDTO fDto : rDto.getFatores()) {
                    List<Long> controleIds = juncaoDAO.listarControlesPorFator(fDto.getId());
                    for (Long controleId : controleIds) {
                        Controle c = controleService.buscarPorId(controleId);
                        if (c != null) {
                            fDto.getControles().add(new ControleDTO(c.getId(), c.getNome(),
                                c.getDescricao(), c.getTipo(), c.getStatus()));
                        }
                    }
                }
            }
        }
        return processos;
    }

    public List<ProcessoDTO> carregarTestes(Long avaliacaoId) {
        List<ProcessoDTO> processos = carregarControles(avaliacaoId);
        for (ProcessoDTO pDto : processos) {
            for (RiscoDTO rDto : pDto.getRiscos()) {
                for (FatorDTO fDto : rDto.getFatores()) {
                    for (ControleDTO cDto : fDto.getControles()) {
                        List<Long> testeIds = juncaoDAO.listarTestesPorControle(cDto.getId());
                        for (Long testeId : testeIds) {
                            Teste t = testeService.buscarPorId(testeId);
                            if (t != null) {
                                cDto.getTestes().add(new TesteDTO(t.getId(), t.getNome(),
                                    t.getDescricao(), t.getTipo(), t.getResultado(),
                                    t.getDataExecucao() != null ? t.getDataExecucao().toString() : null));
                            }
                        }
                        List<Long> modeloIds = juncaoDAO.listarModelosPorControle(cDto.getId());
                        for (Long modeloId : modeloIds) {
                            ModeloNegocio m = modeloNegocioService.buscarPorId(modeloId);
                            if (m != null) {
                                cDto.getModelosNegocio().add(new ModeloNegocioDTO(m.getId(),
                                    m.getNome(), m.getDescricao(), m.getVersao()));
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
