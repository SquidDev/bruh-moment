package net.dblsaiko.bruhmoment.net;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;

import net.dblsaiko.bruhmoment.util.list.CommandList;

public class CommandListPacket<T> {
    public final Type<T> type;

    public final List<T> data;

    private CommandListPacket(Type<T> type, List<T> data) {
        this.type = type;
        this.data = data;
    }

    public void handle(MinecraftClient client) {
        client.execute(() -> {
            this.type.list.clear();
            this.type.list.addAll(this.data);
        });
    }

    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeVarInt(this.data.size());

        for (T el : this.data) {
            String[] strings = this.type.list.serializer.apply(el);
            buffer.writeVarInt(strings.length);

            for (String string : strings) {
                buffer.writeString(string);
            }
        }
    }

    public void sendTo(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        this.toBuffer(buf);
        ServerPlayNetworking.send(player, this.type.packetId, buf);
    }

    public static class Type<T> {
        private final CommandList<T> list;
        public final Identifier packetId;

        public Type(CommandList<T> list) {
            this.list = list;
            this.packetId = new Identifier("bruh-moment", String.format("packet_list_%s", list.name));
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
                ClientPlayNetworking.registerGlobalReceiver(this.packetId, (client, handler, buf, responseSender) -> this.fromBuffer(buf).handle(client));
            }
        }
    }
}
