package br.com.autocenterfiap.peca.service;

import br.com.autocenterfiap.peca.dto.MovimentacaoEstoqueDTO;
import br.com.autocenterfiap.peca.dto.PecaRequestDTO;
import br.com.autocenterfiap.peca.dto.PecaResponseDTO;
import br.com.autocenterfiap.peca.exception.CodigoJaCadastradoException;
import br.com.autocenterfiap.peca.exception.PecaNaoEncontradaException;
import br.com.autocenterfiap.peca.model.Peca;
import br.com.autocenterfiap.peca.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PecaService {

    private final PecaRepository pecaRepository;

    public List<PecaResponseDTO> listar(String categoria, String busca) {
        return pecaRepository.buscarComFiltros(categoria, busca, true)
                .stream()
                .map(PecaResponseDTO::from)
                .toList();
    }

    public PecaResponseDTO buscarPorId(Long id) {
        Peca peca = buscarOuLancarExcecao(id);
        return PecaResponseDTO.from(peca);
    }

    @Transactional
    public PecaResponseDTO criar(PecaRequestDTO dto) {
        if (pecaRepository.existsByCodigo(dto.codigo())) {
            throw new CodigoJaCadastradoException(dto.codigo());
        }
        Peca peca = new Peca(dto);
        return PecaResponseDTO.from(pecaRepository.save(peca));
    }

    @Transactional
    public PecaResponseDTO atualizar(Long id, PecaRequestDTO dto) {
        Peca peca = buscarOuLancarExcecao(id);

        if (pecaRepository.existsByCodigoAndIdNot(dto.codigo(), id)) {
            throw new CodigoJaCadastradoException(dto.codigo());
        }

        peca.atualizarDados(dto);
        return PecaResponseDTO.from(pecaRepository.save(peca));
    }

    @Transactional
    public void desativar(Long id) {
        Peca peca = buscarOuLancarExcecao(id);
        peca.desativar();
        pecaRepository.save(peca);
    }

    @Transactional
    public PecaResponseDTO adicionarEstoque(Long id, MovimentacaoEstoqueDTO dto) {
        Peca peca = buscarOuLancarExcecao(id);
        peca.incrementarEstoque(dto.quantidade());
        return PecaResponseDTO.from(pecaRepository.save(peca));
    }

    @Transactional
    public PecaResponseDTO removerEstoque(Long id, MovimentacaoEstoqueDTO dto) {
        Peca peca = buscarOuLancarExcecao(id);
        peca.decrementarEstoque(dto.quantidade());
        return PecaResponseDTO.from(pecaRepository.save(peca));
    }

    public List<PecaResponseDTO> listarPecasComProblemaDeEstoque() {
        List<Peca> semEstoque = pecaRepository.findPecasSemEstoque();
        List<Peca> estoqueBaixo = pecaRepository.findPecasComEstoqueBaixo();

        return java.util.stream.Stream.concat(semEstoque.stream(), estoqueBaixo.stream())
                .distinct()
                .map(PecaResponseDTO::from)
                .toList();
    }

    public Peca buscarOuLancarExcecao(Long id) {
        return pecaRepository.findById(id)
                .orElseThrow(() -> new PecaNaoEncontradaException(id));
    }
}
