package dev.ix.borderless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.lwjgl.glfw.GLFW;

public class BorderlessMacOS implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("mac")) return;

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            long handle = client.getWindow().handle();
            GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        });
    }
}
