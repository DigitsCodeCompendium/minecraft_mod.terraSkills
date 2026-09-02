package com.terraskills.network;

import com.terraskills.TerraSkills;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SkillProgressPayload(String tree, double progress, double pointsPerDay,
                                   double millisecondsPerDay, boolean generating,
                                   double nutritionBeforeBalance, double maximumNutrition,
                                   double grain, double fruit, double vegetables, double protein, double dairy)
        implements CustomPacketPayload {
    public static final Type<SkillProgressPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TerraSkills.MOD_ID, "skill_progress"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillProgressPayload> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeUtf(payload.tree); buffer.writeDouble(payload.progress); buffer.writeDouble(payload.pointsPerDay);
                buffer.writeDouble(payload.millisecondsPerDay); buffer.writeBoolean(payload.generating);
                buffer.writeDouble(payload.nutritionBeforeBalance); buffer.writeDouble(payload.maximumNutrition);
                buffer.writeDouble(payload.grain); buffer.writeDouble(payload.fruit); buffer.writeDouble(payload.vegetables);
                buffer.writeDouble(payload.protein); buffer.writeDouble(payload.dairy);
            },
            buffer -> new SkillProgressPayload(buffer.readUtf(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(),
                    buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
