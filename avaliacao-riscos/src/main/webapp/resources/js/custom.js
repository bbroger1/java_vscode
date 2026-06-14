var _modalId = null;
var _isNovo = false;

// DEPURACAO AJAX
(function() {
    function bindAjaxDebug() {
        if (typeof jsf !== 'undefined' && jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                var sourceId = data.source ? (data.source.id || '?') : '?';
                console.debug('[AJAX] status=' + data.status + ' source=' + sourceId);
                if (data.status === 'error') {
                    console.error('[AJAX] ERRO:', data.responseXML || data.responseText || 'sem detalhes');
                }
            });
        } else {
            setTimeout(bindAjaxDebug, 200);
        }
    }
    bindAjaxDebug();
})();

$(function () {
    $('.modal').on('hidden.bs.modal', function () {
        var form = $(this).find('form')[0];
        if (form) form.reset();
    });
});

function abrirModal(modalId) {
    setTimeout(function() {
        $('#' + modalId).modal('show');
    }, 150);
}

function abrirModalNovo(modalId) {
    setTimeout(function() {
        var m = $('#' + modalId);
        m.find('input[type="text"], textarea').val('');
        m.find('select').prop('selectedIndex', 0);
        m.modal('show');
    }, 150);
}

function fecharModal(modalId) {
    $('#' + modalId).modal('hide');
}

function fecharModalSeSemErro(modalId, msgId) {
    var temErro = $('#' + msgId).find('li.error, li.fatal').length > 0;
    if (!temErro) {
        $('#' + modalId).modal('hide');
    }
}

function handleAjaxComplete(data) {
    if (data.status === 'success' && _modalId) {
        if (_isNovo) {
            abrirModalNovo(_modalId);
        } else {
            abrirModal(_modalId);
        }
        _modalId = null;
        _isNovo = false;
    }
}

function handleSalvarAjax(data) {
    if (data.status === 'success' && _modalId) {
        var msgId = _modalId + 'Messages';
        var temErro = $('#' + msgId).find('li.error, li.fatal').length > 0;
        if (!temErro) {
            fecharModal(_modalId);
        }
        _modalId = null;
    }
}

function handleFecharModal(data) {
    if (data.status === 'success' && _modalId) {
        fecharModal(_modalId);
        _modalId = null;
    }
}

// Loading indicator
(function() {
    function bindAjaxLoading() {
        if (typeof jsf !== 'undefined' && jsf.ajax) {
            jsf.ajax.addOnEvent(function(data) {
                var status = data.status;
                var overlay = document.getElementById('ajaxLoading');
                if (!overlay) return;
                if (status === 'begin') {
                    overlay.style.display = 'flex';
                } else if (status === 'complete') {
                    overlay.style.display = 'none';
                }
            });
        } else {
            setTimeout(bindAjaxLoading, 200);
        }
    }
    bindAjaxLoading();
})();

$(document).ready(function() {
    // Adiciona botão de fechar em todos os alertas dismissible do JSF
    $('ul.alert-dismissible').each(function() {
        var $ul = $(this);
        // Só adiciona se ainda não tiver
        if ($ul.find('.close').length === 0) {
            $ul.prepend(
                '<button type="button" class="close" data-dismiss="alert" aria-label="Fechar">' +
                '<span aria-hidden="true">&times;</span></button>'
            );
        }
    });
});
