package mekanism.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.MekanismConfig;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.*;
import mekanism.common.inventory.container.ContainerElectricRefrigeratedChest;
import mekanism.common.tile.TileEntityElectricRefrigeratedChest;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiElectricRefrigeratedChest extends GuiMekanism {
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
            guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"), 179, 60));
            guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"), 178, 118).with(GuiSlot.SlotOverlay.POWER));

        }

    public GuiElectricRefrigeratedChest(InventoryPlayer inventory, IInventory inv) {
        super(new ContainerElectricRefrigeratedChest(inventory, null, inv, false));

        xSize += 26;
        ySize += 64;
        isBlock = false;

        guiElements.add(new GuiSecurityTab(this, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png")));
        guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"), 164, 15));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"), 142, 34).with(GuiSlot.SlotOverlay.POWER));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int xAxis = (mouseX - (width - xSize) / 2);
        int yAxis = (mouseY - (height - ySize) / 2);

        fontRendererObj.drawString(LangUtils.localize("tile.MachineBlock.ElectricRefrigeratedChest.name"), 8, 6, 0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "GuiElectricRefrigeratedChest.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

        int xAxis = mouseX - guiWidth;
        int yAxis = mouseY - guiHeight;

        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

    }
}
