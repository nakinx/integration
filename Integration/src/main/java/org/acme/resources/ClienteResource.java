package org.acme.resources;

import jakarta.inject.Inject;
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
import org.acme.business.ClienteBusiness;
import org.acme.business.ClienteBusiness.ClienteNaoEncontradoException;
import org.acme.business.ClienteBusiness.CepInvalidoException;
import org.acme.business.ClienteBusiness.EmailJaCadastradoException;
import org.acme.dto.ClienteRequest;
import org.acme.entities.Cliente;

import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cliente", description = "Mantem de clientes")
public class ClienteResource {

    @Inject
    ClienteBusiness clienteBusiness;

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
        return clienteBusiness.listar();
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
    public Response obter(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathParam("id") Long id
    ) {
        Cliente cliente = clienteBusiness.obter(id);
        if (cliente == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(cliente).build();
    }

    @POST
    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente no sistema. O email deve ser único. O CEP será usado para buscar automaticamente os dados de endereço e coordenadas geográficas.")
    @APIResponses({
        @APIResponse(
            responseCode = "201", 
            description = "Cliente criado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, 
            schema = @Schema(implementation = Cliente.class)
        )),
        @APIResponse(responseCode = "400", description = "Dados inválidos ou CEP não encontrado"),
        @APIResponse(responseCode = "409", description = "Email já cadastrado no sistema")
    })
    public Response criar(@Valid ClienteRequest request) {
        try {
            Cliente cliente = clienteBusiness.criar(request);
            return Response.status(Response.Status.CREATED).entity(cliente).build();
        } catch (EmailJaCadastradoException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        } catch (CepInvalidoException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
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
            @Valid ClienteRequest request
    ) {
        try {
            Cliente cliente = clienteBusiness.atualizar(id, request);
            return Response.ok(cliente).build();
        } catch (ClienteNaoEncontradoException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (EmailJaCadastradoException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        } catch (CepInvalidoException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
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
        try {
            clienteBusiness.deletar(id);
            return Response.noContent().build();
        } catch (ClienteNaoEncontradoException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
