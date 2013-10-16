package net.cyklotron.cms.modules.rest.locations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;


@Path("locations/suggest/areas")
public class Areas
{
    @Inject
    private LocationDatabaseService locationDatabaseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Area> getMatchingLocations(@QueryParam("q") String query,
        @QueryParam("a") @DefaultValue("") String enclosingArea,
        @QueryParam("lmin") @DefaultValue("0") int lmin,
        @QueryParam("lmax") @DefaultValue("7") int lmax,
        @QueryParam("limit") @DefaultValue("20") int limit)
    {
        return toAreas(locationDatabaseService.getAreas(query, enclosingArea, lmin, lmax, limit));
    }

    private List<Area> toAreas(List<Location> locations)
    {
        List<Area> areas = new ArrayList<>(locations.size());
        for(Location location : locations)
        {
            areas.add(new Area(location));
        }
        return areas;
    }
}
