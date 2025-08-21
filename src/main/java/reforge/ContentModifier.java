package reforge;
import arc.Events;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType.ContentInitEvent;
import mindustry.world.blocks.distribution.Router;
public class ContentModifier {
	public static void load() {
		Vars.maxSchematicSize = 1024;
		Events.on(
			ContentInitEvent.class, event -> {
				var content = Vars.content;
				content.blocks().each(block -> {
					block.replaceable = true;
					block.inEditor = true;
					if (block instanceof Router router) router.buildType = () -> router.new RouterBuild() {
						public boolean canControl() {return true;}
					};
				});
				content.items().each(item -> item.hidden = false);
				content.liquids().each(liquid -> liquid.hidden = false);
				content.units().each(unit -> {
					if (unit == UnitTypes.block) return;
					unit.hidden = false;
					unit.rotateSpeed = 1024;
					unit.weapons.each(weapon -> weapon.rotateSpeed = 1024);
				});
			}
		);
	}
}
