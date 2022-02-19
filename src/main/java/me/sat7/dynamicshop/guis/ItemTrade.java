package me.sat7.dynamicshop.guis;

import java.util.ArrayList;
import java.util.Arrays;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.transactions.Buy;
import me.sat7.dynamicshop.transactions.Sell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.LayoutUtil.l;

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

    private Player player;
    private String shopName;
    private String tradeIdx;
    private int deliveryCharge;
    private FileConfiguration shopData;
    private String sellBuyOnly;

    public Inventory getGui(Player player, String shopName, String tradeIdx)
    {
        this.player = player;
        this.shopName = shopName;
        this.tradeIdx = tradeIdx;
        this.deliveryCharge = CalcShipping(player, shopName);
        this.shopData = ShopUtil.shopConfigFiles.get(shopName).get();
        this.sellBuyOnly = shopData.getString(this.tradeIdx + ".tradeType", "");

        inventory = Bukkit.createInventory(player, 18, t("TRADE_TITLE"));

        CreateBalanceButton();
        CreateSellBuyOnlyToggle();
        CreateTradeButtons();
        CreateCloseButton(CLOSE);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null)
        {
            if (e.getSlot() == CLOSE)
            {
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
            else if (e.getSlot() == CHECK_BALANCE)
            {
                if (data.get().contains("Options.flag.jobpoint"))
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("TRADE.BALANCE") + ":§f " + df.format(JobsHook.getCurJobPoints(player)) + "Points");
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("TRADE.BALANCE") + ":§f " + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player)));
                }
            }
            else if (e.getSlot() == SELL_ONLY_TOGGLE)
            {
                if (player.hasPermission("dshop.admin.shopedit"))
                {
                    String path = tradeIdx + ".tradeType";
                    String tradeType = data.get().getString(path);
                    if (tradeType == null || !tradeType.equals("SellOnly"))
                    {
                        data.get().set(path, "SellOnly");
                    } else
                    {
                        data.get().set(path, null);
                    }

                    data.save();
                    DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);
                }
            }
            else if (e.getSlot() == BUY_ONLY_TOGGLE)
            {
                if (player.hasPermission("dshop.admin.shopedit"))
                {
                    String path = tradeIdx + ".tradeType";
                    String tradeType = data.get().getString(path);
                    if (tradeType == null || !tradeType.equals("BuyOnly"))
                    {
                        data.get().set(path, "BuyOnly");
                    } else
                    {
                        data.get().set(path, null);
                    }

                    data.save();
                    DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx);
                }
            }
            else
            {
                ItemStack tempIS = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount());
                tempIS.setItemMeta((ItemMeta) data.get().get(tradeIdx + ".itemStack"));

                // 무한재고&고정가격
                boolean infiniteStock = data.get().getInt(tradeIdx + ".stock") <= 0;

                // 배달비 계산
                ConfigurationSection optionS = data.get().getConfigurationSection("Options");
                if (optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge"))
                {
                    deliveryCharge = CalcShipping(player, shopName);
                    if (deliveryCharge == -1)
                    {
                        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.DELIVERY_CHARGE_NA")); // 다른 월드로 배달 불가능
                        return;
                    }
                }

                if (e.getSlot() <= 10)
                    Sell(optionS, tempIS, deliveryCharge, infiniteStock);
                else
                    Buy(optionS, tempIS, deliveryCharge, infiniteStock);
            }
        }
    }

    public static int CalcShipping(Player player, String shopName)
    {
        int deliverycharge = 0;

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        ConfigurationSection optionS = data.get().getConfigurationSection("Options");
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
                int dist = (int) (player.getLocation().distance(lo) * 0.1 * DynamicShop.plugin.getConfig().getDouble("Shop.DeliveryChargeScale"));
                deliverycharge = 1 + dist;
            }
        }

        return deliverycharge;
    }

    private void CreateBalanceButton()
    {
        String moneyLore = l("TRADE_VIEW.BALANCE");
        String myBalanceString;
        ConfigurationSection optionS = shopData.getConfigurationSection("Options");

        if (optionS.contains("flag.jobpoint"))
        {
            myBalanceString = "§f" + df.format(JobsHook.getCurJobPoints(player)) + "Points";
        } else
        {
            myBalanceString = "§f" + DynamicShop.getEconomy().format(DynamicShop.getEconomy().getBalance(player));
        }
        String balStr;
        if (ShopUtil.getShopBalance(shopName) >= 0)
        {
            double d = ShopUtil.getShopBalance(shopName);
            balStr = DynamicShop.getEconomy().format(d);
            if (optionS.contains("flag.jobpoint")) balStr += "Points";
        } else
        {
            balStr = t("TRADE.SHOP_BAL_INF");
        }

        String shopBalanceString = "";
        if(!shopData.contains("Options.flag.hideshopbalance"))
            shopBalanceString = t("TRADE.SHOP_BAL").replace("{num}", balStr);

        moneyLore = moneyLore.replace("{\\nPlayerBalance}", "\n" + myBalanceString);
        moneyLore = moneyLore.replace("{\\nShopBalance}", shopBalanceString.isEmpty() ? "" : "\n" + shopBalanceString);
        moneyLore = moneyLore.replace("{PlayerBalance}", myBalanceString);
        moneyLore = moneyLore.replace("{ShopBalance}", shopBalanceString);

        String temp = moneyLore.replace(" ","");
        if(ChatColor.stripColor(temp).startsWith("\n"))
            moneyLore = moneyLore.replaceFirst("\n","");

        CreateButton(CHECK_BALANCE, Material.EMERALD, t("TRADE.BALANCE"), moneyLore);
    }

    private void CreateSellBuyOnlyToggle()
    {
        ArrayList<String> sellLore = new ArrayList<>();
        if (sellBuyOnly.equals("SellOnly")) sellLore.add(t("TRADE.SELL_ONLY_LORE"));
        else if (sellBuyOnly.equals("BuyOnly")) sellLore.add(t("TRADE.BUY_ONLY_LORE"));

        if (player.hasPermission("dshop.admin.shopedit"))
            sellLore.add(t("TRADE.TOGGLE_SELLABLE"));

        ArrayList<String> buyLore = new ArrayList<>();
        if (sellBuyOnly.equals("SellOnly")) buyLore.add(t("TRADE.SELL_ONLY_LORE"));
        else if (sellBuyOnly.equals("BuyOnly")) buyLore.add(t("TRADE.BUY_ONLY_LORE"));

        if (player.hasPermission("dshop.admin.shopedit"))
            buyLore.add(t("TRADE.TOGGLE_BUYABLE"));

        CreateButton(SELL_ONLY_TOGGLE, Material.GREEN_STAINED_GLASS, t("TRADE.SELL"), sellLore);
        CreateButton(BUY_ONLY_TOGGLE, Material.RED_STAINED_GLASS, t("TRADE.BUY"), buyLore);
    }

    private void CreateTradeButtons()
    {
        String mat = shopData.getString(tradeIdx + ".mat");
        if (!sellBuyOnly.equals("BuyOnly"))
            CreateTradeButtons(mat, true);
        if (!sellBuyOnly.equals("SellOnly"))
            CreateTradeButtons(mat, false);
    }

    private void CreateTradeButtons(String mat, boolean sell)
    {
        int amount = 1;
        int idx = sell ? 2 : 11;
        for (int i = 1; i < 8; i++)
        {
            ItemStack itemStack = new ItemStack(Material.getMaterial(mat), amount);
            itemStack.setItemMeta((ItemMeta) shopData.get(tradeIdx + ".itemStack"));
            ItemMeta meta = itemStack.getItemMeta();

            int stock = shopData.getInt(tradeIdx + ".stock");
            int maxStock = shopData.getInt(tradeIdx + ".maxStock", -1);

            double price = Calc.calcTotalCost(shopName, tradeIdx, sell ? -amount : amount);
            String lore;
            String priceText;
            if(sell)
            {
                lore = l("TRADE_VIEW.SELL");
                priceText = t("TRADE.SELL_PRICE").replace("{num}", String.valueOf(price));
            }
            else
            {
                lore = l("TRADE_VIEW.BUY");
                priceText = t("TRADE.PRICE").replace("{num}", String.valueOf(price));
            }

            if(!sell)
            {
                if (stock != -1 && stock <= amount) // stock은 1이거나 그보다 작을 수 없음. 단 -1은 무한재고를 의미함.
                    continue;
            }

            String stockText = "";
            if (!shopData.contains("Options.flag.hidestock"))
            {
                if (stock <= 0)
                {
                    stockText = t("TRADE.INF_STOCK");
                } else if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack"))
                {
                    stockText = t("TRADE.STACKS").replace("{num}", (stock / 64) + "");
                } else
                {
                    stockText = String.valueOf(stock);
                }
            }

            String maxStockText = "";
            if (shopData.contains("Options.flag.showmaxstock") && maxStock != -1)
            {
                if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack"))
                {
                    maxStockText = t("TRADE.STACKS").replace("{num}", (maxStock / 64) + "");
                } else
                {
                    maxStockText = String.valueOf(maxStock);
                }

                stockText = t("SHOP.STOCK_2").replace("{stock}", stockText).replace("{max_stock}", maxStockText);
            }
            else
            {
                stockText = t("SHOP.STOCK").replace("{num}", stockText);
            }

            String deliveryChargeText = "";
            if (deliveryCharge > 0)
            {
                if(sell && price < deliveryCharge)
                {
                    deliveryChargeText = "§c" + ChatColor.stripColor(t("MESSAGE.DELIVERY_CHARGE")).replace("{fee}", String.valueOf(deliveryCharge));
                }
                else
                {
                    deliveryChargeText = t("MESSAGE.DELIVERY_CHARGE").replace("{fee}", String.valueOf(deliveryCharge));
                }
            }

            String tradeLoreText = sell ? t("TRADE.CLICK_TO_SELL") : t("TRADE.CLICK_TO_BUY");
            tradeLoreText = tradeLoreText.replace("{amount}", String.valueOf(amount));

            lore = lore.replace("{\\nPrice}", priceText.isEmpty() ? "" : "\n" + priceText);
            lore = lore.replace("{\\nStock}", stockText.isEmpty() ? "" : "\n" + stockText);
            lore = lore.replace("{\\nDeliveryCharge}", deliveryChargeText.isEmpty() ? "" : "\n" + deliveryChargeText);
            lore = lore.replace("{\\nTradeLore}", "\n" + tradeLoreText);

            lore = lore.replace("{Price}", priceText);
            lore = lore.replace("{Stock}", stockText);
            lore = lore.replace("{DeliveryCharge}", deliveryChargeText);
            lore = lore.replace("{TradeLore}", tradeLoreText);

            String temp = lore.replace(" ","");
            if(ChatColor.stripColor(temp).startsWith("\n"))
                lore = lore.replaceFirst("\n","");

            meta.setLore(new ArrayList<>(Arrays.asList(lore.split("\n"))));

            itemStack.setItemMeta(meta);
            inventory.setItem(idx, itemStack);

            idx++;
            amount = amount * 2;
        }
    }

    private void Sell(ConfigurationSection options, ItemStack itemStack, int deliveryCharge, boolean infiniteStock)
    {
        String permission = options.getString("permission");
        if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".sell"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return;
        }

        // 상점이 매입을 거절.
        int stock = shopData.getInt(tradeIdx + ".stock");
        int maxStock = shopData.getInt(tradeIdx + ".maxStock", -1);
        if (maxStock != -1 && maxStock < stock + itemStack.getAmount())
        {
            player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.PURCHASE_REJECTED"));
            return;
        }

        if (options.contains("flag.jobpoint"))
        {
            Sell.sellItemJobPoint(player, shopName, tradeIdx, itemStack, -deliveryCharge, infiniteStock);
        } else
        {
            Sell.sellItemCash(player, shopName, tradeIdx, itemStack, -deliveryCharge, infiniteStock);
        }
    }

    private void Buy(ConfigurationSection options, ItemStack itemStack, int deliveryCharge, boolean infiniteStock)
    {
        String permission = options.getString("permission");
        if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission(permission + ".buy"))
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
            return;
        }

        if (options.contains("flag.jobpoint"))
        {
            Buy.buyItemJobPoint(player, shopName, tradeIdx, itemStack, deliveryCharge, infiniteStock);
        } else
        {
            Buy.buyItemCash(player, shopName, tradeIdx, itemStack, deliveryCharge, infiniteStock);
        }
    }
}
