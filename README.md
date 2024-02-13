# Runelite Plugin Pvp Tutorial
Guide on this osrs plugin here: https://www.slyautomation.com/blog/osrs-plugin-runelite-development-pvp-helper-part-1/

PvP (Player versus Player) plugin using the RuneLite API. The development is presented in a tutorial format, and this summary covers the key points discussed in Part 1:

## Introduction to Runelite PvP Plugin:

A new Runelite PvP plugin is introduced, promising an enhanced PvP experience in OSRS.
The tutorial encourages users to explore the RuneLite API documentation to understand the classes, interfaces, annotations, enums, and packages constituting the API.
RuneLite API Documentation Guide:

Developers are advised to refer to the official RuneLite API documentation for comprehensive insights.
Guidance is provided on exploring package hierarchies, understanding class and interface hierarchies, leveraging annotations and enums, and navigating the interface hierarchy.
Code Development for PvP Plugin:

The tutorial offers a step-by-step guide on coding a PvP plugin using the RuneLite API.
A GitHub link to the source code for the PvP plugin development is provided.

## PvpConfig:

A configuration interface (PvpToolsConfig) is defined to allow users to customize various aspects of the PvP Tools plugin behavior.
Configuration items include boolean and color options, providing control over features like counting players, hiding certain elements, and enabling a risk calculator panel.

## PlayerCountOverlay:

A RuneLite overlay named PlayerCountOverlay is introduced, displaying a table of player counts categorized as "Friendly" and "Enemy" in specific game regions.
The overlay is conditionally rendered based on the player's location and configuration settings.

## ItemDefinition Interface:

An interface named ItemDefinition is defined to represent the properties of in-game items within the context of the OSRS plugin.
The interface provides methods to retrieve and modify various item properties.

## PvpUtil Utility Class:

The PvpUtil utility class contains methods related to PvP activities, such as checking if a world is a Deadman world, calculating the wilderness level, determining if a player is attackable, and calculating the risk or wealth of a player.

## Calculate Risk Function:

The calculateRisk function calculates the risk (wealth) of the local player based on equipped and inventory items, considering tradeability and non-zero values.
The total wealth is accumulated, and the result is formatted as a decimal string.
The text provides links to the GitHub repository containing the source code for the PvP plugin development, and it hints at upcoming parts of the tutorial covering the panel and adding auto-spec and auto-eating functionality.
