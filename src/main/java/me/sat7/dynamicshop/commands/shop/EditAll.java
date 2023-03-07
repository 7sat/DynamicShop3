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
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... editall <purchaseValue | salesValue | valueMin | valueMax | median | stock | max stock | discount> <= | + | - | * | /> <amount>");
        player.sendMessage(" - " + t(player, "HELP.EDIT_ALL"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if (!CheckValid(args, sender))
            return;

        if (args.length > 6)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
            return;
        }

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        String dataType;
        String mod;
        double newValue = 0;

        try
        {
            dataType = args[3];
            if (!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("purchaseValue") && !dataType.equals("salesValue") && !dataType.equals("valueMin") && !dataType.equals("valueMax") && !dataType.equals("maxStock") && !dataType.equals("discount"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            if (dataType.equals("purchaseValue"))
                dataType = "value";
            if (dataType.equals("salesValue"))
                dataType = "value2";

            mod = args[4];
            if (!mod.equals("=") && !mod.equals("+") && !mod.equals("-") && !mod.equals("*") && !mod.equals("/"))
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }

            if (!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("purchaseValue") && !args[5].equals("salesValue") && !args[5].equals("valueMin") && !args[5].equals("valueMax") && !args[5].equals("maxStock"))
                newValue = Double.parseDouble(args[5]);

            if (args[3].equals("discount") && args[5].length() > 3)
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                return;
            }
        }
        catch (Exception e)
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
            }
            catch (Exception e)
            {
                continue;
            }

            switch (args[5])
            {
                case "stock":
                    newValue = shopData.get().getInt(s + ".stock");
                    break;
                case "median":
                    newValue = shopData.get().getInt(s + ".median");
                    break;
                case "purchaseValue":
                    newValue = shopData.get().getDouble(s + ".value");
                    break;
                case "salesValue":
                    newValue = shopData.get().getDouble(s + ".value2", -1);
                    break;
                case "valueMin":
                    newValue = shopData.get().getDouble(s + ".valueMin", -1);
                    break;
                case "valueMax":
                    newValue = shopData.get().getDouble(s + ".valueMax", -1);
                    break;
                case "maxStock":
                    newValue = shopData.get().getInt(s + ".maxStock", -1);
                    break;
                case "discount":
                    newValue = shopData.get().getInt(s + ".discount", 0);
                    break;
            }

            if (newValue < 0)
                continue;

            double originalValue = shopData.get().getDouble(s + "." + dataType);
            double result = 0;

            if (mod.equalsIgnoreCase("="))
            {
                result = newValue;
            }
            else if (mod.equalsIgnoreCase("+"))
            {
                result = originalValue + newValue;
            }
            else if (mod.equalsIgnoreCase("-"))
            {
                result = originalValue - newValue;
            }
            else if (mod.equalsIgnoreCase("/"))
            {
                result = originalValue / newValue;
            }
            else if (mod.equalsIgnoreCase("*"))
            {
                result = originalValue * newValue;
            }

            if (dataType.equals("value") && result < 0.01)
            {
                result = 0.01;
            }

            if (dataType.equals("stock") || dataType.equals("median") || dataType.equals("maxStock") || dataType.equals("discount"))
            {
                int intResult = (int)result;
                shopData.get().set(s + "." + dataType, intResult);
            }
            else
            {
                result = Math.round(result * 1000) / 1000.0;
                shopData.get().set(s + "." + dataType, result);
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
            if (shopData.get().getDouble(s + ".discount") > 90)
            {
                shopData.get().set(s + ".discount", 90);
            }
            if (shopData.get().getDouble(s + ".discount") <= 0)
            {
                shopData.get().set(s + ".discount", null);
            }

            Double value2 = shopData.get().getDouble(s + ".value2");
            if (value2 < 0 || value2.equals(shopData.get().getDouble(s + ".value")))
            {
                shopData.get().set(s + ".value2", null);
            }
        }
        shopData.save();
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"));
    }
}
