package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class EditAll extends DSCMD
{
    public EditAll()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(6);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "editall"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... editall <purchaseValue | salesValue | valueMin | valueMax | median | stock | max stock> <= | + | - | * | /> <amount>");
        player.sendMessage(" - " + t(player, "HELP.EDIT_ALL"));
        player.sendMessage(" - " + t(player, "HELP.EDIT_ALL_2"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        String mod;
        float value = 0;
        String dataType;

        try
        {
            dataType = args[3];
            if (!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("purchaseValue") && !dataType.equals("salesValue") && !dataType.equals("valueMin") && !dataType.equals("valueMax") && !dataType.equals("maxStock"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            if (dataType.equals("purchaseValue"))
                dataType = "value";
            if (dataType.equals("salesValue"))
                dataType = "value2";

            mod = args[4];
            if (!mod.equals("=") &&
                    !mod.equals("+") && !mod.equals("-") &&
                    !mod.equals("*") && !mod.equals("/"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            if (!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("purchaseValue") && !args[5].equals("salesValue") && !args[5].equals("valueMin") && !args[5].equals("valueMax") && !args[5].equals("maxStock"))
                value = Float.parseFloat(args[5]);
        } catch (Exception e)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
            return;
        }

        // 수정
        for (String s : shopData.get().getKeys(false))
        {
            try
            {
                @SuppressWarnings("unused") int i = Integer.parseInt(s); // 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                if (!shopData.get().contains(s + ".value")) continue; //장식용임
            } catch (Exception e)
            {
                continue;
            }

            switch (args[5])
            {
                case "stock":
                    value = shopData.get().getInt(s + ".stock");
                    break;
                case "median":
                    value = shopData.get().getInt(s + ".median");
                    break;
                case "purchaseValue":
                    value = shopData.get().getInt(s + ".value");
                    break;
                case "salesValue":
                    value = shopData.get().getInt(s + ".value2");
                    break;
                case "valueMin":
                    value = shopData.get().getInt(s + ".valueMin");
                    break;
                case "valueMax":
                    value = shopData.get().getInt(s + ".valueMax");
                    break;
                case "maxStock":
                    value = shopData.get().getInt(s + ".maxStock");
                    break;
            }

            if (mod.equalsIgnoreCase("="))
            {
                shopData.get().set(s + "." + dataType, (int) value);
            } else if (mod.equalsIgnoreCase("+"))
            {
                shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) + value));
            } else if (mod.equalsIgnoreCase("-"))
            {
                shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) - value));
            } else if (mod.equalsIgnoreCase("/"))
            {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                {
                    shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) / value));
                }
                else
                {
                    shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) / value);
                }
            } else if (mod.equalsIgnoreCase("*"))
            {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                {
                    shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) * value));
                }
                else
                {
                    shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) * value);
                }
            }

            if (shopData.get().getDouble(s + ".valueMin") < 0)
            {
                shopData.get().set(s + ".valueMin", null);
            }
            if (shopData.get().getDouble(s + ".valueMax") < 0)
            {
                shopData.get().set(s + ".valueMax", null);
            }
            if (shopData.get().getDouble(s + ".maxStock") < 1)
            {
                shopData.get().set(s + ".maxStock", null);
            }
        }
        shopData.save();
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"));
    }
}
