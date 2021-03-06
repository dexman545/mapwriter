package dex.mapwriter3.gui;

import dex.mapwriter3.config.MWConfig;
import dex.mapwriter3.config.ConfigurationHandler;
import dex.mapwriter3.util.MwReference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.BooleanEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ButtonEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig(Screen guiScreen) {
        super(guiScreen, getConfigElements(),
                // new
                // ConfigElement(ConfigurationHandler.configuration.getCategory(MwReference.catOptions)).getChildElements(),
                MwReference.MOD_ID,
                "Options",
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
    }

    /**
     * Compiles a list of config elements
     */
    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        // Add categories to config GUI
        list.add(categoryElement(MwReference.catOptions, "Global Options", "mw.configgui.ctgy.general"));
        list.add(ConfigurationHandler.mwConfig.fullScreenMap().categoryElement("Fullscreen map options", "mw.configgui.ctgy.fullScreenMap"));
        list.add(ConfigurationHandler.mwConfig.largeMap().categoryElement("Large map options", "mw.configgui.ctgy.largeMap"));
        list.add(ConfigurationHandler.mwConfig.smallMap().categoryElement("Small map options", "mw.configgui.ctgy.smallMap"));
        return list;
    }

    /**
     * Creates a button linking to another screen where all options of the
     * category are available
     */
    private static IConfigElement categoryElement(String category, String name, String tooltip_key) {
        return new DummyCategoryElement(name, tooltip_key, new ConfigElement(ConfigurationHandler.configuration.getCategory(category)).getChildElements());
    }

    public static class ModBooleanEntry extends ButtonEntry {
        protected final boolean beforeValue;
        protected boolean currentValue;

        public ModBooleanEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
            this.beforeValue = Boolean.valueOf(configElement.get().toString());
            this.currentValue = this.beforeValue;
            this.btnValue.enabled = this.enabled();
            this.updateValueButtonText();
        }

        @Override
        public void updateValueButtonText() {
            this.btnValue.displayString = I18n.translate(String.valueOf(this.currentValue));
            this.btnValue.packedFGColour = this.currentValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
        }

        @Override
        public void valueButtonPressed(int slotIndex) {
            if (this.enabled()) {
                this.currentValue = !this.currentValue;
            }
        }

        @Override
        public boolean isDefault() {
            return this.currentValue == Boolean.valueOf(this.configElement.getDefault().toString());
        }

        @Override
        public void setToDefault() {
            if (this.enabled()) {
                this.currentValue = Boolean.valueOf(this.configElement.getDefault().toString());
                this.updateValueButtonText();
            }
        }

        @Override
        public boolean isChanged() {
            return this.currentValue != this.beforeValue;
        }

        @Override
        public void undoChanges() {
            if (this.enabled()) {
                this.currentValue = this.beforeValue;
                this.updateValueButtonText();
            }
        }

        @Override
        public boolean saveConfigElement() {
            if (this.enabled() && this.isChanged()) {
                this.configElement.set(this.currentValue);
                return this.configElement.requiresMcRestart();
            }
            return false;
        }

        @Override
        public Boolean getCurrentValue() {
            return this.currentValue;
        }

        @Override
        public Boolean[] getCurrentValues() {
            return new Boolean[]
                    {
                            this.getCurrentValue()
                    };
        }

        @Override
        public boolean enabled() {
            for (IConfigEntry entry : this.owningEntryList.listEntries) {
                if (entry.getName().equals("circular") && (entry instanceof BooleanEntry)) {
                    return Boolean.valueOf(entry.getCurrentValue().toString());
                }
            }

            return true;
        }
    }
}
