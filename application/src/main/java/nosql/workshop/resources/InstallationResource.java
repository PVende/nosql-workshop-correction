package nosql.workshop.resources;

import com.google.inject.Inject;
import net.codestory.http.annotations.Get;
import nosql.workshop.model.Installation;
import nosql.workshop.model.stats.InstallationsStats;
import nosql.workshop.services.InstallationService;

import java.util.List;

import static net.codestory.http.errors.NotFoundException.notFoundIfNull;

/**
 * Resource permettant de gérer l'accès à l'API pour les Installations.
 */
public class InstallationResource {

    private final InstallationService installationService;

    @Inject
    public InstallationResource(InstallationService installationService) {
        this.installationService = installationService;
    }

    @Get("/")
    public List<Installation> list() {
        // TODO gérer correctement la pagination !
        return this.installationService.list(1, 25);
    }

    @Get("/:numero")
    public Installation get(String numero) {
        return notFoundIfNull(this.installationService.get(numero));
    }


    @Get("/random")
    public Installation random() {
        return installationService.random();
    }


    @Get("/stats")
    public InstallationsStats stats() {
        InstallationsStats stats = new InstallationsStats();
        stats.setTotalCount(installationService.count());
        stats.setCountByActivity(installationService.countByActivity());
        stats.setInstallationWithMaxEquipments(installationService.installationWithMaxEquipments());
        return stats;
    }
}
