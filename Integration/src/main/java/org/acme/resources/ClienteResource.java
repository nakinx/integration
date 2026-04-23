package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ViaCEPResponse;
import org.acme.entities.Cliente;
import org.acme.repositories.ClienteRepository;
import org.acme.services.ViaCEPService;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ViaCEPService viaCEPService;

    @GET
    public List<Cliente> listar() {
        return clienteRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Cliente obter(@PathParam("id") Long id) {
        return clienteRepository.findById(id);
    }

    @POST
    @Transactional
    public Response criar(Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("E-mail já cadastrado")
                    .build();
        }

        // Buscar dados de endereço através do ViaCEP caso o CEP seja fornecido
        if (cliente.getCep() != null && !cliente.getCep().isBlank()) {
            ViaCEPResponse resposta = viaCEPService.buscarEnderecoPorCEP(cliente.getCep());
            
            if (resposta != null) {
                cliente.setLogradouro(resposta.getLogradouro());
                cliente.setBairro(resposta.getBairro());
                cliente.setLocalidade(resposta.getLocalidade());
                cliente.setUf(resposta.getUf());
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("CEP inválido ou não encontrado")
                        .build();
            }
        }

        clienteRepository.persist(cliente);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setCep(clienteAtualizado.getCep());
        cliente.setUf(clienteAtualizado.getUf());
        cliente.setLocalidade(clienteAtualizado.getLocalidade());
        cliente.setBairro(clienteAtualizado.getBairro());
        cliente.setLogradouro(clienteAtualizado.getLogradouro());
        clienteRepository.persist(cliente);
        return Response.ok(cliente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = clienteRepository.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
