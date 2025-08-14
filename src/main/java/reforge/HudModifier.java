package reforge;
import arc.*;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.game.EventType.*;
public class HudModifier {
	public static boolean worldFog;
	public static void load() {
		Events.on(
			ClientLoadEvent.class, event -> {
				var game = Vars.ui.settings.game;
				game.checkPref(Core.bundle.get("allowEditRules"), true);
				game.checkPref(Core.bundle.get("allowEditWorldProcessors"), true);
				game.checkPref(Core.bundle.get("bounded"), false);
				game.checkPref(Core.bundle.get("fog"), true);
				game.checkPref(Core.bundle.get("staticFog"), true);
				game.checkPref(Core.bundle.get("darkness"), true);
				game.checkPref(Core.bundle.get("controlPathFinder"), false);
				game.checkPref(Core.bundle.get("showSpawns"), true);
				game.checkPref(Core.bundle.get("possessionAllowed"), true);
				game.checkPref(Core.bundle.get("schematicsAllowed"), true);
				game.checkPref(Core.bundle.get("limitMapArea"), false);
			}
		);
		var rules = Vars.state.rules;
		Events.on(WorldLoadEvent.class, event -> worldFog = rules.fog);
		Events.run(
			Trigger.update, () -> {
				var settings = Core.settings;
				rules.allowEditRules = settings.getBool(Core.bundle.get("allowEditRules"));
				rules.allowEditWorldProcessors = settings.getBool(Core.bundle.get("allowEditWorldProcessors"));
				if (Vars.player.unit() != null) Vars.player.unit().type.bounded = settings.getBool(Core.bundle.get("bounded"));
				if (worldFog) {
					rules.fog = settings.getBool(Core.bundle.get("fog"));
					rules.staticFog = settings.getBool(Core.bundle.get("staticFog"));
				}
				Vars.enableDarkness = settings.getBool(Core.bundle.get("darkness"));
				ControlPathfinder.showDebug = settings.getBool(Core.bundle.get("controlPathFinder"));
				rules.showSpawns = settings.getBool(Core.bundle.get("showSpawns"));
				rules.possessionAllowed = settings.getBool(Core.bundle.get("possessionAllowed"));
				rules.schematicsAllowed = settings.getBool(Core.bundle.get("schematicsAllowed"));
				rules.limitMapArea = settings.getBool(Core.bundle.get("limitMapArea"));
			}
		);
	}
}
