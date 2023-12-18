package com.github.anopensaucedev.craftpayne.client;

import com.github.anopensaucedev.craftpayne.Craft_Payne;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Craft_PayneClient implements ClientModInitializer {

    float cooldownTime = (3f * 20);

    int ticksDiving = 0;

    float timeSinceLeap;
    boolean canLeap;

    @Override
    public void onInitializeClient() {

        KeyBinding Leap = KeyBindingHelper.registerKeyBinding(new KeyBinding("dive", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z,""));
        KeyBinding BulletTime = KeyBindingHelper.registerKeyBinding(new KeyBinding("bullet_time", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C,""));

         ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if(client.player != null) {
                if (ticksDiving >= 1 && ticksDiving < 35) {
                    client.player.setPose(EntityPose.SWIMMING);
                    ticksDiving++;
                } else {
                    ticksDiving = 0;
                }

                timeSinceLeap++;

                if (timeSinceLeap > cooldownTime) {
                    PacketByteBuf buf = PacketByteBufs.create().writeBoolean(true);
                    buf.writeInt(1);
                    ClientPlayNetworking.send(Craft_Payne.divePacket, buf);
                    canLeap = true;
                }

                while (BulletTime.wasPressed()){
                    if(canLeap){ // bullet Time is essentially leaping without being trapped in the diving pose
                        timeSinceLeap = 0;
                        canLeap = false;
                        PacketByteBuf buf = PacketByteBufs.create().writeBoolean(false);
                        buf.writeInt(0);
                        ClientPlayNetworking.send(Craft_Payne.divePacket, buf);
                    }
                }

                while (Leap.wasPressed()) {

                    if (canLeap) {
                        timeSinceLeap = 0;
                        canLeap = false;
                        PacketByteBuf buf = PacketByteBufs.create().writeBoolean(false);
                        buf.writeInt(0);
                        ClientPlayNetworking.send(Craft_Payne.divePacket, buf);
                        Vector3f velocity = new Vector3f(0, 0.3f, 1).rotate(client.gameRenderer.getCamera().getRotation());
                        Vec3d v3dvelocity = new Vec3d(velocity.x, velocity.y, velocity.z);
                        client.player.addVelocity(v3dvelocity);
                        client.player.setPose(EntityPose.SWIMMING);
                        ticksDiving = 1;
                    }

                }
            }
         }));

    }
}
