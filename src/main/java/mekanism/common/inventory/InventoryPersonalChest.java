package mekanism.common.inventory;

import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.block.BlockMachine.MachineType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;

public class InventoryPersonalChest extends InventoryBasic
{
	public EntityPlayer entityPlayer;
	public ItemStack itemStack;

	public boolean reading;

	public InventoryPersonalChest(EntityPlayer player)
	{
		super("PersonalChest", false, 55);
		entityPlayer = player;

		read();
	}

	public InventoryPersonalChest(ItemStack stack)
	{
		super("PersonalChest", false, 55);
		itemStack = stack;

		read();
	}

	@Override
	public void markDirty()
	{
		super.markDirty();

		if(!reading)
		{
			write();
		}
	}

	@Override
	public void openInventory()
	{
		read();
	}

	@Override
	public void closeInventory()
	{
		write();
	}

	public void write()
	{
		NBTTagList tagList = new NBTTagList();

		for(int slotCount = 0; slotCount < getSizeInventory(); slotCount++)
		{
			if(getStackInSlot(slotCount) != null)
			{
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setByte("Slot", (byte)slotCount);
				getStackInSlot(slotCount).writeToNBT(tagCompound);
				tagList.appendTag(tagCompound);
			}
		}

		if(getStack() != null)
		{
			((ISustainedInventory)getStack().getItem()).setInventory(tagList, getStack());
		}
	}

	public void read()
	{
		if(reading)
		{
			return;
		}

		reading = true;

		NBTTagList tagList = ((ISustainedInventory)getStack().getItem()).getInventory(getStack());

		if(tagList != null)
		{
			for(int tagCount = 0; tagCount < tagList.tagCount(); tagCount++)
			{
				NBTTagCompound tagCompound = (NBTTagCompound)tagList.getCompoundTagAt(tagCount);
				byte slotID = tagCompound.getByte("Slot");

				if(slotID >= 0 && slotID < getSizeInventory())
				{
					setInventorySlotContents(slotID, ItemStack.loadItemStackFromNBT(tagCompound));
				}
			}
		}

		reading = false;
	}

	public ItemStack getStack()
	{
		if(itemStack != null) return itemStack;
		if(MachineType.get(entityPlayer.getCurrentEquippedItem()) == MachineType.PERSONAL_CHEST){
			return entityPlayer.getCurrentEquippedItem();	
		}
		else
		{
			Mekanism.logger.error("Unable to write inventory data to Personal Chest.");
			entityPlayer.addChatMessage(new ChatComponentText(EnumColor.RED + "Unable to write inventory data to Personal Chest. Contact TNFC Support! " + EnumColor.DARK_BLUE + entityPlayer.getCommandSenderName() + EnumColor.RED + "!"));
			return null;
		}
	}
}
