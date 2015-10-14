package net.cyklotron.cms.modules.rest.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationDatabaseService;

@Path("locations/exact/areas")
public class ExactAreaList
{
    @Inject
    private LocationDatabaseService locationDatabaseService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Area> getMatchingLocations(List<String> areaIds)
    {
        List<Area> areas = new ArrayList<>();
        for(String areaId : areaIds)
        {
            Map<String, String> fieldValues = new HashMap<>();
            if(areaId.contains("."))
            {
                String tokens[] = areaId.split("\\.");
                fieldValues.put("terc", tokens[0]);
                fieldValues.put("sym", tokens[1]);
                fieldValues.put("areaLevel", "8");
            }
            else
            {
                fieldValues.put("terc", areaId);
                fieldValues.put("areaLevel", Integer.toString(areaId.length()));
            }
            Location l = locationDatabaseService.getExactMatch(fieldValues);
            if(l != null)
            {
                areas.add(new Area(l));
            }
        }
        return areas;
    }

}
