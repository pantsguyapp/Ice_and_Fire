package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdatePodium {

    public long blockPos;
    public ItemStack heldStack;

    public MessageUpdatePodium(long blockPos, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.heldStack = heldStack;

    }

    public MessageUpdatePodium() {
    }

    public static MessageUpdatePodium read(PacketBuffer buf) {
        return new MessageUpdatePodium(buf.readLong(), PacketBufferUtils.readItemStack(buf));
    }

    public static void write(MessageUpdatePodium message, PacketBuffer buf) {
        buf.writeLong(message.blockPos);
        PacketBufferUtils.writeItemStack(buf, message.heldStack);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdatePodium message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context) context.get()).setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world != null) {
                    BlockPos pos = BlockPos.fromLong(message.blockPos);
                    if (player.world.getTileEntity(pos) != null) {
                        if (player.world.getTileEntity(pos) instanceof TileEntityPodium) {
                            TileEntityPodium podium = (TileEntityPodium) player.world.getTileEntity(pos);
                            podium.setInventorySlotContents(0, message.heldStack);
                        }
                    }
                }
            }
        }
    }

}