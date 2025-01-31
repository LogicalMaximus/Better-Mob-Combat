package me.Thelnfamous1.bettermobcombat.platform;

import me.Thelnfamous1.bettermobcombat.BetterMobCombat;
import me.Thelnfamous1.bettermobcombat.network.*;
import me.Thelnfamous1.bettermobcombat.platform.services.IPlatformHelper;
import net.bettercombat.logic.AnimatedHand;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isCastingSpell(LivingEntity mob) {
        return false;
    }

    @Override
    public void playMobAttackAnimation(LivingEntity mob, AnimatedHand animatedHand, String animationName, float length, float upswing) {
        BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> mob),
                new S2CAttackAnimation(mob.getId(), animatedHand, animationName, length, upswing));
    }

    @Override
    public void stopMobAttackAnimation(LivingEntity mob, int downWind) {
        BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> mob),
                S2CAttackAnimation.stop(mob.getId(), downWind));
    }

    @Override
    public void syncServerConfig(ServerPlayer player) {
        BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new S2CConfigSync(BetterMobCombat.getServerConfigSerialized()));
    }

    @Override
    public void playMobAttackSound(ServerLevel world, int mobId, double x, double y, double z, String soundId, float volume, float pitch, long seed, float distance, ResourceKey<Level> dimension) {
        BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(x, y, z, distance, dimension).get()),
                new S2CAttackSound(mobId, x, y, z, soundId, volume, pitch, seed));
    }

    @Override
    public void syncMobComboCount(LivingEntity mob, int comboCount) {
        BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> mob),
                new S2CComboCountSync(mob.getId(), comboCount));
    }

    @Override
    public void syncServerConfig() {
        if(ServerLifecycleHooks.getCurrentServer() != null){
            BMCForgeNetwork.SYNC_CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new S2CConfigSync(BetterMobCombat.getServerConfigSerialized()));
        }
    }

}