package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delbase.Delbase;
import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.composables;
import com.dephoegon.delchoco.client.ChocoboSprintingEventHandler;
import com.dephoegon.delchoco.common.commands.chocoboTeams;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;
import org.spongepowered.include.com.google.gson.JsonPrimitive;

import java.io.*;

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultFloats.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

public class serverVariableAccess {
    public static final Identifier DELCHOCO_CHOCOBO_VARIABLES = new Identifier(DelChoco.DELCHOCO_ID, "chocobo-variables");
    public static final Identifier DELCHOCO_WORLD_VARIABLES = new Identifier(DelChoco.DELCHOCO_ID, "world-variables");
    private static long lastEventTime = 0;
    private static long lastUnloadEventTime = 0;
    private static final long COOL_DOWN = 5000; // 5 seconds
    static class DataHolder {
        JsonObject nullCheck;
        globalVariablesHolder storedVariables;
    }
    public static void init() {
        DelChoco.LOGGER.info("(DelChoco Mod) - Server Variable Access Initialized");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            lastEventTime = System.currentTimeMillis();
            File choco_load_file = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_CHOCOBO_VARIABLES.toString().replace(':', '_'));
            File world_load_file = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_WORLD_VARIABLES.toString().replace(':', '_'));
            if (choco_load_file.exists()) {
                try (Reader reader = new FileReader(choco_load_file)) {
                    Gson gson = new Gson();
                    DataHolder dataHolder = gson.fromJson(reader, DataHolder.class);
                    JsonObject nullCheck = dataHolder.nullCheck;
                    globalVariablesHolder storedVariableSet = dataHolder.storedVariables;
                    setDelchocoConfigVariables(saveFileChocoboVariableNullCheck(nullCheck, storedVariableSet, server.getOverworld()), false);
                    DelChoco.LOGGER.info("(DelChoco Mod) - Chocobo Variables Set from Config file");
                    // Use globalVariables here
                } catch (IOException e) {
                    // Handle error
                    Delbase.LOGGER.warn("Loaded Default Chocobo Variables\nFailed to load saved Chocobo variables data: " + e.getMessage());
                    setDefaultValues(false);
                    saveFile(server.getOverworld(), false);
                }
            } else {
                setDefaultValues(false);
                Delbase.LOGGER.info("Loaded Default Chocobo Variables, File did not exist");
                saveFile(server.getOverworld(), false);
            }
            if (world_load_file.exists()) {
                try (Reader reader = new FileReader(world_load_file)) {
                    Gson gson = new Gson();
                    DataHolder dataHolder = gson.fromJson(reader, DataHolder.class);
                    JsonObject nullCheck = dataHolder.nullCheck;
                    globalVariablesHolder storedVariableSet = dataHolder.storedVariables;
                    setDelchocoConfigVariables(saveFileWorldVariableNullCheck(nullCheck, storedVariableSet, server.getOverworld()), true);
                    DelChoco.LOGGER.info("(DelChoco Mod) - World Variables Set from Config file");
                    // Use globalVariables here
                } catch (IOException e) {
                    // Handle error
                    Delbase.LOGGER.warn("Loaded Default World Variables\nFailed to load saved World variables data: " + e.getMessage());
                    setDefaultValues(true);
                    saveFile(server.getOverworld(), true);
                }
            } else {
                setDefaultValues(true);
                Delbase.LOGGER.info("Loaded World Variables, File did not exist");
                saveFile(server.getOverworld(), true);
            }
            ServerWorldEvents.LOAD.register((unusedServer, world) -> {
                if (!world.isClient()) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastEventTime > COOL_DOWN) {
                        lastEventTime = currentTime;
                        saveFile(server.getOverworld(), true);
                        saveFile(server.getOverworld(), false);
                    }
                }
            });
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                ChocoboSprintingEventHandler.onKeyPress();
            });
            composables.addToList();
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                chocoboTeams.commands(dispatcher);
            });
        });

        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (!world.isClient()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUnloadEventTime > COOL_DOWN) {
                    lastUnloadEventTime = currentTime;
                    saveFile(server.getOverworld(), true);
                    saveFile(server.getOverworld(), false);
                }
            }
        });
    }
    private static void saveFile(@NotNull ServerWorld world, boolean isWorld) {
        File choco_file = new File(world.getServer().getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_CHOCOBO_VARIABLES.toString().replace(':', '_'));
        File world_file = new File(world.getServer().getSavePath(WorldSavePath.ROOT).toFile(), DELCHOCO_WORLD_VARIABLES.toString().replace(':', '_'));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!isWorld){
            try (Writer writer = new FileWriter(choco_file)) {
                gson.toJson(saveFileFormat(false, getCurrentGlobalVariables(false)), writer);
                DelChoco.LOGGER.info("(DelChoco Mod) - Global Variables Saved to Config file");
            } catch (IOException e) {
                // Handle error
                DelChoco.LOGGER.warn("Failure to save global Variables data : " + e.getMessage());
            }
        }
        if (isWorld) {
            try (Writer writer = new FileWriter(world_file)) {
                gson.toJson(saveFileFormat(true, getCurrentGlobalVariables(true)), writer);
                DelChoco.LOGGER.info("(DelChoco Mod) - Global Variables Saved to Config file");

            } catch (IOException e) {
                // Handle error
                DelChoco.LOGGER.warn("Failure to save global Variables data : " + e.getMessage());
            }
        }
    }
    private static void setDelchocoConfigVariables(globalVariablesHolder variables, boolean isWorld) {
        if (isWorld) {
            setChocoboMinPack(getValueInBounds(dCHOCOBO_PACK_MIN, variables.getChocoboMinPack()));
            setChocoboMaxPack(getValueInBounds(dCHOCOBO_PACK_MAX, variables.getChocoboMaxPack()));
            setOverWorldSpawnWeight(getValueInBounds(dOVERWORLD_SPAWN_WEIGHT, variables.getOverWorldSpawnWeight()));
            setMushroomSpawnWeight(getValueInBounds(dMUSHROOM_SPAWN_WEIGHT, variables.getMushroomSpawnWeight()));
            setNetherSpawnWeight(getValueInBounds(dNETHER_SPAWN_WEIGHT, variables.getNetherSpawnWeight()));
            setEndSpawnWeight(getValueInBounds(dEND_SPAWN_WEIGHT, variables.getEndSpawnWeight()));
            setGysahlGreenPatchSize(getValueInBounds(dGYSAHL_GREEN_PATCH_SIZE, variables.getGysahlGreenPatchSize()));
            setGysahlGreenSpawnChance(getValueInBounds(dGYSAHL_GREEN_SPAWN_CHANCE, variables.getGysahlGreenSpawnChance()));
            setCanSpawn(variables.isCanSpawn());
            setOverworldSpawn(variables.isOverworldSpawn());
            setNetherSpawn(variables.isNetherSpawn());
            setEndSpawn(variables.isEndSpawn());
        } else {
            setStamina(getValueInBounds(dSTAMINA, variables.getStamina()));
            setSpeed(getValueInBounds(dSPEED, variables.getSpeed()));
            setHealth(getValueInBounds(dHEALTH, variables.getHealth()));
            setArmor(getValueInBounds(dARMOR, variables.getArmor()));
            setArmorTough(getValueInBounds(dARMOR_TOUGH, variables.getArmorTough()));
            setAttack(getValueInBounds(dATTACK, variables.getAttack()));
            setWeaponModifier(getValueInBounds(dWEAPON_MOD, variables.getWeaponModifier()));
            setHealAmount(getValueInBounds(dHEAL_AMOUNT, variables.getHealAmount()));
            setEggHatchTimeTicks(getValueInBounds(dEGG_HATCH, variables.getEggHatchTimeTicks()));
            setMaxHealth(getValueInBounds(dMAX_HEALTH, variables.getMaxHealth()));
            setMaxSpeed(getValueInBounds(dMAX_SPEED, variables.getMaxSpeed()));
            setStaminaRegen(getValueInBounds(dSTAMINA_REGEN, variables.getStaminaRegen()));
            setTame(getValueInBounds(dTAME, variables.getTame()));
            setStaminaCost(getValueInBounds(dSTAMINA_SPRINT, variables.getStaminaCost()));
            setStaminaGlide(getValueInBounds(dSTAMINA_GLIDE, variables.getStaminaGlide()));
            setStaminaJump(getValueInBounds(dSTAMINA_JUMP, variables.getStaminaJump()));
            setPossLoss(getValueInBounds(dPOS_LOSS, variables.getPossLoss()));
            setPossGain(getValueInBounds(dPOS_GAIN, variables.getPossGain()));
            setPossLossStamina(getValueInBounds(dPOS_LOSS, variables.getPossLossStamina()));
            setPossGainStamina(getValueInBounds(dPOS_GAIN, variables.getPossGainStamina()));
            setPossLossSpeed(getValueInBounds(dPOS_LOSS, variables.getPossLossSpeed()));
            setPossGainSpeed(getValueInBounds(dPOS_GAIN, variables.getPossGainSpeed()));
            setPossLossHealth(getValueInBounds(dPOS_LOSS, variables.getPossLossHealth()));
            setPossGainHealth(getValueInBounds(dPOS_GAIN, variables.getPossGainHealth()));
            setMaxStamina(getValueInBounds(dMAX_STAMINA, variables.getMaxStamina()));
            setMaxStrength(getValueInBounds(dMAX_STRENGTH, variables.getMaxStrength()));
            setMaxArmor(getValueInBounds(dMAX_ARMOR, variables.getMaxArmor()));
            setMaxArmorToughness(getValueInBounds(dMAX_ARMOR_TOUGH, variables.getMaxArmorToughness()));
            setArmorAlpha(getValueInBounds(dARMOR_ALPHA, variables.getArmorAlpha()));
            setWeaponAlpha(getValueInBounds(dWEAPON_ALPHA, variables.getWeaponAlpha()));
            setCollarAlpha(getValueInBounds(dCOLLAR_ALPHA, variables.getCollarAlpha()));
            setSaddleAlpha(getValueInBounds(dSADDLE_ALPHA, variables.getSaddleAlpha()));
            setExtraChocoboEffects(variables.isExtraChocoboEffects());
            setExtraChocoboResourcesOnHit(variables.isExtraChocoboResourcesOnHit());
            setExtraChocoboResourcesOnKill(variables.isExtraChocoboResourcesOnKill());
            setShiftHitBypass(variables.isShiftHitBypass());
            setOwnChocoboHittable(variables.isOwnChocoboHittable());
            setTamedChocoboHittable(variables.isTamedChocoboHittable());
            setOwnerOnlyInventory(variables.isOwnerOnlyInventory());
        }
    }
    private static @NotNull globalVariablesHolder getCurrentGlobalVariables(boolean isWorld) {
        globalVariablesHolder variablesHolder = new globalVariablesHolder();
        if (isWorld) {
            variablesHolder.setChocoboMinPack(getValueInBounds(dCHOCOBO_PACK_MIN, StaticGlobalVariables.getChocoboMinPack()));
            variablesHolder.setChocoboMaxPack(getValueInBounds(dCHOCOBO_PACK_MAX, StaticGlobalVariables.getChocoboMaxPack()));
            variablesHolder.setOverWorldSpawnWeight(getValueInBounds(dOVERWORLD_SPAWN_WEIGHT, StaticGlobalVariables.getOverWorldSpawnWeight()));
            variablesHolder.setMushroomSpawnWeight(getValueInBounds(dMUSHROOM_SPAWN_WEIGHT, StaticGlobalVariables.getMushroomSpawnWeight()));
            variablesHolder.setNetherSpawnWeight(getValueInBounds(dNETHER_SPAWN_WEIGHT, StaticGlobalVariables.getNetherSpawnWeight()));
            variablesHolder.setEndSpawnWeight(getValueInBounds(dEND_SPAWN_WEIGHT, StaticGlobalVariables.getEndSpawnWeight()));
            variablesHolder.setGysahlGreenPatchSize(getValueInBounds(dGYSAHL_GREEN_PATCH_SIZE, StaticGlobalVariables.getGysahlGreenPatchSize()));
            variablesHolder.setGysahlGreenSpawnChance(getValueInBounds(dGYSAHL_GREEN_SPAWN_CHANCE, StaticGlobalVariables.getGysahlGreenSpawnChance()));
            variablesHolder.setCanSpawn(StaticGlobalVariables.getCanSpawn());
            variablesHolder.setOverworldSpawn(StaticGlobalVariables.getOverworldSpawn());
            variablesHolder.setNetherSpawn(StaticGlobalVariables.getNetherSpawn());
            variablesHolder.setEndSpawn(StaticGlobalVariables.getEndSpawn());
        } else  {
            variablesHolder.setStamina(getValueInBounds(dSTAMINA, StaticGlobalVariables.getStamina()));
            variablesHolder.setSpeed(getValueInBounds(dSPEED, StaticGlobalVariables.getSpeed()));
            variablesHolder.setHealth(getValueInBounds(dHEALTH, StaticGlobalVariables.getHealth()));
            variablesHolder.setArmor(getValueInBounds(dARMOR, StaticGlobalVariables.getArmor()));
            variablesHolder.setArmorTough(getValueInBounds(dARMOR_TOUGH, StaticGlobalVariables.getArmorTough()));
            variablesHolder.setAttack(getValueInBounds(dATTACK, StaticGlobalVariables.getAttack()));
            variablesHolder.setWeaponModifier(getValueInBounds(dWEAPON_MOD, StaticGlobalVariables.getWeaponModifier()));
            variablesHolder.setHealAmount(getValueInBounds(dHEAL_AMOUNT, StaticGlobalVariables.getHealAmount()));
            variablesHolder.setEggHatchTimeTicks(getValueInBounds(dEGG_HATCH, StaticGlobalVariables.getEggHatchTimeTicks()));
            variablesHolder.setMaxHealth(getValueInBounds(dMAX_HEALTH, StaticGlobalVariables.getMaxHealth()));
            variablesHolder.setMaxSpeed(getValueInBounds(dMAX_SPEED, StaticGlobalVariables.getMaxSpeed()));
            variablesHolder.setStaminaRegen(getValueInBounds(dSTAMINA_REGEN, StaticGlobalVariables.getStaminaRegen()));
            variablesHolder.setTame(getValueInBounds(dTAME, StaticGlobalVariables.getTame()));
            variablesHolder.setStaminaCost(getValueInBounds(dSTAMINA_SPRINT, StaticGlobalVariables.getStaminaCost()));
            variablesHolder.setStaminaGlide(getValueInBounds(dSTAMINA_GLIDE, StaticGlobalVariables.getStaminaGlide()));
            variablesHolder.setStaminaJump(getValueInBounds(dSTAMINA_JUMP, StaticGlobalVariables.getStaminaJump()));
            variablesHolder.setPossLoss(getValueInBounds(dPOS_LOSS, StaticGlobalVariables.getPossLoss()));
            variablesHolder.setPossGain(getValueInBounds(dPOS_GAIN, StaticGlobalVariables.getPossGain()));
            variablesHolder.setPossLossStamina(getValueInBounds(dPOS_LOSS, StaticGlobalVariables.getPossLossStamina()));
            variablesHolder.setPossGainStamina(getValueInBounds(dPOS_GAIN, StaticGlobalVariables.getPossGainStamina()));
            variablesHolder.setPossLossSpeed(getValueInBounds(dPOS_LOSS, StaticGlobalVariables.getPossLossSpeed()));
            variablesHolder.setPossGainSpeed(getValueInBounds(dPOS_GAIN, StaticGlobalVariables.getPossGainSpeed()));
            variablesHolder.setPossLossHealth(getValueInBounds(dPOS_LOSS, StaticGlobalVariables.getPossLossHealth()));
            variablesHolder.setPossGainHealth(getValueInBounds(dPOS_GAIN, StaticGlobalVariables.getPossGainHealth()));
            variablesHolder.setMaxStamina(getValueInBounds(dMAX_STAMINA, StaticGlobalVariables.getMaxStamina()));
            variablesHolder.setMaxStrength(getValueInBounds(dMAX_STRENGTH, StaticGlobalVariables.getMaxStrength()));
            variablesHolder.setMaxArmor(getValueInBounds(dMAX_ARMOR, StaticGlobalVariables.getMaxArmor()));
            variablesHolder.setMaxArmorToughness(getValueInBounds(dMAX_ARMOR_TOUGH, StaticGlobalVariables.getMaxArmorToughness()));
            variablesHolder.setArmorAlpha(getValueInBounds(dARMOR_ALPHA, StaticGlobalVariables.getArmorAlpha()));
            variablesHolder.setWeaponAlpha(getValueInBounds(dWEAPON_ALPHA, StaticGlobalVariables.getWeaponAlpha()));
            variablesHolder.setCollarAlpha(getValueInBounds(dCOLLAR_ALPHA, StaticGlobalVariables.getCollarAlpha()));
            variablesHolder.setSaddleAlpha(getValueInBounds(dSADDLE_ALPHA, StaticGlobalVariables.getSaddleAlpha()));
            variablesHolder.setExtraChocoboEffects(StaticGlobalVariables.getExtraChocoboEffects());
            variablesHolder.setExtraChocoboResourcesOnHit(StaticGlobalVariables.getExtraChocoboResourcesOnHit());
            variablesHolder.setExtraChocoboResourcesOnKill(StaticGlobalVariables.getExtraChocoboResourcesOnKill());
            variablesHolder.setShiftHitBypass(StaticGlobalVariables.getShiftHitBypass());
            variablesHolder.setOwnChocoboHittable(StaticGlobalVariables.getOwnChocoboHittable());
            variablesHolder.setTamedChocoboHittable(StaticGlobalVariables.getTamedChocoboHittable());
            variablesHolder.setOwnerOnlyInventory(StaticGlobalVariables.getOwnerOnlyInventory());
        }
        return variablesHolder;
    }
    private static globalVariablesHolder saveFileWorldVariableNullCheck(@NotNull JsonObject fieldNames, globalVariablesHolder variablesHolder, ServerWorld world) {
        boolean save = false;
        if (!fieldNames.has(ChocoboMinPack_name)) { variablesHolder.setChocoboMinPack(dCHOCOBO_PACK_MIN.getDefault()); save = true; }
        if (!fieldNames.has(ChocoboMaxPack_name)) { variablesHolder.setChocoboMaxPack(dCHOCOBO_PACK_MAX.getDefault()); save = true; }
        if (!fieldNames.has(OverWorldSpawnWeight_name)) { variablesHolder.setOverWorldSpawnWeight(dOVERWORLD_SPAWN_WEIGHT.getDefault()); save = true; }
        if (!fieldNames.has(MushroomSpawnWeight_name)) { variablesHolder.setMushroomSpawnWeight(dMUSHROOM_SPAWN_WEIGHT.getDefault()); save = true; }
        if (!fieldNames.has(NetherSpawnWeight_name)) { variablesHolder.setNetherSpawnWeight(dNETHER_SPAWN_WEIGHT.getDefault()); save = true; }
        if (!fieldNames.has(EndSpawnWeight_name)) { variablesHolder.setEndSpawnWeight(dEND_SPAWN_WEIGHT.getDefault()); save = true; }
        if (!fieldNames.has(GysahlGreenPatchSize_name)) { variablesHolder.setGysahlGreenPatchSize(dGYSAHL_GREEN_PATCH_SIZE.getDefault()); save = true; }
        if (!fieldNames.has(GysahlGreenSpawnChance_name)) { variablesHolder.setGysahlGreenSpawnChance(dGYSAHL_GREEN_SPAWN_CHANCE.getDefault()); save = true; }
        if (!fieldNames.has(CanSpawn_name)) { variablesHolder.setCanSpawn(dCanSpawn); save = true; }
        if (!fieldNames.has(OverworldSpawn_name)) { variablesHolder.setOverworldSpawn(dOverworldSpawn); save = true; }
        if (!fieldNames.has(NetherSpawn_name)) { variablesHolder.setNetherSpawn(dNetherSpawn); save = true; }
        if (!fieldNames.has(EndSpawn_name)) { variablesHolder.setEndSpawn(dEndSpawn); save = true; }
        if (save) saveFile(world, true);
        return variablesHolder;
    }
    private static globalVariablesHolder saveFileChocoboVariableNullCheck(@NotNull JsonObject fieldNames, globalVariablesHolder variablesHolder, ServerWorld world) {
        boolean save = false;
        if (!fieldNames.has(Stamina_name)) { variablesHolder.setStamina(dSTAMINA.getDefault()); save = true; }
        if (!fieldNames.has(Speed_name)) { variablesHolder.setSpeed(dSPEED.getDefault()); save = true; }
        if (!fieldNames.has(Health_name)) { variablesHolder.setHealth(dHEALTH.getDefault()); save = true; }
        if (!fieldNames.has(Armor_name)) { variablesHolder.setArmor(dARMOR.getDefault()); save = true; }
        if (!fieldNames.has(ArmorTough_name)) { variablesHolder.setArmorTough(dARMOR_TOUGH.getDefault()); save = true; }
        if (!fieldNames.has(Attack_name)) { variablesHolder.setAttack(dATTACK.getDefault()); save = true; }
        if (!fieldNames.has(WeaponModifier_name)) { variablesHolder.setWeaponModifier(dWEAPON_MOD.getDefault()); save = true; }
        if (!fieldNames.has(HealAmount_name)) { variablesHolder.setHealAmount(dHEAL_AMOUNT.getDefault()); save = true; }
        if (!fieldNames.has(EggHatchTimeTicks_name)) { variablesHolder.setEggHatchTimeTicks(dEGG_HATCH.getDefault()); save = true; }
        if (!fieldNames.has(MaxHealth_name)) { variablesHolder.setMaxHealth(dMAX_HEALTH.getDefault()); save = true; }
        if (!fieldNames.has(MaxSpeed_name)) { variablesHolder.setMaxSpeed(dMAX_SPEED.getDefault()); save = true; }
        if (!fieldNames.has(StaminaRegen_name)) { variablesHolder.setStaminaRegen(dSTAMINA_REGEN.getDefault()); save = true; }
        if (!fieldNames.has(Tame_name)) { variablesHolder.setTame(dTAME.getDefault()); save = true; }
        if (!fieldNames.has(StaminaCost_name)) { variablesHolder.setStaminaCost(dSTAMINA_SPRINT.getDefault()); save = true; }
        if (!fieldNames.has(StaminaGlide_name)) { variablesHolder.setStaminaGlide(dSTAMINA_GLIDE.getDefault()); save = true; }
        if (!fieldNames.has(StaminaJump_name)) { variablesHolder.setStaminaJump(dSTAMINA_JUMP.getDefault()); save = true; }
        if (!fieldNames.has(PossLoss_name)) { variablesHolder.setPossLoss(dPOS_LOSS.getDefault()); save = true; }
        if (!fieldNames.has(PossGain_name)) { variablesHolder.setPossGain(dPOS_GAIN.getDefault()); save = true; }
        if (!fieldNames.has(PossLossStamina_name)) { variablesHolder.setPossLossStamina(dPOS_LOSS.getDefault()); save = true; }
        if (!fieldNames.has(PossGainStamina_name)) { variablesHolder.setPossGainStamina(dPOS_GAIN.getDefault()); save = true; }
        if (!fieldNames.has(PossLossSpeed_name)) { variablesHolder.setPossLossSpeed(dPOS_LOSS.getDefault()); save = true; }
        if (!fieldNames.has(PossGainSpeed_name)) { variablesHolder.setPossGainSpeed(dPOS_GAIN.getDefault()); save = true; }
        if (!fieldNames.has(PossLossHealth_name)) { variablesHolder.setPossLossHealth(dPOS_LOSS.getDefault()); save = true; }
        if (!fieldNames.has(PossGainHealth_name)) { variablesHolder.setPossGainHealth(dPOS_GAIN.getDefault()); save = true; }
        if (!fieldNames.has(MaxStamina_name)) { variablesHolder.setMaxStamina(dMAX_STAMINA.getDefault()); save = true; }
        if (!fieldNames.has(MaxStrength_name)) { variablesHolder.setMaxStrength(dMAX_STRENGTH.getDefault()); save = true; }
        if (!fieldNames.has(MaxArmor_name)) { variablesHolder.setMaxArmor(dMAX_ARMOR.getDefault()); save = true; }
        if (!fieldNames.has(MaxArmorToughness_name)) { variablesHolder.setMaxArmorToughness(dMAX_ARMOR_TOUGH.getDefault()); save = true; }
        if (!fieldNames.has(ArmorAlpha_name)) { variablesHolder.setArmorAlpha(dARMOR_ALPHA.getDefault()); save = true; }
        if (!fieldNames.has(WeaponAlpha_name)) { variablesHolder.setWeaponAlpha(dWEAPON_ALPHA.getDefault()); save = true; }
        if (!fieldNames.has(CollarAlpha_name)) { variablesHolder.setCollarAlpha(dCOLLAR_ALPHA.getDefault()); save = true; }
        if (!fieldNames.has(SaddleAlpha_name)) { variablesHolder.setSaddleAlpha(dSADDLE_ALPHA.getDefault()); save = true; }
        if (!fieldNames.has(ExtraChocoboEffects_name)) { variablesHolder.setExtraChocoboEffects(dExtraChocoboEffects); save = true; }
        if (!fieldNames.has(ExtraChocoboResourcesOnHit_name)) { variablesHolder.setExtraChocoboResourcesOnHit(dExtraChocoboResourcesOnHit); save = true; }
        if (!fieldNames.has(ExtraChocoboResourcesOnKill_name)) { variablesHolder.setExtraChocoboResourcesOnKill(dExtraChocoboResourcesOnKill); save = true; }
        if (!fieldNames.has(ShiftHitBypass_name)) { variablesHolder.setShiftHitBypass(dShiftHitBypass); save = true; }
        if (!fieldNames.has(OwnChocoboHittable_name)) { variablesHolder.setOwnChocoboHittable(dOwnChocoboHittable); save = true; }
        if (!fieldNames.has(TamedChocoboHittable_name)) { variablesHolder.setTamedChocoboHittable(dTamedChocoboHittable); save = true; }
        if (!fieldNames.has(OwnerOnlyInventory_name)) { variablesHolder.setOwnerOnlyInventory(dOwnerOnlyInventoryAccess); save = true; }
        if (save) saveFile(world, false);
        return variablesHolder;
    }
    private static @NotNull org.spongepowered.include.com.google.gson.JsonObject saveFileFormat(boolean isWorld, globalVariablesHolder holder) {
        org.spongepowered.include.com.google.gson.JsonObject variables = new org.spongepowered.include.com.google.gson.JsonObject();
        if (isWorld) {
            variables.add(ChocoboMinPack_name, new JsonPrimitive(holder.getChocoboMinPack()));
            variables.add(ChocoboMinPack_name+"_comment", new JsonPrimitive("Minimum number of Chocobos in a pack. Minimum value is "+dCHOCOBO_PACK_MIN.getMin()+", Default is "+dCHOCOBO_PACK_MIN.getDefault()+", Max Value is "+dCHOCOBO_PACK_MIN.getMax()+"."));
            variables.add(ChocoboMaxPack_name, new JsonPrimitive(holder.getChocoboMaxPack()));
            variables.add(ChocoboMaxPack_name+"_comment", new JsonPrimitive("Maximum number of Chocobos in a pack. Minimum value is "+dCHOCOBO_PACK_MAX.getMin()+", Default is "+dCHOCOBO_PACK_MAX.getDefault()+", Max Value is "+dCHOCOBO_PACK_MAX.getMax()+"."));
            variables.add(OverWorldSpawnWeight_name, new JsonPrimitive(holder.getOverWorldSpawnWeight()));
            variables.add(OverWorldSpawnWeight_name+"_comment", new JsonPrimitive("Weight of Chocobo spawning in the overworld. Minimum value is "+dOVERWORLD_SPAWN_WEIGHT.getMin()+", Default is "+dOVERWORLD_SPAWN_WEIGHT.getDefault()+", Max Value is "+dOVERWORLD_SPAWN_WEIGHT.getMax()+"."));
            variables.add(MushroomSpawnWeight_name, new JsonPrimitive(holder.getMushroomSpawnWeight()));
            variables.add(MushroomSpawnWeight_name+"_comment", new JsonPrimitive("Weight of Chocobo spawning in the mushroom biome. Minimum value is "+dMUSHROOM_SPAWN_WEIGHT.getMin()+", Default is "+dMUSHROOM_SPAWN_WEIGHT.getDefault()+", Max Value is "+dMUSHROOM_SPAWN_WEIGHT.getMax()+"."));
            variables.add(NetherSpawnWeight_name, new JsonPrimitive(holder.getNetherSpawnWeight()));
            variables.add(NetherSpawnWeight_name+"_comment", new JsonPrimitive("Weight of Chocobo spawning in the nether. Minimum value is "+dNETHER_SPAWN_WEIGHT.getMin()+", Default is "+dNETHER_SPAWN_WEIGHT.getDefault()+", Max Value is "+dNETHER_SPAWN_WEIGHT.getMax()+"."));
            variables.add(EndSpawnWeight_name, new JsonPrimitive(holder.getEndSpawnWeight()));
            variables.add(EndSpawnWeight_name+"_comment", new JsonPrimitive("Weight of Chocobo spawning in the end. Minimum value is "+dEND_SPAWN_WEIGHT.getMin()+", Default is "+dEND_SPAWN_WEIGHT.getDefault()+", Max Value is "+dEND_SPAWN_WEIGHT.getMax()+"."));
            variables.add(GysahlGreenPatchSize_name, new JsonPrimitive(holder.getGysahlGreenPatchSize()));
            variables.add(GysahlGreenPatchSize_name+"_comment", new JsonPrimitive("Size of Gysahl Green patches. Minimum value is "+dGYSAHL_GREEN_PATCH_SIZE.getMin()+", Default is "+dGYSAHL_GREEN_PATCH_SIZE.getDefault()+", Max Value is "+dGYSAHL_GREEN_PATCH_SIZE.getMax()+"."));
            variables.add(GysahlGreenSpawnChance_name, new JsonPrimitive(holder.getGysahlGreenSpawnChance()));
            variables.add(GysahlGreenSpawnChance_name+"_comment", new JsonPrimitive("Chance of Gysahl Green spawning in a patch. Minimum value is "+dGYSAHL_GREEN_SPAWN_CHANCE.getMin()+", Default is "+dGYSAHL_GREEN_SPAWN_CHANCE.getDefault()+", Max Value is "+dGYSAHL_GREEN_SPAWN_CHANCE.getMax()+"."));
            variables.add(CanSpawn_name, new JsonPrimitive(holder.isCanSpawn()));
            variables.add(CanSpawn_name+"_comment", new JsonPrimitive("Allows Chocobos spawn in this world or not?, true or false. default is true."));
            variables.add(OverworldSpawn_name, new JsonPrimitive(holder.isOverworldSpawn()));
            variables.add(OverWorldSpawnWeight_name+"_comment", new JsonPrimitive("Allows Chocobos to spawn in the overworld, true or false. default is true."));
            variables.add(NetherSpawn_name, new JsonPrimitive(holder.isNetherSpawn()));
            variables.add(NetherSpawn_name+"_comment", new JsonPrimitive("Allows Chocobos to spawn in the nether, true or false. default is true."));
            variables.add(EndSpawn_name, new JsonPrimitive(holder.isEndSpawn()));
            variables.add(EndSpawn_name+"_comment", new JsonPrimitive("Allows Chocobos to spawn in the end, true or false. default is true."));
        } else {
            variables.add(Stamina_name, new JsonPrimitive(holder.getStamina()));
            variables.add(Stamina_name+"_comment", new JsonPrimitive("Chocobo Stamina. Minimum value is "+dSTAMINA.getMin()+", Default is "+dSTAMINA.getDefault()+", Max Value is "+dSTAMINA.getMax()+"."));
            variables.add(Speed_name, new JsonPrimitive(holder.getSpeed()));
            variables.add(Speed_name+"_comment", new JsonPrimitive("Chocobo Speed. Minimum value is "+dSPEED.getMin()+", Default is "+dSPEED.getDefault()+", Max Value is "+dSPEED.getMax()+"."));
            variables.add(Health_name, new JsonPrimitive(holder.getHealth()));
            variables.add(Health_name+"_comment", new JsonPrimitive("Chocobo Health. Minimum value is "+dHEALTH.getMin()+", Default is "+dHEALTH.getDefault()+", Max Value is "+dHEALTH.getMax()+"."));
            variables.add(Armor_name, new JsonPrimitive(holder.getArmor()));
            variables.add(Armor_name+"_comment", new JsonPrimitive("Chocobo Armor. Minimum value is "+dARMOR.getMin()+", Default is "+dARMOR.getDefault()+", Max Value is "+dARMOR.getMax()+"."));
            variables.add(ArmorTough_name, new JsonPrimitive(holder.getArmorTough()));
            variables.add(ArmorTough_name+"_comment", new JsonPrimitive("Chocobo Armor Toughness. Minimum value is "+dARMOR_TOUGH.getMin()+", Default is "+dARMOR_TOUGH.getDefault()+", Max Value is "+dARMOR_TOUGH.getMax()+"."));
            variables.add(Attack_name, new JsonPrimitive(holder.getAttack()));
            variables.add(Attack_name+"_comment", new JsonPrimitive("Chocobo Attack. Minimum value is "+dATTACK.getMin()+", Default is "+dATTACK.getDefault()+", Max Value is "+dATTACK.getMax()+"."));
            variables.add(WeaponModifier_name, new JsonPrimitive(holder.getWeaponModifier()));
            variables.add(WeaponModifier_name+"_comment", new JsonPrimitive("Chocobo Weapon Modifier. Minimum value is "+dWEAPON_MOD.getMin()+", Default is "+dWEAPON_MOD.getDefault()+", Max Value is "+dWEAPON_MOD.getMax()+"."));
            variables.add(HealAmount_name, new JsonPrimitive(holder.getHealAmount()));
            variables.add(HealAmount_name+"_comment", new JsonPrimitive("Chocobo Heal Amount. Minimum value is "+dHEAL_AMOUNT.getMin()+", Default is "+dHEAL_AMOUNT.getDefault()+", Max Value is "+dHEAL_AMOUNT.getMax()+"."));
            variables.add(EggHatchTimeTicks_name, new JsonPrimitive(holder.getEggHatchTimeTicks()));
            variables.add(EggHatchTimeTicks_name+"_comment", new JsonPrimitive("Chocobo Egg Hatch Time. Minimum value is "+dEGG_HATCH.getMin()+", Default is "+dEGG_HATCH.getDefault()+", Max Value is "+dEGG_HATCH.getMax()+"."));
            variables.add(MaxHealth_name, new JsonPrimitive(holder.getMaxHealth()));
            variables.add(MaxHealth_name+"_comment", new JsonPrimitive("Chocobo Max Health. Minimum value is "+dMAX_HEALTH.getMin()+", Default is "+dMAX_HEALTH.getDefault()+", Max Value is "+dMAX_HEALTH.getMax()+"."));
            variables.add(MaxSpeed_name, new JsonPrimitive(holder.getMaxSpeed()));
            variables.add(MaxSpeed_name+"_comment", new JsonPrimitive("Chocobo Max Speed. Minimum value is "+dMAX_SPEED.getMin()+", Default is "+dMAX_SPEED.getDefault()+", Max Value is "+dMAX_SPEED.getMax()+"."));
            variables.add(StaminaRegen_name, new JsonPrimitive(holder.getStaminaRegen()));
            variables.add(StaminaRegen_name+"_comment", new JsonPrimitive("Chocobo Stamina Regen. Minimum value is "+dSTAMINA_REGEN.getMin()+", Default is "+dSTAMINA_REGEN.getDefault()+", Max Value is "+dSTAMINA_REGEN.getMax()+"."));
            variables.add(Tame_name, new JsonPrimitive(holder.getTame()));
            variables.add(Tame_name+"_comment", new JsonPrimitive("Chocobo Tame Chance. Minimum value is "+dTAME.getMin()+", Default is "+dTAME.getDefault()+", Max Value is "+dTAME.getMax()+"."));
            variables.add(StaminaCost_name, new JsonPrimitive(holder.getStaminaCost()));
            variables.add(StaminaCost_name+"_comment", new JsonPrimitive("Chocobo Stamina Sprint Cost. Minimum value is "+dSTAMINA_SPRINT.getMin()+", Default is "+dSTAMINA_SPRINT.getDefault()+", Max Value is "+dSTAMINA_SPRINT.getMax()+"."));
            variables.add(StaminaGlide_name, new JsonPrimitive(holder.getStaminaGlide()));
            variables.add(StaminaGlide_name+"_comment", new JsonPrimitive("Chocobo Stamina Glide Cost. Minimum value is "+dSTAMINA_GLIDE.getMin()+", Default is "+dSTAMINA_GLIDE.getDefault()+", Max Value is "+dSTAMINA_GLIDE.getMax()+"."));
            variables.add(StaminaJump_name, new JsonPrimitive(holder.getStaminaJump()));
            variables.add(StaminaJump_name+"_comment", new JsonPrimitive("Chocobo Stamina Jump Cost. Minimum value is "+dSTAMINA_JUMP.getMin()+", Default is "+dSTAMINA_JUMP.getDefault()+", Max Value is "+dSTAMINA_JUMP.getMax()+"."));
            variables.add(PossLoss_name, new JsonPrimitive(holder.getPossLoss()));
            variables.add(PossLoss_name+"_comment", new JsonPrimitive("Chocobo Potential Loss of Attack, Defence, (Armor)Toughness, when breeding to the Chicbo. Minimum value is "+dPOS_LOSS.getMin()+", Default is "+dPOS_LOSS.getDefault()+", Max Value is "+dPOS_LOSS.getMax()+"."));
            variables.add(PossGain_name, new JsonPrimitive(holder.getPossGain()));
            variables.add(PossGain_name+"_comment", new JsonPrimitive("Chocobo Potential Gain of Attack, Defence, (Armor)Toughness, when breeding to the Chicbo. Minimum value is "+dPOS_GAIN.getMin()+", Default is "+dPOS_GAIN.getDefault()+", Max Value is "+dPOS_GAIN.getMax()+"."));
            variables.add(PossLossStamina_name, new JsonPrimitive(holder.getPossLossStamina()));
            variables.add(PossLossStamina_name+"_comment", new JsonPrimitive("Chocobo Potential Loss of Stamina, when breeding to the Chicbo. Minimum value is "+dPOS_LOSS.getMin()+", Default is "+dPOS_LOSS.getDefault()+", Max Value is "+dPOS_LOSS.getMax()+"."));
            variables.add(PossGainStamina_name, new JsonPrimitive(holder.getPossGainStamina()));
            variables.add(PossGainStamina_name+"_comment", new JsonPrimitive("Chocobo Potential Gain of Stamina, when breeding to the Chicbo. Minimum value is "+dPOS_GAIN.getMin()+", Default is "+dPOS_GAIN.getDefault()+", Max Value is "+dPOS_GAIN.getMax()+"."));
            variables.add(PossLossSpeed_name, new JsonPrimitive(holder.getPossLossSpeed()));
            variables.add(PossLossSpeed_name+"_comment", new JsonPrimitive("Chocobo Potential Loss of Speed, when breeding to the Chicbo. Minimum value is "+dPOS_LOSS.getMin()+", Default is "+dPOS_LOSS.getDefault()+", Max Value is "+dPOS_LOSS.getMax()+"."));
            variables.add(PossGainSpeed_name, new JsonPrimitive(holder.getPossGainSpeed()));
            variables.add(PossGainSpeed_name+"_comment", new JsonPrimitive("Chocobo Potential Gain of Speed, when breeding to the Chicbo. Minimum value is "+dPOS_GAIN.getMin()+", Default is "+dPOS_GAIN.getDefault()+", Max Value is "+dPOS_GAIN.getMax()+"."));
            variables.add(PossLossHealth_name, new JsonPrimitive(holder.getPossLossHealth()));
            variables.add(PossLossHealth_name+"_comment", new JsonPrimitive("Chocobo Potential Loss of Health, when breeding to the Chicbo. Minimum value is "+dPOS_LOSS.getMin()+", Default is "+dPOS_LOSS.getDefault()+", Max Value is "+dPOS_LOSS.getMax()+"."));
            variables.add(PossGainHealth_name, new JsonPrimitive(holder.getPossGainHealth()));
            variables.add(PossGainHealth_name+"_comment", new JsonPrimitive("Chocobo Potential Gain of Health, when breeding to the Chicbo. Minimum value is "+dPOS_GAIN.getMin()+", Default is "+dPOS_GAIN.getDefault()+", Max Value is "+dPOS_GAIN.getMax()+"."));
            variables.add(MaxStamina_name, new JsonPrimitive(holder.getMaxStamina()));
            variables.add(MaxStamina_name+"_comment", new JsonPrimitive("Chocobo Max Stamina. Minimum value is "+dMAX_STAMINA.getMin()+", Default is "+dMAX_STAMINA.getDefault()+", Max Value is "+dMAX_STAMINA.getMax()+"."));
            variables.add(MaxStrength_name, new JsonPrimitive(holder.getMaxStrength()));
            variables.add(MaxStrength_name+"_comment", new JsonPrimitive("Chocobo Max Strength. Minimum value is "+dMAX_STRENGTH.getMin()+", Default is "+dMAX_STRENGTH.getDefault()+", Max Value is "+dMAX_STRENGTH.getMax()+"."));
            variables.add(MaxArmor_name, new JsonPrimitive(holder.getMaxArmor()));
            variables.add(MaxArmor_name+"_comment", new JsonPrimitive("Chocobo Max Armor. Minimum value is "+dMAX_ARMOR.getMin()+", Default is "+dMAX_ARMOR.getDefault()+", Max Value is "+dMAX_ARMOR.getMax()+"."));
            variables.add(MaxArmorToughness_name, new JsonPrimitive(holder.getMaxArmorToughness()));
            variables.add(MaxArmorToughness_name+"_comment", new JsonPrimitive("Chocobo Max Armor Toughness. Minimum value is "+dMAX_ARMOR_TOUGH.getMin()+", Default is "+dMAX_ARMOR_TOUGH.getDefault()+", Max Value is "+dMAX_ARMOR_TOUGH.getMax()+"."));
            variables.add(ArmorAlpha_name, new JsonPrimitive(holder.getArmorAlpha()));
            variables.add(ArmorAlpha_name+"_comment", new JsonPrimitive("Chocobo Armor Alpha value in Float. Minimum value is "+dARMOR_ALPHA.getMin()+", Default is "+dARMOR_ALPHA.getDefault()+", Max Value is "+dARMOR_ALPHA.getMax()+"."));
            variables.add(WeaponAlpha_name, new JsonPrimitive(holder.getWeaponAlpha()));
            variables.add(WeaponAlpha_name+"_comment", new JsonPrimitive("Chocobo Weapon Alpha value in Float. Minimum value is "+dWEAPON_ALPHA.getMin()+", Default is "+dWEAPON_ALPHA.getDefault()+", Max Value is "+dWEAPON_ALPHA.getMax()+"."));
            variables.add(CollarAlpha_name, new JsonPrimitive(holder.getCollarAlpha()));
            variables.add(CollarAlpha_name+"_comment", new JsonPrimitive("Chocobo Collar Alpha value in Float. Minimum value is "+dCOLLAR_ALPHA.getMin()+", Default is "+dCOLLAR_ALPHA.getDefault()+", Max Value is "+dCOLLAR_ALPHA.getMax()+"."));
            variables.add(SaddleAlpha_name, new JsonPrimitive(holder.getSaddleAlpha()));
            variables.add(SaddleAlpha_name+"_comment", new JsonPrimitive("Chocobo Saddle Alpha value in Float. Minimum value is "+dSADDLE_ALPHA.getMin()+", Default is "+dSADDLE_ALPHA.getDefault()+", Max Value is "+dSADDLE_ALPHA.getMax()+"."));
            variables.add(ExtraChocoboEffects_name, new JsonPrimitive(holder.isExtraChocoboEffects()));
            variables.add(ExtraChocoboEffects_name+"_comment", new JsonPrimitive("Chocobo Extra Effects. true or false. default is true."));
            variables.add(ExtraChocoboResourcesOnHit_name, new JsonPrimitive(holder.isExtraChocoboResourcesOnHit()));
            variables.add(ExtraChocoboResourcesOnHit_name+"_comment", new JsonPrimitive("Chocobo Extra Resources on Hit. true or false. default is true."));
            variables.add(ExtraChocoboResourcesOnKill_name, new JsonPrimitive(holder.isExtraChocoboResourcesOnKill()));
            variables.add(ExtraChocoboResourcesOnKill_name+"_comment", new JsonPrimitive("Chocobo Extra Resources on Kill. true or false. default is true."));
            variables.add(ShiftHitBypass_name, new JsonPrimitive(holder.isShiftHitBypass()));
            variables.add(ShiftHitBypass_name+"_comment", new JsonPrimitive("Chocobo Shift Hit Bypass. true or false. default is true."));
            variables.add(OwnChocoboHittable_name, new JsonPrimitive(holder.isOwnChocoboHittable()));
            variables.add(OwnChocoboHittable_name+"_comment", new JsonPrimitive("Chocobo Own Chocobo Hittable. true or false. default is false."));
            variables.add(TamedChocoboHittable_name, new JsonPrimitive(holder.isTamedChocoboHittable()));
            variables.add(TamedChocoboHittable_name+"_comment", new JsonPrimitive("Chocobo Tamed Chocobo Hittable. true or false. default is false."));
            variables.add(OwnerOnlyInventory_name, new JsonPrimitive(holder.isOwnerOnlyInventory()));
            variables.add(OwnerOnlyInventory_name+"_comment", new JsonPrimitive("Chocobo Owner Only Inventory. true or false. default is false."));
        }
        return variables;
    }
}