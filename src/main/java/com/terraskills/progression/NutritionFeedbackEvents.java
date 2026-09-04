package com.terraskills.progression;

import com.terraskills.TerraSkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = TerraSkills.MOD_ID)
public final class NutritionFeedbackEvents {
    private static final Map<UUID, PendingMeal> FOOD_BEING_EATEN = new ConcurrentHashMap<>();

    private NutritionFeedbackEvents() {}

    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getItem().getFoodProperties(player) != null) {
            FOOD_BEING_EATEN.put(player.getUUID(), new PendingMeal(
                    SkillPointGeneration.calculateNutrition(player), event.getItem().copyWithCount(1)));
        }
    }

    @SubscribeEvent
    public static void onUseStopped(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof ServerPlayer player) FOOD_BEING_EATEN.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onUseFinished(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PendingMeal meal = FOOD_BEING_EATEN.remove(player.getUUID());
        if (meal == null || !PlayerProgress.nutritionMessagesEnabled(player)) return;

        SkillPointGeneration.NutritionBreakdown after = SkillPointGeneration.calculateNutrition(player);
        player.sendSystemMessage(Component.literal("Ate ").withStyle(ChatFormatting.GRAY)
                .append(meal.stack().getHoverName().copy().withStyle(style -> style
                        .withColor(ChatFormatting.GOLD)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackInfo(meal.stack())))))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.2f", after.beforeBalance()))
                        .withStyle(changeColor(meal.before().beforeBalance(), after.beforeBalance())))
                .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("×%.2f", after.balanceMultiplier()))
                        .withStyle(changeColor(meal.before().balanceMultiplier(), after.balanceMultiplier())))
                .append(Component.literal(") → ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.format("%.2f", after.afterBalance()))
                        .withStyle(changeColor(meal.before().afterBalance(), after.afterBalance()))));
    }

    private static ChatFormatting changeColor(double before, double after) {
        if (after > before + 0.000_000_5) return ChatFormatting.GREEN;
        if (after < before - 0.000_000_5) return ChatFormatting.RED;
        return ChatFormatting.WHITE;
    }

    private record PendingMeal(SkillPointGeneration.NutritionBreakdown before, ItemStack stack) {}

}
