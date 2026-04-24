package org.acme.business;

import org.acme.business.ClienteBusiness.CepInvalidoException;
import org.acme.business.ClienteBusiness.ClienteNaoEncontradoException;
import org.acme.business.ClienteBusiness.EmailJaCadastradoException;
import org.acme.dto.ClienteRequest;
import org.acme.dto.NominatimResponse;
import org.acme.dto.ViaCEPResponse;
import org.acme.entities.Cliente;
import org.acme.repositories.ClienteRepository;
import org.acme.services.NominatimService;
import org.acme.services.ViaCEPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteBusinessTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ViaCEPService viaCEPService;

    @Mock
    NominatimService nominatimService;

    @InjectMocks
    ClienteBusiness clienteBusiness;

    private ClienteRequest clienteRequest;
    private Cliente cliente;
    private ViaCEPResponse viaCEPResponse;
    private NominatimResponse nominatimResponse;

    @BeforeEach
    void setUp() {
        // Setup ClienteRequest
        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João Silva");
        clienteRequest.setEmail("joao@example.com");
        clienteRequest.setCep("01310-100");

        // Setup Cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@example.com");
        cliente.setCep("01310-100");

        // Setup ViaCEPResponse
        viaCEPResponse = new ViaCEPResponse();
        viaCEPResponse.setCEP("01310-100");
        viaCEPResponse.setLogradouro("Avenida Paulista");
        viaCEPResponse.setBairro("Bela Vista");
        viaCEPResponse.setLocalidade("São Paulo");
        viaCEPResponse.setUf("SP");

        // Setup NominatimResponse
        nominatimResponse = new NominatimResponse();
        nominatimResponse.setLatitude(-23.561414);
        nominatimResponse.setLongitude(-46.656559);
        nominatimResponse.setMapUrl("https://www.openstreetmap.org/?mlat=-23.561414&mlon=-46.656559&zoom=15");
    }

    @Test
    void testListar_DeveRetornarListaDeClientes() {
        // Arrange
        List<Cliente> clientes = Arrays.asList(cliente, new Cliente());
        when(clienteRepository.listAll()).thenReturn(clientes);

        // Act
        List<Cliente> resultado = clienteBusiness.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).listAll();
    }

    @Test
    void testObter_DeveRetornarClientePorId() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(cliente);

        // Act
        Cliente resultado = clienteBusiness.obter(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getNome(), resultado.getNome());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testCriar_ComSucesso_SemCEP() throws Exception {
        // Arrange
        clienteRequest.setCep(null);
        when(clienteRepository.findByEmail(anyString())).thenReturn(null);

        // Act
        Cliente resultado = clienteBusiness.criar(clienteRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteRequest.getNome(), resultado.getNome());
        assertEquals(clienteRequest.getEmail(), resultado.getEmail());
        verify(clienteRepository, times(1)).findByEmail(clienteRequest.getEmail());
        verify(clienteRepository, times(1)).persist(any(Cliente.class));
        verify(viaCEPService, never()).buscarEnderecoPorCEP(anyString());
    }

    @Test
    void testCriar_ComSucesso_ComCEPValido() throws Exception {
        // Arrange
        when(clienteRepository.findByEmail(anyString())).thenReturn(null);
        when(viaCEPService.buscarEnderecoPorCEP(anyString())).thenReturn(viaCEPResponse);
        when(nominatimService.buscarCoordenadas(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(nominatimResponse);

        // Act
        Cliente resultado = clienteBusiness.criar(clienteRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteRequest.getNome(), resultado.getNome());
        assertEquals(clienteRequest.getEmail(), resultado.getEmail());
        assertEquals(viaCEPResponse.getLogradouro(), resultado.getLogradouro());
        assertEquals(viaCEPResponse.getBairro(), resultado.getBairro());
        assertEquals(nominatimResponse.getLatitude(), resultado.getLatitude());
        assertEquals(nominatimResponse.getLongitude(), resultado.getLongitude());
        
        verify(clienteRepository, times(1)).findByEmail(clienteRequest.getEmail());
        verify(clienteRepository, times(1)).persist(any(Cliente.class));
        verify(viaCEPService, times(1)).buscarEnderecoPorCEP(clienteRequest.getCep());
        verify(nominatimService, times(1)).buscarCoordenadas(
                viaCEPResponse.getLogradouro(),
                viaCEPResponse.getBairro(),
                viaCEPResponse.getLocalidade(),
                viaCEPResponse.getUf()
        );
    }

    @Test
    void testCriar_DeveLancarExcecao_EmailJaCadastrado() {
        // Arrange
        when(clienteRepository.findByEmail(anyString())).thenReturn(cliente);

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(
                EmailJaCadastradoException.class,
                () -> clienteBusiness.criar(clienteRequest)
        );

        assertEquals("E-mail já cadastrado no sistema", exception.getMessage());
        verify(clienteRepository, times(1)).findByEmail(clienteRequest.getEmail());
        verify(clienteRepository, never()).persist(any(Cliente.class));
    }

    @Test
    void testCriar_DeveLancarExcecao_CepInvalido() {
        // Arrange
        when(clienteRepository.findByEmail(anyString())).thenReturn(null);
        when(viaCEPService.buscarEnderecoPorCEP(anyString())).thenReturn(null);

        // Act & Assert
        CepInvalidoException exception = assertThrows(
                CepInvalidoException.class,
                () -> clienteBusiness.criar(clienteRequest)
        );

        assertEquals("CEP inválido ou não encontrado", exception.getMessage());
        verify(viaCEPService, times(1)).buscarEnderecoPorCEP(clienteRequest.getCep());
        verify(clienteRepository, never()).persist(any(Cliente.class));
    }

    @Test
    void testAtualizar_ComSucesso_SemAlterarCEP() throws Exception {
        // Arrange
        cliente.setCep("01310-100");
        clienteRequest.setCep("01310-100"); // Mesmo CEP
        
        when(clienteRepository.findById(1L)).thenReturn(cliente);

        // Act
        Cliente resultado = clienteBusiness.atualizar(1L, clienteRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteRequest.getNome(), resultado.getNome());
        assertEquals(clienteRequest.getEmail(), resultado.getEmail());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).persist(any(Cliente.class));
        verify(viaCEPService, never()).buscarEnderecoPorCEP(anyString());
    }

    @Test
    void testAtualizar_ComSucesso_AlterandoCEP() throws Exception {
        // Arrange
        cliente.setCep("01310-100");
        clienteRequest.setCep("04567-890"); // CEP diferente
        
        when(clienteRepository.findById(1L)).thenReturn(cliente);
        when(viaCEPService.buscarEnderecoPorCEP(anyString())).thenReturn(viaCEPResponse);
        when(nominatimService.buscarCoordenadas(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(nominatimResponse);

        // Act
        Cliente resultado = clienteBusiness.atualizar(1L, clienteRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteRequest.getCep(), resultado.getCep());
        verify(viaCEPService, times(1)).buscarEnderecoPorCEP(clienteRequest.getCep());
        verify(nominatimService, times(1)).buscarCoordenadas(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testAtualizar_DeveLancarExcecao_ClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(null);

        // Act & Assert
        ClienteNaoEncontradoException exception = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> clienteBusiness.atualizar(1L, clienteRequest)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, never()).persist(any(Cliente.class));
    }

    @Test
    void testAtualizar_DeveLancarExcecao_EmailJaCadastrado() {
        // Arrange
        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setEmail("email.antigo@example.com");
        
        Cliente clienteComEmailNovo = new Cliente();
        clienteComEmailNovo.setId(2L);
        clienteComEmailNovo.setEmail("joao@example.com");
        
        clienteRequest.setEmail("joao@example.com"); // Tentar usar email de outro cliente
        
        when(clienteRepository.findById(1L)).thenReturn(clienteExistente);
        when(clienteRepository.findByEmail("joao@example.com")).thenReturn(clienteComEmailNovo);

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(
                EmailJaCadastradoException.class,
                () -> clienteBusiness.atualizar(1L, clienteRequest)
        );

        assertEquals("E-mail já cadastrado para outro cliente", exception.getMessage());
        verify(clienteRepository, never()).persist(any(Cliente.class));
    }

    @Test
    void testDeletar_ComSucesso() throws Exception {
        // Arrange
        when(clienteRepository.deleteById(1L)).thenReturn(true);

        // Act
        clienteBusiness.deletar(1L);

        // Assert
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletar_DeveLancarExcecao_ClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.deleteById(1L)).thenReturn(false);

        // Act & Assert
        ClienteNaoEncontradoException exception = assertThrows(
                ClienteNaoEncontradoException.class,
                () -> clienteBusiness.deletar(1L)
        );

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
