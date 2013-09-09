package powercrystals.minefactoryreloaded.farmables.ranchables;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RanchableChicken implements IFactoryRanchable {
    protected Random rand = new Random();

    @Override
    public Class<? extends EntityLiving> getRanchableEntity() {
        return EntityChicken.class;
    }

    @Override
    public List<ItemStack> ranch(World world, EntityLiving entity, IInventory rancher) {
        List<ItemStack> drops = new LinkedList<ItemStack>();
        EntityChicken chicken = ((EntityChicken) entity);
        if (chicken.timeUntilNextEgg < 300) {
            chicken.playSound("mob.chicken.plop", 1.0F, (chicken.rand.nextFloat() - chicken.rand.nextFloat()) * 0.2F + 1.0F);
            chicken.attackEntityFrom(DamageSource.generic, 0);
            chicken.setRevengeTarget(chicken); // panic
            chicken.timeUntilNextEgg = chicken.rand.nextInt(6000) + 6200;
            if (rand.nextInt(4) != 0) {
                drops.add(new ItemStack(Item.egg));
            } else {
                int k = chicken.rand.nextInt(4) + 1;
                drops.add(new ItemStack(Item.feather, k));
            }
        }
        return drops;
    }
}
