package powercrystals.minefactoryreloaded.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

/* packet values:
 * 0: current work
 * 1: current energy
 * 2: current idle
 * 3: current tank 
 * 4: tank id
 * 5: tank meta
 */

public class ContainerFactoryPowered extends ContainerFactoryInventory {
    public ContainerFactoryPowered(TileEntityFactoryPowered te, InventoryPlayer inv) {
        super(te, inv);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < crafters.size(); i++) {
            ICrafting crafter = (ICrafting) crafters.get(i);
            crafter.sendProgressBarUpdate(this, 0, ((TileEntityFactoryPowered) _te).getWorkDone());
            crafter.sendProgressBarUpdate(this, 1, ((TileEntityFactoryPowered) _te).getEnergyStored());
            crafter.sendProgressBarUpdate(this, 2, ((TileEntityFactoryPowered) _te).getIdleTicks());
        }
    }

    @Override
    public void updateProgressBar(int var, int value) {
        super.updateProgressBar(var, value);

        if (var == 0) ((TileEntityFactoryPowered) _te).setWorkDone(value);
        else if (var == 1) ((TileEntityFactoryPowered) _te).setEnergyStored(value);
        else if (var == 2) ((TileEntityFactoryPowered) _te).setIdleTicks(value);
    }
}
