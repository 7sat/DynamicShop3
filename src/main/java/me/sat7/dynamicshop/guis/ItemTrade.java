package me.sat7.dynamicshop.guis;

import java.text.DecimalFormat;
import java.util.ArrayList;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.transactions.Buy;
import me.sat7.dynamicshop.transactions.Sell;
import me.sat7.dynamicshop.utilities.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.transactions.Calc;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.DynaShopAPI.df;

public final class ItemTrade extends InGameUI
{
    public ItemTrade()
    {
        uiType = UI_TYPE.ItemTrade;
    }

    private final int CLOSE = 9;
    private final int SELL_ONLY_TOGGLE = 1;
    private final int BUY_ONLY_TOGGLE = 10;
    private final int CHECK_BALANCE = 0;

    public Inventory getGui(Player player, String shopName, String tradeIdx)
    {
        inventory = Bukkit.createInventory(player, 18, t("TRADE_TITLE"));

        // 배달비
        ConfigurationSection optionS = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
        int deliverycharge = 0;
        if (optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge"))
        {
            boolean sameworld = true;
            boolean outside = false;
            if (!player.getWorld().getName().equals(optionS.getString("world"))) sameworld = false;

            String[] shopPos1 = optionS.getString("pos1").split("_");
            String[] shopPos2 = optionS.getString("pos2").split("_");
            int x1 = Integer.parseInt(shopPos1[0]);
            int y1 = Integer.parseInt(shopPos1[1]);
            int z1 = Integer.parseInt(shopPos1[2]);
            int x2 = Integer.parseInt(shopPos2[0]);
            int y2 = Integer.parseInt(shopPos2[1]);
            int z2 = Integer.parseInt(shopPos2[2]);

            if (!((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2) ||
                    (x2 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x1))) outside = true;
            if (!((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2) ||
                    (y2 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y1))) outside = true;
            if (!((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2) ||
                    (z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1))) outside = true;

            if (!sameworld)
            {
                deliverycharge = -1;
            } else if (outside)
            {
                Location lo = new Location(player.getWorld(), x1, y1, z1);
                int dist = (int) (player.getLocation().distance(lo) * 0.1 * DynamicShop.plugin.getConfig().getDouble("DeliveryChargeScale"));
                deliverycharge = 1 + dist;
            }
        }

        String buyStr = t("BUY");
        String sellStr = t("SELL");
        String stockStr = t("STOCK");
        String tradeStr = ShopUtil.ccShop.get().getString(shopName + "." + tradeIdx + ".tradeType");
        if (tradeStr == null) tradeStr = "SB";

        ArrayList<String> sellLore = new ArrayList();
        if (tradeStr.equals("SellOnly")) sellLore.add(t("SELLONLY_LORE"));
        if (tradeStr.equals("BuyOnly")) sellLore.add(t("BUYONLY_LORE"));
        if (player.hasPermission("dshop.admin.shopedit"))
            sellLore.add(t("TOGGLE_SELLABLE"));

        ArrayList<String> buyLore = new ArrayList();
        if (tradeStr.equals("SellOnly")) buyLore.add(t("SELLONLY_LORE"));
        if (tradeStr.equals("BuyOnly")) buyLore.add(t("BUYONLY_LORE"));
        if (player.hasPermission("dshop.admin.shopedit"))
            buyLore.add(t("TOGGLE_BUYABLE"));

        CreateButton(SELL_ONLY_TOGGLE, Material.GREEN_STAINED_GLASS, sellStr, sellLore);
        CreateButton(BUY_ONLY_TOGGLE, Material.RED_STAINED_GLASS, buyStr, buyLore);

        String mat = ShopUtil.ccShop.get().getString(shopName + "." + tradeIdx + ".mat");
        // 판매
        if (!tradeStr.equals("BuyOnly"))
        {
            int amount = 1;
            int idx = 2;
            for (int i = 1; i < 8; i++)
            {
                String priceStr = t("SELLPRICE");

                ItemStack sell = new ItemStack(Material.getMaterial(mat), amount);
                sell.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = sell.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(sellStr + " x" + amount);
                lore.add(priceStr + Calc.calcTotalCost(shopName, tradeIdx, -amount));

                if (!ShopUtil.ccShop.get().getBoolean(shopName + ".Options.hideStock"))
                {
                    if (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr + "INF");
                    } else if (DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr + (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") / 64) + " Stacks");
                    } else
                    {
                        lore.add(stockStr + ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock"));
                    }
                }

                if (deliverycharge > 0)
                    lore.add(t("DELIVERYCHARGE") + ": " + deliverycharge);

                meta.setLore(lore);

                sell.setItemMeta(meta);

                inventory.setItem(idx, sell);

                idx++;
                amount = amount * 2;
            }
        }

        // 구매
        if (!tradeStr.equals("SellOnly"))
        {
            String priceStr = t("PRICE");

            int amount = 1;
            int idx = 11;
            for (int i = 1; i < 8; i++)
            {
                ItemStack buy = new ItemStack(Material.getMaterial(mat), amount);
                buy.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                ItemMeta meta = buy.getItemMeta();

                ArrayList<String> lore = new ArrayList<>();
                lore.add(buyStr + " x" + amount);
                lore.add(priceStr + Calc.calcTotalCost(shopName, tradeIdx, amount));

                if (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") != -1)
                {
                    if (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") <= amount)
                    {
                        continue;
                    }
                }

                if (!ShopUtil.ccShop.get().getBoolean(shopName + ".Options.hideStock"))
                {
                    if (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") <= 0)
                    {
                        lore.add(stockStr + "INF");
                    } else if (DynamicShop.plugin.getConfig().getBoolean("DisplayStockAsStack"))
                    {
                        lore.add(stockStr + (ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") / 64) + " Stacks");
                    } else
                    {
                        lore.add(stockStr + ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock"));
                    }
                }

                if (deliverycharge > 0)
                    lore.add(t("DELIVERYCHARGE") + ": " + deliverycharge);

                meta.setLore(lore);

                buy.setItemMeta(meta);

                inventory.setItem(idx, buy);

                idx++;
                amount = amount * 2;
            }
        }

        // 잔액 버튼
        ArrayList<String> moneyLore = new ArrayList<>();
        if (optionS.contains("flag.jobpoint"))
        {
            moneyLore.add("§f" + df.format(JobsHook.getCurJobPoints(player)) + "Points");
        } else
        {
            moneyLore.add("§f" + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player)));
        }
        String balStr = "";
        if (ShopUtil.getShopBalance(shopName) >= 0)
        {
            double d = ShopUtil.getShopBalance(shopName);
            balStr = DynamicShop.getEconomy().format(d);
            if (optionS.contains("flag.jobpoint")) balStr += "Points";
        } else
        {
            balStr = t("SHOP_BAL_INF");
        }
        moneyLore.add("§3" + ChatColor.stripColor(t("SHOP_BAL")));
        moneyLore.add("§f" + balStr);

        CreateButton(CHECK_BALANCE, Material.EMERALD, t("BALANCE"), moneyLore); // 잔액 확인 버튼
        CreateCloseButton(CLOSE); // 닫기 버튼

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        String[] temp = DynamicShop.userInteractItem.get(player.getUniqueId()).split("/");
        String shopName = temp[0];
        String tradeIdx = temp[1];

        if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null)
        {
            // 닫기
            if (e.getSlot() == CLOSE)
            {
                DynamicShop.userInteractItem.put(player.getUniqueId(), "");

                // 표지판을 클릭해서 거래화면에 진입한 경우에는 상점UI로 돌아가는 대신 인벤토리를 닫음
                if (DynamicShop.userTempData.get(player.getUniqueId()).equalsIgnoreCase("sign"))
                {
                    DynamicShop.userTempData.put(player.getUniqueId(), "");
                    player.closeInventory();
                } else
                {
                    DynaShopAPI.openShopGui(player, shopName, 1);
                }
            }
            // 구매 또는 판매
            else
            {
                // 잔액확인 버튼
                if (e.getSlot() == CHECK_BALANCE)
                {
                    if (ShopUtil.ccShop.get().contains(shopName + ".Options.flag.jobpoint"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("BALANCE") + ":§f " + df.format(JobsHook.getCurJobPoints(player)) + "Points");
                    } else
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("BALANCE") + ":§f " + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player)));
                    }
                    return;
                }

                // 판매 토글
                if (e.getSlot() == SELL_ONLY_TOGGLE)
                {
                    if (player.hasPermission("dshop.admin.shopedit"))
                    {
                        String path = shopName + "." + tradeIdx + ".tradeType";
                        String tradeType = ShopUtil.ccShop.get().getString(path);
                        if (tradeType == null || !tradeType.equals("SellOnly"))
                        {
                            ShopUtil.ccShop.get().set(path, "SellOnly");
                        } else
                        {
                            ShopUtil.ccShop.get().set(path, null);
                        }

                        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);
                        ShopUtil.ccShop.save();
                    }
                    return;
                }
                // 구매 토글
                if (e.getSlot() == BUY_ONLY_TOGGLE)
                {
                    if (player.hasPermission("dshop.admin.shopedit"))
                    {
                        String path = shopName + "." + tradeIdx + ".tradeType";
                        String tradeType = ShopUtil.ccShop.get().getString(path);
                        if (tradeType == null || !tradeType.equals("BuyOnly"))
                        {
                            ShopUtil.ccShop.get().set(path, "BuyOnly");
                        } else
                        {
                            ShopUtil.ccShop.get().set(path, null);
                        }

                        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);
                        ShopUtil.ccShop.save();
                    }
                    return;
                }

                // 거래와 관련된 버튼들
                double priceSum = 0;

                ItemStack tempIS = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount());
                tempIS.setItemMeta((ItemMeta) ShopUtil.ccShop.get().get(shopName + "." + tradeIdx + ".itemStack"));

                // 무한재고&고정가격
                boolean infiniteStock = ShopUtil.ccShop.get().getInt(shopName + "." + tradeIdx + ".stock") <= 0;

                // 로컬샵이면 아에 창을 못열었고 딜리버리샵인데 월드가 다르면 배달불가.
                ConfigurationSection optionS = ShopUtil.ccShop.get().getConfigurationSection(shopName).getConfigurationSection("Options");
                int deliverycharge = 0;
                if (optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge"))
                {
                    String lore = e.getCurrentItem().getItemMeta().getLore().toString();
                    if (lore.contains(t("DELIVERYCHARGE")))
                    {
                        String[] tempLoreArr = lore.split(": ");
                        deliverycharge = Integer.parseInt(tempLoreArr[tempLoreArr.length - 1].replace("]", ""));
                        if (deliverycharge == -1)
                        {
                            player.sendMessage(DynamicShop.dsPrefix + t("DELIVERYCHARGE_NA"));
                            return;
                        } else
                        {
                            if (e.getSlot() <= 9)
                            {
                                priceSum -= deliverycharge;
                            } else
                            {
                                priceSum += deliverycharge;
                            }
                        }
                    }
                }

                String permission = optionS.getString("permission");
                // 판매
                if (e.getSlot() <= 10)
                {
                    // 판매권한 확인
                    if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".sell"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                        return;
                    }

                    if (optionS.contains("flag.jobpoint"))
                    {
                        Sell.sellItemJobPoint(player, shopName, tradeIdx, tempIS, priceSum, deliverycharge, infiniteStock);
                    } else
                    {
                        Sell.sellItemCash(player, shopName, tradeIdx, tempIS, priceSum, deliverycharge, infiniteStock);
                    }
                }
                // 구매
                else
                {
                    // 구매 권한 확인
                    if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".buy"))
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                        return;
                    }

                    if (optionS.contains("flag.jobpoint"))
                    {
                        Buy.buyItemJobPoint(player, shopName, tradeIdx, tempIS, priceSum, deliverycharge, infiniteStock);
                    } else
                    {
                        Buy.buyItemCash(player, shopName, tradeIdx, tempIS, priceSum, deliverycharge, infiniteStock);
                    }
                }
            }
        }
    }
}
