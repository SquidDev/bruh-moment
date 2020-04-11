package net.dblsaiko.bruhmoment.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;

import net.dblsaiko.bruhmoment.CommandList;

public class CommandListPacket<T> {

    public final Type<T> type;

    public final List<T> data;

    private CommandListPacket(Type<T> type, List<T> data) {
        this.type = type;
        this.data = data;
    }

    public void handle(PacketContext ctx) {
        ctx.getTaskQueue().execute(() -> {
            type.list.clear();
            type.list.addAll(data);
        });
    }

    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeVarInt(data.size());
        for (T el : data) {
            String[] strings = type.list.serializer.apply(el);
            buffer.writeVarInt(strings.length);
            for (String string : strings) {
                buffer.writeString(string);
            }
        }
    }

    public void sendTo(PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        toBuffer(buf);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, type.packetId, buf);
    }

    public static class Type<T> {

        final CommandList<T> list;
        public final Identifier packetId;

        public Type(CommandList<T> list) {
            this.list = list;
            packetId = new Identifier("bruh-moment", String.format("packet_list_%s", list.name));
        }

        public CommandListPacket<T> fromBuffer(PacketByteBuf buffer) {
            int elLen = buffer.readVarInt();
            List<T> list = new ArrayList<>(elLen);
            for (int i = 0; i < elLen; i++) {
                int partsLen = buffer.readVarInt();
                String[] parts = new String[partsLen];
                for (int i1 = 0; i1 < partsLen; i1++) {
                    parts[i1] = buffer.readString();
                }
                T el = this.list.parser.apply(parts);
                if (el != null) list.add(el);
            }
            return this.of(list);
        }

        public CommandListPacket<T> of(List<T> data) {
            return new CommandListPacket<>(this, data);
        }

        public void register() {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientSidePacketRegistry.INSTANCE.register(packetId, (packetContext, packetByteBuf) -> fromBuffer(packetByteBuf).handle(packetContext));
            }
        }

    }

}
