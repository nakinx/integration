package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.acme.dto.ViaCEPResponse;
import org.acme.entities.Cliente;
import org.acme.repositories.ClienteRepository;
import org.acme.services.ViaCEPService;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cliente", description = "Mantem de clientes")
public class ClienteResource {

    @Inject
    ClienteRepository clienteRepository;

    @GET
    @Operation(
        summary = "Listar todos os clientes", 
        description = "Retorna uma lista com todos os clientes cadastrados"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Lista de clientes retornada com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = Cliente.class)
        ))
    })
    public List<Cliente> listar() {
        return clienteRepository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Obter cliente por ID", 
        description = "Retorna os detalhes de um cliente específico pelo seu ID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Cliente encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = Cliente.class)
        )),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Cliente obter(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathParam("id") Long id
    ) {
        return clienteRepository.findById(id);
    }

    @POST
    @Transactional
    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente no sistema. O email deve ser único.")
    @APIResponses({
        @APIResponse(
            responseCode = "201", 
            description = "Cliente criado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = Cliente.class)
        )),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "409", description = "Email já cadastrado no sistema")
    })
    public Response criar(@Valid Cliente cliente) {

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
    @Operation(
        summary = "Atualizar cliente", 
        description = "Atualiza os dados de um cliente existente"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Cliente atualizado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = Cliente.class))
        ),
        @APIResponse(responseCode = "400", description = "Dados inválidos"),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado"),
        @APIResponse(responseCode = "409", description = "Email já cadastrado para outro cliente")
    })
    public Response atualizar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathParam("id") Long id, 
            @Valid Cliente clienteAtualizado
    ) {

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
    @Operation(
        summary = "Deletar cliente", 
        description = "Remove um cliente do sistema"
    )
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @APIResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public Response deletar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathParam("id") Long id
    ) {
        boolean deletado = clienteRepository.deleteById(id);
    
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    
        return Response.noContent().build();
    }
}
