package com.dephoegon.delchoco.common.commands;

import com.dephoegon.delchoco.DelChoco;
import com.dephoegon.delchoco.common.entities.Chocobo;
import com.dephoegon.delchoco.common.init.ModAttributes;
import com.dephoegon.delchoco.common.teamColors;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static com.dephoegon.delchoco.DelChoco.DELCHOCO_ID;

@SuppressWarnings("SameReturnValue")
public class chocoboTeams {
    public static void commands(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> delChocobo = CommandManager.literal("DelChoco");
        delChocobo.then(CommandManager.literal("TeamInitialize").requires((commandSource) -> commandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("refresh").executes(chocoboTeams::refreshTeams))
                .then(CommandManager.literal("create").executes(chocoboTeams::createTeams)));
        delChocobo.then(CommandManager.literal("TeamSettings").then(CommandManager.literal("TeamFriendlyFire")
                .then(CommandManager.literal("True").executes((command) -> setFriendlyFire(command, true)))
                .then(CommandManager.literal("False").executes((command)-> setFriendlyFire(command, false)))));
        Arrays.stream(teamColors.values()).map(delCo -> CommandManager.literal("Player")
                .then(CommandManager.literal("JoinTeam").then(CommandManager.literal(delCo.getColorName())
                .executes((command) -> join(command, delCo.getTeamName()))))).forEach(delChocobo::then);
        delChocobo.then(CommandManager.literal("Player").then(CommandManager.literal("LeaveTeam").executes(chocoboTeams::leave)));
        delChocobo.then(CommandManager.literal("Chocobo").then(CommandManager.literal("RiddenChocoboStats").executes(chocoboTeams::sendList)));
        dispatcher.register(delChocobo);
    }

    private static int setFriendlyFire(@NotNull CommandContext<ServerCommandSource> commandSourceStack, boolean fire) throws CommandSyntaxException {
        DelChoco.LOGGER.info("set teams Friendly fire");
        ServerPlayerEntity player = commandSourceStack.getSource().getPlayer();
        Team playerTeam = player.getScoreboard().getPlayerTeam(player.getName().getString());
        if (playerTeam != null) {
            playerTeam.setFriendlyFireAllowed(fire);
            commandSourceStack.getSource().sendFeedback(Text.literal("Player "+ player.getName().getString()+ " set friendly fire to " + fire + " for " + playerTeam.getName()), true);
        } else { commandSourceStack.getSource().sendFeedback(Text.literal("Player "+ player.getName().getString()+ " Must be on a team to set Friendly fire for their team"), true); }
        return 1;
    }
    private static int join(@NotNull CommandContext<ServerCommandSource> commandSourceStack, String teamName) throws CommandSyntaxException {
        Scoreboard scoreboard = commandSourceStack.getSource().getWorld().getScoreboard();
        addTeams(scoreboard, teamName, commandSourceStack);
        ServerPlayerEntity player = commandSourceStack.getSource().getPlayer();
        Team playerTeam = scoreboard.getTeam(teamName);
        assert playerTeam != null;
        scoreboard.addPlayerToTeam(player.getName().getString(), playerTeam);
        commandSourceStack.getSource().sendFeedback(Text.literal("Player " + player.getName().getString() + " added to " + teamName + " team."), true);
        return 1;
    }
    private static int leave(@NotNull CommandContext<ServerCommandSource> commandSourceStack) throws CommandSyntaxException {
        Scoreboard scoreboard = commandSourceStack.getSource().getServer().getScoreboard();
        ServerPlayerEntity player = commandSourceStack.getSource().getPlayer();
        scoreboard.removePlayerFromTeam(player.getName().getString(), player.getScoreboard().getPlayerTeam(player.getName().getString()));
        commandSourceStack.getSource().sendFeedback(Text.literal("Player " + player.getName().getString() + " left their team."), true);
        return 1;
    }
    private static int refreshTeams(@NotNull CommandContext<ServerCommandSource> commandSourceStack) {
        Scoreboard scoreboard = commandSourceStack.getSource().getServer().getScoreboard();
        for (teamColors colors: teamColors.values()) {
            removeTeams(scoreboard, colors.getTeamName(), commandSourceStack);
            addTeams(scoreboard, colors.getTeamName(), commandSourceStack);
        }
        return 1;
    }
    private static void removeTeams(@NotNull Scoreboard scoreboard, String tName, CommandContext<ServerCommandSource> commandSourceStack) {
        Team chocoboTeam = scoreboard.getTeam(tName);
        if (chocoboTeam != null) {
            scoreboard.removeTeam(chocoboTeam);
            commandSourceStack.getSource().sendFeedback(Text.literal("Removed " + tName + "team"), true);
        }
    }
    private static void addTeams(@NotNull Scoreboard scoreboard, String tName, CommandContext<ServerCommandSource> commandSourceStack) {
        Team chocoboTeam = scoreboard.getPlayerTeam(tName);
        if (chocoboTeam == null) {
            scoreboard.addTeam(tName);
            commandSourceStack.getSource().sendFeedback(Text.literal("Added " + tName + "team"), true);
        }
    }
    private static int createTeams(@NotNull CommandContext<ServerCommandSource> commandSourceStack) {
        Scoreboard scoreboard = commandSourceStack.getSource().getServer().getScoreboard();
        Arrays.stream(teamColors.values()).forEach(colors -> addTeams(scoreboard, colors.getTeamName(), commandSourceStack));
        return 1;
    }
    @SuppressWarnings("SameReturnValue")
    private static int sendList(@NotNull CommandContext<ServerCommandSource> commandContext) {
        ServerCommandSource source = commandContext.getSource();
        Entity commandEntity = source.getEntity();
        if(commandEntity instanceof PlayerEntity player) {
            Entity mount = player.getVehicle();
            if (!(mount instanceof Chocobo chocobo)) {
                source.sendFeedback(Text.translatable("command." + DELCHOCO_ID + ".chocobo.not_riding_chocobo"), false);
                return 0;
            } else {
                source.sendFeedback(getText("get_health", chocobo, EntityAttributes.GENERIC_MAX_HEALTH), false);
                source.sendFeedback(getText("get_resistance", chocobo, EntityAttributes.GENERIC_ARMOR), false);
                source.sendFeedback(getText("get_speed", chocobo, EntityAttributes.GENERIC_MOVEMENT_SPEED), false);
                source.sendFeedback(getText("get_stamina", chocobo, ModAttributes.CHOCOBO_MAX_STAMINA), false);
                source.sendFeedback(getText("get_attack", chocobo, EntityAttributes.GENERIC_ATTACK_DAMAGE), false);
                source.sendFeedback(getText(chocobo.getGenerationString()), false);
            }
        }
        return 0;
    }
    @Contract("_, _, _ -> new")
    private static @NotNull Text getText(String key, @NotNull Chocobo chocobo, EntityAttribute attribute) { return Text.translatable("command." + DELCHOCO_ID + ".chocobo." + key, Objects.requireNonNull(chocobo.getAttributeInstance(attribute)).getValue()); }
    @Contract(value = "_ -> new", pure = true)
    private static @NotNull Text getText(String value) { return Text.translatable("command." + DELCHOCO_ID + ".chocobo." + "get_generation", value); }
}