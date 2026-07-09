package br.com.autocenterfiap.veiculo.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {
    private Long id;
    private String placa;
    private String chassi;
    private String renavam;
    private String marca;
    private String modelo;
    private Integer anoFabricacao;
    private Integer anoModelo;
    private String cor;
    private Long quilometragem;
    private TipoCombustivel tipoCombustivel;
    private CategoriaVeiculo categoriaVeiculo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataUltimaAtualizacao;

    public void validarDominio() {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa do veículo não pode estar vazia");
        }
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("Marca do veículo é obrigatória");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("Modelo do veículo é obrigatório");
        }
        if (tipoCombustivel == null) {
            throw new IllegalArgumentException("Tipo de combustível do veículo é obrigatório");
        }
        if (categoriaVeiculo == null) {
            throw new IllegalArgumentException("Categoria do veículo é obrigatória");
        }
    }

    public void atualizar(String placa, String chassi, String renavam, String marca, String modelo,
                          Integer anoFabricacao, Integer anoModelo, String cor, Long quilometragem,
                          TipoCombustivel tipoCombustivel, CategoriaVeiculo categoriaVeiculo) {
        this.placa = placa;
        this.chassi = chassi;
        this.renavam = renavam;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.tipoCombustivel = tipoCombustivel;
        this.categoriaVeiculo = categoriaVeiculo;
        this.dataUltimaAtualizacao = LocalDateTime.now();
        validarDominio();
    }
}
