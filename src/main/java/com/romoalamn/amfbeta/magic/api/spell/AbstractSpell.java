package com.romoalamn.amfbeta.magic.api.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AbstractSpell extends ForgeRegistryEntry<AbstractSpell> implements INBTSerializable {
    public static AbstractSpell EMPTY = new AbstractSpell() {
        @Override
        public INBT serializeNBT() {
            return new CompoundNBT();
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            // do nothing, as it this class is useless.
        }
        @Override
        public void cast(IWorld worldIn, LivingEntity caster, Vec3d direction, ItemStack wandIn, Hand handIn) {

        }

        @Override
        public void renderCast(IWorld world, SpellInstance instance, Vec3d origin, Vec3d direction, ItemStack wandIn) {

        }
    };
    public abstract void cast(IWorld worldIn, LivingEntity caster, Vec3d direction, ItemStack wandIn, Hand handIn);
    public abstract void renderCast(IWorld world, SpellInstance instance, Vec3d origin, Vec3d direction, ItemStack wandIn);
    public String getTranslationKey(){
        return "spell.magic.empty";
    }
}
