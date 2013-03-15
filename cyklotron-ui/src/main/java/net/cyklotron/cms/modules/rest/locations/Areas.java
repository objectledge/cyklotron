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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Path("locations/suggest/areas")
public class Areas
{
    @Inject
    private LocationDatabaseService locationDatabaseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Area> getMatchingLocations(@QueryParam("q") String query,
        @QueryParam("level") @DefaultValue("7") int level,
        @QueryParam("limit") @DefaultValue("20") int limit)
    {
        return toAreas(locationDatabaseService.getAreas(query, level, limit));
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

    @JsonInclude(Include.NON_NULL)
    public static class Area
    {
        private final String areaName;

        private final String areaType;

        private final String terc;

        private final String sym;

        private final String province;

        private final String district;

        private final String commune;

        public Area(Location loc)
        {
            this.areaName = loc.get("areaName");
            this.areaType = loc.get("areaType");
            this.terc = loc.get("terc");
            this.sym = loc.get("sym");
            int level = Integer.parseInt(loc.get("areaLevel"));
            this.province = (level > 2) ? loc.get("province") : null;
            this.district = (level > 4) ? loc.get("district") : null;
            this.commune = (level > 7) ? loc.get("commune") : null;
        }

        public String getAreaName()
        {
            return areaName;
        }

        public String getAreaType()
        {
            return areaType;
        }

        public String getTerc()
        {
            return terc;
        }

        public String getSym()
        {
            return sym;
        }

        public String getProvince()
        {
            return province;
        }

        public String getDistrict()
        {
            return district;
        }

        public String getCommune()
        {
            return commune;
        }
    }
}
