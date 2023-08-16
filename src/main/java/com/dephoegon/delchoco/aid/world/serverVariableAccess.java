package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delbase.Delbase;
import com.dephoegon.delchoco.DelChoco;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.NotNull;

import java.io.*;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.setChocoboMinPack;
import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.setStamina;
import static com.dephoegon.delchoco.aid.world.defaultVariables.*;

public class serverVariableAccess {
    public static final Identifier DELCHOCO_CHOCOBO_VARIABLES = new Identifier(DelChoco.Mod_ID, "chocobo-variables");
    public static final Identifier DELCHOCO_WORLD_VARIABLES = new Identifier(DelChoco.Mod_ID, "world-variables");
    private long lastEventTime = 0;
    private long lastUnloadEventTime = 0;
    private final long COOL_DOWN = 5000; // 5 seconds
    public void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            lastEventTime = System.currentTimeMillis();
            File choco_load_file = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_CHOCOBO_VARIABLES.toString().replace(':', '_'));
            File world_load_file = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_WORLD_VARIABLES.toString().replace(':', '_'));
            if (choco_load_file.exists()) {
                try (DataInputStream input = new DataInputStream(new FileInputStream(choco_load_file))) {
                    globalVariablesHolder storedVariables = new globalVariablesHolder(NbtIo.readCompressed(input), false);
                    setDelchocoConfigVariables(storedVariables, false);
                    DelChoco.LOGGER.info("(DelChoco Mod) - Chocobo Variables Set from Config file");
                    // Use globalVariables here
                } catch (IOException e) {
                    // Handle error
                    Delbase.LOGGER.warn("Loaded Default Chocobo Variables\nFailed to load saved Chocobo variables data: " + e.getMessage());
                    setDefaultValues(false);
                }
            } else {
                setDefaultValues(false);
                Delbase.LOGGER.info("Loaded Default Chocobo Variables, File did not exist");
            }
            if (world_load_file.exists()) {
                try (DataInputStream input = new DataInputStream(new FileInputStream(world_load_file))) {
                    globalVariablesHolder storedVariables = new globalVariablesHolder(NbtIo.readCompressed(input), true);
                    setDelchocoConfigVariables(storedVariables, true);
                    DelChoco.LOGGER.info("(DelChoco Mod) - World Variables Set from Config file");
                    // Use globalVariables here
                } catch (IOException e) {
                    // Handle error
                    Delbase.LOGGER.warn("Loaded Default World Variables\nFailed to load saved World variables data: " + e.getMessage());
                    setDefaultValues(true);
                }
            } else {
                setDefaultValues(false);
                Delbase.LOGGER.info("Loaded World Variables, File did not exist");
            }
            ServerWorldEvents.LOAD.register((unusedServer, world) -> {
                if (!world.isClient()) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastEventTime > COOL_DOWN) {
                        lastEventTime = currentTime;
                        saveFile(server.getOverworld());
                    }
                }
            });
        });

        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (!world.isClient()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUnloadEventTime > COOL_DOWN) {
                    lastUnloadEventTime = currentTime;
                    saveFile(server.getOverworld());
                }
            }
        });
    }
    private void saveFile(@NotNull ServerWorld world) {
        File choco_file = new File(world.getServer().getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_CHOCOBO_VARIABLES.toString().replace(':', '_'));
        File world_file = new File(world.getServer().getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_WORLD_VARIABLES.toString().replace(':', '_'));
        try (DataOutputStream choco_output = new DataOutputStream(new FileOutputStream(choco_file))) {
            globalVariablesHolder currentVariables = new globalVariablesHolder();
            getCurrentGlobalVariables(currentVariables, false);
            // Set globalVariables here
            if (globalVariablesChanged(currentVariables, false)) {
                NbtIo.writeCompressed(currentVariables.chocoboToNbt(new NbtCompound()), choco_output);
                DelChoco.LOGGER.info("(DelChoco Mod) - Global Variables Saved to Config file");
            }
        } catch (IOException e) {
            // Handle error
            DelChoco.LOGGER.warn("Failure to save global Variables data : "+ e.getMessage());
        }
        try (DataOutputStream world_output = new DataOutputStream(new FileOutputStream(world_file))) {
            globalVariablesHolder currentVariables = new globalVariablesHolder();
            getCurrentGlobalVariables(currentVariables, true);
            // Set globalVariables here
            if (globalVariablesChanged(currentVariables, true)) {
                NbtIo.writeCompressed(currentVariables.worldToNbt(new NbtCompound()), world_output);
                DelChoco.LOGGER.info("(DelChoco Mod) - Global Variables Saved to Config file");
            }
        } catch (IOException e) {
            // Handle error
            DelChoco.LOGGER.warn("Failure to save global Variables data : "+ e.getMessage());
        }
    }
    // TODO: Triple Check
    //  Triple Check Variables are identical in type & name
    //  have the same intent are the same across all 3 Holders [defaults, global, static]
    //  Defaults have correct Getters
    //  GlobalVariableHolder & StaticGlobalVariables have correct setters & getters.
    private void setDelchocoConfigVariables(globalVariablesHolder variables, boolean isWorld) {
        if (isWorld) {
            setChocoboMinPack(variables.getChocoboMinPack());
        } else {
            setStamina(variables.getStamina());
        }
    }
    private void getCurrentGlobalVariables(globalVariablesHolder variablesHolder, boolean isWorld) {
        if (isWorld) {
            variablesHolder.setChocoboMinPack(StaticGlobalVariables.getChocoboMinPack());
        } else  {
            variablesHolder.setStamina(StaticGlobalVariables.getStamina());
        }
    }
    private boolean globalVariablesChanged(globalVariablesHolder currentVariables, boolean isWorld) {
        if (isWorld) {
            globalWorldVariableNullCheck();

            return false; // temp false to sort same value return
        } else {
            globalChocoboVariableNullCheck();

            return true; // temp true to sort same value return
        }
    }
    private void globalChocoboVariableNullCheck() {
        if (StaticGlobalVariables.getStamina() == null) { StaticGlobalVariables.setStamina(dSTAMINA.getDefault().intValue()); }
        if (StaticGlobalVariables.getSpeed() == null) { StaticGlobalVariables.setSpeed(dSPEED.getDefault().intValue()); }
        if (StaticGlobalVariables.getHealth() == null) { StaticGlobalVariables.setHealth(dHEALTH.getDefault().intValue()); }
        if (StaticGlobalVariables.getArmor() == null) { StaticGlobalVariables.setArmor(dARMOR.getDefault().intValue()); }
        if (StaticGlobalVariables.getArmorTough() == null) { StaticGlobalVariables.setArmorTough(dARMOR_TOUGH.getDefault().intValue()); }
        if (StaticGlobalVariables.getAttack() == null) { StaticGlobalVariables.setAttack(dATTACK.getDefault().intValue()); }


    }
    private void globalWorldVariableNullCheck() {
        if (StaticGlobalVariables.getCanSpawn() == null) { StaticGlobalVariables.setCanSpawn(dCanSpawn); }
        if (StaticGlobalVariables.getEndSpawn() == null) { StaticGlobalVariables.setEndSpawn(dEndSpawn); }
    }
}