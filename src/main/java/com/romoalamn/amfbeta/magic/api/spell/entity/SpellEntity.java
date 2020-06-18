package com.romoalamn.amfbeta.magic.api.spell.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SpellEntity extends Entity {
    public SpellEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    protected LivingEntity caster;
    public DamageSource getDamageSource(String directType, String indirectType){
        DamageSource src;
        if (caster == null) {
            src = (new IndirectEntityDamageSource(directType, this, this)).setFireDamage().setProjectile();
        } else {
            src = (new IndirectEntityDamageSource(indirectType, this, caster)).setFireDamage().setProjectile();
        }
        return src;
    }

    @Override
    protected void registerData() {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
