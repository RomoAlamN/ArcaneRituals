package com.romoalamn.amfbeta.magic.api.wand;

import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;

public interface IWand {
    void castEffect(IWorld worldIn, LivingEntity caster, AbstractSpell spell, Vec3d direction);

    Attunement getAttunement(ItemStack stack);

    ResourceLocation getItemRegistryName();
}
