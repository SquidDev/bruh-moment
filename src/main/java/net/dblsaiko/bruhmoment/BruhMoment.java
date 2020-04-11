package net.dblsaiko.bruhmoment;

import net.fabricmc.api.ModInitializer;

import java.lang.reflect.Field;

import net.dblsaiko.qcommon.cfg.core.ConfigApiImpl;
import net.dblsaiko.qcommon.cfg.core.api.ConfigApi;
import net.dblsaiko.qcommon.cfg.core.cvar.CvarSyncManager;

public class BruhMoment implements ModInitializer {

    // TODO: actually make this accessible from cfg's side
    public static CvarSyncManager csm;

    @Override
    public void onInitialize() {
        try {
            Field $cvarSyncManager = ConfigApiImpl.class.getDeclaredField("cvarSyncManager");
            $cvarSyncManager.setAccessible(true);
            csm = (CvarSyncManager) $cvarSyncManager.get(ConfigApi.getInstanceMut());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Configuration.init();
    }

}
