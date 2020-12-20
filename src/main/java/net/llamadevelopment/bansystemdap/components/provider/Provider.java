package net.llamadevelopment.bansystemdap.components.provider;

import lombok.SneakyThrows;
import net.llamadevelopment.bansystemdap.BanSystemDAP;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.function.Consumer;

public class Provider {

    public void connect(final BanSystemDAP instance) {

    }

    public void disconnect(final BanSystemDAP instance) {

    }

    public void updatePlayerIp(final String player, final InetSocketAddress socketAddress) {

    }

    public void getDuplicateAccounts(final String player, final Consumer<Set<String>> accounts) {

    }

    public void hasEntry(final String player, final Consumer<Boolean> has) {

    }

    public String getDevice(int device) {
        switch (device) {
            case 1:
                return "Android";
            case 2:
                return "IOS";
            case 3:
                return "macOS";
            case 4:
                return "FireOS";
            case 5:
                return "GearVR";
            case 6:
                return "HoloLens";
            case 7:
                return "Windows 10";
            case 8:
                return "Dedicated";
            case 9:
                return "Orbis";
            case 10:
                return "NX";
            case 11:
                return "PS4";
            case 13:
                return "Xbox";
            default:
                return "N/A";
        }
    }

    public String getInput(int input) {
        switch (input) {
            case 1:
                return "Mouse";
            case 2:
                return "Touch";
            case 3:
                return "Controller";
            default:
                return "N/A";
        }
    }

    @SneakyThrows
    public String hash(String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes(StandardCharsets.UTF_8));

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public String getProvider() {
        return null;
    }

}
