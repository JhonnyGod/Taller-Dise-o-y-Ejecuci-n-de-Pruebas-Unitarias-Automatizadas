
package com.cps.disenodepruebas.taller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.cps.disenodepruebas.taller.dto.Precio;
import com.cps.disenodepruebas.taller.dto.TotalEntrada;
import com.cps.disenodepruebas.taller.entity.enums.TipoCliente;

class EnvioServiceTest {

    private final EnvioService envioService = new EnvioService();

    // =====================================================
    // 1. PRUEBAS HAPPY PATH
    // =====================================================

    @ParameterizedTest(name = "{0}")
    @MethodSource("casosHappyPath")
    @DisplayName("Debe calcular correctamente diferentes tarifas")
    void debeCalcularPrecioCorrectamente(
            String escenario,
            BigDecimal monto,
            TipoCliente tipoCliente,
            Boolean cupon,
            Integer distancia,
            BigDecimal esperado) {

        // Arrange
        TotalEntrada entrada = crearEntrada(
            monto, tipoCliente, cupon, distancia
        );

        // Act
        Precio resultado = envioService.obtenerPrecio(entrada);

        // Assert
        verificarPrecio(esperado, resultado);
    }

    static Stream<Arguments> casosHappyPath() {
        return Stream.of(
            Arguments.of(
                "ESTANDAR sin cupón",
                new BigDecimal("100"),
                TipoCliente.ESTANDAR,
                false, 30,
                new BigDecimal("105")
            ),
            Arguments.of(
                "VIP sin cupón",
                new BigDecimal("100"),
                TipoCliente.VIP,
                false, 30,
                new BigDecimal("90")
            ),
            Arguments.of(
                "CORPORATIVO con compra de 500",
                new BigDecimal("500"),
                TipoCliente.CORPORATIVO,
                false, 30,
                new BigDecimal("405")
            ),
            Arguments.of(
                "CORPORATIVO con compra de 999.99",
                new BigDecimal("999.99"),
                TipoCliente.CORPORATIVO,
                false, 100,
                new BigDecimal("799.99")
            ),
            Arguments.of(
                "CORPORATIVO con compra de 1000",
                new BigDecimal("1000"),
                TipoCliente.CORPORATIVO,
                false, 100,
                new BigDecimal("700")
            ),
            Arguments.of(
                "VIP con cupón válido",
                new BigDecimal("100"),
                TipoCliente.VIP,
                true, 30,
                new BigDecimal("85.75")
            ),
            Arguments.of(
                "CORPORATIVO con cupón válido",
                new BigDecimal("1000"),
                TipoCliente.CORPORATIVO,
                true, 250,
                new BigDecimal("665")
            )
        );
    }

    // =====================================================
    // 2. VALORES LÍMITE DE MONTO
    // =====================================================

    @ParameterizedTest(name = "Monto de compra: {0}")
    @MethodSource("casosLimiteMonto")
    @DisplayName("Debe manejar correctamente los límites del monto")
    void debeRespetarLimitesDelMonto(
            BigDecimal monto,
            BigDecimal esperado) {

        // Arrange
        TotalEntrada entrada = crearEntrada(
            monto,
            TipoCliente.ESTANDAR,
            false,
            50
        );

        // Act
        Precio resultado = envioService.obtenerPrecio(entrada);

        // Assert
        verificarPrecio(esperado, resultado);
    }

    static Stream<Arguments> casosLimiteMonto() {
        return Stream.of(
            Arguments.of(
                new BigDecimal("0.01"),
                new BigDecimal("5.01")
            ),
            Arguments.of(
                new BigDecimal("499.99"),
                new BigDecimal("504.99")
            ),
            Arguments.of(
                new BigDecimal("500.00"),
                new BigDecimal("500.00")
            ),
            Arguments.of(
                new BigDecimal("999.99"),
                new BigDecimal("999.99")
            ),
            Arguments.of(
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
            )
        );
    }

    // =====================================================
    // 3. VALORES LÍMITE DE DISTANCIA
    // =====================================================

    @ParameterizedTest(name = "Distancia: {0} km")
    @MethodSource("casosLimiteDistancia")
    @DisplayName("Debe calcular correctamente las tarifas por distancia")
    void debeRespetarLimitesDeDistancia(
            Integer distancia,
            BigDecimal esperado) {

        // Arrange
        TotalEntrada entrada = crearEntrada(
            new BigDecimal("100"),
            TipoCliente.ESTANDAR,
            false,
            distancia
        );

        // Act
        Precio resultado = envioService.obtenerPrecio(entrada);

        // Assert
        verificarPrecio(esperado, resultado);
    }

    static Stream<Arguments> casosLimiteDistancia() {
        return Stream.of(
            Arguments.of(1, new BigDecimal("105")),
            Arguments.of(50, new BigDecimal("105")),
            Arguments.of(51, new BigDecimal("110")),
            Arguments.of(200, new BigDecimal("110")),
            Arguments.of(201, new BigDecimal("120")),
            Arguments.of(500, new BigDecimal("120"))
        );
    }

    // =====================================================
    // 4. PRUEBAS DE ENVÍO GRATUITO
    // =====================================================

    @ParameterizedTest(name = "{0}")
    @MethodSource("casosEnvioGratis")
    @DisplayName("Debe aplicar correctamente las condiciones de envío gratuito")
    void debeAplicarEnvioGratuitoCorrectamente(
            String escenario,
            BigDecimal monto,
            TipoCliente tipoCliente,
            Boolean cupon,
            BigDecimal esperado) {

        // Arrange
        TotalEntrada entrada = crearEntrada(
            monto, tipoCliente, cupon, 50
        );

        // Act
        Precio resultado = envioService.obtenerPrecio(entrada);

        // Assert
        verificarPrecio(esperado, resultado);
    }

    static Stream<Arguments> casosEnvioGratis() {
        return Stream.of(
            Arguments.of(
                "Monto neto inferior a 500",
                new BigDecimal("499.99"),
                TipoCliente.ESTANDAR,
                false,
                new BigDecimal("504.99")
            ),
            Arguments.of(
                "Monto neto exactamente de 500",
                new BigDecimal("500"),
                TipoCliente.ESTANDAR,
                false,
                new BigDecimal("500")
            ),
            Arguments.of(
                "Monto neto VIP superior a 500",
                new BigDecimal("600"),
                TipoCliente.VIP,
                false,
                new BigDecimal("510")
            ),
            Arguments.of(
                "Descuentos que dejan el monto por debajo de 500",
                new BigDecimal("580"),
                TipoCliente.VIP,
                true,
                new BigDecimal("473.35")
            )
        );
    }

    // =====================================================
    // 5. PRUEBAS DE EXCEPCIONES
    // =====================================================

    @ParameterizedTest(name = "{0}")
    @MethodSource("casosInvalidos")
    @DisplayName("Debe rechazar argumentos inválidos")
    void debeLanzarExcepcionConArgumentosInvalidos(
            String escenario,
            TotalEntrada entrada,
            String mensajeEsperado) {

        // Arrange
        // La entrada inválida se prepara en casosInvalidos().

        // Act y Assert
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> envioService.obtenerPrecio(entrada)
        );

        assertEquals(mensajeEsperado, excepcion.getMessage());
    }

    static Stream<Arguments> casosInvalidos() {
        return Stream.of(
            Arguments.of(
                "Monto nulo",
                crearEntrada(null, TipoCliente.VIP, true, 50),
                "El monto de compra debe ser mayor que cero"
            ),
            Arguments.of(
                "Monto igual a cero",
                crearEntrada(BigDecimal.ZERO, TipoCliente.VIP, true, 50),
                "El monto de compra debe ser mayor que cero"
            ),
            Arguments.of(
                "Monto negativo",
                crearEntrada(new BigDecimal("-10"), TipoCliente.VIP, true, 50),
                "El monto de compra debe ser mayor que cero"
            ),
            Arguments.of(
                "Distancia nula",
                crearEntrada(new BigDecimal("100"), TipoCliente.VIP, true, null),
                "La distancia debe estar entre 1 y 500 km"
            ),
            Arguments.of(
                "Distancia igual a cero",
                crearEntrada(new BigDecimal("100"), TipoCliente.VIP, true, 0),
                "La distancia debe estar entre 1 y 500 km"
            ),
            Arguments.of(
                "Distancia superior a 500",
                crearEntrada(new BigDecimal("100"), TipoCliente.VIP, true, 501),
                "La distancia debe estar entre 1 y 500 km"
            ),
            Arguments.of(
                "Tipo de cliente nulo",
                crearEntrada(new BigDecimal("100"), null, true, 50),
                "El tipo de cliente es obligatorio"
            ),
            Arguments.of(
                "Cupón nulo",
                crearEntrada(new BigDecimal("100"), TipoCliente.VIP, null, 50),
                "Debe indicar si el cupón es válido"
            )
        );
    }

    @Test
    @DisplayName("Debe rechazar un DTO nulo")
    void debeLanzarExcepcionCuandoElDtoEsNulo() {

        // Arrange
        TotalEntrada entrada = null;

        // Act y Assert
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> envioService.obtenerPrecio(entrada)
        );

        assertEquals(
            "Los datos de entrada son obligatorios",
            excepcion.getMessage()
        );
    }

    // =====================================================
    // 6. MÉTODOS AUXILIARES
    // =====================================================

    private static TotalEntrada crearEntrada(
            BigDecimal monto,
            TipoCliente tipoCliente,
            Boolean cupon,
            Integer distancia) {

        TotalEntrada entrada = new TotalEntrada();

        entrada.setMontoCompra(monto);
        entrada.setTipoCliente(tipoCliente);
        entrada.setEsCuponValido(cupon);
        entrada.setDistanciaKm(distancia);

        return entrada;
    }

    private void verificarPrecio(
            BigDecimal esperado,
            Precio resultado) {

        assertEquals(
            0,
            esperado.compareTo(resultado.getPrecio()),
            "El precio calculado no coincide con el esperado"
        );
    }
}
