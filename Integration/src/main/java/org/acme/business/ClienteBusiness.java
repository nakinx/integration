package org.acme.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dto.ClienteRequest;
import org.acme.dto.NominatimResponse;
import org.acme.dto.ViaCEPResponse;
import org.acme.entities.Cliente;
import org.acme.repositories.ClienteRepository;
import org.acme.services.NominatimService;
import org.acme.services.ViaCEPService;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ClienteBusiness {

    private static final Logger logger = Logger.getLogger(ClienteBusiness.class);

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ViaCEPService viaCEPService;

    @Inject
    NominatimService nominatimService;

    /**
     * Lista todos os clientes cadastrados
     */
    public List<Cliente> listar() {
        return clienteRepository.listAll();
    }

    /**
     * Obtém um cliente pelo ID
     */
    public Cliente obter(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Cria um novo cliente com validação de email único e busca de endereço/coordenadas
     */
    @Transactional
    public Cliente criar(ClienteRequest request) throws EmailJaCadastradoException, CepInvalidoException {
        // Validar se email já está cadastrado
        if (clienteRepository.findByEmail(request.getEmail()) != null) {
            logger.warn("Tentativa de criar cliente com email já cadastrado: " + request.getEmail());
            throw new EmailJaCadastradoException("E-mail já cadastrado no sistema");
        }

        // Criar novo cliente com os dados da requisição
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setCep(request.getCep());

        // Buscar dados de endereço através do ViaCEP caso o CEP seja fornecido
        if (cliente.getCep() != null && !cliente.getCep().isBlank()) {
            enriquecerClienteComEndereco(cliente);
        }

        clienteRepository.persist(cliente);
        logger.info("Cliente criado com sucesso: " + cliente.getId() + " - " + cliente.getEmail());
        
        return cliente;
    }

    /**
     * Atualiza um cliente existente
     */
    @Transactional
    public Cliente atualizar(Long id, ClienteRequest request) throws ClienteNaoEncontradoException, EmailJaCadastradoException, CepInvalidoException {
        Cliente cliente = clienteRepository.findById(id);
        
        if (cliente == null) {
            logger.warn("Tentativa de atualizar cliente não encontrado com ID: " + id);
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }

        // Validar se o novo email já está cadastrado para outro cliente
        if (!cliente.getEmail().equals(request.getEmail())) {
            Cliente clienteComEmail = clienteRepository.findByEmail(request.getEmail());
            if (clienteComEmail != null) {
                logger.warn("Tentativa de atualizar para email já cadastrado: " + request.getEmail());
                throw new EmailJaCadastradoException("E-mail já cadastrado para outro cliente");
            }
        }

        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());

        // Verificar se o CEP foi modificado
        if (request.getCep() != null && !request.getCep().equals(cliente.getCep())) {
            if (!request.getCep().isBlank()) {
                cliente.setCep(request.getCep());
                enriquecerClienteComEndereco(cliente);
            } else {
                cliente.setCep(request.getCep());
                limparDadosEndereco(cliente);
            }
        }

        clienteRepository.persist(cliente);
        logger.info("Cliente atualizado com sucesso: " + cliente.getId());
        
        return cliente;
    }

    /**
     * Deleta um cliente pelo ID
     */
    @Transactional
    public void deletar(Long id) throws ClienteNaoEncontradoException {
        boolean deletado = clienteRepository.deleteById(id);
        
        if (!deletado) {
            logger.warn("Tentativa de deletar cliente não encontrado com ID: " + id);
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }
        
        logger.info("Cliente deletado com sucesso: " + id);
    }

    /**
     * Enriquece os dados do cliente buscando endereço no ViaCEP e coordenadas no Nominatim
     */
    private void enriquecerClienteComEndereco(Cliente cliente) throws CepInvalidoException {
        ViaCEPResponse resposta = viaCEPService.buscarEnderecoPorCEP(cliente.getCep());
        
        if (resposta == null) {
            logger.warn("CEP inválido ou não encontrado: " + cliente.getCep());
            throw new CepInvalidoException("CEP inválido ou não encontrado");
        }

        cliente.setLogradouro(resposta.getLogradouro());
        cliente.setBairro(resposta.getBairro());
        cliente.setLocalidade(resposta.getLocalidade());
        cliente.setUf(resposta.getUf());

        // Buscar coordenadas via Nominatim
        NominatimResponse coordenadas = nominatimService.buscarCoordenadas(
                resposta.getLogradouro(),
                resposta.getBairro(),
                resposta.getLocalidade(),
                resposta.getUf()
        );

        if (coordenadas != null) {
            cliente.setLatitude(coordenadas.getLatitude());
            cliente.setLongitude(coordenadas.getLongitude());
            cliente.setMapUrl(coordenadas.getMapUrl());
            logger.info("Coordenadas encontradas para CEP: " + cliente.getCep());
        } else {
            logger.warn("Coordenadas não encontradas para o endereço do CEP: " + cliente.getCep());
        }
    }

    /**
     * Remove dados de endereço e coordenadas do cliente
     */
    private void limparDadosEndereco(Cliente cliente) {
        cliente.setLogradouro(null);
        cliente.setBairro(null);
        cliente.setLocalidade(null);
        cliente.setUf(null);
        cliente.setLatitude(null);
        cliente.setLongitude(null);
        cliente.setMapUrl(null);
    }

    // Exceções de negócio
    public static class EmailJaCadastradoException extends Exception {
        public EmailJaCadastradoException(String message) {
            super(message);
        }
    }

    public static class CepInvalidoException extends Exception {
        public CepInvalidoException(String message) {
            super(message);
        }
    }

    public static class ClienteNaoEncontradoException extends Exception {
        public ClienteNaoEncontradoException(String message) {
            super(message);
        }
    }
}
