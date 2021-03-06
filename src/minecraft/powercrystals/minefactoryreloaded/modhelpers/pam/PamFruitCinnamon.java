package powercrystals.minefactoryreloaded.modhelpers.pam;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PamFruitCinnamon extends PamFruit {
    private int _cinnamonItemId;

    public PamFruitCinnamon(int sourceId, int itemId) {
        super(sourceId);
        _cinnamonItemId = itemId;
    }

    @Override
    public ItemStack getReplacementBlock(World world, int x, int y, int z) {
        return new ItemStack(getSourceBlockId(), 1, 0);
    }

    @Override
    public List<ItemStack> getDrops(World world, Random rand, int x, int y, int z) {
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(_cinnamonItemId, 1, 0));
        return drops;
    }
}
