package me.shihab.revolut.resources;

import io.dropwizard.hibernate.UnitOfWork;
import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);

    private final TransferService transferService;

    public TransferResource(TransferService transferService) {
        this.transferService = transferService;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public TransferDTO transfer(@Valid TransferDTO transferDTO) throws RuntimeException {
        LOGGER.info("Received transfer request: {}", transferDTO.toString());
        return transferService.transfer(transferDTO);
    }
}
