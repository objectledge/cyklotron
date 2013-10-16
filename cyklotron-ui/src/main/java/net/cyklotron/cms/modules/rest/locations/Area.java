package net.cyklotron.cms.modules.rest.locations;

import net.cyklotron.cms.locations.Location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Area
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