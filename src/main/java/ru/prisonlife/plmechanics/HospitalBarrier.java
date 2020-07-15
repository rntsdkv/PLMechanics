package ru.prisonlife.plmechanics;

import org.bukkit.Location;
import org.bukkit.World;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.database.json.BoldPoint;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class HospitalBarrier {

    public World world;

    public int x1;
    public int y1;
    public int z1;

    public int x2;
    public int y2;
    public int z2;

    public HospitalBarrier(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;

        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;

        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public boolean isInside(Location location) {
        BoldPoint boldPoint1 = BoldPoint.fromLocation(new Location(world, x1, y1, z1));
        BoldPoint boldPoint2 = BoldPoint.fromLocation(new Location(world, x2, y2, z2));
        BoldPoint boldPoint = BoldPoint.fromLocation(location);
        return PrisonLife.getPositionManager().atArea(boldPoint1, boldPoint2, boldPoint);
    }
}
