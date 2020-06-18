package com.romoalamn.amfbeta.magic.impl.spells;

import com.romoalamn.amfbeta.magic.api.spell.Spell;
import com.romoalamn.amfbeta.magic.impl.spells.entity.SpellMagicMissileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SpellMagicMissile extends Spell {
    @Override
    public void cast(IWorld worldIn, LivingEntity caster, Vec3d direction, ItemStack wandIn, Hand handIn) {
        super.cast(worldIn, caster, direction, wandIn, handIn);
        if(!worldIn.isRemote()){
            SpellMagicMissileEntity proj = new SpellMagicMissileEntity(caster,(World) worldIn);
            proj.setVelocityAndDirection(0.2f, caster.getLookVec());
            worldIn.addEntity(proj);
        }
    }
}
