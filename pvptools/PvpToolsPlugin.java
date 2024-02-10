
package net.runelite.client.plugins.pvptools;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;

import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.plugins.pvptools.PvpToolsPanel.htmlLabel;
import static net.runelite.client.plugins.pvptools.PvpUtil.isAttackable;

import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.*;
import org.apache.commons.lang3.ArrayUtils;


@PluginDescriptor(
		name = "PvP Tools",
		enabledByDefault = false,
		description = "Enable the PvP Tools panel",
		tags = {"panel", "pvp", "pk"}
)
public class PvpToolsPlugin extends Plugin
{
	public boolean hideNPCs2D;
	public boolean hideNPCs;
	public boolean hideOthers;
	public boolean hideOthers2D;
	public boolean hideFriends;
	public boolean hideFriendsChatMembers;
	public boolean hideClanMembers;
	public boolean hideIgnoredPlayers;
	public boolean hideLocalPlayer;
	public boolean hideLocalPlayer2D;
	public boolean hideAttackers;
	public boolean hideProjectiles;

	public boolean hideEnemy;
	@Inject
	PlayerCountOverlay playerCountOverlay;

	private PvpToolsPanel panel;

	private NavigationButton navButton;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	public boolean hideAll;
	public boolean loaded;

	@Inject
	private Hooks hooks;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	private final PvpToolsPlugin uhPvpToolsPlugin = this;

	private static final CopyOnWriteArrayList<Player> clanMembers = new CopyOnWriteArrayList<>();

	/**
	 * ActionListener for the missing cc members and refresh buttons
	 */


	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private KeyManager keyManager;

	@Inject
	private PvpToolsConfig config;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof Player)
		{
			Player player = (Player) renderable;
			Player local = client.getLocalPlayer();

			if (player.getName() == null)
			{
				// player.isFriend() and player.isFriendsChatMember() npe when the player has a null name
				return true;
			}

			// Allow hiding local self in pvp, which is an established meta.
			// It is more advantageous than renderself due to being able to still render local player 2d
			if (player == local)
			{
				return !(drawingUI ? hideLocalPlayer2D : hideLocalPlayer);
			}

			if (hideAttackers && player.getInteracting() == local)
			{
				return false; // hide
			}
			if (isAttackable(client, player))
			{
				//final Polygon poly = Perspective.getCanvasTileAreaPoly(client, player.getLocalLocation(), 1);
				//renderPoly((Graphics2D) player, config.highlightColor(), config.fillColor(), poly);
				return !hideEnemy;
			}

			if (player.isFriend())
			{
				return !hideFriends;
			}
			if (player.isFriendsChatMember())
			{
				return !hideFriendsChatMembers;
			}
			if (player.isClanMember())
			{
				return !hideClanMembers;
			}
			if (client.getIgnoreContainer().findByName(player.getName()) != null)
			{
				return !hideIgnoredPlayers;
			}

			return !(drawingUI ? hideOthers2D : hideOthers);
		}
		else if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;

			if (npc.getInteracting() == client.getLocalPlayer())
			{
				boolean b = hideAttackers;
				// This allows hiding 2d for all npcs, including attackers.
				if (hideNPCs2D || hideNPCs)
				{
					b &= drawingUI ? hideNPCs2D : hideNPCs;
				}
				return !b;
			}

			return !(drawingUI ? hideNPCs2D : hideNPCs);
		}
		else if (renderable instanceof Projectile)
		{
			return !hideProjectiles;
		}
		else if (renderable instanceof GraphicsObject)
		{

			switch (((GraphicsObject) renderable).getId())
			{
				case GraphicID.MELEE_NYLO_DEATH:
				case GraphicID.RANGE_NYLO_DEATH:
				case GraphicID.MAGE_NYLO_DEATH:
				case GraphicID.MELEE_NYLO_EXPLOSION:
				case GraphicID.RANGE_NYLO_EXPLOSION:
				case GraphicID.MAGE_NYLO_EXPLOSION:
					return false;
				default:
					return true;
			}
		}

		return true;
	}


	private void renderPoly(Graphics2D graphics, Color borderColor, Color fillColor, Shape polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(borderColor);
			graphics.setStroke(new BasicStroke((float) 1));
			graphics.draw(polygon);
			graphics.setColor(fillColor);
			graphics.fill(polygon);
		}
	}
	private List<String> getMissingMembers()
	{
		CopyOnWriteArrayList<Player> ccMembers = clanMembers;
		ArrayList<String> missingMembers = new ArrayList<>();
		if (client.getFriendsChatManager() != null)
		{
			for (FriendsChatMember friendsChatMember : client.getFriendsChatManager().getMembers())
			{
				if (!Objects.isNull(friendsChatMember))
				{
					List<String> arrayList = ccMembers.stream().map(player -> Text.removeTags(Text.standardize(player.getName()))).collect(Collectors.toList());
					if (!arrayList.contains(Text.removeTags(Text.standardize(friendsChatMember.getName()))) && !missingMembers.contains(friendsChatMember.getName()))
					{
						missingMembers.add("[W" + friendsChatMember.getWorld() + "] - " + friendsChatMember.getName());
					}
				}
			}
		}

		return missingMembers;
	}

	private List<String> getCurrentMembers()
	{
		CopyOnWriteArrayList<Player> ccMembers = clanMembers;
		ArrayList<String> currentMembers = new ArrayList<>();
		if (client.getFriendsChatManager() != null)
		{
			for (FriendsChatMember friendsChatMember : client.getFriendsChatManager().getMembers())
			{
				if (!Objects.isNull(friendsChatMember))
				{
					List<String> arrayList = ccMembers.stream().map(player -> Text.removeTags(Text.standardize(player.getName()))).collect(Collectors.toList());
					if (arrayList.contains(Text.removeTags(Text.standardize(friendsChatMember.getName()))) && !currentMembers.contains(friendsChatMember.getName()))
					{
						currentMembers.add(friendsChatMember.getName());
					}
				}
			}
		}

		return currentMembers;
	}


	@Provides
	PvpToolsConfig config(ConfigManager configManager)
	{
		return configManager.getConfig(PvpToolsConfig.class);
	}
	private int[] overheadCount = new int[]{0, 0, 0};

	@Getter
	private int enemyPlayerCount = 0;
	@Getter
	private int friendlyPlayerCount = 0;
	@Override
	protected void startUp()
	{
		updateConfig();
		overlayManager.add(playerCountOverlay);
		hooks.registerRenderableDrawListener(drawListener);

		//keyManager.registerKeyListener(renderselfHotkeyListener);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "skull.png");


		panel = new PvpToolsPanel();
		panel.init();

		navButton = NavigationButton.builder()
				.tooltip("PvP Tools")
				.icon(icon)
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

	}
	@Subscribe
	public void onGameTick(GameTick game)
	{
		updateConfig();
	}
	@Override
	protected void shutDown()
	{
		hooks.unregisterRenderableDrawListener(drawListener);
		overlayManager.remove(playerCountOverlay);
		//keyManager.unregisterKeyListener(renderselfHotkeyListener);
		clientToolbar.removeNavigation(navButton);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			//resetCastOptions();
		}

		loaded = false;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!"pvptools".equals(configChanged.getGroup()))
		{
			updateConfig();
		}

		switch (configChanged.getKey())
		{
			case "countPlayers":
				if (config.countPlayers())
				{
					updatePlayers();
				}
				if (!config.countPlayers())
				{
					panel.disablePlayerCount();
				}
				break;
			case "countOverHeads":
				if (config.countOverHeads())
				{
					countOverHeads();
				}
				if (!config.countOverHeads())
				{
					panel.disablePrayerCount();
				}
				break;
			case "riskCalculator":
				if (config.riskCalculatorEnabled())
				{
					getCarriedWealth();
				}
				if (!config.riskCalculatorEnabled())
				{
					panel.disableRiskCalculator();
				}
				break;
		}
	}

	private void updateConfig()
	{
		hideOthers = config.hideOthers();
		hideOthers2D = config.hideOthers2D();

		hideFriends = config.hideFriends();
		hideFriendsChatMembers = config.hideFriendsChatMembers();
		hideClanMembers = config.hideClanChatMembers();
		hideIgnoredPlayers = config.hideIgnores();
		hideEnemy = config.hideEnemy();
		hideLocalPlayer = config.hideLocalPlayer();
		hideLocalPlayer2D = config.hideLocalPlayer2D();

		hideNPCs = config.hideNPCs();
		hideNPCs2D = config.hideNPCs2D();

		hideAttackers = config.hideAttackers();

		hideProjectiles = config.hideProjectiles();
	}

	@Subscribe
	private void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer().equals(client.getItemContainer(InventoryID.INVENTORY)) &&
				config.riskCalculatorEnabled())
		{
			getCarriedWealth();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState().equals(GameState.LOGGED_IN))
		{
			if (config.riskCalculatorEnabled())
			{
				getCarriedWealth();
			}
			if (config.countPlayers())
			{
				updatePlayers();
			}
			if (!loaded)
			{
				//setCastOptions();
			}
		}
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event)
	{
		if (config.countPlayers() && isAttackable(client, event.getPlayer()))
		{
			updatePlayers();
		}
		if (config.countOverHeads())
		{
			countOverHeads();
		}
	}

	@Subscribe
	private void onPlayerDespawned(PlayerDespawned event)
	{
		if (config.countPlayers() && isAttackable(client, event.getPlayer()))
		{
			updatePlayers();
		}
		if (config.countOverHeads())
		{
			countOverHeads();
		}
	}

	/**
	 * Updates the PvP Tools panel with the numbers for enemy protection prayers
	 */
	private void updatePrayerNumbers()
	{
		panel.numMageJLabel.setText(htmlLabel("Enemies Praying Mage: ", String.valueOf(overheadCount[0])));
		panel.numRangeJLabel.setText(htmlLabel("Enemies Praying Range: ", String.valueOf(overheadCount[1])));
		panel.numMeleeJLabel.setText(htmlLabel("Enemies Praying Melee: ", String.valueOf(overheadCount[2])));
		panel.numMageJLabel.repaint();
		panel.numRangeJLabel.repaint();
		panel.numMeleeJLabel.repaint();
	}

	private void updatePlayers()
	{
		friendlyPlayerCount = 0;
		enemyPlayerCount = 0;
		if (config.countPlayers())
		{
			for (Player p : client.getPlayers())
			{
				if (Objects.nonNull(p))
				{
					if (p.equals(client.getLocalPlayer()))
					{
						continue;
					}
					if (isAttackable(client, p))
					{
						if (p.isFriendsChatMember())
						{
							friendlyPlayerCount++;
						}
						else
						{
							enemyPlayerCount++;
						}
					}
				}
			}

			panel.numOther.setText(htmlLabel("Other Player Count: ", String.valueOf(enemyPlayerCount)));
			panel.numCC.setText(htmlLabel("Friendly Player Count: ", String.valueOf(friendlyPlayerCount)));
			panel.numCC.repaint();
			panel.numOther.repaint();
		}
	}

	private void countOverHeads()
	{
		overheadCount = new int[]{0, 0, 0};
		for (Player p : client.getPlayers())
		{
			if (Objects.nonNull(p) && isAttackable(client, p) &&
					!p.isFriendsChatMember() && !(p.getOverheadIcon() == null))
			{
				switch (p.getOverheadIcon())
				{
					case MAGIC:
						overheadCount[0]++;
						break;
					case RANGED:
						overheadCount[1]++;
						break;
					case MELEE:
						overheadCount[2]++;
						break;
				}
			}
		}
		updatePrayerNumbers();
	}

	/**
	 * Calculates the player's risk based on Item Price of all items in their inventory and equipment
	 */
	private void getCarriedWealth()
	{
		if (!config.riskCalculatorEnabled())
		{
			return;
		}

		final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		final Player player = client.getLocalPlayer();

		if (equipment == null || equipment.getItems() == null ||
				inventory == null || inventory.getItems() == null ||
				player == null)
		{
			return;
		}

		final Item[] items = ArrayUtils.addAll(equipment.getItems(), inventory.getItems());
		final TreeMap<Integer, Item> priceMap = new TreeMap<>(Comparator.comparingInt(Integer::intValue));
		int wealth = 0;
		for (Item i : items)
		{
			int value = (itemManager.getItemPrice(i.getId()) * i.getQuantity());
			final ItemComposition itemComposition = itemManager.getItemComposition(i.getId());

			if (!itemComposition.isTradeable() && value == 0)
			{
				value = itemComposition.getPrice() * i.getQuantity();
				priceMap.put(value, i);
			}
			else
			{
				value = itemManager.getItemPrice(i.getId()) * i.getQuantity();
				if (i.getId() > 0 && value > 0)
				{
					priceMap.put(value, i);
				}
			}
			wealth += value;
		}

		panel.totalRiskLabel.setText(htmlLabel("Total risk: ", QuantityFormatter.quantityToRSDecimalStack(wealth)));
		panel.totalRiskLabel.repaint();

		int itemLimit = 0;
		if (player.getSkullIcon() != null && player.getSkullIcon() == SkullIcon.SKULL)
		{
			itemLimit = 1;
		}
		if (player.getSkullIcon() == null)
		{
			itemLimit = 4;
		}

		AsyncBufferedImage itemImage = null;

		NavigableMap<Integer, Item> descendingMap = priceMap.descendingMap();

		for (int i = 0; i < itemLimit; i++)
		{
			if (i == 0)
			{
				if (!descendingMap.isEmpty())
				{
					itemImage = itemManager.getImage(descendingMap.pollFirstEntry().getValue().getId());
				}
			}
			else
			{
				if (!descendingMap.isEmpty())
				{
					itemManager.getItemComposition(priceMap.descendingMap().pollFirstEntry().getValue().getId())
							.getName();
				}
			}
		}
		panel.riskProtectingItem.setText(htmlLabel("Risk Protecting Item: ",
				QuantityFormatter.quantityToRSDecimalStack(descendingMap.keySet().stream().mapToInt(Integer::intValue).sum())));
		panel.riskProtectingItem.repaint();

		panel.biggestItemLabel.setText("Most Valuable Item: ");
		if (itemImage != null)
		{
			itemImage.addTo(panel.biggestItemLabel);
		}
		panel.biggestItemLabel.repaint();
	}





}