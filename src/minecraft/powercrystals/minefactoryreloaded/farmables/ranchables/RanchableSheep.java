package powercrystals.minefactoryreloaded.farmables.ranchables;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

import java.util.LinkedList;
import java.util.List;

public class RanchableSheep implements IFactoryRanchable {
    @Override
    public Class<? extends EntityLiving> getRanchableEntity() {
        return EntitySheep.class;
    }

    @Override
    public List<ItemStack> ranch(World world, EntityLiving entity, IInventory rancher) {
        EntitySheep s = (EntitySheep) entity;

        if (s.getSheared() || s.getGrowingAge() < 0) {
            return null;
        }

        List<ItemStack> stacks = new LinkedList<ItemStack>();
        stacks.add(new ItemStack(Block.cloth, 1, s.getFleeceColor()));
        s.setSheared(true);

        return stacks;
    }
}
