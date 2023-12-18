package com.github.anopensaucedev.craftpayne;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.ServerTickManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Craft_Payne implements ModInitializer {

    public static final String modID = "craftpayne";
    public static final Identifier divePacket = new Identifier(modID,"divepacket");



    @Override
    public void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(divePacket,(server, player, handler, buf, responseSender) -> {
            int scale = buf.readInt(); // floats were broken for me
            boolean reset = buf.readBoolean();
            ServerTickManager manager = server.getTickManager();
            if(!reset){
                if(scale == 0 ){ // 0 = leap
                manager.setTickRate(20.0f * 0.5f);
                }else {
                    manager.setTickRate(20.0f);
                }
            }else {
                manager.setTickRate(20);
            }
        });
    }
}
