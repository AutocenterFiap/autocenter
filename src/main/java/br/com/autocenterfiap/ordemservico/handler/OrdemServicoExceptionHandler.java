package br.com.autocenterfiap.ordemservico.handler;

import br.com.autocenterfiap.comum.model.ErroResposta;
import br.com.autocenterfiap.ordemservico.application.exception.*;
import br.com.autocenterfiap.ordemservico.application.port.ObservabilidadePort;
import br.com.autocenterfiap.servico.domain.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.domain.exception.ServicoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "br.com.autocenterfiap.ordemservico")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrdemServicoExceptionHandler {

    private final ObservabilidadePort observabilidadePort;

    public OrdemServicoExceptionHandler() {
        this.observabilidadePort = null;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public OrdemServicoExceptionHandler(ObservabilidadePort observabilidadePort) {
        this.observabilidadePort = observabilidadePort;
    }

    @ExceptionHandler(OrdemServicoJaAbertaParaVeiculoException.class)
    public ResponseEntity<ErroResposta> handleConflitoDeDados(OrdemServicoJaAbertaParaVeiculoException ex, HttpServletRequest request){
        log.warn("Conflito de dados OS na URI={}: {}", request.getRequestURI(), ex.getMessage());
        if (observabilidadePort != null) {
            observabilidadePort.registrarErroIntegracao("ordem_servico", "CONFLITO_DADOS");
        }
        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(OrdemServicoNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> handleOrdemServicoNaoEncontrada(OrdemServicoNaoEncontradaException ex, HttpServletRequest request){
        log.warn("Ordem de serviço não encontrada na URI={}: {}", request.getRequestURI(), ex.getMessage());
        if (observabilidadePort != null) {
            observabilidadePort.registrarErroIntegracao("ordem_servico", "NAO_ENCONTRADA");
        }
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(StatusOSInvalidoException.class)
    public ResponseEntity<ErroResposta> handleValidacaoDeDados(StatusOSInvalidoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(StatusOSItemInvalidoException.class)
    public ResponseEntity<ErroResposta> handleStatusOSItemInvalido(StatusOSItemInvalidoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(OSItemServicoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleOSItemServicoNaoEncontrado(OSItemServicoNaoEncontradoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ServicoInativoException.class)
    public ResponseEntity<ErroResposta> handleServicoInativo(ServicoInativoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Serviço Inativo",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleServicoNaoEncontrado(ServicoNaoEncontradoException ex, HttpServletRequest request){
        ErroResposta erro = new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
}
