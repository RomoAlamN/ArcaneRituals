package com.romoalamn.amfbeta.magic.api;

import com.romoalamn.amfbeta.magic.api.network.Channel;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.spell.SpellInstance;
import com.romoalamn.amfbeta.magic.api.wand.Attunement;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpellUtilities {
    public static void addSpell(ItemStack stack, AbstractSpell spell) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.put("SpellContained", spell.serializeNBT());
        stack.setTag(tag);
    }

    public static void addAttunement(ItemStack stack, Attunement att) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.put("WandAttunement", att.serializeNBT());
        stack.setTag(tag);
    }

    public static void informPlayersOfSpellSuccess(IWorld worldIn, SpellInstance spell, Vec3d start, Vec3d result, ItemStack wandIn) {
        Channel.sendSuccessfulSpellToEveryone(worldIn, spell, start, result, wandIn);
    }

    @Nullable
    public static Attunement getAttunement(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains("WandAttunement")) {
            Attunement att = new Attunement("anything");
            att.deserializeNBT(tag);
            return att;
        }
        return null;
    }

    @Nullable
    public static AbstractSpell getSpell(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        System.out.println(tag);
        if (tag.contains("SpellContained")) {
            String id = tag.getCompound("SpellContained").getString("id");
            return MagicRegistries.INSTANCE.SPELLS.getValue(new ResourceLocation(id));
        }
        return AbstractSpell.EMPTY;
    }
}
