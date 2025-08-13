package reforge;
import arc.*;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.game.EventType.*;
public class HudModifier {
	public static boolean worldFog;
	public static void load() {
		Events.on(ClientLoadEvent.class, event -> {
				var graphics = Vars.ui.settings.graphics;
				graphics.checkPref(Core.bundle.get("settings.allowEditRules"), true);
				graphics.checkPref(Core.bundle.get("settings.allowEditWorldProcessors"), true);
				graphics.checkPref(Core.bundle.get("settings.bounded"), false);
				graphics.checkPref(Core.bundle.get("settings.fog"), true);
				graphics.checkPref(Core.bundle.get("settings.staticFog"), true);
				graphics.checkPref(Core.bundle.get("settings.darkness"), true);
				graphics.checkPref(Core.bundle.get("settings.controlPathFinder"), false);
				graphics.checkPref(Core.bundle.get("settings.showSpawns"), true);
				graphics.checkPref(Core.bundle.get("settings.possessionAllowed"), true);
				graphics.checkPref(Core.bundle.get("settings.schematicsAllowed"), true);
				graphics.checkPref(Core.bundle.get("settings.limitMapArea"), false);
			}
		);
		var rules = Vars.state.rules;
		Events.on(WorldLoadEvent.class, event -> worldFog = rules.fog);
		Events.run(Trigger.update, () -> {
				var settings = Core.settings;
				rules.allowEditRules = settings.getBool(Core.bundle.get("settings.allowEditRules"));
				rules.allowEditWorldProcessors = settings.getBool(Core.bundle.get("settings.allowEditWorldProcessors"));
				if (Vars.player.unit() != null) Vars.player.unit().type.bounded = settings.getBool(Core.bundle.get("settings.bounded"));
				if (worldFog) {
					rules.fog = settings.getBool(Core.bundle.get("settings.fog"));
					rules.staticFog = settings.getBool(Core.bundle.get("settings.staticFog"));
				}
				Vars.enableDarkness = settings.getBool(Core.bundle.get("settings.darkness"));
				ControlPathfinder.showDebug = settings.getBool(Core.bundle.get("settings.controlPathFinder"));
				rules.showSpawns = settings.getBool(Core.bundle.get("settings.showSpawns"));
				rules.possessionAllowed = settings.getBool(Core.bundle.get("settings.possessionAllowed"));
				rules.schematicsAllowed = settings.getBool(Core.bundle.get("settings.schematicsAllowed"));
				rules.limitMapArea = settings.getBool(Core.bundle.get("settings.limitMapArea"));
			}
		);
	}
}
