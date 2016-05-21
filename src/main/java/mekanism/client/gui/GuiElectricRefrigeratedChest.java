package mekanism.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.energy.IEnergizedItem;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.common.inventory.container.ContainerElectricRefrigeratedChest;
import mekanism.common.tile.TileEntityElectricRefrigeratedChest;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiElectricRefrigeratedChest extends GuiMekanism
{
    public TileEntityElectricRefrigeratedChest tileEntity;
    public boolean isBlock;

    public GuiElectricRefrigeratedChest(InventoryPlayer inventory, TileEntityElectricRefrigeratedChest tentity)
    {
        super(tentity, new ContainerElectricRefrigeratedChest(inventory, tentity, null, true));

        xSize+=26;
        ySize+=64;
        tileEntity = tentity;
        isBlock = true;


        guiElements.add(new GuiSecurityTab(this, tileEntity, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png")));
    }

    public GuiElectricRefrigeratedChest(InventoryPlayer inventory, IInventory inv)
    {
        super(new ContainerElectricRefrigeratedChest(inventory, null, inv, false));

        xSize+=26;
        ySize+=64;
        isBlock = false;

        guiElements.add(new GuiSecurityTab(this, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int xAxis = (mouseX - (width - xSize) / 2);
        int yAxis = (mouseY - (height - ySize) / 2);

        fontRendererObj.drawString(LangUtils.localize("tile.MachineBlock.ElectricRefrigeratedChest.name"), 8, 6, 0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

        if(xAxis >= 180 && xAxis <= 184 && yAxis >= 32 && yAxis <= 84)
        {
            drawCreativeTabHoveringText(MekanismUtils.getEnergyDisplay(getEnergy()), xAxis, yAxis);
        }

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

        int xAxis = (mouseX - (width - xSize) / 2);
        int yAxis = (mouseY - (height - ySize) / 2);

        if(xAxis >= 179 && xAxis <= 197 && yAxis >= 88 && yAxis <= 106)
        {
            drawTexturedModalRect(guiWidth + 179, guiHeight + 88, 176 + 26, 52, 18, 18);
        }
        else {
            drawTexturedModalRect(guiWidth + 179, guiHeight + 88, 176 + 26, 70, 18, 18);
        }

        int displayInt = getScale();
        drawTexturedModalRect(guiWidth + 180, guiHeight + 32 + 52 - displayInt, 176 + 26, 52 - displayInt, 4, displayInt);
    }

    public int getScale()
    {
        if(isBlock)
        {
            return tileEntity.getScaledEnergyLevel(52);
        }
        else {
            ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
            return (int)(((IEnergizedItem)stack.getItem()).getEnergy(stack)*52 / ((IEnergizedItem)stack.getItem()).getMaxEnergy(stack));
        }
    }

    public double getEnergy()
    {
        if(isBlock)
        {
            return tileEntity.getEnergy();
        }
        else {
            ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
            return ((IEnergizedItem)stack.getItem()).getEnergy(stack);
        }
    }
}
