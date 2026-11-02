# Taller de pruebas unitarias – Calculadora de tarifas de envío

**Integrantes:** 
- Juan Camilo Diaz Valencia
- Jairo Andrés Gómez Cardona
- Juan David Rojas Narváez
- Jhony Fernando Duque Villada


**Fecha:** 2 de noviembre de 2026

## 1. Introducción

En este taller se desarrolló una aplicación para calcular el costo de un envío teniendo en cuenta el monto de compra, el tipo de cliente, los cupones de descuento y la distancia.

Se realizaron pruebas unitarias para comprobar que los cálculos fueran correctos y que el sistema rechazara los datos inválidos.

## 2. Entorno de pruebas

| Herramienta | Versión o uso |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.2 |
| JUnit | 5 |
| Maven | Gestión y ejecución de pruebas |
| JaCoCo | 0.8.14 – Cobertura de código |
| Lombok | Generación de getters y setters |

Se seleccionó JUnit 5 por su integración con Java y Maven, además de permitir ejecutar pruebas parametrizadas para evaluar diferentes escenarios sin repetir código.

## 3. Casos de prueba

Se utilizaron tres tipos de pruebas: camino feliz, valores límite y excepciones.

### 3.1. Camino feliz

| ID | Escenario | Entrada | Resultado |
|---|---|---|---:|
| HP-01 | Cliente estándar | $100, sin cupón, 30 km | $105 |
| HP-02 | Cliente VIP | $100, sin cupón, 30 km | $90 |
| HP-03 | Corporativo | $500, sin cupón, 30 km | $405 |
| HP-04 | Corporativo | $999,99, sin cupón, 100 km | $799,99 |
| HP-05 | Corporativo | $1.000, sin cupón, 100 km | $700 |
| HP-06 | VIP con cupón | $100, con cupón, 30 km | $85,75 |
| HP-07 | Corporativo con cupón | $1.000, con cupón, 250 km | $665 |

### 3.2. Valores límite

**Monto de compra:** cliente estándar, sin cupón y distancia de 50 km.

| ID | Monto | Resultado esperado |
|---|---:|---:|
| VL-01 | $0,01 | $5,01 |
| VL-02 | $499,99 | $504,99 |
| VL-03 | $500,00 | $500,00 |
| VL-04 | $999,99 | $999,99 |
| VL-05 | $1.000,00 | $1.000,00 |

**Distancia:** compra de $100, cliente estándar y sin cupón.

| ID | Distancia | Resultado esperado |
|---|---:|---:|
| VL-06 | 1 km | $105 |
| VL-07 | 50 km | $105 |
| VL-08 | 51 km | $110 |
| VL-09 | 200 km | $110 |
| VL-10 | 201 km | $120 |
| VL-11 | 500 km | $120 |

### 3.3. Excepciones

| ID | Escenario | Resultado esperado |
|---|---|---|
| EX-01 | DTO nulo | IllegalArgumentException |
| EX-02 | Monto nulo | IllegalArgumentException |
| EX-03 | Monto igual a cero | IllegalArgumentException |
| EX-04 | Monto negativo | IllegalArgumentException |
| EX-05 | Distancia nula | IllegalArgumentException |
| EX-06 | Distancia igual a cero | IllegalArgumentException |
| EX-07 | Distancia superior a 500 | IllegalArgumentException |
| EX-08 | Tipo de cliente nulo | IllegalArgumentException |
| EX-09 | Cupón nulo | IllegalArgumentException |

## 4. Desarrollo de las pruebas

### Paso 1. Clase principal

Se implementó la clase `EnvioService`, encargada de realizar los cálculos y aplicar las reglas de negocio.

**Captura:** código fuente de `EnvioService.java`.

### Paso 2. Clase de pruebas

Se creó `EnvioServiceTest.java`, utilizando JUnit 5 y pruebas parametrizadas para evaluar los diferentes escenarios.

Se utilizaron las aserciones `assertEquals()` y `assertThrows()` para verificar los resultados y las excepciones.

**Captura:** código fuente de `EnvioServiceTest.java`.

### Paso 3. Ejecución de las pruebas

Las pruebas se ejecutaron desde la terminal mediante el comando:

```bash
mvn clean test
```

**Captura:** resultado de la ejecución de Maven.

### Paso 4. Cobertura de código

Se utilizó JaCoCo para generar el informe de cobertura mediante:

```bash
mvn clean verify
```

El informe se encuentra en:

```text
target/site/jacoco/index.html
```

**Captura:** informe de cobertura generado por JaCoCo.

## 5. Resultados

La ejecución de las pruebas permitió comprobar los cálculos de la tarifa de envío, las condiciones de descuentos, los límites establecidos y el manejo de entradas inválidas.

Según el informe de JaCoCo, se obtuvieron los siguientes resultados:

| Métrica | Cobertura |
|---|---:|
| Instrucciones | 73 % |
| Ramas | 94 % |

La clase `EnvioService` alcanzó un 97 % de cobertura de instrucciones y un 94 % de cobertura de ramas.

## 6. Conclusión

Las pruebas realizadas permitieron verificar el funcionamiento de la calculadora de tarifas de envío y detectar posibles errores en las reglas de negocio. El uso de JUnit 5 facilitó la ejecución de diferentes escenarios, mientras que JaCoCo permitió medir la cobertura alcanzada por las pruebas.
