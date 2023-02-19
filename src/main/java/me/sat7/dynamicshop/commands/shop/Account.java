package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.economyhook.PlayerpointHook;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.economyhook.JobsHook;
import me.sat7.dynamicshop.utilities.ShopUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.n;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Account extends DSCMD
{
    public Account()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(5);
        validArgCount.add(6);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "account"));
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... account set <amount>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... account linkto <shopname>");
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": ... account transfer <target> <amount>");
        player.sendMessage(" - " + t(player, "HELP.ACCOUNT"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        CustomConfig targetShopData = ShopUtil.shopConfigFiles.get(args[4]);
        switch (args[3])
        {
            case "set":
                try
                {
                    if (Double.parseDouble(args[4]) < 0)
                    {
                        shopData.get().set("Options.Balance", null);
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + t(sender, "SHOP.SHOP_BAL_INF"));
                    } else
                    {
                        shopData.get().set("Options.Balance", Double.parseDouble(args[4]));
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[4]);
                    }
                    shopData.save();
                } catch (Exception e)
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                    return;
                }
                break;
            case "linkto":
                // 그런 상점(타깃) 없음
                if (!ShopUtil.shopConfigFiles.containsKey(args[4]))
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"));
                    return;
                }

                // 타깃 상점이 연동계좌임
                if (targetShopData.get().contains("Options.Balance"))
                {
                    try
                    {
                        // temp 를 직접 사용하지는 않지만 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                        Double temp = Double.parseDouble(targetShopData.get().getString("Options.Balance"));
                    } catch (Exception e)
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_LINK_TARGET_ERR"));
                        return;
                    }
                }

                // 출발상점을 타깃으로 하는 상점이 있음
                for (CustomConfig tempShopData : ShopUtil.shopConfigFiles.values())
                {
                    String temp = tempShopData.get().getString("Options.Balance");
                    try
                    {
                        if (temp != null && temp.equals(args[1]))
                        {
                            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.NESTED_STRUCTURE"));
                            return;
                        }
                    } catch (Exception e)
                    {
                        DynamicShop.console.sendMessage(DynamicShop.dsPrefix(sender) + e);
                    }
                }

                // 출발 상점과 도착 상점이 같음
                if (args[1].equals(args[4]))
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                    return;
                }

                // 출발 상점과 도착 상점의 통화 유형이 다름
                if ((shopData.get().contains("Options.flag.jobpoint") != targetShopData.get().contains("Options.flag.jobpoint")) ||
                    (shopData.get().contains("Options.flag.playerpoint") != targetShopData.get().contains("Options.flag.playerpoint")) )
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_DIFF_CURRENCY"));
                    return;
                }

                shopData.get().set("Options.Balance", args[4]);
                shopData.save();
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[4]);
                break;
            case "transfer":
                //[4] 대상 [5] 금액
                if (args.length < 6)
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                    return;
                }

                double amount;
                // 마지막 인자가 숫자가 아님
                try
                {
                    amount = Double.parseDouble(args[5]);
                } catch (Exception e)
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"));
                    return;
                }

                // 출발 상점이 무한계좌임
                if (!shopData.get().contains("Options.Balance"))
                {
                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_HAS_INF_BAL").replace("{shop}", args[1]));
                    return;
                }

                // 출발 상점에 돈이 부족
                if (ShopUtil.getShopBalance(args[1]) < amount)
                {
                    if (shopData.get().contains("Options.flag.jobpoint"))
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.NOT_ENOUGH_POINT").
                                replace("{bal}", n(ShopUtil.getShopBalance(args[1]))));
                    }
                    else if (shopData.get().contains("Options.flag.playerpoint"))
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.NOT_ENOUGH_PLAYER_POINT").
                                replace("{bal}", n(ShopUtil.getShopBalance(args[1]))));
                    }
                    else
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.NOT_ENOUGH_MONEY").
                                replace("{bal}", n(ShopUtil.getShopBalance(args[1]))));
                    }
                    return;
                }

                // 다른 상점으로 송금
                if (ShopUtil.shopConfigFiles.containsKey(args[4]))
                {
                    // 도착 상점이 무한계좌임
                    if (!targetShopData.get().contains("Options.Balance"))
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_HAS_INF_BAL").replace("{shop}", args[4]));
                        return;
                    }

                    // 출발 상점과 도착 상점이 같음
                    if (args[1].equals(args[4]))
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"));
                        return;
                    }

                    // 출발 상점과 도착 상점의 통화 유형이 다름
                    if ((shopData.get().contains("Options.flag.jobpoint") != targetShopData.get().contains("Options.flag.jobpoint")) ||
                        (shopData.get().contains("Options.flag.playerpoint") != targetShopData.get().contains("Options.flag.playerpoint")) )
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_DIFF_CURRENCY"));
                        return;
                    }

                    // 송금.
                    ShopUtil.addShopBalance(args[1], amount * -1);
                    ShopUtil.addShopBalance(args[4], amount);

                    shopData.save();
                    targetShopData.save();

                    sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.TRANSFER_SUCCESS"));
                }
                // 플레이어에게 송금
                else
                {
                    try
                    {
                        Player target = Bukkit.getPlayer(args[4]);

                        if (target == null)
                        {
                            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.PLAYER_NOT_EXIST"));
                            return;
                        }

                        if (shopData.get().contains("Options.flag.jobpoint"))
                        {
                            JobsHook.addJobsPoint(target, amount);
                            ShopUtil.addShopBalance(args[1], amount * -1);
                            shopData.save();

                            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.TRANSFER_SUCCESS"));
                        }
                        else if (shopData.get().contains("Options.flag.playerpoint"))
                        {
                            PlayerpointHook.addPP(target, amount);
                            ShopUtil.addShopBalance(args[1], amount * -1);
                            shopData.save();

                            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.TRANSFER_SUCCESS"));
                        }
                        else
                        {
                            Economy econ = DynamicShop.getEconomy();
                            EconomyResponse er = econ.depositPlayer(target, amount);

                            if (er.transactionSuccess())
                            {
                                ShopUtil.addShopBalance(args[1], amount * -1);
                                shopData.save();

                                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.TRANSFER_SUCCESS"));
                            } else
                            {
                                sender.sendMessage(DynamicShop.dsPrefix(sender) + "Transfer failed");
                            }
                        }
                    } catch (Exception e)
                    {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + "Transfer failed. /" + e);
                    }
                }
                break;
        }
    }
}
