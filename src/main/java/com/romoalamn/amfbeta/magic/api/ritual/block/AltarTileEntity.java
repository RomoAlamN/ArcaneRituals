package com.romoalamn.amfbeta.magic.api.ritual.block;

import com.romoalamn.amfbeta.magic.api.ritual.RitualBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AltarTileEntity extends TileEntity {
    public AltarTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(this::createItemHandler);

    public AltarTileEntity() {
        this(RitualBlocks.ALTAR_TILE.get());
    }

    private IItemHandler createItemHandler (){
        return new ItemStackHandler(1);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
