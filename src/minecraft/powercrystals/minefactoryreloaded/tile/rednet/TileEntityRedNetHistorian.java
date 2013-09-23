package powercrystals.minefactoryreloaded.tile.rednet;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import powercrystals.minefactoryreloaded.core.ArrayQueue;
import powercrystals.minefactoryreloaded.net.NetworkHandler;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

import java.io.DataInputStream;
import java.io.IOException;

public class TileEntityRedNetHistorian extends TileEntityFactory {
    /*private class HistorianData
	{
		int value;
		long worldTime;
		
		public HistorianData(int value, long worldTime)
		{
			this.value = value;
			this.worldTime = worldTime;
		}
	}*/

    private ArrayQueue<Integer> _valuesClient = new ArrayQueue<Integer>(100);
    private int _currentValueClient = 0;
    private int _currentSubnet = 0;

    private int[] _lastValues = new int[16];
    //private Map<Integer, Queue<HistorianData>> _data = new HashMap<Integer, Queue<HistorianData>>();

    public TileEntityRedNetHistorian() {
		/*for(int i = 0; i < 16; i++)
		{
			_data.put(i, new ArrayBlockingQueue<HistorianData>(100));
		}*/
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("facing", getDirectionFacing().ordinal());
        data.setInteger("subnet", _currentSubnet);
        data.setInteger("current", _lastValues[_currentSubnet]);
        Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, data);
        return packet;
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        _currentSubnet = pkt.data.getInteger("subnet");
        _currentValueClient = pkt.data.getInteger("current");
        rotateDirectlyTo(pkt.data.getInteger("facing"));
    }

    @Override
    public void validate() {
        super.validate();
        if (!worldObj.isRemote) {
            setSelectedSubnet(_currentSubnet);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) {
            _valuesClient.pop();
            _valuesClient.push(_currentValueClient);
        }
    }

    public void valuesChanged(int[] values) {
        for (int i = 0; i < 16; i++) {
            if (values[i] != _lastValues[i]) {
                //_data.get(i).add(new HistorianData(values[i], worldObj.getWorldTime()));
                _lastValues[i] = values[i];
                if (i == _currentSubnet) {
                    PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 50, worldObj.provider.dimensionId,
                            NetworkHandler.getBuilder().startBuild(xCoord, yCoord, zCoord).append(values[i]).build());
                            //PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.HistorianValueChanged, new Object[]{xCoord, yCoord, zCoord, values[i]}));
                }
            }
        }
    }

    public Integer[] getValues() {
        Integer[] values = new Integer[_valuesClient.size()];
        return _valuesClient.toArray(values);
    }

    public void setSelectedSubnet(int newSubnet) {
        _currentSubnet = newSubnet;
        if (worldObj.isRemote) {
            _valuesClient.fill(null);
        } else {
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 50, worldObj.provider.dimensionId,
                    NetworkHandler.getBuilder().startBuild(xCoord, yCoord, zCoord).append(_lastValues[_currentSubnet]).build());
                    //PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.HistorianValueChanged, new Object[]{xCoord, yCoord, zCoord, _lastValues[_currentSubnet]}));
        }
    }

    @Override
    public void updateClient(DataInputStream stream, EntityPlayer player) throws IOException {
        setClientValue(stream.readInt());
    }

    public int getSelectedSubnet() {
        return _currentSubnet;
    }

    public void setClientValue(int value) {
        _currentValueClient = value;
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        _currentSubnet = nbttagcompound.getInteger("subnet");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("subnet", _currentSubnet);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
