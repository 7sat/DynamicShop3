package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.StringUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_ITEM_INFO;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class ItemInfo extends DSCMD
{

    public ItemInfo()
    {
        inGameUseOnly = true;
        permission = P_ADMIN_ITEM_INFO;
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "iteminfo"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds iteminfo");
        player.sendMessage(" - " + t(player, "HELP.ITEMINFO_USAGE"));
        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if (!CheckValid(args, sender))
            return;
        Player p = (Player) sender;
        Material material = p.getInventory().getItemInMainHand().getType();

        if (material == Material.AIR)
        {
            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.ITEMINFO_HAND_EMPTY"));
        } else
        {
            String result = StringUtil.getShortenedNameSign(material.name());
            p.sendMessage(t(p, "HELP.ITEMINFO_REALNAME").replace("{item_realname}", material.name().toUpperCase()));
            p.sendMessage(t(p, "HELP.ITEMINFO_SIGN_NAME").replace("{item_signname}", result));
        }
    }
}
