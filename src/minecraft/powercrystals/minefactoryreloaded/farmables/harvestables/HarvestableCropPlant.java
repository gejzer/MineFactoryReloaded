package powercrystals.minefactoryreloaded.farmables.harvestables;

import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;

import java.util.Map;

public class HarvestableCropPlant extends HarvestableStandard {
    private int _targetMeta;

    public HarvestableCropPlant(int blockID, int targetMeta) {
        super(blockID, HarvestType.Normal);
        _targetMeta = targetMeta;
    }

    @Override
    public boolean canBeHarvested(World world, Map<String, Boolean> harvesterSettings, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= _targetMeta;
    }
}
