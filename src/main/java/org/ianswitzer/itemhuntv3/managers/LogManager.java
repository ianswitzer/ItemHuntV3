package org.ianswitzer.itemhuntv3.managers;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogManager {
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private boolean writeable;

    public LogManager() {
        open();
    }

    public void writeLog(String message) {
        if (!writeable) return;

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd [HH:mm:ss] ").format(new java.util.Date());
            bufferedWriter.append(timeStamp).append(message).append("\n");
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.INFO, exception.toString());
        }
    }

    public void open() {
        try {
            File file = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemHuntV3")).getDataFolder(), "ItemHuntLogs.txt");
            Bukkit.getLogger().log(Level.INFO, "Logging to: " + file.getPath());

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            writeable = true;
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.INFO, exception.toString());
            writeable = false;
        }
    }

    public void close() {
        writeable = false;

        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (fileWriter != null) fileWriter.close();
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.INFO, exception.toString());
        }
    }
}
