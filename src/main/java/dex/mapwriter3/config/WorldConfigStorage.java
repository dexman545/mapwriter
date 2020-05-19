package dex.mapwriter3.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

@org.aeonbits.owner.Config.HotReload(type = org.aeonbits.owner.Config.HotReloadType.ASYNC) //set value = X for interval of X seconds. Default: 5
@org.aeonbits.owner.Config.Sources({"file:${worldDir}"})
public interface WorldConfigStorage extends Config, Reloadable, Accessible, Mutable {
}
