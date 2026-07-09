package br.com.autocenterfiap.veiculo.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import br.com.autocenterfiap.veiculo.domain.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.domain.enums.TipoCombustivel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de saída do veículo")
public class VeiculoResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador único do veículo", example = "1")
    private Long id;

    @Schema(description = "Placa do veículo", example = "ABC1D23")
    private String placa;

    @Schema(description = "Chassi do veículo", example = "9BWZZZ377VT004251")
    private String chassi;

    @Schema(description = "RENAVAM do veículo", example = "12345678901")
    private String renavam;

    @Schema(description = "Marca do veículo", example = "Toyota")
    private String marca;

    @Schema(description = "Modelo do veículo", example = "Corolla")
    private String modelo;

    @Schema(description = "Ano de fabricação do veículo", example = "2020")
    private Integer anoFabricacao;

    @Schema(description = "Ano do modelo do veículo", example = "2021")
    private Integer anoModelo;

    @Schema(description = "Cor do veículo", example = "Preto")
    private String cor;

    @Schema(description = "Quilometragem atual do veículo", example = "45000")
    private Long quilometragem;

    @Schema(description = "Tipo de combustível do veículo", example = "FLEX")
    private TipoCombustivel tipoCombustivel;

    @Schema(description = "Categoria do veículo", example = "CARRO")
    private CategoriaVeiculo categoriaVeiculo;

    @Schema(description = "Data de cadastro do veículo", example = "2024-01-10T14:30:00")
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização do veículo", example = "2024-01-15T10:20:00")
    private LocalDateTime dataUltimaAtualizacao;
}
