package com.romoalamn.amfbeta.magic.api.spell;

import com.romoalamn.amfbeta.magic.api.SpellUtilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;

import java.util.Random;

public class Spell extends AbstractSpell {

    @Override
    public void cast(IWorld worldIn, LivingEntity caster, Vec3d direction, ItemStack wandIn, Hand handIn) {
        // create some particles.
        //double check.
        Vec3d origin = caster.getEyePosition(1.0f);
        if (worldIn.isRemote()) {
            Random r = worldIn.getRandom();
            for (int i = 0; i < 10; i++) {
                double x = randomWithVariance(r, origin.x, 1.0);
                double y = randomWithVariance(r, origin.y, 2.0);
                double z = randomWithVariance(r, origin.z, 2.0);
                worldIn.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            }
        } else {
            SpellUtilities.informPlayersOfSpellSuccess(worldIn,
                    new SpellInstance(this, new CompoundNBT()),
                    caster.getEyePosition(1.0f),
                    caster.getEyePosition(1.0f),
                    wandIn);
        }
    }

    @Override
    public void renderCast(IWorld world, SpellInstance instance, Vec3d origin, Vec3d direction, ItemStack wandIn) {

    }

    protected double randomWithVariance(Random r, double origin, double variance) {
        return origin + r.nextDouble() * (2 * variance) - variance;
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", getRegistryName().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        // do not use!!!
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
