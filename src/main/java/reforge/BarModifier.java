package reforge;
import arc.Events;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType.ContentInitEvent;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
public class BarModifier {
	public static void load() {
		Events.on(
			ContentInitEvent.class, event -> Vars.content.blocks().each(block -> {
				block.addBar(
					"health", building -> new Bar(
						() -> "\uE813 " + Strings.autoFixed(building.health(), 1) + " / " + Strings.autoFixed(building.maxHealth(), 1),
						() -> Pal.health,
						building::healthf
					)
				);
				if (block.armor != 0) block.addBar(
					"armor",
					building -> new Bar(() -> "\uE86B " + Strings.autoFixed(block.armor, 1), () -> Pal.ammo, () -> block.armor)
				);
			})
		);
	}
}
