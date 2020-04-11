package net.dblsaiko.bruhmoment;

import net.dblsaiko.qcommon.cfg.core.api.ConfigApi;
import net.dblsaiko.qcommon.cfg.core.api.cvar.BoolConVar;
import net.dblsaiko.qcommon.cfg.core.api.cvar.CvarOptions;

public class Configuration {

    public static BoolConVar filterSurvival = null;
    public static BoolConVar filterCreative = null;
    public static BoolConVar filterAdventure = null;

    public static CommandList<EntityEntry> entityAttackBlacklist = null;
    public static CommandList<EntityEntry> entityInteractBlacklist = null;
    public static CommandList<BlockEntry> blockInteractBlacklist = null;
    public static CommandList<ItemEntry> itemInteractBlacklist = null;

    private Configuration() {
    }

    public static void init() {
        ConfigApi.Mutable api = ConfigApi.getInstanceMut();

        entityAttackBlacklist = new CommandList<>("brm_eattackbl", EntityEntry::fromArgs, EntityEntry::toArgs);
        entityInteractBlacklist = new CommandList<>("brm_einteractbl", EntityEntry::fromArgs, EntityEntry::toArgs);
        blockInteractBlacklist = new CommandList<>("brm_binteractbl", BlockEntry::fromArgs, BlockEntry::toArgs);
        itemInteractBlacklist = new CommandList<>("brm_iinteractbl", ItemEntry::fromArgs, ItemEntry::toArgs);

        CvarOptions opts = CvarOptions.create().save("bruhmoment").sync();
        filterSurvival = api.addConVar("brm_filter_survival", BoolConVar.owned(false), opts);
        filterCreative = api.addConVar("brm_filter_creative", BoolConVar.owned(false), opts);
        filterAdventure = api.addConVar("brm_filter_adventure", BoolConVar.owned(false), opts);
        entityAttackBlacklist.register(api);
        entityInteractBlacklist.register(api);
        blockInteractBlacklist.register(api);
        itemInteractBlacklist.register(api);
    }

}
