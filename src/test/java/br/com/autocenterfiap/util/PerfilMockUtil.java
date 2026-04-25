package br.com.autocenterfiap.util;

import br.com.autocenterfiap.security.repository.entity.Perfil;
import br.com.autocenterfiap.security.enums.PerfilType;
import br.com.autocenterfiap.security.controller.request.PerfilRequest;
import br.com.autocenterfiap.security.controller.response.PerfilResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PerfilMockUtil {
    private PerfilMockUtil() {
    }

    public static Perfil createPerfilMock(PerfilType perfilType) {
        return new Perfil(1L, perfilType, null);
    }

    public static List<Perfil> createPerfisMock(PerfilType... perfilTypes) {
        return IntStream.range(0, perfilTypes.length)
                .mapToObj(i -> new Perfil((long) (i + 1), perfilTypes[i], null))
                .collect(Collectors.toList());
    }

    public static List<PerfilRequest> createPerfisRequestMock() {
        return List.of(new PerfilRequest(PerfilType.ADMIN));
    }

    public static List<PerfilResponse> createPerfisResponseMock() {
        return List.of(new PerfilResponse(PerfilType.ADMIN));
    }

}
