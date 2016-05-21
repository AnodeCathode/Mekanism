package mekanism.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.api.energy.IEnergizedItem;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.block.BlockMachine.MachineType;
import mekanism.common.inventory.InventoryElectricRefrigeratedChest;
import mekanism.common.tile.TileEntityElectricRefrigeratedChest;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketElectricRefrigeratedChest implements IMessageHandler<PacketElectricRefrigeratedChest.ElectricRefrigeratedChestMessage, IMessage>
    {
        @Override
        public IMessage onMessage(ElectricRefrigeratedChestMessage message, MessageContext context)
        {
		EntityPlayer player = PacketHandler.getPlayer(context);

		if(message.packetType == ElectricRefrigeratedChestPacketType.SERVER_OPEN)
		{
			try {
				if(message.isBlock)
				{
					TileEntityElectricRefrigeratedChest tileEntity = (TileEntityElectricRefrigeratedChest)message.coord4D.getTileEntity(player.worldObj);
                    if(message.useEnergy)
                    {
                        tileEntity.setEnergy(tileEntity.getEnergy() - 100);
                    }

					MekanismUtils.openElectricRefrigeratedChestGui((EntityPlayerMP)player, tileEntity, null, true);
				}
				else {
					ItemStack stack = player.getCurrentEquippedItem();

					if(MachineType.get(stack) == MachineType.ELECTRIC_REFRIGERATED_CHEST)
					{
                        if(message.useEnergy)
                        {
                            ((IEnergizedItem)stack.getItem()).setEnergy(stack, ((IEnergizedItem)stack.getItem()).getEnergy(stack) - 100);
                        }

						InventoryElectricRefrigeratedChest inventory = new InventoryElectricRefrigeratedChest(player);
						MekanismUtils.openElectricRefrigeratedChestGui((EntityPlayerMP)player, null, inventory, false);
					}
				}
			} catch(Exception e) {
				Mekanism.logger.error("Error while handling electric chest open packet.");
				e.printStackTrace();
			}
		}
		else if(message.packetType == ElectricRefrigeratedChestPacketType.CLIENT_OPEN)
		{
			try {
				int x = message.coord4D != null ? message.coord4D.xCoord : 0;
				int y = message.coord4D != null ? message.coord4D.yCoord : 0;
				int z = message.coord4D != null ? message.coord4D.zCoord : 0;

				Mekanism.proxy.openElectricRefrigeratedChest(player, message.guiType, message.windowId, message.isBlock, x, y, z);
			} catch(Exception e) {
				Mekanism.logger.error("Error while handling electric chest open packet.");
				e.printStackTrace();
			}
		}

		return null;
        }

	public static class ElectricRefrigeratedChestMessage implements IMessage
	{
		public ElectricRefrigeratedChestPacketType packetType;

		public boolean isBlock;

		public int guiType;
		public int windowId;

        public boolean useEnergy;

		public Coord4D coord4D;

		public ElectricRefrigeratedChestMessage() {}

		//This is a really messy implementation...
		public ElectricRefrigeratedChestMessage(ElectricRefrigeratedChestPacketType type, boolean b1, boolean b2, int i1, int i2, Coord4D c1)
		{
			packetType = type;

			switch(packetType)
			{
				case CLIENT_OPEN:
					guiType = i1;
					windowId = i2;
					isBlock = b1;

					if(isBlock)
					{
						coord4D = c1;
					}

					break;
				case SERVER_OPEN:
                    useEnergy = b1;
					isBlock = b2;

					if(isBlock)
					{
						coord4D = c1;
					}

					break;
			}
		}

		@Override
		public void toBytes(ByteBuf dataStream)
		{
			dataStream.writeInt(packetType.ordinal());

			switch(packetType)
			{
				case CLIENT_OPEN:
					dataStream.writeInt(guiType);
					dataStream.writeInt(windowId);
					dataStream.writeBoolean(isBlock);

					if(isBlock)
					{
						dataStream.writeInt(coord4D.xCoord);
 						dataStream.writeInt(coord4D.yCoord);
						dataStream.writeInt(coord4D.zCoord);
						dataStream.writeInt(coord4D.dimensionId);
					}

					break;
				case SERVER_OPEN:
                    dataStream.writeBoolean(useEnergy);
					dataStream.writeBoolean(isBlock);

					if(isBlock)
					{
						dataStream.writeInt(coord4D.xCoord);
						dataStream.writeInt(coord4D.yCoord);
						dataStream.writeInt(coord4D.zCoord);
						dataStream.writeInt(coord4D.dimensionId);
					}

					break;
			}
		}

		@Override
		public void fromBytes(ByteBuf dataStream)
		{
            packetType = ElectricRefrigeratedChestPacketType.values()[dataStream.readInt()];

			if(packetType == ElectricRefrigeratedChestPacketType.SERVER_OPEN)
			{
                useEnergy = dataStream.readBoolean();
				isBlock = dataStream.readBoolean();

				if(isBlock)
				{
					coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
				}
			}
			else if(packetType == ElectricRefrigeratedChestPacketType.CLIENT_OPEN)
			{
				guiType = dataStream.readInt();
				windowId = dataStream.readInt();
				isBlock = dataStream.readBoolean();

				if(isBlock)
				{
					coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
				}
			}
		}
	}

	public static enum ElectricRefrigeratedChestPacketType
	{
		CLIENT_OPEN,
		SERVER_OPEN
	}
}