package reforge;
import arc.*;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Icon;

import java.util.function.BiFunction;
public enum Setting {
	ALLOW_EDIT_RULES("allowEditRules", true, Core.settings::getBool),
	ALLOW_EDIT_WORLD_PROCESSORS("allowEditWorldProcessors", true, Core.settings::getBool),
	BOUNDED("bounded", false, Core.settings::getBool),
	FOG("fog", true, Core.settings::getBool),
	STATIC_FOG("staticFog", true, Core.settings::getBool),
	DARKNESS("darkness", true, Core.settings::getBool),
	CONTROL_PATH_FINDER("controlPathFinder", false, Core.settings::getBool),
	SHOW_SPAWNS("showSpawns", true, Core.settings::getBool),
	POSSESSION_ALLOWED("possessionAllowed", true, Core.settings::getBool),
	SCHEMATICS_ALLOWED("schematicsAllowed", true, Core.settings::getBool),
	LIMIT_MAP_AREA("limitMapArea", false, Core.settings::getBool);
	private final String key;
	private final Object defaultValue;
	private final BiFunction<String, Object, Object> getter;
	@SuppressWarnings("unchecked")
	<T> Setting(String key, T defaultValue, BiFunction<String, T, T> getter) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.getter = (BiFunction<String, Object, Object>) getter;
	}
	public static void load() {
		Events.on(
			ClientLoadEvent.class, event -> Vars.ui.settings.addCategory(
				Core.bundle.get("reforge"), Icon.layers, table -> {
					for (var setting : values()) {
						var bundleKey = Core.bundle.get(setting.key);
						if (setting.defaultValue instanceof Boolean bool) table.checkPref(bundleKey, bool);
					}
				}
			)
		);
	}
	/**
	 * 获取设置的值
	 *
	 * @param <T> 值的类型
	 * @return 设置值
	 */
	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) getter.apply(Core.bundle.get(key), defaultValue);
	}
}
