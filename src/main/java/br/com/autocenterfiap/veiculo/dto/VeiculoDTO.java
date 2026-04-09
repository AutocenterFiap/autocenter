package br.com.autocenterfiap.veiculo.dto;

import br.com.autocenterfiap.veiculo.enums.CategoriaVeiculo;
import br.com.autocenterfiap.veiculo.enums.TipoCombustivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VeiculoDTO(@NotBlank
                         @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$", message = "Placa inválida")
                         String placa,
                         String chassi,
                         String renavam,
                         @NotBlank(message = "Especificar a Marca do veículo é Obrigatório!") String marca,
                         @NotBlank(message = "Especificar o Modelo do veículo é Obrigatório!") String modelo,
                         Integer anoFabricacao,
                         Integer anoModelo,
                         String cor,
                         Long quilometragem,
                         @NotNull(message = "Tipo de Combustível Inválido") TipoCombustivel tipoCombustivel,
                         @NotNull(message = "Categoria Inválida") CategoriaVeiculo categoriaVeiculo) {
}
