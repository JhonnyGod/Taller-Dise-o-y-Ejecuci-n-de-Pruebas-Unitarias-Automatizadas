
package com.cps.disenodepruebas.taller.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.cps.disenodepruebas.taller.dto.Precio;
import com.cps.disenodepruebas.taller.dto.TotalEntrada;
import com.cps.disenodepruebas.taller.entity.enums.TipoCliente;

@Service
public class EnvioService {

    // Constantes generales
    private static final BigDecimal CERO = BigDecimal.ZERO;
    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal MONTO_ENVIO_GRATIS = new BigDecimal("500");
    private static final BigDecimal MONTO_CORPORATIVO = new BigDecimal("1000");

    // Porcentajes de descuento
    private static final BigDecimal DESCUENTO_VIP = new BigDecimal("15");
    private static final BigDecimal DESCUENTO_CORPORATIVO = new BigDecimal("20");
    private static final BigDecimal DESCUENTO_CORPORATIVO_MAYOR = new BigDecimal("30");
    private static final BigDecimal DESCUENTO_CUPON = new BigDecimal("5");

    public Precio obtenerPrecio(TotalEntrada dto) {

        // 1. Validar los argumentos de entrada
        validarEntrada(dto);

        BigDecimal montoCompra = dto.getMontoCompra();

        // 2. Aplicar descuento según el tipo de cliente
        BigDecimal montoConDescuento = aplicarDescuentoCliente(
            montoCompra,
            dto.getTipoCliente()
        );

        // 3. Aplicar descuento del cupón, si es válido
        if (Boolean.TRUE.equals(dto.getEsCuponValido())) {
            montoConDescuento = aplicarDescuento(
                montoConDescuento,
                DESCUENTO_CUPON
            );
        }

        // 4. Determinar el costo del envío
        BigDecimal costoEnvio = calcularCostoEnvio(dto.getDistanciaKm());

        // 5. Comprobar si el envío es gratuito
        boolean envioGratis =
            montoConDescuento.compareTo(MONTO_ENVIO_GRATIS) >= 0
            || (dto.getTipoCliente() == TipoCliente.CORPORATIVO
                && montoCompra.compareTo(MONTO_CORPORATIVO) >= 0);

        if (envioGratis) {
            costoEnvio = CERO;
        }

        // 6. Calcular el total
        BigDecimal total = montoConDescuento.add(costoEnvio);

        Precio precioFinal = new Precio();
        precioFinal.setPrecio(total);

        return precioFinal;
    }

    private void validarEntrada(TotalEntrada dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                "Los datos de entrada son obligatorios"
            );
        }

        if (dto.getMontoCompra() == null
                || dto.getMontoCompra().compareTo(CERO) <= 0) {
            throw new IllegalArgumentException(
                "El monto de compra debe ser mayor que cero"
            );
        }

        if (dto.getDistanciaKm() == null
                || dto.getDistanciaKm() < 1
                || dto.getDistanciaKm() > 500) {
            throw new IllegalArgumentException(
                "La distancia debe estar entre 1 y 500 km"
            );
        }

        if (dto.getTipoCliente() == null) {
            throw new IllegalArgumentException(
                "El tipo de cliente es obligatorio"
            );
        }

        if (dto.getEsCuponValido() == null) {
            throw new IllegalArgumentException(
                "Debe indicar si el cupón es válido"
            );
        }
    }

    private BigDecimal aplicarDescuentoCliente(
            BigDecimal monto,
            TipoCliente tipoCliente) {

        switch (tipoCliente) {
            case ESTANDAR:
                return monto;

            case VIP:
                return aplicarDescuento(monto, DESCUENTO_VIP);

            case CORPORATIVO:
                if (monto.compareTo(MONTO_CORPORATIVO) < 0) {
                    return aplicarDescuento(monto, DESCUENTO_CORPORATIVO);
                }

                return aplicarDescuento(monto, DESCUENTO_CORPORATIVO_MAYOR);

            default:
                throw new IllegalArgumentException(
                    "Tipo de cliente no válido"
                );
        }
    }

    private BigDecimal aplicarDescuento(
            BigDecimal monto,
            BigDecimal porcentaje) {

        BigDecimal descuento = monto
            .multiply(porcentaje)
            .divide(CIEN, 2, RoundingMode.HALF_UP);

        return monto.subtract(descuento);
    }

    private BigDecimal calcularCostoEnvio(Integer distanciaKm) {

        if (distanciaKm <= 50) {
            return new BigDecimal("5");
        }

        if (distanciaKm <= 200) {
            return new BigDecimal("10");
        }

        return new BigDecimal("20");
    }
}
