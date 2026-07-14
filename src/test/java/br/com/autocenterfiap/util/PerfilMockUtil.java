package br.com.autocenterfiap.util;

import br.com.autocenterfiap.security.adapter.in.request.PerfilRequest;
import br.com.autocenterfiap.security.adapter.in.response.PerfilResponse;
import br.com.autocenterfiap.security.domain.enums.PerfilType;
import br.com.autocenterfiap.security.infrastructure.persistence.jpa.entity.PerfilJpaEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PerfilMockUtil {
    private PerfilMockUtil() {
    }

    public static PerfilJpaEntity createPerfilMock(PerfilType perfilType) {
        return new PerfilJpaEntity(1L, perfilType, null);
    }

    public static List<PerfilJpaEntity> createPerfisMock(PerfilType... perfilTypes) {
        return IntStream.range(0, perfilTypes.length)
                .mapToObj(i -> new PerfilJpaEntity((long) (i + 1), perfilTypes[i], null))
                .collect(Collectors.toList());
    }

    public static List<PerfilRequest> createPerfisRequestMock() {
        return List.of(new PerfilRequest(PerfilType.ADMIN));
    }

    public static List<PerfilResponse> createPerfisResponseMock() {
        return List.of(new PerfilResponse(PerfilType.ADMIN));
    }

}
