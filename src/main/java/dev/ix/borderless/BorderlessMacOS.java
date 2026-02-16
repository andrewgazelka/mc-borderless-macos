package dev.ix.borderless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.system.macosx.ObjCRuntime;

import static org.lwjgl.system.JNI.*;

/**
 * Hides the macOS title bar while keeping NSWindowStyleMaskTitled so that
 * tiling window managers (AeroSpace, etc.) still recognise the window as
 * AXStandardWindow.
 */
public class BorderlessMacOS implements ClientModInitializer {

    private static final int NS_FULL_SIZE_CONTENT_VIEW = 1 << 15;

    @Override
    public void onInitializeClient() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("mac")) return;

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            long glfwHandle = client.getWindow().handle();
            removeTitleBar(glfwHandle);
        });
    }

    private static void removeTitleBar(long glfwHandle) {
        long nsWindow = GLFWNativeCocoa.glfwGetCocoaWindow(glfwHandle);
        long msg = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");

        // Current style mask — keep NSWindowStyleMaskTitled (bit 0)
        long mask = invokePPP(nsWindow, sel("styleMask"), msg);
        // Add NSWindowStyleMaskFullSizeContentView so content extends under titlebar
        mask |= NS_FULL_SIZE_CONTENT_VIEW;
        invokePPV(nsWindow, sel("setStyleMask:"), (int) mask, msg);

        // Transparent titlebar background
        invokePPV(nsWindow, sel("setTitlebarAppearsTransparent:"), true, msg);

        // Hide title text (NSWindowTitleHidden = 1)
        invokePPV(nsWindow, sel("setTitleVisibility:"), 1, msg);

        // Hide traffic-light buttons: close(0), miniaturize(1), zoom(2)
        long selBtn = sel("standardWindowButton:");
        long selHide = sel("setHidden:");
        for (int i = 0; i <= 2; i++) {
            long button = invokePPP(nsWindow, selBtn, i, msg);
            if (button != 0) {
                invokePPV(button, selHide, true, msg);
            }
        }
    }

    private static long sel(String name) {
        return ObjCRuntime.sel_registerName(name);
    }
}
