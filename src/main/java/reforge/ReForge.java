package reforge;
import mindustry.mod.Mod;
@SuppressWarnings("unused")
public class ReForge extends Mod {
	public ReForge() {
		Setting.load();
		ContentModifier.load();
		BarModifier.load();
		HudModifier.load();
	}
}
