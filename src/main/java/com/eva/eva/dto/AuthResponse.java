
package com.eva.eva.dto;

public record AuthResponse(
        String mensaje,
        Long id,
        String nombre,
        String correo
) {}
