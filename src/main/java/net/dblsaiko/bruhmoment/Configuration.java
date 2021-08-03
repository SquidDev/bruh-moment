package net.dblsaiko.bruhmoment;

import net.dblsaiko.bruhmoment.util.list.BlockEntry;
import net.dblsaiko.bruhmoment.util.list.CommandList;
import net.dblsaiko.bruhmoment.util.list.EntityEntry;
import net.dblsaiko.bruhmoment.util.list.ItemEntry;
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

        entityAttackBlacklist = new CommandList<>("brm_eattackbl", "Entities the player is forbidden to attack\nUsage: brm_eattackbl_add (-|[!]entity-id) (-|[!]item-id)", EntityEntry::fromArgs, EntityEntry::toArgs);
        entityInteractBlacklist = new CommandList<>("brm_einteractbl","Entities the player is forbidden to interact with\nUsage: brm_einteractbl_add (-|[!]entity-id) (-|[!]item-id)", EntityEntry::fromArgs, EntityEntry::toArgs);
        blockInteractBlacklist = new CommandList<>("brm_binteractbl", "Blocks the player is forbidden to interact with\nUsage: binteractbl_add (-|[!]block-id) (-|[!]item-id)",BlockEntry::fromArgs, BlockEntry::toArgs);
        itemInteractBlacklist = new CommandList<>("brm_iinteractbl", "Items the player is forbidden to use\nUsage: brm_iinteractbl_add (-|[!]item-id)",ItemEntry::fromArgs, ItemEntry::toArgs);

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
