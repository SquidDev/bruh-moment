package net.dblsaiko.bruhmoment;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.server.PlayerStream;
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
    public final Function<String[], T> parser;
    public final Function<T, String[]> serializer;
    public final CommandListPacket.Type<T> packetType;

    public CommandList(String name, Function<String[], T> parser, Function<T, String[]> serializer) {
        this.name = name;
        this.parser = parser;
        this.serializer = serializer;
        this.packetType = new CommandListPacket.Type<>(this);
    }

    public void register(ConfigApi.Mutable api) {
        api.addCommand(String.format("%s_add", name), this::add);
        api.addCommand(String.format("%s_clear", name), this::clear);
        api.addCommand(String.format("%s_del", name), this::remove);
        api.addCommand(String.format("%s_list", name), this::list);
        api.registerPersistenceListener(this);
        api.registerSyncListener(this);
        packetType.register();
    }

    public void clear() {
        entries.clear();
    }

    public void add(T element) {
        entries.add(element);
    }

    public void remove(T element) {
        entries.remove(element);
    }

    public void addAll(Collection<? extends T> data) {
        entries.addAll(data);
    }

    @Override
    public Iterator<T> iterator() {
        return entries.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        entries.forEach(action);
    }

    public boolean contains(Object element) {
        return entries.contains(element);
    }

    @Override
    public Spliterator<T> spliterator() {
        return entries.spliterator();
    }

    public Stream<T> stream() {
        return entries.stream();
    }

    public Stream<T> parallelStream() {
        return entries.parallelStream();
    }

    @Override
    public void write(PersistenceContext persistenceContext) {
        ConfigApi api = ConfigApi.getInstance();
        persistenceContext.write("bruhmoment", linePrinter -> {
            linePrinter.print();
            linePrinter.printf(api.escape(String.format("%s_clear", name)));
            entries.stream()
                .map(serializer)
                .map(arr -> Arrays.stream(arr).map(api::escape).collect(Collectors.joining(" ")))
                .forEach(s -> linePrinter.printf("%s %s", api.escape(String.format("%s_add", name)), s));
        });
    }

    @Override
    public void updateAll(Set<PlayerEntity> players) {
        players.forEach(player -> packetType.of(entries).sendTo(player));
    }

    private void add(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }
        T element = parser.apply(strings);
        if (element == null) return;
        add(element);
        onUpdate();
    }

    private void clear(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }
        clear();
        onUpdate();
    }

    private void remove(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        if (BruhMoment.csm.isActive()) {
            linePrinter.print("cvar is locked by server");
            return;
        }
        T element = parser.apply(strings);
        if (element == null) return;
        remove(element);
        onUpdate();
    }

    private void list(String[] strings, ExecSource execSource, LinePrinter linePrinter, ControlFlow controlFlow) {
        ConfigApi api = ConfigApi.getInstance();
        entries.stream()
            .map(serializer)
            .map(arr -> Arrays.stream(arr).map(api::escape).collect(Collectors.joining(" ")))
            .forEach(linePrinter::print);
    }

    private void onUpdate() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            PlayerStream.all((MinecraftServer) FabricLoader.getInstance().getGameInstance()).forEach(player -> {
                packetType.of(entries).sendTo(player);
            });
        }
    }

}
