package br.com.autocenterfiap.ordemservico.infrastructure.persistence.jpa.entity;

import br.com.autocenterfiap.ordemservico.domain.enums.StatusItemServico;
import br.com.autocenterfiap.servico.infrastructure.persistence.jpa.entity.ServicoJpaEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "os_item_servico")
@EntityListeners(AuditingEntityListener.class)
public class OSItemServicoJpaEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoJpaEntity ordemServicoJpaEntity;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoJpaEntity servico;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorItemServico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusItemServico statusServico;

    @Column
    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraFim;

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataUltimaAtualizacao;

    @PrePersist
    public void prePersist() {
        if (this.statusServico == null) {
            this.statusServico = StatusItemServico.AGUARDANDO_INICIO;
        }
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}
