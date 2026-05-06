package br.com.autocenterfiap.ordemservico.service;

import br.com.autocenterfiap.ordemservico.dto.MetricaTempoGastoServicoDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoRequestDTO;
import br.com.autocenterfiap.ordemservico.dto.OSItemServicoResponseDTO;
import br.com.autocenterfiap.ordemservico.enums.StatusItemServico;
import br.com.autocenterfiap.ordemservico.enums.StatusOS;
import br.com.autocenterfiap.ordemservico.exception.OSItemServicoNaoEncontradoException;
import br.com.autocenterfiap.ordemservico.exception.OrdemServicoNaoEncontradaException;
import br.com.autocenterfiap.ordemservico.exception.StatusOSInvalidoException;
import br.com.autocenterfiap.ordemservico.exception.StatusOSItemInvalidoException;
import br.com.autocenterfiap.ordemservico.model.OSItemServico;
import br.com.autocenterfiap.ordemservico.model.OrdemServico;
import br.com.autocenterfiap.ordemservico.repository.OSItemServicoRepository;
import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.servico.enums.StatusServico;
import br.com.autocenterfiap.servico.exception.ServicoInativoException;
import br.com.autocenterfiap.servico.model.Servico;
import br.com.autocenterfiap.servico.service.ServicoService;
import br.com.autocenterfiap.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OSItemServicoService {

    private final OSItemServicoRepository osItemServicoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoService servicoService;

    @Transactional(readOnly = true)
    public List<OSItemServicoResponseDTO> listarPorOS(Long ordemServicoId) {
        log.info("Listando itens de serviço da Ordem de Serviço ID={}", ordemServicoId);

        List<OSItemServico> itens = osItemServicoRepository.findByOrdemServicoId(ordemServicoId);

        log.info("Total de {} itens de serviço encontrados para a OS ID={}", itens.size(), ordemServicoId);

        return itens.stream()
                .map(OSItemServicoResponseDTO::new)
                .toList();
    }

    @Transactional
    public OSItemServicoResponseDTO adicionarServicoNaOS(Long ordemServicoId, OSItemServicoRequestDTO dto) {
        log.info("Adicionando serviço ID={} na Ordem de Serviço ID={}", dto.servicoId(), ordemServicoId);

        OrdemServico ordemServico = findOrdemServicoById(ordemServicoId);

        if (ordemServico.getStatusOS() != StatusOS.EM_DIAGNOSTICO) {
            log.warn("Tentativa de adicionar serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível adicionar serviços em ordens de serviço que estejam no status 'EM_DIAGNOSTICO'");
        }

        Servico servico = servicoService.buscarPorId(dto.servicoId());

        if (servico.getStatus() == StatusServico.INATIVO) {
            log.warn("Tentativa de adicionar serviço inativo: ID={}, Descrição={}",
                    servico.getId(), servico.getDescricao());
            throw new ServicoInativoException(servico.getDescricao());
        }

        OSItemServico item = new OSItemServico();
        item.setOrdemServico(ordemServico);
        item.setServico(servico);
        item.setValorItemServico(servico.getValor());
        item.setStatusServico(StatusItemServico.AGUARDANDO_INICIO);

        // Recalcula o valor total da OS após adicionar o serviço
        ordemServico.getOsItensServicos().add(item);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        OSItemServico itemSalvo = osItemServicoRepository.save(item);

        log.info("Serviço adicionado com sucesso na OS: Item ID={}, Serviço={}, Valor={}",
                itemSalvo.getId(), servico.getDescricao(), servico.getValor());

        return new OSItemServicoResponseDTO(itemSalvo);
    }

    @Transactional
    public OSItemServicoResponseDTO iniciarServico(Long ordemServicoId, Long servicoId) {
        log.info("Iniciando execução do serviço: OS ID={}, Serviço ID={}", ordemServicoId, servicoId);

        OrdemServico ordemServico = findOrdemServicoById(ordemServicoId);

        if (ordemServico.getStatusOS() != StatusOS.EM_EXECUCAO) {
            log.warn("Tentativa de iniciar serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível iniciar serviços em ordens de serviço que estejam no status 'EM_EXECUCAO'");
        }

        OSItemServico item = findOsItemServico(ordemServicoId, servicoId);

        if (item.getStatusServico() != StatusItemServico.AGUARDANDO_INICIO) {
            log.warn("Tentativa de iniciar serviço com status inválido: Item ID={}, Status Atual={}",
                    item.getId(), item.getStatusServico());
            throw new StatusOSItemInvalidoException("Só é possível iniciar um serviço que esteja no status 'AGUARDANDO_INICIO'");
        }

        item.setStatusServico(StatusItemServico.EXECUTANDO);
        item.setDataHoraInicio(LocalDateTime.now());

        OSItemServico itemAtualizado = osItemServicoRepository.save(item);

        log.info("Serviço iniciado: Item ID={}, Status={}", item.getId(), StatusItemServico.EXECUTANDO);

        return new OSItemServicoResponseDTO(itemAtualizado);
    }

    @Transactional
    public OSItemServicoResponseDTO finalizarServico(Long ordemServicoId, Long servicoId) {
        log.info("Finalizando execução do serviço: OS ID={}, Serviço ID={}", ordemServicoId, servicoId);

        OSItemServico item = findOsItemServico(ordemServicoId, servicoId);

        if (item.getStatusServico() != StatusItemServico.EXECUTANDO) {
            log.warn("Tentativa de finalizar serviço com status inválido: Item ID={}, Status Atual={}",
                    item.getId(), item.getStatusServico());
            throw new StatusOSInvalidoException("Só é possível finalizar um serviço que esteja no status 'EXECUTANDO'");
        }

        item.setStatusServico(StatusItemServico.FINALIZADO);
        item.setDataHoraFim(LocalDateTime.now());

        OSItemServico itemAtualizado = osItemServicoRepository.save(item);

        log.info("Serviço finalizado: Item ID={}, Status={}, Duração={}min",
                item.getId(), StatusItemServico.FINALIZADO,
                java.time.Duration.between(item.getDataHoraInicio(), item.getDataHoraFim()).toMinutes());

        return new OSItemServicoResponseDTO(itemAtualizado);
    }

    @Transactional
    public void removerServicoDaOS(Long ordemServicoId, Long servicoId) {
        log.info("Removendo serviço da OS: OS ID={}, Serviço ID={}", ordemServicoId, servicoId);

        OrdemServico ordemServico = findOrdemServicoById(ordemServicoId);

        if (ordemServico.getStatusOS() != StatusOS.EM_DIAGNOSTICO) {
            log.warn("Tentativa de remover serviço em OS com status inválido: OS ID={}, Status Atual={}",
                    ordemServicoId, ordemServico.getStatusOS());
            throw new StatusOSInvalidoException("Só é possível remover serviços de ordens de serviço que estejam no status 'EM_DIAGNOSTICO'");
        }

        OSItemServico item = findOsItemServico(ordemServicoId, servicoId);

        if (item.getStatusServico() != StatusItemServico.AGUARDANDO_INICIO) {
            log.warn("Tentativa de remover serviço com status inválido: Item ID={}, Status Atual={}",
                    item.getId(), item.getStatusServico());
            throw new StatusOSItemInvalidoException("Só é possível remover um serviço que esteja no status 'AGUARDANDO_INICIO'");
        }

        // Recalcula o valor total da OS após remover o serviço
        ordemServico.getOsItensServicos().remove(item);
        ordemServico.setValorTotal(Util.calcularValorTotal(ordemServico));

        log.info("Serviço removido da OS com sucesso: Item ID={}, Serviço={}",
                item.getId(), item.getServico().getDescricao());
    }

    private OrdemServico findOrdemServicoById(Long ordemServicoId) {
        return ordemServicoRepository.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
    }

    private OSItemServico findOsItemServico(Long ordemServicoId, Long servicoId) {
        return osItemServicoRepository.findByServicoIdAndOrdemServicoId(servicoId, ordemServicoId)
                .orElseThrow(() -> new OSItemServicoNaoEncontradoException(ordemServicoId, servicoId));
    }

    public List<MetricaTempoGastoServicoDTO> getMetricaTempoGastoServico() {
        log.info("Calculando métrica de tempo gasto por serviço");
        List<OSItemServico> servicos = osItemServicoRepository.findAll();

        var mediaTempoPorServico = calculaMediaServicos(servicos);

        return mediaTempoPorServico.entrySet().stream()
                .map(OSItemServicoService::getMetricaTempoGastoServicoDTO).toList();

    }

    private static MetricaTempoGastoServicoDTO getMetricaTempoGastoServicoDTO(Map.Entry<String, Double> entry) {
        Duration duration = Duration.ofSeconds(entry.getValue().longValue());

        String tempoFormatado = String.format(
                "%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        return new MetricaTempoGastoServicoDTO(entry.getKey(), tempoFormatado);
    }

    private static Map<String, Double> calculaMediaServicos(List<OSItemServico> servicos) {
        return servicos.stream()
                .filter(item -> item.getStatusServico() == StatusItemServico.FINALIZADO)
                .collect(Collectors.groupingBy(
                        item -> item.getServico().getDescricao(),
                        Collectors.averagingLong(item ->
                                Duration.between(
                                        item.getDataHoraInicio(),
                                        item.getDataHoraFim()
                                ).getSeconds()
                        )
                ));
    }

}
