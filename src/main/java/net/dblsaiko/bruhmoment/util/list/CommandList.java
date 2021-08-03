package net.dblsaiko.bruhmoment.util.list;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dblsaiko.bruhmoment.BruhMoment;
import net.dblsaiko.bruhmoment.net.CommandListPacket;
import net.dblsaiko.qcommon.cfg.core.api.ConfigApi;
import net.dblsaiko.qcommon.cfg.core.api.ExecSource;
import net.dblsaiko.qcommon.cfg.core.api.LinePrinter;
import net.dblsaiko.qcommon.cfg.core.api.cmd.ControlFlow;
import net.dblsaiko.qcommon.cfg.core.api.persistence.PersistenceContext;
import net.dblsaiko.qcommon.cfg.core.api.persistence.PersistenceListener;
import net.dblsaiko.qcommon.cfg.core.api.sync.SyncListener;

public class CommandList<T> implements PersistenceListener, SyncListener, Iterable<T> {
    private final List<T> entries = new ArrayList<>();

    public final String name;
    private final String desc;
    public final Function<String[], T> parser;
    public final Function<T, String[]> serializer;
    public final CommandListPacket.Type<T> packetType;

    public CommandList(String name, String desc, Function<String[], T> parser, Function<T, String[]> serializer) {
        this.name = name;
        this.desc = desc;
        this.parser = parser;
        this.serializer = serializer;
        this.packetType = new CommandListPacket.Type<>(this);
    }

    public void register(ConfigApi.Mutable api) {
        api.addCommand(String.format("%s_add", this.name), this::add);
        api.addCommand(String.format("%s_clear", this.name), this::clear);
        api.addCommand(String.format("%s_del", this.name), this::remove);
        api.addCommand(String.format("%s_list", this.name), this::list);
        api.registerPersistenceListener(this);
        api.registerSyncListener(this);
        this.packetType.register();
    }

    public void clear() {
        this.entries.clear();
    }

    public void add(T element) {
        this.entries.add(element);
    }

    public void remove(T element) {
        this.entries.remove(element);
    }

    public void addAll(Collection<? extends T> data) {
        this.entries.addAll(data);
    }

    @Override
    public Iterator<T> iterator() {
        return this.entries.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.entries.forEach(action);
    }

    public boolean contains(Object element) {
        return this.entries.contains(element);
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.entries.spliterator();
    }

    public Stream<T> stream() {
        return this.entries.stream();
    }

    public Stream<T> parallelStream() {
        return this.entries.parallelStream();
    }

    @Override
    public void write(PersistenceContext persistenceContext) {
        ConfigApi api = ConfigApi.getInstance();
        persistenceContext.write("bruhmoment", linePrinter -> {
            linePrinter.print();
            Arrays.stream(this.desc.split("\n")).map(s -> String.format("// %s", s)).forEach(linePrinter::print);
            linePrinter.printf(api.escape(String.format("%s_clear", this.name)));
            this.entries.stream()
                .map(this.serializer)
                .map(arr -> Arrays.stream(arr).map(api::escape).collect(Collectors.joining(" ")))
                .forEach(s -> linePrinter.printf("%s %s", api.escape(String.format("%s_add", this.name)), s));
        });
    }

    @Override
    public void updateAll(Set<PlayerEntity> players) {
        players.forEach(player -> this.packetType.of(this.entries).sendTo((ServerPlayerEntity) player));
    }

    private void add(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }

        T element = this.parser.apply(strings);
        if (element == null) return;
        this.add(element);
        this.onUpdate();
    }

    private void clear(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }

        this.clear();
        this.onUpdate();
    }

    private void remove(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }

        T element = this.parser.apply(strings);
        if (element == null) return;
        this.remove(element);
        this.onUpdate();
    }

    private void list(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        ConfigApi api = ConfigApi.getInstance();
        this.entries.stream()
            .map(this.serializer)
            .map(arr -> Arrays.stream(arr).map(api::escape).collect(Collectors.joining(" ")))
            .forEach(linePrinter::print);
    }

    private void onUpdate() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PlayerLookup.all((MinecraftServer) FabricLoader.getInstance().getGameInstance()).forEach(player -> {
                this.packetType.of(this.entries).sendTo(player);
            });
        }
    }
}
