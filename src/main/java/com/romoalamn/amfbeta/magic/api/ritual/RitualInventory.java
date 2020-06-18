package com.romoalamn.amfbeta.magic.api.ritual;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RitualInventory extends Inventory {
    private final World world;
    private final BlockPos ourPosition;

    private final ArrayList<TileEntity> altars = new ArrayList<>();

    public RitualInventory(World w, BlockPos pos) {
        world = w;
        ourPosition = pos;
        rescanAltars();
    }

    private void rescanAltars() {
        altars.clear();
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                BlockPos position = ourPosition.east(x - 5).north(x - 5);
                BlockState state = world.getBlockState(position);
                if (state.getBlock() == RitualBlocks.ALTAR.get()) {
                    altars.add(world.getTileEntity(position));
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return altars.size();
    }

    @Override
    public boolean isEmpty() {
        return altars.isEmpty() || areAllAltarsEmpty();
    }

    private boolean areAllAltarsEmpty() {
        Holder<Boolean> notEmpty = new Holder<>();
        for (TileEntity tile : altars) {
            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                if (getStackInSlot(0).isEmpty()) {
                    notEmpty.set(true);
                }
            });
            if (notEmpty.get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the stack in the given slot.
     *
     * @param index
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index >= altars.size()) {
            return ItemStack.EMPTY;
        }
        Holder<ItemStack> holder = new Holder<>();
        altars.get(index).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            holder.set(h.getStackInSlot(0));
        });
        return holder.getOrElse(ItemStack.EMPTY);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     *
     * @param index
     * @param count
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getStackInSlot(index);
        if (stack != ItemStack.EMPTY && stack.getCount() > count) {
            ItemStack ret = new ItemStack(stack.getItem(), count);
            stack.shrink(count);
            return ret;
        } else {
            removeStackFromSlot(index);
            return stack;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     *
     * @param index
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index >= altars.size()) {
            return ItemStack.EMPTY;
        }
        Holder<ItemStack> holder = new Holder<>();
        altars.get(index).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            ItemStack stack = h.getStackInSlot(0);
            holder.set(h.extractItem(0, stack.getCount(), false));
        });
        return holder.getOrElse(ItemStack.EMPTY);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param index
     * @param stack
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        removeStackFromSlot(index);
        altars.get(index).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                h.insertItem(0, stack, false);
        });
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty() {
        super.markDirty();
        rescanAltars();
    }

    @Override
    public void clear() {
        for(TileEntity altar : altars){
            altar.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h-> {
                h.extractItem(0, 64, true);
            });
        }
    }

    static class Holder<T> {
        private T value;

        public T get() {
            return value;
        }

        public T getOrElse(T other) {
            if (value == null) {
                return other;
            }
            return value;
        }

        public void set(T newVal) {
            value = newVal;
        }
    }
}
