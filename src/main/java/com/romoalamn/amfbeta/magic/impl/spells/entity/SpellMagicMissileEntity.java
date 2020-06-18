package com.romoalamn.amfbeta.magic.impl.spells.entity;

import com.romoalamn.amfbeta.magic.api.spell.entity.DirectionSerializer;
import com.romoalamn.amfbeta.magic.api.spell.entity.SpellEntity;
import com.romoalamn.amfbeta.magic.impl.SpellRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SpellMagicMissileEntity extends SpellEntity implements IProjectile {

    private static final int MAX_DISTANCE = 16;
    private static final int MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

    private static final int EXPLODE_DISTANCE = 2;
    private static final int EXPLODE_DISTANCE_SQ = EXPLODE_DISTANCE * EXPLODE_DISTANCE;

//    private LivingEntity caster;
    private Entity target;
    private static final DataParameter<Float> VELOCITY = EntityDataManager.createKey(SpellMagicMissileEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Vec3d> DIR = EntityDataManager.createKey(SpellMagicMissileEntity.class, DirectionSerializer.INSTANCE);

    private boolean canTarget(Entity ent){
        return ent instanceof LivingEntity
                && ent.getDistanceSq(this) < MAX_DISTANCE_SQ
                && !ent.isInvisible()
                && !ent.equals(caster)
                && !ent.isOnSameTeam(this);
    }
    private boolean canCollide(Entity ent){
        return ent.getDistanceSq(this) < EXPLODE_DISTANCE_SQ && !ent.equals(caster)
                && !(ent instanceof SpellEntity)
                && !ent.isOnSameTeam(this);
    }
    private int compareWithContract(Entity ent, Entity ent2){
        if(ent.getDistanceSq(this) == ent2.getDistanceSq(this)) return 0;
        return ent.getDistanceSq(this) < ent2.getDistanceSq(this) ? -1: 1;
    }

    public SpellMagicMissileEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.getBoundingBox();
    }

    public SpellMagicMissileEntity(LivingEntity caster, World world) {
        this(SpellRegister.MAGIC_MISSILE_ENTITY.get(), world);
        this.caster = caster;
        this.setPosition(caster.getPosXWidth(0.3), caster.getPosYEye(), caster.getPosZWidth(0.3));
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(VELOCITY, 0.0f);
        dataManager.register(DIR, Vec3d.ZERO);
        this.setNoGravity(true);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    int ticksNull = 0;

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        setMotion(dataManager.get(DIR).scale(dataManager.get(VELOCITY)));
        super.tick();
        if (target == null && ticksNull > 500) {
            // failExplode();
            if (!world.isRemote) {
                this.remove();
            }
            System.out.println("I timed out!");
            explode(SuccessType.FAIL);
            return;
        }
        if (this.isEntityInsideOpaqueBlock()) {
            //explode, damaging nearby entities.
            explode(SuccessType.SUCCESS);
        }
        if (!world.isRemote) {
            List<Entity> collision = world.getEntitiesWithinAABB((EntityType<Entity>) null, getBoundingBox(), this::canCollide);
            if (!collision.isEmpty()) {
                for (Entity ent : collision) {
                    DamageSource src = getDamageSource("missile", "magicCast");
                    if (!world.isRemote) {
                        ent.attackEntityFrom(src, 6.0f);
                    }
                }
                remove();

                //explode()
                explode(SuccessType.SUCCESS);
            }
        }
        // set the target if someone is within range.
        if (target == null || !target.isAlive()) {
            if(!world.isRemote && ticksExisted % 5 == 0) {
                List<Entity> enemies = world.getEntitiesWithinAABB((EntityType<Entity>) null, getBoundingBox().grow(100), this::canTarget);
                enemies.sort(this::compareWithContract);
                ticksNull++;
                if (!enemies.isEmpty() && enemies.get(0) != null && !enemies.equals(caster)) {
                    target = enemies.get(0);
                    ticksNull = 0;
                }
                this.setMotion(dataManager.get(DIR).normalize().scale(dataManager.get(VELOCITY)));
            }
        } else {
            Vec3d targetVector = target.getEyePosition(1.0f);
            Vec3d motionVector = targetVector.subtract(getPositionVector());
            Vec3d mot = motionVector.normalize().scale(dataManager.get(VELOCITY));
            this.setMotion(mot);
            if (target.getDistanceSq(this) > MAX_DISTANCE_SQ) {
                System.out.println("Too far!");
                target = null;
            }
        }
        if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.ENCHANT,
                        getPosXRandom(.5),
                        getPosYHeight(.5),
                        getPosZRandom(.5), 0, -0.1, 0);
            }
        }
        Vec3d pos = this.getPositionVec();
        Vec3d mot = this.getMotion();
        this.setPosition(pos.add(mot));
    }
    private double rand_range(double amount){
        return rand.nextDouble() * amount * 2 - amount;
    }
    private Vec3d randomize(Vec3d vec, double amount){
        return new Vec3d(vec.x + rand_range(amount), vec.y + rand_range(amount), vec.z + rand_range(amount));
    }

    private void setPosition(Vec3d pos) {
        this.setPosition(pos.x, pos.y, pos.z);
    }

    public void setVelocityAndDirection(float speed, Vec3d look) {
        dataManager.set(VELOCITY, speed);
        dataManager.set(DIR, look);
    }

    private Vec3d getDirection() {
        return dataManager.get(DIR);
    }

    private float getVelocity() {
        return dataManager.get(VELOCITY);
    }

    private void explode(SuccessType type) {
        if (type == SuccessType.FAIL) {
            if (world.isRemote) {
                // no damage, just a pop and some particles.
                world.playSound(null, getPosition(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                for (int i = 0; i < 10; i++) {
                    world.addParticle(ParticleTypes.SMOKE, getPosXRandom(1.5), getPosYHeight(1.5f), getPosZRandom(1.5), 0, 0.1, 0);
                }
            }
        } else {
            // do some damage
            if (!world.isRemote) {
                List<Entity> a = world.getEntitiesWithinAABB((EntityType<Entity>) null, getBoundingBox().grow(2.0), (ent) -> ent instanceof LivingEntity && !ent.isInWater());
                for (Entity t : a) {
                    DamageSource src = getDamageSource("explosion", "magicCast");
                    t.attackEntityFrom(src, 4.0f);
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    world.addParticle(ParticleTypes.EXPLOSION, getPosXRandom(1.5), getPosYHeight(1.5), getPosZRandom(1.5), rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
                }
            }
            remove();
        }
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     *
     * @param x
     * @param y
     * @param z
     * @param velocity
     * @param inaccuracy
     */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {

    }

    private enum SuccessType {
        FAIL, SUCCESS
    }

}
