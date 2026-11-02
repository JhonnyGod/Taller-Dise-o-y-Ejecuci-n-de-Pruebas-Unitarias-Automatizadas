package com.cps.disenodepruebas.taller.dto;

import java.math.BigDecimal;

import com.cps.disenodepruebas.taller.entity.enums.TipoCliente;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class TotalEntrada {

    @NotNull(message = "El monto de compra es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = false,
        message = "El monto de compra debe ser mayor que cero"
    )
    private BigDecimal montoCompra;

    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipoCliente;

    @NotNull(message = "Debe indicar si el cupón es válido")
    private Boolean esCuponValido;

    @NotNull(message = "La distancia es obligatoria")
    @Min(value = 1, message = "La distancia mínima es de 1 km")
    @Max(value = 500, message = "La distancia máxima es de 500 km")
    private Integer distanciaKm;

}