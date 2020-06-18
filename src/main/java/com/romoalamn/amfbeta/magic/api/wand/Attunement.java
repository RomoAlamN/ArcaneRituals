package com.romoalamn.amfbeta.magic.api.wand;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Attunement extends ForgeRegistryEntry<Attunement> implements INBTSerializable {
    private String name;
    public Attunement(String name){
        this.name = name;
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", name);
        return tag;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        name = ((CompoundNBT) nbt).getString("id");
    }
}
