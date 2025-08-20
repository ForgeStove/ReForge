package reforge;
import arc.Events;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.game.EventType.*;
public class HudModifier {
	public static boolean worldFog;
	public static void load() {
		var rules = Vars.state.rules;
		Events.on(WorldLoadEvent.class, event -> worldFog = rules.fog);
		Events.run(
			Trigger.update, () -> {
				rules.allowEditRules = Setting.ALLOW_EDIT_RULES.get();
				rules.allowEditWorldProcessors = Setting.ALLOW_EDIT_WORLD_PROCESSORS.get();
				if (Vars.player.unit() != null) Vars.player.unit().type.bounded = Setting.BOUNDED.get();
				if (worldFog) {
					rules.fog = Setting.FOG.get();
					rules.staticFog = Setting.STATIC_FOG.get();
				}
				Vars.enableDarkness = Setting.DARKNESS.get();
				ControlPathfinder.showDebug = Setting.CONTROL_PATH_FINDER.get();
				rules.showSpawns = Setting.SHOW_SPAWNS.get();
				rules.possessionAllowed = Setting.POSSESSION_ALLOWED.get();
				rules.schematicsAllowed = Setting.SCHEMATICS_ALLOWED.get();
				rules.limitMapArea = Setting.LIMIT_MAP_AREA.get();
			}
		);
	}
}
