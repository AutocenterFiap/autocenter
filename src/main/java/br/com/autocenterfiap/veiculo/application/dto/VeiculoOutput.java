package br.com.autocenterfiap.veiculo.application.dto;

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
public class VeiculoOutput {
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
}
