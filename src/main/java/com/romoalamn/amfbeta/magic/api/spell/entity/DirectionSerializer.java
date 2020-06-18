package com.romoalamn.amfbeta.magic.api.spell.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DirectionSerializer implements IDataSerializer<Vec3d>{
    public static final DirectionSerializer INSTANCE = new DirectionSerializer();

    @Override
    public void write(PacketBuffer buf, Vec3d value) {
        buf.writeDouble(value.x);
        buf.writeDouble(value.y);
        buf.writeDouble(value.z);
    }

    @Override
    public Vec3d read(PacketBuffer buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3d(x, y, z);
    }

    @Override
    public Vec3d copyValue(Vec3d value) {
        return new Vec3d(value.x, value.y, value.z);
    }
}
