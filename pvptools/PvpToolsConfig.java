
package net.runelite.client.plugins.pvptools;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("pvptools")
public interface PvpToolsConfig extends Config
{
	@ConfigItem(
			keyName = "countPlayers",
			name = "Count Players",
			description = "When in PvP zones, counts the attackable players in and not in player's CC",
			position = 0
	)
	default boolean countPlayers()
	{
		return true;
	}

	@ConfigItem(
			keyName = "countOverHeads",
			name = "Count Enemy Overheads",
			description = "Counts the number of each protection prayer attackable targets not in your CC are currently using",
			position = 1
	)
	default boolean countOverHeads()
	{
		return true;
	}


	@ConfigItem(
			position = 1,
			keyName = "hidePlayers",
			name = "Hide Others",
			description = "Configures whether or not other players are hidden"
	)
	default boolean hideOthers()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "hidePlayers2D",
			name = "Hide Others 2D",
			description = "Configures whether or not other players 2D elements are hidden"
	)
	default boolean hideOthers2D()
	{
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "hideFriends",
			name = "Hide Friends",
			description = "Configures whether or not friends are hidden"
	)
	default boolean hideFriends()
	{
		return false;
	}

	@ConfigItem(
			position = 4,
			keyName = "hideClanMates", // is actually friends chat
			name = "Hide Friends Chat members",
			description = "Configures whether or not friends chat members are hidden"
	)
	default boolean hideFriendsChatMembers()
	{
		return false;
	}

	@ConfigItem(
			position = 5,
			keyName = "hideClanChatMembers",
			name = "Hide Clan Chat members",
			description = "Configures whether or not clan chat members are hidden"
	)
	default boolean hideClanChatMembers()
	{
		return false;
	}

	@ConfigItem(
			position = 6,
			keyName = "hideIgnores",
			name = "Hide Ignores",
			description = "Configures whether or not ignored players are hidden"
	)
	default boolean hideIgnores()
	{
		return false;
	}

	@ConfigItem(
			position = 7,
			keyName = "hideLocalPlayer",
			name = "Hide Local Player",
			description = "Configures whether or not the local player is hidden"
	)
	default boolean hideLocalPlayer()
	{
		return false;
	}

	@ConfigItem(
			position = 8,
			keyName = "hideLocalPlayer2D",
			name = "Hide Local Player 2D",
			description = "Configures whether or not the local player's 2D elements are hidden"
	)
	default boolean hideLocalPlayer2D()
	{
		return false;
	}

	@ConfigItem(
			position = 9,
			keyName = "hideNPCs",
			name = "Hide NPCs",
			description = "Configures whether or not NPCs are hidden"
	)
	default boolean hideNPCs()
	{
		return false;
	}

	@ConfigItem(
			position = 10,
			keyName = "hideNPCs2D",
			name = "Hide NPCs 2D",
			description = "Configures whether or not NPCs 2D elements are hidden"
	)
	default boolean hideNPCs2D()
	{
		return false;
	}

	@ConfigItem(
			position = 11,
			keyName = "hidePets",
			name = "Hide Other Players' Pets",
			description = "Configures whether or not other player pets are hidden"
	)
	default boolean hidePets()
	{
		return false;
	}

	@ConfigItem(
			position = 12,
			keyName = "hideAttackers",
			name = "Hide Attackers",
			description = "Configures whether or not NPCs/players attacking you are hidden"
	)
	default boolean hideAttackers()
	{
		return false;
	}

	@ConfigItem(
			position = 13,
			keyName = "hideProjectiles",
			name = "Hide Projectiles",
			description = "Configures whether or not projectiles are hidden"
	)
	default boolean hideProjectiles()
	{
		return false;
	}


	@ConfigItem(
			position = 14,
			keyName = "hideEnemies",
			name = "Hide Enemies",
			description = "Configures whether or not Enemies attackable are hidden"
	)
	default boolean hideEnemy()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			position = 15,
			keyName = "npcColor",
			name = "Highlight Color",
			description = "Color of the NPC highlight border, menu, and text"
	)
	default Color highlightColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
			position = 16,
			keyName = "fillColor",
			name = "Fill Color",
			description = "Color of the NPC highlight fill"
	)
	default Color fillColor()
	{
		return new Color(0, 255, 255, 20);
	}


	@ConfigItem(
			keyName = "riskCalculator",
			name = "Risk Calculator",
			description = "Enables a panel in the PvP Tools Panel that shows the players current risk",
			position = 8
	)
	default boolean riskCalculatorEnabled()
	{
		return true;
	}
}



