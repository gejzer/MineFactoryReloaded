package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSyringeCure extends ItemSyringe {
    public ItemSyringeCure(int id) {
        super(id);
    }

    @Override
    public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe) {
        return (entity instanceof EntityZombie && ((EntityZombie) entity).isVillager());
    }

    @Override
    public boolean inject(World world, EntityLivingBase entity, ItemStack syringe) {
        ((EntityZombie) entity).startConversion(300);
        return true;
    }
}
