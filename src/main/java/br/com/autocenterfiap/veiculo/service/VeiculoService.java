package br.com.autocenterfiap.veiculo.service;

import br.com.autocenterfiap.ordemservico.repository.OrdemServicoRepository;
import br.com.autocenterfiap.veiculo.dto.VeiculoDTO;
import br.com.autocenterfiap.veiculo.dto.VeiculoResponseDTO;
import br.com.autocenterfiap.veiculo.enums.TipoOperacao;
import br.com.autocenterfiap.veiculo.exception.VeiculoEmUsoException;
import br.com.autocenterfiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.autocenterfiap.veiculo.model.Veiculo;
import br.com.autocenterfiap.veiculo.repository.VeiculoRepository;
import br.com.autocenterfiap.veiculo.validator.VeiculoValidationContext;
import br.com.autocenterfiap.veiculo.validator.VeiculoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final List<VeiculoValidator> validators;

    public VeiculoService(VeiculoRepository veiculoRepository, OrdemServicoRepository ordemServicoRepository, List<VeiculoValidator> validators) {
        this.veiculoRepository = veiculoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.validators = validators;
    }

    public VeiculoResponseDTO buscarPorId(Long id){
        Veiculo veiculo = this.findById(id);
        return new VeiculoResponseDTO(veiculo);
    }

    public VeiculoResponseDTO buscarPorPlaca(String placa){
        Veiculo veiculo = this.findByPlaca(placa);
        return new VeiculoResponseDTO(veiculo);
    }

    public Page<VeiculoResponseDTO> listarTodos(Pageable pageable){
        return veiculoRepository.findAll(pageable).map(VeiculoResponseDTO::new);
    }

    @Transactional
    public VeiculoResponseDTO criar(VeiculoDTO veiculoDTO){
        // Rodando as Validações de Chassi, Renavam e Placa
        var validationContext = new VeiculoValidationContext(veiculoDTO, TipoOperacao.CREATE);
        validators.forEach(v -> v.validate(validationContext));

        Veiculo veiculo = new Veiculo(veiculoDTO);
        veiculo = veiculoRepository.save(veiculo);
        return new VeiculoResponseDTO(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizar(Long id,VeiculoDTO veiculoDTO){
        Veiculo veiculo = this.findById(id);

        var validationContext = new VeiculoValidationContext(id,veiculoDTO,TipoOperacao.UPDATE);
        validators.forEach(v -> v.validate(validationContext));

        veiculo.atualizarDados(veiculoDTO);
        return new VeiculoResponseDTO(veiculo);
    }

    @Transactional
    public void deletar(Long id){
        Veiculo veiculo = this.findById(id);

        boolean veiculoEmUso = ordemServicoRepository.existsByVeiculoId(id);
        if(veiculoEmUso) throw new VeiculoEmUsoException("Não é possível deletar o veículo, pois ele está associado a uma ordem de serviço ativa.");

        veiculoRepository.delete(veiculo);
    }

    private Veiculo findById(Long id){
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com ID " + id + " não existe!"));
    }

    private Veiculo findByPlaca(String placa){
        return veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veiculo com Placa " + placa + " não existe!"));
    }


}
