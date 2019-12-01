package at.patrickmayr.addon;

import com.sun.management.OperatingSystemMXBean;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.ModuleCategoryRegistry;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

import java.lang.management.ManagementFactory;

public class CpuModule extends SimpleModule {

    private final OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    @Override
    public String getDisplayName() {
        return "CPU";
    }

    @Override
    public String getDisplayValue() {
        return ((int) (this.os.getSystemCpuLoad() * 100)) + "%";
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(0);
    }

    @Override
    public ControlElement.IconData getIconData() {
        return new ControlElement.IconData(Material.BOOK);
    }

    @Override
    public void loadSettings() {

    }

    @Override
    public String getSettingName() {
        return "CPU Auslastung";
    }

    @Override
    public String getDescription() {
        return "Zeigt die aktuelle CPU Auslastung an!";
    }

    @Override
    public int getSortingId() {
        return 0;
    }

    @Override
    public ModuleCategory getCategory() {
        return ModuleCategoryRegistry.CATEGORY_INFO;
    }
}
