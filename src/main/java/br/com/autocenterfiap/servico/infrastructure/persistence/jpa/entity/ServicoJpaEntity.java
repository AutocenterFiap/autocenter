package br.com.autocenterfiap.servico.infrastructure.persistence.jpa.entity;

import br.com.autocenterfiap.servico.domain.enums.StatusServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "servicos")
@EntityListeners(AuditingEntityListener.class)
public class ServicoJpaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusServico status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @CreatedDate
    @Column(updatable = false, name = "data_criacao")
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;
}
