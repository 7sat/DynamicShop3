package me.sat7.dynamicshop.Events;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OnSignClick  implements Listener
{
    // 생성
    @EventHandler
    public void onSignChange(SignChangeEvent e)
    {
        if(!e.getPlayer().hasPermission("dshop.admin.createsign")) return;

        if(e.getLine(0).equalsIgnoreCase("[dshop]")|| e.getLine(0).equalsIgnoreCase("[ds]")||e.getLine(0).equalsIgnoreCase("[dynamicshop]"))
        {
            String shopname = e.getLine(1);

            int x = e.getBlock().getX();
            int y = e.getBlock().getY();
            int z = e.getBlock().getZ();
            String signId = x + "_" + y + "_" + z;

            if(e.getLine(1).length() == 0)
            {
                e.setLine(1,"Error");
                e.setLine(2,"shop name is null");
                e.getBlock().getState().update();
                return;
            }

            //e.setLine(0,"§3[DynamicShop]");
            e.setLine(0,e.getLine(3));
            e.setLine(1,"§a"+e.getLine(1));
            e.setLine(3,"");
            e.getBlock().getState().update();

            DynamicShop.ccSign.get().set(signId+".shop" , ChatColor.stripColor(e.getLine(1)));

            try
            {
                String shop = ChatColor.stripColor(e.getLine(1));
                String mat = ChatColor.stripColor(e.getLine(2)).toUpperCase();
                int i = DynaShopAPI.FindItemFromShop(shop,new ItemStack(Material.getMaterial(mat)));

                e.setLine(2, DynamicShop.ccShop.get().getConfigurationSection(shop).getString(i+".mat"));

                DynamicShop.ccSign.get().set(signId+".mat" , mat);
            }catch (Exception exception)
            {
                e.setLine(2,"");
            }

            DynamicShop.ccSign.save();
        }
    }

    // 상호작용
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getClickedBlock().getType().toString().contains("WALL_SIGN")) {

                Sign s = (Sign) e.getClickedBlock().getState();

                int x = e.getClickedBlock().getX();
                int y = e.getClickedBlock().getY();
                int z = e.getClickedBlock().getZ();
                String signId = x + "_" + y + "_" + z;

                if(DynamicShop.ccSign.get().contains(signId))
                {
                    String shopName = ChatColor.stripColor(DynamicShop.ccSign.get().getString(signId+".shop"));
                    // 상점 존재 확인
                    if(DynamicShop.ccShop.get().contains(shopName))
                    {
                        //권한 확인
                        String permission = DynamicShop.ccShop.get().getString(shopName+".Options.permission");
                        if(permission != null && permission.length()>0 )
                        {
                            if(!p.hasPermission(permission) && !p.hasPermission(permission+".buy") && !p.hasPermission(permission+".sell"))
                            {
                                p.sendMessage(DynamicShop.dsPrefix + DynamicShop.ccLang.get().getString("ERR.NO_PERMISSION"));
                                return;
                            }
                        }

                        try
                        {
                            int idx = DynaShopAPI.FindItemFromShop(shopName,new ItemStack(Material.getMaterial(DynamicShop.ccSign.get().getString(signId+".mat"))));

                            if(idx != -1)
                            {
                                DynamicShop.ccUser.get().set(p.getUniqueId().toString()+".interactItem",shopName+"/"+idx);
                                DynamicShop.ccUser.get().set(p.getUniqueId().toString()+".tmpString","sign");
                                DynamicShop.ccUser.save();

                                DynaShopAPI.OpenItemTradeInven(p,shopName,String.valueOf(idx));
                            }
                            else
                            {
                                DynaShopAPI.OpenShopGUI(p, shopName, 1);
                            }
                        }
                        catch (Exception exception)
                        {
                            DynaShopAPI.OpenShopGUI(p, shopName, 1);
                        }
                    }
                    else
                    {
                        p.sendMessage(DynamicShop.dsPrefix+DynamicShop.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                    }
                }
                else
                {
//                    if(s.getLine(0).equals("§3[DynamicShop]") && e.getPlayer().hasPermission("dshop.admin.createsign"))
//                    {
//                        s.setLine(1,"Error");
//                        s.setLine(2,"YML data Missing");
//                        s.update();
//                    }
                }
            }
        }
    }

    // 파괴
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        ArrayList<Block> signList = new ArrayList<>();
        signList.add(e.getBlock());
        signList.add(e.getBlock().getRelative(BlockFace.EAST));
        signList.add(e.getBlock().getRelative(BlockFace.WEST));
        signList.add(e.getBlock().getRelative(BlockFace.NORTH));
        signList.add(e.getBlock().getRelative(BlockFace.SOUTH));

        for (Block b : signList)
        {
            if(b.getType().toString().contains("WALL_SIGN"))
            {
                int x = b.getX();
                int y = b.getY();
                int z = b.getZ();
                String signId = x + "_" + y + "_" + z;

                if(DynamicShop.ccSign.get().contains(signId))
                {
                    if(!e.getPlayer().hasPermission("dshop.admin.destroysign"))
                    {
                        e.setCancelled(true);
                    }
                    else
                    {
                        DynamicShop.ccSign.get().set(signId,null);
                        DynamicShop.ccSign.save();
                    }
                }
            }
        }
    }

    // 상점 표지판이 폭발하는것 방지
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        List<Block> b = event.blockList();

        for (Block bl:b)
        {
            if(bl.getType().toString().contains("WALL_SIGN"))
            {
                int x = bl.getX();
                int y = bl.getY();
                int z = bl.getZ();
                String signId = x + "_" + y + "_" + z;
                if(DynamicShop.ccSign.get().contains(signId))
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    // 상점 표지판이 불타는것 방지
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e)
    {
        ArrayList<Block> signList = new ArrayList<>();
        signList.add(e.getBlock());
        signList.add(e.getBlock().getRelative(BlockFace.EAST));
        signList.add(e.getBlock().getRelative(BlockFace.WEST));
        signList.add(e.getBlock().getRelative(BlockFace.NORTH));
        signList.add(e.getBlock().getRelative(BlockFace.SOUTH));

        for (Block b : signList)
        {
            if(b.getType().toString().contains("WALL_SIGN"))
            {
                int x = b.getX();
                int y = b.getY();
                int z = b.getZ();
                String signId = x + "_" + y + "_" + z;

                if(DynamicShop.ccSign.get().contains(signId))
                {
                    e.setCancelled(true);
                }
            }
        }
    }
}
