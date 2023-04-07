package me.sat7.dynamicshop.utilities;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class UserUtil
{
    public static CustomConfig ccUser = new CustomConfig(); // 가급적 save 호출 피할 것. onDisable 에서 처리함.
    public static final HashMap<UUID, String> userTempData = new HashMap<>();
    public static final HashMap<UUID, String> userInteractItem = new HashMap<>();

    public static void Init()
    {
        ccUser.setup("User", null);
        ccUser.get().options().copyDefaults(true);
        BackwardCompatibility();
        ccUser.save();

        LoadTradeLimitDataFromYML();
    }

    private static void BackwardCompatibility()
    {
        int userVersion = ConfigUtil.GetConfigVersion();
        if (userVersion < 3)
        {
            for (String s : ccUser.get().getKeys(false))
            {
                ConfigurationSection cs = ccUser.get().getConfigurationSection(s);
                if (cs != null)
                {
                    // 구버전에 있던 데이터를 삭제함.
                    cs.set("tmpString", null);
                    cs.set("interactItem", null);
                }
            }
        }
    }

    public static void CreateNewPlayerData(Player player)
    {
        UUID uuid = player.getUniqueId();
        userTempData.put(uuid, "");
        userInteractItem.put(uuid, "");
        ccUser.get().set(uuid + ".lastJoin", System.currentTimeMillis());
        ccUser.get().set(uuid + ".cmdHelp", true);
    }

    public static void CreateDummyPlayerData(Player sender, int count)
    {
        if (!DynamicShop.DEBUG_MODE)
            return;

        sender.sendMessage(DynamicShop.dsPrefix(sender) + "Start creating dummy data...");

        Random generator = new Random();
        Object tradingVolumeData = ccUser.get().get(sender.getUniqueId() + ".tradingVolume");

        for (int i = 0; i < count; i++)
        {
            UUID uuid = UUID.randomUUID();
            userTempData.put(uuid, "");
            userInteractItem.put(uuid, "");

            int old = (generator.nextInt() % 100) + 3;
            ccUser.get().set(uuid + ".lastJoin", System.currentTimeMillis() - ((long) old * 1000 * 60 * 60 * 24));

            ccUser.get().set(uuid + ".cmdHelp", true);

            if (i % 5 == 0)
            {
                ccUser.get().set(uuid + ".tradingVolume", tradingVolumeData);
            }
        }

        ccUser.save();

        sender.sendMessage(DynamicShop.dsPrefix(sender) + "Dummy Player Data Created x " + count);
    }

    public static boolean RecreateUserData(Player player)
    {
        if (ccUser.get().contains(player.getUniqueId().toString()))
            return true;

        CreateNewPlayerData(player);
        ccUser.save();

        return ccUser.get().contains(player.getUniqueId().toString());
    }

    public static void OnPluginDisable()
    {
        SaveTradeLimitDataToYML();
        ccUser.save();
    }

    // ----------------------------------------------------------------------

    // 구매 & 판매 수량 제한
    // - 상점의 데이터는 [상점+idx] 로 관리.
    // - 유저의 데이터는 [상점+hash] 로 관리.
    public static final HashMap<String, HashMap<String, HashMap<UUID, Integer>>> tradingVolume = new HashMap<>();

    public static void LoadTradeLimitDataFromYML()
    {
        for (String uuid : ccUser.get().getKeys(false))
        {
            ConfigurationSection userDataSection = ccUser.get().getConfigurationSection(uuid + ".tradingVolume");
            if (userDataSection == null)
                continue;

            for (String shopName : userDataSection.getKeys(false))
            {
                ConfigurationSection shopDataSection = userDataSection.getConfigurationSection(shopName);
                if (shopDataSection == null)
                    continue;

                for (String itemHash : shopDataSection.getKeys(false))
                {
                    if (!tradingVolume.containsKey(shopName))
                        tradingVolume.put(shopName, new HashMap<>());

                    if (!tradingVolume.get(shopName).containsKey(itemHash))
                        tradingVolume.get(shopName).put(itemHash, new HashMap<>());

                    tradingVolume.get(shopName).get(itemHash).put(UUID.fromString(uuid), shopDataSection.getInt(itemHash));
                }
            }
        }
    }
    public static void SaveTradeLimitDataToYML()
    {
        for (String uuid : ccUser.get().getKeys(false))
        {
            ConfigurationSection userDataSection = ccUser.get().getConfigurationSection(uuid + ".tradingVolume");
            if (userDataSection == null)
                continue;

            ccUser.get().set(uuid + ".tradingVolume", null);
        }

        for (Map.Entry<String, HashMap<String, HashMap<UUID, Integer>>> shopMap : tradingVolume.entrySet())
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(shopMap.getKey());
            if (data == null)
                continue;

            for (Map.Entry<String, HashMap<UUID, Integer>> itemMap : tradingVolume.get(shopMap.getKey()).entrySet())
            {
                String hash = itemMap.getKey();
                if (!ShopUtil.hashExist(shopMap.getKey(), hash))
                    continue;

                for (Map.Entry<UUID, Integer> uuidMap : tradingVolume.get(shopMap.getKey()).get(itemMap.getKey()).entrySet())
                {
                    ccUser.get().set(uuidMap.getKey() + ".tradingVolume." + shopMap.getKey() + "." + itemMap.getKey(), uuidMap.getValue());
                }
            }
        }
    }
    public static void RepetitiveTask()
    {
        SaveTradeLimitDataToYML();
        ccUser.save();
    }

    public static int GetPlayerTradingVolume(Player player, String shopName, String hash)
    {
        UUID uuid = player.getUniqueId();
        if (!tradingVolume.containsKey(shopName) || tradingVolume.get(shopName) == null ||
            !tradingVolume.get(shopName).containsKey(hash) || tradingVolume.get(shopName).get(hash) == null ||
            !tradingVolume.get(shopName).get(hash).containsKey(uuid))
        {
            return 0;
        }

        return tradingVolume.get(shopName).get(hash).get(uuid);
    }

    public static void OnPlayerTradeLimitedItem(Player player, String shopName, String hash, int amount, boolean isSell)
    {
        UUID uuid = player.getUniqueId();
        if (!tradingVolume.containsKey(shopName))
            tradingVolume.put(shopName, new HashMap<>());

        if (!tradingVolume.get(shopName).containsKey(hash))
            tradingVolume.get(shopName).put(hash, new HashMap<>());

        if (isSell)
            amount *= -1;

        if (!tradingVolume.get(shopName).get(hash).containsKey(uuid))
        {
            tradingVolume.get(shopName).get(hash).put(uuid, amount);
        }
        else
        {
            tradingVolume.get(shopName).get(hash).put(uuid, tradingVolume.get(shopName).get(hash).get(uuid) + amount);
        }
    }

    public static int CheckTradeLimitPerPlayer(Player player, String shopName, int tradeIdx, String hash, int tradeAmount, boolean isSell)
    {
        int limit = isSell ? ShopUtil.GetSellLimitPerPlayer(shopName, tradeIdx) : ShopUtil.GetBuyLimitPerPlayer(shopName, tradeIdx);
        if (limit == 0)
            return tradeAmount;

        int left = GetTradingLimitLeft(player, shopName, tradeIdx, hash, isSell);
        return Math.min(left, tradeAmount);
    }

    public static int GetTradingLimitLeft(Player player, String shopName, int itemIdx, String hash, boolean isSell)
    {
        int playerCurrent = GetPlayerTradingVolume(player, shopName, hash);
        int limit = isSell ? ShopUtil.GetSellLimitPerPlayer(shopName, itemIdx) : ShopUtil.GetBuyLimitPerPlayer(shopName, itemIdx);
        if (limit == 0)
            return Integer.MAX_VALUE;

        if (isSell)
        {
            int temp = limit + playerCurrent;
            return Math.max(0, temp);
        }
        else
        {
            int temp = limit - playerCurrent;
            return Math.max(0, temp);
        }
    }

    public static void ClearTradeLimitData(String shopName)
    {
        if (!tradingVolume.containsKey(shopName))
            return;

        tradingVolume.remove(shopName);
    }
    public static void ClearTradeLimitData(UUID player, String shopName)
    {
        for (Map.Entry<String, HashMap<String, HashMap<UUID, Integer>>> shopMap : tradingVolume.entrySet())
        {
            if (!shopMap.getKey().equals(shopName))
                continue;

            for (Map.Entry<String, HashMap<UUID, Integer>> itemMap : tradingVolume.get(shopMap.getKey()).entrySet())
            {
                for (Map.Entry<UUID, Integer> uuidMap : tradingVolume.get(shopMap.getKey()).get(itemMap.getKey()).entrySet())
                {
                    if (uuidMap.getKey().equals(player))
                    {
                        tradingVolume.get(shopMap.getKey()).get(itemMap.getKey()).remove(player);
                    }
                }
            }
        }
    }
    public static void ClearTradeLimitData(String shopName, int idx)
    {
        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        if (data == null)
            return;

        String hash = HashUtil.CreateHashString(data.get().getString(idx + ".mat"), data.get().getString(idx + ".itemStack"));

        ClearTradeLimitData(shopName, hash);
    }
    public static void ClearTradeLimitData(String shopName, String hash)
    {
        if (!tradingVolume.containsKey(shopName) || tradingVolume.get(shopName) == null ||
            !tradingVolume.get(shopName).containsKey(hash))
            return;

        tradingVolume.get(shopName).remove(hash);
    }

    public static void OnRenameShop(String shopName, String newName)
    {
        if (!tradingVolume.containsKey(shopName) || tradingVolume.get(shopName) == null)
            return;

        tradingVolume.put(newName, tradingVolume.get(shopName));
        tradingVolume.remove(shopName);
    }
    public static void OnMergeShop(String shopA, String shopB)
    {
        if (!tradingVolume.containsKey(shopB) || tradingVolume.get(shopB) == null)
            return;

        if (!tradingVolume.containsKey(shopA))
            tradingVolume.put(shopA, new HashMap<>());

        HashMap<String, HashMap<UUID, Integer>> clone = (HashMap<String, HashMap<UUID, Integer>>) tradingVolume.get(shopB).clone();
        for (Map.Entry<String, HashMap<UUID, Integer>> shopBData : clone.entrySet())
        {
            tradingVolume.get(shopA).put(shopBData.getKey(), shopBData.getValue());
        }

        tradingVolume.remove(shopB);
    }
}
