package com.romoalamn.amfbeta.magic.impl.spells;

import com.romoalamn.amfbeta.magic.api.SpellUtilities;
import com.romoalamn.amfbeta.magic.api.spell.Spell;
import com.romoalamn.amfbeta.magic.api.spell.SpellInstance;
import com.romoalamn.amfbeta.magic.impl.spells.entity.SpellFireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SpellFireball extends Spell {
    @Override
    public void cast(IWorld worldIn, LivingEntity caster, Vec3d direction, ItemStack wandIn, Hand handIn) {
        // cast it on the server. If it succeeds, server sends a packet instructing the client to display the animations.
        if (!worldIn.isRemote()) {
            Vec3d start = caster.getEyePosition(1.0f);
            Vec3d end = caster.getLookVec().mul(16.0, 16.0, 16.0).add(start);
            RayTraceContext ctx = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, caster);
            BlockRayTraceResult result = worldIn.rayTraceBlocks(ctx);
            if (result.getType() == RayTraceResult.Type.BLOCK) {
                CompoundNBT extraData = caster.serializeNBT();
                Vec3d hit = result.getHitVec();
                SpellUtilities.informPlayersOfSpellSuccess(worldIn, new SpellInstance(this, extraData), start, hit, wandIn);
                // apply damage.
                SpellFireballEntity entity = new SpellFireballEntity((World)worldIn, caster);
                entity.giveExtraData(extraData);
                entity.setPosition(hit.x, hit.y, hit.z);
                worldIn.addEntity(entity);
            }
        }
    }

    @Override
    public void renderCast(IWorld world, SpellInstance instance, Vec3d origin, Vec3d direction, ItemStack wandIn) {
        // fireball is rendered using the entity.
        //will call super when we implement spell equations.
    }
}
