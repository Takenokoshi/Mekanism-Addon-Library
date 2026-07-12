package com.takenokoshi.mekaddonlib.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.neoforged.fml.loading.LoadingModList;

public class MekALMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".emextras.mekmm.")) {
            return LoadingModList.get().getModFileById("emextras") != null
                    && LoadingModList.get().getModFileById("mekmm") != null;
        }
        if (mixinClassName.contains(".mekanism_extras.mekmm.")) {
            return LoadingModList.get().getModFileById("mekanism_extras") != null
                    && LoadingModList.get().getModFileById("mekmm") != null;
        }
        if (mixinClassName.contains(".emextras.")) {
            return LoadingModList.get().getModFileById("emextras") != null;
        }
        if (mixinClassName.contains(".mekanism_extras.")) {
            return LoadingModList.get().getModFileById("mekanism_extras") != null;
        }
        if (mixinClassName.contains(".mekanismelements.")) {
            return LoadingModList.get().getModFileById("mekanismelements") != null;
        }
        if (mixinClassName.contains(".mekmm.")) {
            return LoadingModList.get().getModFileById("mekmm") != null;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}
