package TFC.Containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import TFC.TFCItems;
import TFC.API.Metal;
import TFC.Containers.Slots.SlotForShowOnly;
import TFC.Core.Metal.MetalRegistry;

public class ContainerLiquidVessel extends ContainerTFC 
{
	private World world;
	private int posX;
	private int posY;
	private int posZ;
	private EntityPlayer player;
	public InventoryCrafting containerInv = new InventoryCrafting(this, 1, 1);

	public int bagsSlotNum = 0;
	public int metalAmount = 0;

	public ContainerLiquidVessel(InventoryPlayer playerinv, World world, int x, int y, int z) {
		this.player = playerinv.player;
		this.world = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		bagsSlotNum = player.inventory.currentItem;
		layoutContainer(playerinv);

	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	private void layoutContainer(IInventory playerInventory) {

		this.addSlotToContainer(new Slot(containerInv, 0, 80, 34));

		int row;
		int col;

		for (row = 0; row < 9; ++row) 
		{
			if(row == bagsSlotNum) {
				this.addSlotToContainer(new SlotForShowOnly(playerInventory, row, 8 + row * 18, 142));
			} else {
				this.addSlotToContainer(new Slot(playerInventory, row, 8 + row * 18, 142));
			}
		}

		for (row = 0; row < 3; ++row) 
		{
			for (col = 0; col < 9; ++col) 
			{
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9+9, 8 + col * 18, 84 + row * 18));
			}
		}
	}

	@Override
	public void detectAndSendChanges()
	{
		//Load the metal info from the liquid container
		NBTTagCompound nbt = player.inventory.getStackInSlot(bagsSlotNum).getTagCompound();

		if(nbt != null)
		{
			Metal m = MetalRegistry.instance.getMetalFromString((nbt.getString("MetalType")));
			metalAmount = nbt.getInteger("MetalAmount");

			if(!world.isRemote && m != null)
			{
				ItemStack input = containerInv.getStackInSlot(0);
				if(input != null && input.getItem().itemID == TFCItems.CeramicMold.itemID && input.getItemDamage() == 1 && metalAmount > 0)
				{
					int amt = 99;
					containerInv.setInventorySlotContents(0, new ItemStack(m.MeltedItemID, 1, amt));
					if(metalAmount-1 <= 0)
					{
						nbt.removeTag("MetalType");
						nbt.removeTag("MetalAmount");
						player.inventory.getStackInSlot(bagsSlotNum).setItemDamage(1);
					} else 
					{
						nbt.setInteger("MetalAmount", metalAmount-2);
					}

					player.inventory.getStackInSlot(bagsSlotNum).setTagCompound(nbt);
				}
				else if(input != null && input.getItem().itemID == m.MeltedItemID && input.getItemDamage() > 0)
				{
					input.setItemDamage(input.getItemDamage() - 1);

					if(metalAmount-1 <= 0)
					{
						nbt.removeTag("MetalType");
						nbt.removeTag("MetalAmount");
						player.inventory.getStackInSlot(bagsSlotNum).setItemDamage(1);
					} else 
					{
						nbt.setInteger("MetalAmount", metalAmount-1);
					}
				}
			}
		}
		super.detectAndSendChanges();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int clickedIndex) {
		ItemStack returnedStack = null;
		Slot clickedSlot = (Slot)this.inventorySlots.get(clickedIndex);

		if (clickedSlot != null
				&& clickedSlot.getHasStack())
		{
			ItemStack clickedStack = clickedSlot.getStack();
			returnedStack = clickedStack.copy();

			if (clickedIndex == 0)
			{
				if (!this.mergeItemStack(clickedStack, 1, inventorySlots.size(), true)) {
					return null;
				}
			}
			else if (clickedIndex > 0 && clickedIndex < inventorySlots.size() && clickedStack.getItem().itemID == TFCItems.CeramicMold.itemID && 
					clickedStack.getItemDamage() == 1) {
				if (!this.mergeItemStack(clickedStack, 0, 1, false)) {
					return null;
				}
			}

			if (clickedStack.stackSize == 0) {
				clickedSlot.putStack((ItemStack)null);
			} else {
				clickedSlot.onSlotChanged();
			}

			if (clickedStack.stackSize == returnedStack.stackSize) {
				return null;
			}
			clickedSlot.onPickupFromSlot(player, clickedStack);
		}
		return returnedStack;
	}
}
