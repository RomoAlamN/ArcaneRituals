package com.romoalamn.amfbeta.magic.api.spell;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class SpellInstance {
    private AbstractSpell spell;
    private CompoundNBT extraData;
    public int tickCount = 0;
    public SpellInstance(@Nonnull AbstractSpell spell, @Nonnull CompoundNBT extraData){
        this.spell = spell;
        this.extraData = extraData;
    }
    @Nonnull
    public AbstractSpell getSpell() {
        return spell;
    }

    public CompoundNBT getData() {
        return extraData;
    }
}
