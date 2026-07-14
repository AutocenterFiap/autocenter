package br.com.autocenterfiap.cliente.application.usecase;

import br.com.autocenterfiap.cliente.application.dto.CriarClienteInput;
import br.com.autocenterfiap.cliente.application.dto.ClienteOutput;
import br.com.autocenterfiap.cliente.application.mapper.ClienteApplicationMapper;
import br.com.autocenterfiap.cliente.application.port.ClienteRepositoryPort;
import br.com.autocenterfiap.cliente.application.port.ValidadorDocumentoPort;
import br.com.autocenterfiap.cliente.domain.entity.Cliente;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoInvalidoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteDocumentoJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.exception.ClienteEmailJaCadastradoException;
import br.com.autocenterfiap.cliente.domain.service.ValidadorDocumento;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class CriarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ValidadorDocumentoPort validadorDocumentoPort;

    public CriarClienteUseCase(
        ClienteRepositoryPort clienteRepositoryPort,
        ValidadorDocumentoPort validadorDocumentoPort
    ) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.validadorDocumentoPort = validadorDocumentoPort;
    }

    public ClienteOutput executar(CriarClienteInput input) {
        log.info("Iniciando criação de cliente: tipo={}, documento={}",
            input.getTipoCliente(), input.getDocumento());

        try {
            Cliente clienteNovo = ClienteApplicationMapper.toEntity(input);

            clienteNovo.validarDominio();

            // Validar tipo de cliente com tamanho do documento
            clienteNovo.validarDocumentoPorTipo();
            log.debug("Documento possui tamanho correto para o tipo de cliente");

            // Validar dígitos verificadores do documento (CPF/CNPJ)
            ValidadorDocumento validador = validadorDocumentoPort.obterValidador(
                clienteNovo.getTipoCliente().name()
            );

            if (!validador.validar(clienteNovo.getDocumento())) {
                log.warn("Documento inválido: tipo={}, documento={}",
                    validador.getTipoDocumento(), clienteNovo.getDocumento());
                throw new ClienteDocumentoInvalidoException(
                    validador.getTipoDocumento(),
                    clienteNovo.getDocumento()
                );
            }
            log.debug("Documento passou na validação de dígitos verificadores");

            // Verificar duplicidade de documento
            if (clienteRepositoryPort.existePorDocumento(clienteNovo.getDocumento())) {
                log.warn("Tentativa de cadastro com documento já existente: {}",
                    clienteNovo.getDocumento());
                throw new ClienteDocumentoJaCadastradoException(clienteNovo.getDocumento());
            }
            log.debug("Documento não está duplicado");

            // Verificar duplicidade de email
            if (clienteRepositoryPort.existePorEmail(clienteNovo.getEmail())) {
                log.warn("Tentativa de cadastro com email já existente: {}",
                    clienteNovo.getEmail());
                throw new ClienteEmailJaCadastradoException(clienteNovo.getEmail());
            }
            log.debug("Email não está duplicado");

            clienteNovo.definirDataCriacao(LocalDateTime.now());

            Cliente clienteSalvo = clienteRepositoryPort.salvar(clienteNovo);
            log.info("Cliente criado com sucesso: ID={}, Nome={}, Documento={}",
                clienteSalvo.getId(), clienteSalvo.getNome(), clienteSalvo.getDocumento());

            return ClienteApplicationMapper.toOutput(clienteSalvo);

        } catch (Exception e) {
            log.error("Erro ao criar cliente: documento={}, erro={}",
                input.getDocumento(), e.getMessage(), e);
            throw e;
        }
    }
}

