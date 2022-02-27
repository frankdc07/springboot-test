package org.frank.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.frank.test.springboot.app.Datos.*;

import org.frank.test.springboot.app.exceptions.DineroInsuficienteException;
import org.frank.test.springboot.app.models.Banco;
import org.frank.test.springboot.app.models.Cuenta;
import org.frank.test.springboot.app.repositories.BancoRepository;
import org.frank.test.springboot.app.repositories.CuentaRepository;
import org.frank.test.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;

	@MockBean
	BancoRepository bancoRepository;

	@Autowired
	CuentaService cuentaService;

	@BeforeEach
	void setUp() {
//		cuentaRepository = mock(CuentaRepository.class);
//		bancoRepository = mock(BancoRepository.class);
//		cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta_002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		cuentaService.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		int totalTransferencias = cuentaService.revisarTotalTransferencias(1L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());
		assertEquals(1, totalTransferencias);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);

		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta_002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {
			cuentaService.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		int totalTransferencias = cuentaService.revisarTotalTransferencias(1L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());
		assertEquals(0, totalTransferencias);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);

		verify(cuentaRepository, never()).save(any(Cuenta.class));

		verify(bancoRepository).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta_001());

		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1L);

		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Andres", cuenta1.getPersona());
		assertEquals("Andres", cuenta2.getPersona());
		verify(cuentaRepository, times(2)).findById(1L);
	}

	@Test
	void testFindAll() {
		// Given
		List<Cuenta> cuentas = Arrays.asList(crearCuenta_001().orElseThrow(), crearCuenta_002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(cuentas);

		// When
		List<Cuenta> result = cuentaService.findAll();

		// Then
		assertEquals(2, result.size());
		assertFalse(result.isEmpty());
		assertTrue(result.contains(crearCuenta_002().orElseThrow()));
		verify(cuentaRepository).findAll();
	}

	@Test
	void testSave() {
		// Given
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		// When
		Cuenta result = cuentaService.save(cuenta);

		// Then
		assertEquals(3L, result.getId());
		assertEquals("Pepe", result.getPersona());
		assertEquals("3000", result.getSaldo().toPlainString());
		verify(cuentaRepository).save(any());
	}
}
