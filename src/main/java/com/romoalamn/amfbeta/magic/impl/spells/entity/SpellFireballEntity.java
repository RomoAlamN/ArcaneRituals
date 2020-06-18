package com.romoalamn.amfbeta.magic.impl.spells.entity;

import com.romoalamn.amfbeta.magic.api.spell.entity.SpellEntity;
import com.romoalamn.amfbeta.magic.impl.SpellRegister;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpellFireballEntity extends SpellEntity {
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(SpellFireballEntity.class, DataSerializers.VARINT);

    public SpellFireballEntity(EntityType<? extends IForgeEntity> type, World worldIn) {
        super(type, worldIn);
        this.setInvulnerable(true);
    }

    public SpellFireballEntity(World world, LivingEntity caster) {
        this(SpellRegister.FIREBALL_ENTITY.get(), world);
        this.caster = caster;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(COLOR, 0xFFBB5500);
    }

    public void tickLife() {
        if (this.ticksExisted > 20) {
            this.remove();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();
        tickLife();
        // don't damage creatures immediately.
        if (!world.isRemote) {
            if (this.ticksExisted == 5) {
                AxisAlignedBB coll = this.getBoundingBox().grow(20);
                List<Entity> ent = world.getEntitiesWithinAABB((EntityType<Entity>) null, coll, (entity) -> entity.getDistanceSq(this) < 20);
                for (Entity t : ent) {
                    DamageSource src = getDamageSource("onFire", "fireball");
                    t.attackEntityFrom(src, 6.0F);
                }
            }
        } else {
            //create the particles
            for (int i = 0; i < 1; i++) {
                if(rand.nextBoolean()) continue;
                double x = getPosXRandom(1.0);
                double y = getPosYRandom();
                double z = getPosZRandom(1.0);
                Vec3d ppos = new Vec3d(x, y, z);
                Vec3d opos = getPositionVec();

                Vec3d mot = ppos.subtract(opos).normalize().mul(2.0, 2.0, 2.0);

                world.addParticle(ParticleTypes.EXPLOSION, x, y, z, mot.x, mot.y, mot.z);
            }
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("FireColor")) {
            this.dataManager.set(COLOR, compound.getInt("FireColor"));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("FireColor", this.dataManager.get(COLOR));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void giveExtraData(CompoundNBT extraData) {
        if (extraData.contains("FireColor")) {
            this.dataManager.set(COLOR, extraData.getInt("FireColor"));
        }
    }
}
