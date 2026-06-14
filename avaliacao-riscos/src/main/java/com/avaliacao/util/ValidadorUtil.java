package com.avaliacao.util;

import com.avaliacao.exception.NegocioException;

public class ValidadorUtil {

    private ValidadorUtil() {
    }

    public static void validarObrigatorio(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NegocioException("O campo '" + nomeCampo + "' é obrigatorio.");
        }
    }

    public static void validarTamanhoMaximo(String valor, int max, String nomeCampo) {
        if (valor != null && valor.length() > max) {
            throw new NegocioException(
                "O campo '" + nomeCampo + "' deve ter no maximo " + max + " caracteres.");
        }
    }

    public static void validarTamanhoMinimo(String valor, int min, String nomeCampo) {
        if (valor == null || valor.trim().length() < min) {
            throw new NegocioException(
                "O campo '" + nomeCampo + "' deve ter pelo menos " + min + " caracteres.");
        }
    }
}
