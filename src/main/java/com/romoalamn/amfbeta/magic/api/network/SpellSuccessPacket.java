package com.romoalamn.amfbeta.magic.api.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.romoalamn.amfbeta.magic.api.MagicRegistries;
import com.romoalamn.amfbeta.magic.api.spell.AbstractSpell;
import com.romoalamn.amfbeta.magic.api.spell.SpellInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SpellSuccessPacket {
    SpellInstance spellSucceeded;
    Vec3d castSource;
    Vec3d hitLocation;
    ItemStack wandUsed;

    public SpellSuccessPacket(SpellInstance spell, Vec3d start, Vec3d hit, ItemStack wandIn) {
        spellSucceeded = spell;
        castSource = start;
        hitLocation = hit;
        wandUsed = wandIn;
    }

    public static void encode(SpellSuccessPacket pkt, PacketBuffer buf) {
        //write the spell's registry name
        buf.writeResourceLocation(Objects.requireNonNull(pkt.spellSucceeded.getSpell().getRegistryName()));
        buf.writeCompoundTag(pkt.spellSucceeded.getData());
        //write the data of the wand used.
        buf.writeItemStack(pkt.wandUsed);
        //write the caster's location (where they cast the spell from)
        buf.writeDouble(pkt.castSource.x);
        buf.writeDouble(pkt.castSource.y);
        buf.writeDouble(pkt.castSource.z);
        //write the hitLocation of the ray trace
        buf.writeDouble(pkt.hitLocation.x);
        buf.writeDouble(pkt.hitLocation.y);
        buf.writeDouble(pkt.hitLocation.z);

    }

    public static SpellSuccessPacket decode(PacketBuffer buf) {
        try {
            AbstractSpell spell = MagicRegistries.INSTANCE.SPELLS.getValue(new ResourceLocation(buf.readString()));
            CompoundNBT extraData = JsonToNBT.getTagFromJson(buf.readString());

            ItemStack wand = buf.readItemStack();

            Vec3d startVec = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
            Vec3d hit = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );

            return new SpellSuccessPacket(new SpellInstance(spell, extraData), startVec, hit, wand);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Handler {
        public static void handle(final SpellSuccessPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            // todo: use the IWand to tell the client to register the spell.
            // ie. Add a renderSpellTick with a tickCount parameter.
            //never mind this was done using an entity instead
            // might be useful for something in the future.
            ctx.get().setPacketHandled(true);
        }
    }
}
