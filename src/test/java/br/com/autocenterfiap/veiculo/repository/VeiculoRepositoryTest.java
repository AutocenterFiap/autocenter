package br.com.autocenterfiap.veiculo.repository;

import br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.repository.OrdemServicoJpaRepository;
import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.entity.VeiculoJpaEntity;
import br.com.autocenterfiap.veiculo.infrastructure.persistence.jpa.repository.VeiculoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("VeiculoRepository - Testes de Integração")
class VeiculoRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VeiculoJpaRepository repository;

    @Autowired
    private OrdemServicoJpaRepository ordemServicoJpaRepository;

    private VeiculoJpaEntity veiculo;
    private VeiculoJpaEntity veiculoSegundo;

    @BeforeEach
    void setUp() {
        ordemServicoJpaRepository.deleteAll();
        ordemServicoJpaRepository.flush();
        repository.deleteAll();
        repository.flush();

        veiculo = new VeiculoJpaEntity();
        veiculo.setPlaca("ABC1D23");
        veiculo.setChassi("9BWZZZ377VT004251");
        veiculo.setRenavam("82106426707");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAnoFabricacao(2020);
        veiculo.setAnoModelo(2021);
        veiculo.setCor("Preto");
        veiculo.setQuilometragem(45000L);
        veiculo.setTipoCombustivel(TipoCombustivel.DIESEL);
        veiculo.setCategoriaVeiculo(CategoriaVeiculo.CARRO);

        veiculoSegundo = new VeiculoJpaEntity();
        veiculoSegundo.setPlaca("ABC1D26");
        veiculoSegundo.setChassi("8BWZZZ377VT004251");
        veiculoSegundo.setRenavam("23022215548");
        veiculoSegundo.setMarca("Kawasaki");
        veiculoSegundo.setModelo("Ninja");
        veiculoSegundo.setAnoFabricacao(2026);
        veiculoSegundo.setAnoModelo(2026);
        veiculoSegundo.setCor("Verde");
        veiculoSegundo.setQuilometragem(0L);
        veiculoSegundo.setTipoCombustivel(TipoCombustivel.GASOLINA);
        veiculoSegundo.setCategoriaVeiculo(CategoriaVeiculo.MOTO);
    }

    @Test
    public void deveSalvarVeiculoQuandoValido(){
        VeiculoJpaEntity salvo = entityManager.persist(veiculo);
        entityManager.flush();

        assertNotNull(salvo.getId());
        assertEquals("ABC1D23", salvo.getPlaca());
        assertEquals("9BWZZZ377VT004251", salvo.getChassi());
        assertEquals("82106426707", salvo.getRenavam());
    }

    @Test
    public void deveRetornarListaDeVeiculosQuandoHouverVeiculosCadastrados(){
        entityManager.persist(veiculo);
        entityManager.persist(veiculoSegundo);
        entityManager.flush();

        List<VeiculoJpaEntity> veiculos = repository.findAll();

        assertNotNull(veiculos);
        assertEquals(2, veiculos.size());
    }

    @Test
    public void deveDeletarVeiculoQuandoIdExistir(){
        VeiculoJpaEntity salvo = entityManager.persist(veiculo);
        entityManager.flush();

        repository.deleteById(salvo.getId());
        entityManager.flush();

        Optional<VeiculoJpaEntity> deletado = repository.findById(salvo.getId());
        assertTrue(deletado.isEmpty());
    }

    @Test
    public void deveRetornarVeiculoQuandoBuscarPorPlacaExistente(){
        entityManager.persist(veiculo);
        entityManager.flush();
        
        Optional<VeiculoJpaEntity> encontrado = repository.findByPlaca("ABC1D23");
        
        assertTrue(encontrado.isPresent());
        assertEquals("ABC1D23", encontrado.get().getPlaca());
    }

    @Test
    public void deveRetornarVazioQuandoBuscarPorPlacaInexistente(){
        Optional<VeiculoJpaEntity> encontrado = repository.findByPlaca("ABC1D23");
        assertTrue(encontrado.isEmpty());
    }

    // --- TESTES DE EXISTS BY PLACA ---

    @Test
    public void deveRetornarTrueQuandoVerificarExistenciaDePlacaCadastrada(){
        entityManager.persist(veiculo);
        entityManager.flush();

        boolean encontrado = repository.existsByPlaca(veiculo.getPlaca());
        assertTrue(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarExistenciaDePlacaNaoCadastrada(){
        boolean encontrado = repository.existsByPlaca("XYZ9X99");
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarPlacaParaOMesmoVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo = entityManager.persist(veiculo);
        entityManager.flush();

        // Passando a PRÓPRIA placa e o PRÓPRIO ID. Isso significa: "Alguém MAIS tem essa placa?" -> Falso
        boolean encontrado = repository.existsByPlacaAndIdNot(salvo.getPlaca(), salvo.getId());
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarTrueQuandoVerificarPlacaQuePertenceAOutroVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo1 = entityManager.persist(veiculo);
        VeiculoJpaEntity salvo2 = entityManager.persist(veiculoSegundo);
        entityManager.flush();

        // O Veiculo 1 tenta atualizar para a placa do Veiculo 2
        boolean encontrado = repository.existsByPlacaAndIdNot(salvo2.getPlaca(), salvo1.getId());
        assertTrue(encontrado);
    }

    // --- TESTES DE EXISTS BY RENAVAM ---

    @Test
    public void deveRetornarTrueQuandoVerificarExistenciaDeRenavamCadastrado(){
        entityManager.persist(veiculo);
        entityManager.flush();

        boolean encontrado = repository.existsByRenavam(veiculo.getRenavam());
        assertTrue(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarExistenciaDeRenavamNaoCadastrado(){
        boolean encontrado = repository.existsByRenavam("00000000000");
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarRenavamParaOMesmoVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo = entityManager.persist(veiculo);
        entityManager.flush();

        boolean encontrado = repository.existsByRenavamAndIdNot(salvo.getRenavam(), salvo.getId());
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarTrueQuandoVerificarRenavamQuePertenceAOutroVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo1 = entityManager.persist(veiculo);
        VeiculoJpaEntity salvo2 = entityManager.persist(veiculoSegundo);
        entityManager.flush();

        boolean encontrado = repository.existsByRenavamAndIdNot(salvo2.getRenavam(), salvo1.getId());
        assertTrue(encontrado);
    }

    // --- TESTES DE EXISTS BY CHASSI ---

    @Test
    public void deveRetornarTrueQuandoVerificarExistenciaDeChassiCadastrado(){
        entityManager.persist(veiculo);
        entityManager.flush();

        boolean encontrado = repository.existsByChassi(veiculo.getChassi());
        assertTrue(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarExistenciaDeChassiNaoCadastrado(){
        boolean encontrado = repository.existsByChassi("0BWZZZ000VT000000");
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarFalseQuandoVerificarChassiParaOMesmoVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo = entityManager.persist(veiculo);
        entityManager.flush();

        boolean encontrado = repository.existsByChassiAndIdNot(salvo.getChassi(), salvo.getId());
        assertFalse(encontrado);
    }

    @Test
    public void deveRetornarTrueQuandoVerificarChassiQuePertenceAOutroVeiculoNaAtualizacao(){
        VeiculoJpaEntity salvo1 = entityManager.persist(veiculo);
        VeiculoJpaEntity salvo2 = entityManager.persist(veiculoSegundo);
        entityManager.flush();

        boolean encontrado = repository.existsByChassiAndIdNot(salvo2.getChassi(), salvo1.getId());
        assertTrue(encontrado);
    }

}