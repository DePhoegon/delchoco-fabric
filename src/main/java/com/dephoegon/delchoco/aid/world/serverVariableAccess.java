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

import static com.dephoegon.delchoco.aid.world.StaticGlobalVariables.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultBooleans.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultDoubles.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultFloats.*;
import static com.dephoegon.delchoco.aid.world.dValues.defaultInts.*;

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
            setChocoboMaxPack(variables.getChocoboMaxPack());
            setOverWorldSpawnWeight(variables.getOverWorldSpawnWeight());
            setMushroomSpawnWeight(variables.getMushroomSpawnWeight());
            setNetherSpawnWeight(variables.getNetherSpawnWeight());
            setEndSpawnWeight(variables.getEndSpawnWeight());
            setGysahlGreenPatchSize(variables.getGysahlGreenPatchSize());
            setGysahlGreenSpawnChance(variables.getGysahlGreenSpawnChance());
            setCanSpawn(variables.isCanSpawn());
            setOverworldSpawn(variables.isOverworldSpawn());
            setNetherSpawn(variables.isNetherSpawn());
            setEndSpawn(variables.isEndSpawn());
        } else {
            setStamina(variables.getStamina());
            setSpeed(variables.getSpeed());
            setHealth(variables.getHealth());
            setArmor(variables.getArmor());
            setArmorTough(variables.getArmorTough());
            setAttack(variables.getAttack());
            setWeaponModifier(variables.getWeaponModifier());
            setHealAmount(variables.getHealAmount());
            setEggHatchTimeTicks(variables.getEggHatchTimeTicks());
            setMaxHealth(variables.getMaxHealth());
            setMaxSpeed(variables.getMaxSpeed());
            setStaminaRegen(variables.getStaminaRegen());
            setTame(variables.getTame());
            setStaminaCost(variables.getStaminaCost());
            setStaminaGlide(variables.getStaminaGlide());
            setStaminaJump(variables.getStaminaJump());
            setPossLoss(variables.getPossLoss());
            setPossGain(variables.getPossGain());
            setMaxStamina(variables.getMaxStamina());
            setMaxStrength(variables.getMaxStrength());
            setMaxArmor(variables.getMaxArmor());
            setMaxArmorToughness(variables.getMaxArmorToughness());
            setArmorAlpha(variables.getArmorAlpha());
            setWeaponAlpha(variables.getWeaponAlpha());
            setCollarAlpha(variables.getCollarAlpha());
            setSaddleAlpha(variables.getSaddleAlpha());
            setExtraChocoboEffects(variables.isExtraChocoboEffects());
            setExtraChocoboResourcesOnHit(variables.isExtraChocoboResourcesOnHit());
            setExtraChocoboResourcesOnKill(variables.isExtraChocoboResourcesOnKill());
            setShiftHitBypass(variables.isShiftHitBypass());
            setOwnChocoboHittable(variables.isOwnChocoboHittable());
            setTamedChocoboHittable(variables.isTamedChocoboHittable());
        }
    }
    private void getCurrentGlobalVariables(globalVariablesHolder variablesHolder, boolean isWorld) {
        if (isWorld) {
            variablesHolder.setChocoboMinPack(StaticGlobalVariables.getChocoboMinPack());
            variablesHolder.setChocoboMaxPack(StaticGlobalVariables.getChocoboMaxPack());
            variablesHolder.setOverworldSpawn(StaticGlobalVariables.getOverworldSpawn());
            variablesHolder.setMushroomSpawnWeight(StaticGlobalVariables.getMushroomSpawnWeight());
            variablesHolder.setNetherSpawnWeight(StaticGlobalVariables.getNetherSpawnWeight());
            variablesHolder.setEndSpawnWeight(StaticGlobalVariables.getEndSpawnWeight());
            variablesHolder.setGysahlGreenPatchSize(StaticGlobalVariables.getGysahlGreenPatchSize());
            variablesHolder.setGysahlGreenSpawnChance(StaticGlobalVariables.getGysahlGreenSpawnChance());
            variablesHolder.setCanSpawn(StaticGlobalVariables.getCanSpawn());
            variablesHolder.setOverworldSpawn(StaticGlobalVariables.getOverworldSpawn());
            variablesHolder.setNetherSpawn(StaticGlobalVariables.getNetherSpawn());
            variablesHolder.setEndSpawn(StaticGlobalVariables.getEndSpawn());
        } else  {
            variablesHolder.setStamina(StaticGlobalVariables.getStamina());
            variablesHolder.setSpeed(StaticGlobalVariables.getSpeed());
            variablesHolder.setHealth(StaticGlobalVariables.getHealth());
            variablesHolder.setArmor(StaticGlobalVariables.getArmor());
            variablesHolder.setArmorTough(StaticGlobalVariables.getArmorTough());
            variablesHolder.setAttack(StaticGlobalVariables.getAttack());
            variablesHolder.setWeaponModifier(StaticGlobalVariables.getWeaponModifier());
            variablesHolder.setHealAmount(StaticGlobalVariables.getHealAmount());
            variablesHolder.setEggHatchTimeTicks(StaticGlobalVariables.getEggHatchTimeTicks());
            variablesHolder.setMaxHealth(StaticGlobalVariables.getMaxHealth());
            variablesHolder.setMaxSpeed(StaticGlobalVariables.getMaxSpeed());
            variablesHolder.setStaminaRegen(StaticGlobalVariables.getStaminaRegen());
            variablesHolder.setTame(StaticGlobalVariables.getTame());
            variablesHolder.setStaminaCost(StaticGlobalVariables.getStaminaCost());
            variablesHolder.setStaminaGlide(StaticGlobalVariables.getStaminaGlide());
            variablesHolder.setStaminaJump(StaticGlobalVariables.getStaminaJump());
            variablesHolder.setPossLoss(StaticGlobalVariables.getPossLoss());
            variablesHolder.setPossGain(StaticGlobalVariables.getPossGain());
            variablesHolder.setMaxStamina(StaticGlobalVariables.getMaxStamina());
            variablesHolder.setMaxStrength(StaticGlobalVariables.getMaxStrength());
            variablesHolder.setMaxArmor(StaticGlobalVariables.getMaxArmor());
            variablesHolder.setMaxArmorToughness(StaticGlobalVariables.getMaxArmorToughness());
            variablesHolder.setArmorAlpha(StaticGlobalVariables.getArmorAlpha());
            variablesHolder.setWeaponAlpha(StaticGlobalVariables.getWeaponAlpha());
            variablesHolder.setCollarAlpha(StaticGlobalVariables.getCollarAlpha());
            variablesHolder.setSaddleAlpha(StaticGlobalVariables.getSaddleAlpha());
            variablesHolder.setExtraChocoboEffects(StaticGlobalVariables.getExtraChocoboEffects());
            variablesHolder.setExtraChocoboResourcesOnHit(StaticGlobalVariables.getExtraChocoboResourcesOnHit());
            variablesHolder.setExtraChocoboResourcesOnKill(StaticGlobalVariables.getExtraChocoboResourcesOnKill());
            variablesHolder.setShiftHitBypass(StaticGlobalVariables.getShiftHitBypass());
            variablesHolder.setOwnChocoboHittable(StaticGlobalVariables.getOwnChocoboHittable());
            variablesHolder.setTamedChocoboHittable(StaticGlobalVariables.getTamedChocoboHittable());
        }
    }
    private boolean globalVariablesChanged(globalVariablesHolder currentVariables, boolean isWorld) {
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
            return currentVariables.isTamedChocoboHittable() != StaticGlobalVariables.getTamedChocoboHittable();
        }
    }
    private void globalChocoboVariableNullCheck() {
        if (StaticGlobalVariables.getStamina() == null) { StaticGlobalVariables.setStamina(dSTAMINA.getDefault()); }
        if (StaticGlobalVariables.getSpeed() == null) { StaticGlobalVariables.setSpeed(dSPEED.getDefault()); }
        if (StaticGlobalVariables.getHealth() == null) { StaticGlobalVariables.setHealth(dHEALTH.getDefault()); }
        if (StaticGlobalVariables.getArmor() == null) { StaticGlobalVariables.setArmor(dARMOR.getDefault()); }
        if (StaticGlobalVariables.getArmorTough() == null) { StaticGlobalVariables.setArmorTough(dARMOR_TOUGH.getDefault()); }
        if (StaticGlobalVariables.getAttack() == null) { StaticGlobalVariables.setAttack(dATTACK.getDefault()); }
        if (StaticGlobalVariables.getWeaponModifier() == null) { StaticGlobalVariables.setWeaponModifier(dWEAPON_MOD.getDefault()); }
        if (StaticGlobalVariables.getHealAmount() == null) { StaticGlobalVariables.setHealAmount(dHEAL_AMOUNT.getDefault()); }
        if (StaticGlobalVariables.getEggHatchTimeTicks() == null) { StaticGlobalVariables.setEggHatchTimeTicks(dEGG_HATCH.getDefault()); }
        if (StaticGlobalVariables.getMaxHealth() == null) { StaticGlobalVariables.setMaxHealth(dMAX_HEALTH.getDefault()); }
        if (StaticGlobalVariables.getMaxSpeed() == null) { StaticGlobalVariables.setMaxSpeed(dMAX_SPEED.getDefault()); }
        if (StaticGlobalVariables.getStaminaRegen() == null) { StaticGlobalVariables.setStaminaRegen(dSTAMINA_REGEN.getDefault()); }
        if (StaticGlobalVariables.getTame() == null) { StaticGlobalVariables.setTame(dTAME.getDefault()); }
        if (StaticGlobalVariables.getStaminaCost() == null) { StaticGlobalVariables.setStaminaCost(dSTAMINA_SPRINT.getDefault()); }
        if (StaticGlobalVariables.getStaminaGlide() == null) { StaticGlobalVariables.setStaminaGlide(dSTAMINA_GLIDE.getDefault()); }
        if (StaticGlobalVariables.getStaminaJump() == null) { StaticGlobalVariables.setStaminaJump(dSTAMINA_JUMP.getDefault()); }
        if (StaticGlobalVariables.getPossLoss() == null) { StaticGlobalVariables.setPossLoss(dPOS_LOSS.getDefault()); }
        if (StaticGlobalVariables.getPossGain() == null) { StaticGlobalVariables.setPossGain(dPOS_GAIN.getDefault()); }
        if (StaticGlobalVariables.getMaxStamina() == null) { StaticGlobalVariables.setMaxStamina(dMAX_STAMINA.getDefault()); }
        if (StaticGlobalVariables.getMaxStrength() == null) { StaticGlobalVariables.setMaxStrength(dMAX_STRENGTH.getDefault()); }
        if (StaticGlobalVariables.getMaxArmor() == null) { StaticGlobalVariables.setMaxArmor(dMAX_ARMOR.getDefault()); }
        if (StaticGlobalVariables.getMaxArmorToughness() == null) { StaticGlobalVariables.setMaxArmorToughness(dMAX_ARMOR_TOUGH.getDefault()); }
        if (StaticGlobalVariables.getArmorAlpha() == null) { StaticGlobalVariables.setArmorAlpha(dARMOR_ALPHA.getDefault()); }
        if (StaticGlobalVariables.getWeaponAlpha() == null) { StaticGlobalVariables.setWeaponAlpha(dWEAPON_ALPHA.getDefault()); }
        if (StaticGlobalVariables.getCollarAlpha() == null) { StaticGlobalVariables.setCollarAlpha(dCOLLAR_ALPHA.getDefault()); }
        if (StaticGlobalVariables.getSaddleAlpha() == null) { StaticGlobalVariables.setCollarAlpha(dCOLLAR_ALPHA.getDefault()); }
        if (StaticGlobalVariables.getSaddleAlpha() == null) { StaticGlobalVariables.setSaddleAlpha(dSADDLE_ALPHA.getDefault()); }
        if (StaticGlobalVariables.getExtraChocoboEffects() == null) { StaticGlobalVariables.setExtraChocoboEffects(dExtraChocoboEffects); }
        if (StaticGlobalVariables.getExtraChocoboResourcesOnHit() == null) { StaticGlobalVariables.setExtraChocoboResourcesOnHit(dExtraChocoboResourcesOnHit); }
        if (StaticGlobalVariables.getExtraChocoboResourcesOnKill() == null) { StaticGlobalVariables.setExtraChocoboResourcesOnKill(dExtraChocoboResourcesOnKill); }
        if (StaticGlobalVariables.getShiftHitBypass() == null) { StaticGlobalVariables.setShiftHitBypass(dShiftHitBypass); }
        if (StaticGlobalVariables.getOwnChocoboHittable() == null) { StaticGlobalVariables.setOwnChocoboHittable(dOwnChocoboHittable); }
        if (StaticGlobalVariables.getTamedChocoboHittable() == null) { StaticGlobalVariables.setTamedChocoboHittable(dTamedChocoboHittable); }
    }
    private void globalWorldVariableNullCheck() {
        if (StaticGlobalVariables.getChocoboMinPack() == null) { StaticGlobalVariables.setChocoboMinPack(dCHOCOBO_PACK_MIN.getDefault()); }
        if (StaticGlobalVariables.getChocoboMaxPack() == null) { StaticGlobalVariables.setChocoboMaxPack(dCHOCOBO_PACK_MAX.getDefault()); }
        if (StaticGlobalVariables.getOverWorldSpawnWeight() == null) { StaticGlobalVariables.setOverWorldSpawnWeight(dOVERWORLD_SPAWN_WEIGHT.getDefault()); }
        if (StaticGlobalVariables.getNetherSpawnWeight() == null) { StaticGlobalVariables.setNetherSpawnWeight(dNETHER_SPAWN_WEIGHT.getDefault()); }
        if (StaticGlobalVariables.getEndSpawnWeight() == null) { StaticGlobalVariables.setNetherSpawnWeight(dEND_SPAWN_WEIGHT.getDefault()); }
        if (StaticGlobalVariables.getGysahlGreenPatchSize() == null) { StaticGlobalVariables.setGysahlGreenPatchSize(dGYSAHL_GREEN_PATCH_SIZE.getDefault()); }
        if (StaticGlobalVariables.getGysahlGreenSpawnChance() == null) { StaticGlobalVariables.setGysahlGreenSpawnChance(dGYSAHL_GREEN_SPAWN_CHANCE.getDefault()); }
        if (StaticGlobalVariables.getCanSpawn() == null) { StaticGlobalVariables.setCanSpawn(dCanSpawn); }
        if (StaticGlobalVariables.getOverworldSpawn() == null) { StaticGlobalVariables.setOverworldSpawn(dOverworldSpawn); }
        if (StaticGlobalVariables.getNetherSpawn() == null) { StaticGlobalVariables.setNetherSpawn(dNetherSpawn); }
        if (StaticGlobalVariables.getEndSpawn() == null) { StaticGlobalVariables.setEndSpawn(dEndSpawn); }
    }
}