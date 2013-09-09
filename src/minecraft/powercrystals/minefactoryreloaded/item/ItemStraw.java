package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.MFRRegistry;

public class ItemStraw extends ItemFactory {
    public ItemStraw(int id) {
        super(id);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
            if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
                int blockId = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
                if (MFRRegistry.getFluidDrinkHandlers().containsKey(blockId)) {
                    MFRRegistry.getFluidDrinkHandlers().get(blockId).onDrink(player);
                    world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
                }
            }
        }

        return stack;
    }


    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }


    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
            if (MFRRegistry.getFluidDrinkHandlers().containsKey(world.getBlockId(mop.blockX, mop.blockY, mop.blockZ))) {
                player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            }
        }
        return stack;
    }
}
