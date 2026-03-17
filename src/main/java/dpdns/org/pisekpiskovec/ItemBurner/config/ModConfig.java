package dpdns.org.pisekpiskovec.ItemBurner.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Builder builder = new Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        public final IntValue maxBurnStack;
        public final IntValue chronofluxToChronoresin;
        public final IntValue chronoresinProduced;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("item_burner");

            maxBurnStack = builder.comment("Default max stack").defineInRange("maxBurnStack", 64, 1, 99);
            chronofluxToChronoresin = builder.comment("How much Chronoflux required to make Chronoresin").defineInRange("chronofluxToChronoresin", 10, 1, 100000);
            chronoresinProduced = builder.comment("How many Chronoresin will be produced from a Chronoflux").defineInRange("chronoresinProduced", 1, 1, 100000);

            builder.pop();
        }
    }
}
