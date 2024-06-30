package me.Thelnfamous1.bettermobcombat.network;

import me.Thelnfamous1.bettermobcombat.BetterMobCombatCommon;
import me.Thelnfamous1.bettermobcombat.config.BMCServerConfig;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BetterMobCombatNetworkClient {
    static void handleAttackAnimation(int mobId, String animationName, float length, AnimatedHand animatedHand, float upswing) {
        Entity entity = Minecraft.getInstance().level.getEntity(mobId);
        if (entity instanceof LivingEntity) {
            if (animationName.equals(Packets.AttackAnimation.StopSymbol)) {
                ((PlayerAttackAnimatable)entity).stopAttackAnimation(length);
            } else {
                ((PlayerAttackAnimatable)entity).playAttackAnimation(animationName, animatedHand, length, upswing);
            }
        }
    }

    public static void handleConfigSync(String json) {
        BMCServerConfig config = BMCServerConfig.deserialize(json);
        BetterMobCombatCommon.updateServerConfig(config, true);
    }

    public static void handlePlaySound(int mobId, double x, double y, double z, String soundId, float volume, float pitch, long seed) {
        try {
            if (BetterCombatClient.config.weaponSwingSoundVolume == 0) {
                return;
            }

            Entity entity = Minecraft.getInstance().level.getEntity(mobId);
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(soundId));
            int configVolume = BetterCombatClient.config.weaponSwingSoundVolume;
            volume *= ((float)Math.min(Math.max(configVolume, 0), 100) / 100.0F);
            Minecraft.getInstance().level.playLocalSound(x, y, z, soundEvent, entity.getSoundSource(), volume, pitch, true);
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }
}
