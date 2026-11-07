/*
 * Copyright 2026 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.xaerozoomout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.map.common.config.option.WorldMapProfiledConfigOptions;

/**
 * Forces Xaero's World Map built-in "Unlimited Zoom Out" option on, which lowers the minimum zoom
 * from 0.0625x to 0.001953125x.
 *
 * <p>This targets the config manager rather than {@code GuiMap} because {@code GuiMap} is large
 * enough that NeoForge's mandatory frame recomputation exceeds ASM's operation budget.</p>
 */
@Mixin(
        value = ClientConfigManager.class,
        remap = false
)
public abstract class MixinClientConfigManager {

    @Inject(
            method = "getEffective(Lxaero/lib/common/config/option/ConfigOption;)Ljava/lang/Object;",
            at = @At("RETURN"),
            cancellable = true
    )
    private <T> void forceUnlimitedZoomOut(ConfigOption<T> option, CallbackInfoReturnable<T> cir) {
        if (option == WorldMapProfiledConfigOptions.UNLIMITED_ZOOM_OUT) {
            //noinspection unchecked
            cir.setReturnValue((T) Boolean.TRUE);
        }
    }
}
