package dev.ix.borderless.mixin;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow @Final private long window;

    /**
     * After the window is fully initialized, remove decorations on macOS.
     * This runs after VulkanMod's own Window mixin, so it overrides
     * VulkanMod setting GLFW_DECORATED = true.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void removeMacOSDecorations(CallbackInfo ci) {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            GLFW.glfwSetWindowAttrib(this.window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        }
    }
}
