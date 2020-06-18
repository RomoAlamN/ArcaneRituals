package com.romoalamn.amfbeta.magic.api.network;

import com.romoalamn.amfbeta.MagicMod;
import com.romoalamn.amfbeta.magic.api.spell.SpellInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Channel {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MagicMod.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(()-> PROTOCOL_VERSION)
            .simpleChannel();
    public static void registerChannel(){
        int pktId = 0;
        HANDLER.registerMessage(pktId ++, SpellSuccessPacket.class, SpellSuccessPacket::encode, SpellSuccessPacket::decode, SpellSuccessPacket.Handler::handle);
    }
    // must be called server side.
    public static void sendSuccessfulSpellToEveryone(IWorld world, SpellInstance spell, Vec3d start, Vec3d result, ItemStack wandIn) {
        if(!world.isRemote()){
            for(PlayerEntity ent : world.getPlayers()){
                sendSuccessfulSpellToPlayer((ServerPlayerEntity) ent, spell, start, result, wandIn);
            }
        }
    }
    public static void sendSuccessfulSpellToPlayer(ServerPlayerEntity player, SpellInstance spell, Vec3d start, Vec3d result, ItemStack wandIn){
        HANDLER.sendTo(new SpellSuccessPacket(spell, start, result, wandIn), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
