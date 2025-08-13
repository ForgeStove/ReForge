package io.github.forgestove.reforge;
import arc.*;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.game.EventType.*;
public class HudModifier {
	public static boolean worldFog;
	public static void load() {
		Events.on(ClientLoadEvent.class, event -> {
				var graphics = Vars.ui.settings.graphics;
				graphics.checkPref("AllowEditRules", true);
				graphics.checkPref("AllowEditWorldProcessors", true);
				graphics.checkPref("Bounded", false);
				graphics.checkPref("Fog", true);
				graphics.checkPref("StaticFog", true);
				graphics.checkPref("Darkness", true);
				graphics.checkPref("ControlPathFinder", false);
				graphics.checkPref("ShowSpawns", true);
				graphics.checkPref("PossessionAllowed", true);
				graphics.checkPref("SchematicsAllowed", true);
				graphics.checkPref("LimitMapArea", false);
			}
		);
		var rules = Vars.state.rules;
		Events.on(WorldLoadEvent.class, event -> worldFog = rules.fog);
		Events.run(Trigger.update, () -> {
				var settings = Core.settings;
				rules.allowEditRules = settings.getBool("AllowEditRules");
				rules.allowEditWorldProcessors = settings.getBool("AllowEditWorldProcessors");
				if (Vars.player.unit() != null) Vars.player.unit().type.bounded = settings.getBool("Bounded");
				if (worldFog) {
					rules.fog = settings.getBool("Fog");
					rules.staticFog = settings.getBool("StaticFog");
				}
				Vars.enableDarkness = settings.getBool("Darkness");
				ControlPathfinder.showDebug = settings.getBool("ControlPathFinder");
				rules.showSpawns = settings.getBool("ShowSpawns");
				rules.possessionAllowed = settings.getBool("PossessionAllowed");
				rules.schematicsAllowed = settings.getBool("SchematicsAllowed");
				rules.limitMapArea = settings.getBool("LimitMapArea");
			}
		);
	}
}
