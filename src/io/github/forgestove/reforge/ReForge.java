package io.github.forgestove.reforge;
import mindustry.mod.Mod;
@SuppressWarnings("unused")
public class ReForge extends Mod {
	public ReForge() {
		ContentModifier.load();
		BarModifier.load();
		HudModifier.load();
	}
}
