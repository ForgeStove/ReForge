package reforge;
import arc.Events;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType.ContentInitEvent;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
public class BarModifier {
	public static void load() {
		Events.on(ContentInitEvent.class,
			event -> Vars.content.blocks()
				.each(block -> block.addBar("health",
					entity -> new Bar(() -> " " + Strings.autoFixed(entity.health(), 1) + " / " + Strings.autoFixed(entity.maxHealth(), 1),
						() -> Pal.health,
						entity::healthf
					)
				))
		);
	}
}
