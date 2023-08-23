package com.dephoegon.delchoco.aid.world;

import com.dephoegon.delbase.Delbase;
import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.aid.composables;
import com.dephoegon.delchoco.client.ChocoboSprintingEventHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.NotNull;

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
    public static void init() {
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
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                ChocoboSprintingEventHandler.onKeyPress();
            });
            composables.addToList();
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
    private static void saveFile(@NotNull ServerWorld world) {
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
    private static void getCurrentGlobalVariables(globalVariablesHolder variablesHolder, boolean isWorld) {
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
    }
    private static boolean globalVariablesChanged(globalVariablesHolder currentVariables, boolean isWorld) {
        if (isWorld) {
            globalWorldVariableNullCheck();
            if (currentVariables.getChocoboMinPack() != StaticGlobalVariables.getChocoboMinPack()) { return true; }
            if (currentVariables.getChocoboMaxPack() != StaticGlobalVariables.getChocoboMaxPack()) { return true; }
            if (currentVariables.getOverWorldSpawnWeight() != StaticGlobalVariables.getOverWorldSpawnWeight()) { return true; }
            if (currentVariables.getMushroomSpawnWeight() != StaticGlobalVariables.getMushroomSpawnWeight()) { return true; }
            if (currentVariables.getNetherSpawnWeight() != StaticGlobalVariables.getNetherSpawnWeight()) { return true; }
            if (currentVariables.getEndSpawnWeight() != StaticGlobalVariables.getEndSpawnWeight()) { return true; }
            if (currentVariables.getGysahlGreenPatchSize() != StaticGlobalVariables.getGysahlGreenPatchSize()) { return true; }
            if (currentVariables.getGysahlGreenSpawnChance() != StaticGlobalVariables.getGysahlGreenSpawnChance()) { return true; }
            if (currentVariables.isCanSpawn() != StaticGlobalVariables.getCanSpawn()) { return true; }
            if (currentVariables.isOverworldSpawn() != StaticGlobalVariables.getOverworldSpawn()) { return true; }
            if (currentVariables.isNetherSpawn() != StaticGlobalVariables.getNetherSpawn()) { return true; }
            return currentVariables.isEndSpawn() != StaticGlobalVariables.getEndSpawn();
        } else {
            globalChocoboVariableNullCheck();
            if (currentVariables.getStamina() != StaticGlobalVariables.getStamina()) { return true; }
            if (currentVariables.getSpeed() != StaticGlobalVariables.getSpeed()) { return true; }
            if (currentVariables.getHealth() != StaticGlobalVariables.getHealth()) { return true; }
            if (currentVariables.getArmor() != StaticGlobalVariables.getArmor()) { return true; }
            if (currentVariables.getAttack() != StaticGlobalVariables.getAttack()) { return true; }
            if (currentVariables.getWeaponModifier() != StaticGlobalVariables.getWeaponModifier()) { return true; }
            if (currentVariables.getHealAmount() != StaticGlobalVariables.getHealAmount()) { return true; }
            if (currentVariables.getEggHatchTimeTicks() != StaticGlobalVariables.getEggHatchTimeTicks()) { return true; }
            if (currentVariables.getMaxHealth() != StaticGlobalVariables.getMaxHealth()) { return true; }
            if (currentVariables.getMaxSpeed() != StaticGlobalVariables.getMaxSpeed()) { return true; }
            if (currentVariables.getStaminaRegen() != StaticGlobalVariables.getStaminaRegen()) { return true; }
            if (currentVariables.getTame() != StaticGlobalVariables.getTame()) { return true; }
            if (currentVariables.getStaminaCost() != StaticGlobalVariables.getStaminaCost()) { return true; }
            if (currentVariables.getStaminaGlide() != StaticGlobalVariables.getStaminaGlide()) { return true; }
            if (currentVariables.getStaminaJump() != StaticGlobalVariables.getStaminaJump()) { return true; }
            if (currentVariables.getPossLoss() != StaticGlobalVariables.getPossLoss()) { return true; }
            if (currentVariables.getPossGain() != StaticGlobalVariables.getPossGain()) { return true; }
            if (currentVariables.getPossLossStamina() != StaticGlobalVariables.getPossLossStamina()) { return true; }
            if (currentVariables.getPossGainStamina() != StaticGlobalVariables.getPossGainStamina()) { return true; }
            if (currentVariables.getPossLossSpeed() != StaticGlobalVariables.getPossLossSpeed()) { return true; }
            if (currentVariables.getPossGainSpeed() != StaticGlobalVariables.getPossGainSpeed()) { return true; }
            if (currentVariables.getPossLossHealth() != StaticGlobalVariables.getPossLossHealth()) { return true; }
            if (currentVariables.getPossGainHealth() != StaticGlobalVariables.getPossGainHealth()) { return true; }
            if (currentVariables.getMaxStamina() != StaticGlobalVariables.getMaxStamina()) { return true; }
            if (currentVariables.getMaxArmor() != StaticGlobalVariables.getMaxArmor()) { return true; }
            if (currentVariables.getMaxArmorToughness() != StaticGlobalVariables.getMaxArmorToughness()) { return true; }
            if (currentVariables.getArmorAlpha() != StaticGlobalVariables.getArmorAlpha()) { return true; }
            if (currentVariables.getWeaponAlpha() != StaticGlobalVariables.getWeaponAlpha()) { return true; }
            if (currentVariables.getCollarAlpha() != StaticGlobalVariables.getCollarAlpha()) { return true; }
            if (currentVariables.getSaddleAlpha() != StaticGlobalVariables.getSaddleAlpha()) { return true; }
            if (currentVariables.isExtraChocoboEffects() != StaticGlobalVariables.getExtraChocoboEffects()) { return true; }
            if (currentVariables.isExtraChocoboResourcesOnHit() != StaticGlobalVariables.getExtraChocoboResourcesOnHit()) { return true; }
            if (currentVariables.isExtraChocoboResourcesOnKill() != StaticGlobalVariables.getExtraChocoboResourcesOnKill()) { return true; }
            if (currentVariables.isShiftHitBypass() != StaticGlobalVariables.getShiftHitBypass()) { return true; }
            if (currentVariables.isOwnChocoboHittable() != StaticGlobalVariables.getOwnChocoboHittable()) { return true; }
            if (currentVariables.isOwnerOnlyInventory() != StaticGlobalVariables.getOwnerOnlyInventory()) { return true; }
            return currentVariables.isTamedChocoboHittable() != StaticGlobalVariables.getTamedChocoboHittable();
        }
    }
    private static void globalChocoboVariableNullCheck() {
        StaticGlobalVariables.setStamina(ChocoConfigGet(StaticGlobalVariables.getStamina(), dSTAMINA.getDefault()));
                StaticGlobalVariables.setSpeed(ChocoConfigGet(StaticGlobalVariables.getSpeed(), dSPEED.getDefault()));
                StaticGlobalVariables.setHealth(ChocoConfigGet(StaticGlobalVariables.getHealth(), dHEALTH.getDefault()));
                StaticGlobalVariables.setArmor(ChocoConfigGet(StaticGlobalVariables.getArmor(), dARMOR.getDefault()));
                StaticGlobalVariables.setArmorTough(ChocoConfigGet(StaticGlobalVariables.getArmorTough(), dARMOR_TOUGH.getDefault()));
                StaticGlobalVariables.setAttack(ChocoConfigGet(StaticGlobalVariables.getAttack(), dATTACK.getDefault()));
                StaticGlobalVariables.setWeaponModifier(ChocoConfigGet(StaticGlobalVariables.getWeaponModifier(), dWEAPON_MOD.getDefault()));
                StaticGlobalVariables.setHealAmount(ChocoConfigGet(StaticGlobalVariables.getHealAmount(), dHEAL_AMOUNT.getDefault()));
                StaticGlobalVariables.setEggHatchTimeTicks(ChocoConfigGet(StaticGlobalVariables.getEggHatchTimeTicks(), dEGG_HATCH.getDefault()));
                StaticGlobalVariables.setMaxHealth(ChocoConfigGet(StaticGlobalVariables.getMaxHealth(), dMAX_HEALTH.getDefault()));
                StaticGlobalVariables.setMaxSpeed(ChocoConfigGet(StaticGlobalVariables.getMaxSpeed(), dMAX_SPEED.getDefault()));
                StaticGlobalVariables.setStaminaRegen(ChocoConfigGet(StaticGlobalVariables.getStaminaRegen(), dSTAMINA_REGEN.getDefault()));
                StaticGlobalVariables.setTame(ChocoConfigGet(StaticGlobalVariables.getTame(), dTAME.getDefault()));
                StaticGlobalVariables.setStaminaCost(ChocoConfigGet(StaticGlobalVariables.getStaminaCost(), dSTAMINA_SPRINT.getDefault()));
                StaticGlobalVariables.setStaminaGlide(ChocoConfigGet(StaticGlobalVariables.getStaminaGlide(), dSTAMINA_GLIDE.getDefault()));
                StaticGlobalVariables.setStaminaJump(ChocoConfigGet(StaticGlobalVariables.getStaminaJump(), dSTAMINA_JUMP.getDefault()));
                StaticGlobalVariables.setPossLoss(ChocoConfigGet(StaticGlobalVariables.getPossLoss(), dPOS_LOSS.getDefault()));
                StaticGlobalVariables.setPossGain(ChocoConfigGet(StaticGlobalVariables.getPossGain(), dPOS_GAIN.getDefault()));
                StaticGlobalVariables.setPossLossHealth(ChocoConfigGet(StaticGlobalVariables.getPossLossHealth(), dPOS_LOSS.getDefault()));
                StaticGlobalVariables.setPossGainHealth(ChocoConfigGet(StaticGlobalVariables.getPossGainHealth(), dPOS_GAIN.getDefault()));
                StaticGlobalVariables.setPossLossSpeed(ChocoConfigGet(StaticGlobalVariables.getPossLossSpeed(), dPOS_LOSS.getDefault()));
                StaticGlobalVariables.setPossGainSpeed(ChocoConfigGet(StaticGlobalVariables.getPossGainSpeed(), dPOS_GAIN.getDefault()));
                StaticGlobalVariables.setPossLossStamina(ChocoConfigGet(StaticGlobalVariables.getPossLossStamina(), dPOS_LOSS.getDefault()));
                StaticGlobalVariables.setPossGainStamina(ChocoConfigGet(StaticGlobalVariables.getPossGainStamina(), dPOS_GAIN.getDefault()));
                StaticGlobalVariables.setMaxStamina(ChocoConfigGet(StaticGlobalVariables.getMaxStamina(), dMAX_STAMINA.getDefault()));
                StaticGlobalVariables.setMaxStrength(ChocoConfigGet(StaticGlobalVariables.getMaxStrength(), dMAX_STRENGTH.getDefault()));
                StaticGlobalVariables.setMaxArmor(ChocoConfigGet(StaticGlobalVariables.getMaxArmor(), dMAX_STAMINA.getDefault()));
                StaticGlobalVariables.setMaxArmorToughness(ChocoConfigGet(StaticGlobalVariables.getMaxArmorToughness(), dMAX_ARMOR_TOUGH.getDefault()));
                StaticGlobalVariables.setArmorAlpha(ChocoConfigGet(StaticGlobalVariables.getArmorAlpha(), dARMOR_ALPHA.getDefault()));
                StaticGlobalVariables.setWeaponAlpha(ChocoConfigGet(StaticGlobalVariables.getWeaponAlpha(), dWEAPON_ALPHA.getDefault()));
                StaticGlobalVariables.setCollarAlpha(ChocoConfigGet(StaticGlobalVariables.getCollarAlpha(), dCOLLAR_ALPHA.getDefault()));
                StaticGlobalVariables.setSaddleAlpha(ChocoConfigGet(StaticGlobalVariables.getSaddleAlpha(), dSADDLE_ALPHA.getDefault()));
                StaticGlobalVariables.setExtraChocoboEffects(ChocoConfigGet(StaticGlobalVariables.getExtraChocoboEffects(), dExtraChocoboEffects));
                StaticGlobalVariables.setExtraChocoboResourcesOnHit(ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnHit(), dExtraChocoboResourcesOnHit));
                StaticGlobalVariables.setExtraChocoboResourcesOnKill(ChocoConfigGet(StaticGlobalVariables.getExtraChocoboResourcesOnKill(), dExtraChocoboResourcesOnKill));
                StaticGlobalVariables.setShiftHitBypass(ChocoConfigGet(StaticGlobalVariables.getShiftHitBypass(), dShiftHitBypass));
                StaticGlobalVariables.setOwnChocoboHittable(ChocoConfigGet(StaticGlobalVariables.getOwnChocoboHittable(), dOwnChocoboHittable));
                StaticGlobalVariables.setTamedChocoboHittable(ChocoConfigGet(StaticGlobalVariables.getTamedChocoboHittable(), dTamedChocoboHittable));
                StaticGlobalVariables.setOwnerOnlyInventory(ChocoConfigGet(StaticGlobalVariables.getOwnerOnlyInventory(), dOwnerOnlyInventoryAccess));
    }
    private static void globalWorldVariableNullCheck() {
        StaticGlobalVariables.setChocoboMinPack(ChocoConfigGet(StaticGlobalVariables.getChocoboMinPack(), dCHOCOBO_PACK_MIN.getDefault()));
        StaticGlobalVariables.setChocoboMaxPack(ChocoConfigGet(StaticGlobalVariables.getChocoboMaxPack(), dCHOCOBO_PACK_MAX.getDefault()));
        StaticGlobalVariables.setOverWorldSpawnWeight(ChocoConfigGet(StaticGlobalVariables.getOverWorldSpawnWeight(), dOVERWORLD_SPAWN_WEIGHT.getDefault()));
        StaticGlobalVariables.setNetherSpawnWeight(ChocoConfigGet(StaticGlobalVariables.getNetherSpawnWeight(), dNETHER_SPAWN_WEIGHT.getDefault()));
        StaticGlobalVariables.setEndSpawnWeight(ChocoConfigGet(StaticGlobalVariables.getEndSpawnWeight(), dEND_SPAWN_WEIGHT.getDefault()));
        StaticGlobalVariables.setGysahlGreenPatchSize(ChocoConfigGet(StaticGlobalVariables.getGysahlGreenPatchSize(), dGYSAHL_GREEN_PATCH_SIZE.getDefault()));
        StaticGlobalVariables.setGysahlGreenSpawnChance(ChocoConfigGet(StaticGlobalVariables.getGysahlGreenSpawnChance(), dGYSAHL_GREEN_SPAWN_CHANCE.getDefault()));
        StaticGlobalVariables.setCanSpawn(ChocoConfigGet(StaticGlobalVariables.getCanSpawn(), dCanSpawn));
        StaticGlobalVariables.setOverworldSpawn(ChocoConfigGet(StaticGlobalVariables.getOverworldSpawn(), dOverworldSpawn));
        StaticGlobalVariables.setNetherSpawn(ChocoConfigGet(StaticGlobalVariables.getNetherSpawn(), dNetherSpawn));
        StaticGlobalVariables.setEndSpawn(ChocoConfigGet(StaticGlobalVariables.getEndSpawn(), dEndSpawn));
    }
}