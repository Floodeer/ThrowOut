package com.floodeer.throwout;

import lombok.Getter;

import java.util.Arrays;

public enum ServerVersion {

    VERSION_18(47, "v1_8"),
    PRE_13(340, "v1_9,v1_10,v1_11,v1_12"),
    VERSION_13(393, "v1_13"),
    VERSION_14(477, "v1_14"),
    VERSION_15(573, "v1_15"),
    VERSION_16(735, "v1_16"),
    VERSION_17(755, "v1_17"),
    VERSION_118(757, "v1_18"),
    VERSION_19(759, "v1_19"),
    VERSION_20(763, "v1_20");

    @Getter
    private final int protocol;
    private final String identifier;

    ServerVersion(int protocol, String netMinecraftServer) {
        this.protocol = protocol;
        this.identifier = netMinecraftServer;
    }

    public static ServerVersion getFromString(String nmsver) {
        if (nmsver.startsWith("v1_9") || nmsver.startsWith("v1_10") || nmsver.startsWith("v1_11") || nmsver.startsWith("v1_12")) {
            return PRE_13;
        } else {
            String[] parts = nmsver.split("_");
            if (parts.length >= 2) {
                String versionNumber = parts[1];
                return Arrays.stream(ServerVersion.values())
                        .filter(cur -> cur.getNMSIdentifier().endsWith(versionNumber))
                        .findFirst()
                        .orElse(VERSION_19);
            }
        }
        return VERSION_19;
    }

    public static boolean isLegacy() {
        return ThrowOut.get().getVersion().getProtocol() <= 340;
    }

    public static boolean inBetween() {
        return ThrowOut.get().getVersion().getProtocol() > 47 &&ThrowOut.get().getVersion().getProtocol() < 393;
    }

    public String getNMSIdentifier() {
        return (getProtocol() == 47 || getProtocol() >= 393) ? identifier : "v1_12";
    }

    public boolean hasHexSupport() {
        return this.protocol >= 735;
    }

    @Override
    public String toString() {
        return identifier;
    }
}