

package net.runelite.client.plugins.pvptools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import org.apache.commons.lang3.ArrayUtils;

import static net.runelite.client.plugins.pvptools.PvpUtil.isDeadmanWorld;

@Singleton
public class PlayerCountOverlay extends Overlay
{
	private static final int[] CLAN_WARS_REGIONS = {9520, 13135, 13134, 13133, 13131, 13130, 13387, 13386};

	private final PvpToolsPlugin plugin;
	private final PvpToolsConfig config;
	private final Client client;

	@Inject
	public PlayerCountOverlay(final PvpToolsPlugin plugin, final PvpToolsConfig config, final Client client)
	{
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGHEST);
		setPosition(OverlayPosition.TOP_LEFT);
		setPreferredPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.countPlayers() &&
				(client.getVarbitValue(Varbits.IN_WILDERNESS) == 1 || WorldType.isPvpWorld(client.getWorldType())
						|| ArrayUtils.contains(CLAN_WARS_REGIONS, client.getMapRegions()[0]) ||
						isDeadmanWorld(client.getWorldType())))
		{
			// Make this stop showing up when its not relevant
			TableComponent tableComponent = new TableComponent();
			TableElement[] firstRowElements = {
					TableElement.builder().content("Friendly").color(Color.GREEN).build(),
					TableElement.builder().content(String.valueOf(plugin.getFriendlyPlayerCount())).build()};
			TableRow firstRow = TableRow.builder().elements(Arrays.asList(firstRowElements)).build();
			TableElement[] secondRowElements = {
					TableElement.builder().content("Enemy").color(Color.RED).build(),
					TableElement.builder().content(String.valueOf(plugin.getEnemyPlayerCount())).build()};
			TableRow secondRow = TableRow.builder().elements(Arrays.asList(secondRowElements)).build();
			tableComponent.addRows(firstRow, secondRow);
			return tableComponent.render(graphics);
		}
		return null;
	}
}
