package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.MathUtil;
import me.sat7.dynamicshop.utilities.RotationUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;

import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.*;

public class RotationEditor extends InGameUI
{
    public RotationEditor()
    {
        uiType = UI_TYPE.RotationEditor;
    }

    private final int DATA_0 = 10;
    private final int DATA_6 = DATA_0 + 7;
    private final int CLOSE = 27;
    private final int PERIOD = 31;
    private final int TIMER = 32;
    private final int APPLY_CHANGES = 33;
    private final int TOGGLE_ENABLE = 35;

    private Player player;
    private String shopName;
    private CustomConfig shopData;

    private int currentRotation;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private String timeLore;

    private long period; // 밀리초
    private long period_new; // 밀리초
    private long nextTimer; // 밀리초
    private long nextTimer_new; // 밀리초

    private int moveTarget;

    public Inventory getGui(Player player, String shopName)
    {
        this.player = player;
        this.shopName = shopName;
        this.shopData = ShopUtil.shopConfigFiles.get(shopName);
        this.currentRotation = RotationUtil.GetCurrentRotationIndex(shopName);
        this.period = MathUtil.TickToMilliSeconds(shopData.get().getLong("Options.Rotation.Period", dayInTick));
        this.period_new = period;
        this.nextTimer = shopData.get().getLong("Options.Rotation.NextTimer");
        this.nextTimer_new = nextTimer;

        this.moveTarget = -1;

        inventory = Bukkit.createInventory(player, 36, t(player, "ROTATION_EDITOR_TITLE") + "§7 | §8" + shopName);

        RefreshUI();

        CreateCloseButton(player, CLOSE);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        player = (Player) e.getWhoClicked();

        if (e.getSlot() == TOGGLE_ENABLE)
        {
            if (e.isLeftClick())
                OnClickToggleEnable();
        } else if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopSettingGui(player, shopName);
        } else if (e.getSlot() == PERIOD)
        {
            long editAmount = 0;
            if (e.isLeftClick())
                editAmount = -hourInMilliSeconds;
            else if (e.isRightClick())
                editAmount = hourInMilliSeconds;

            if (e.isShiftClick())
                editAmount *= 10;

            period_new += editAmount;
            period_new = Clamp(period_new, hourInMilliSeconds, dayInMilliSeconds * 3);

            nextTimer_new = nextTimer - period + period_new;

            RefreshUI();
        } else if (e.getSlot() == TIMER)
        {
            long editAmount = 0;
            if (e.isLeftClick())
                editAmount = -hourInMilliSeconds;
            else if (e.isRightClick())
                editAmount = hourInMilliSeconds;

            if (!e.isShiftClick())
                editAmount /= 6;

            nextTimer_new += editAmount;
            nextTimer_new = nextTimer_new / 1000 / 60 / 10;
            nextTimer_new = nextTimer_new * 1000 * 60 * 10; // 10분단위로 내림처리.
            RefreshUI();
        } else if (e.getSlot() == APPLY_CHANGES)
        {
            OnClickApply(e.isLeftClick(), e.isRightClick());
        } else if (e.getSlot() >= DATA_0 && e.getSlot() <= DATA_6 && e.getCurrentItem() != null)
        {
            OnClickDataButton(e.getCurrentItem().getType() != Material.CHEST, e.getSlot() - DATA_0,
                    e.isLeftClick(), e.isRightClick(), e.isShiftClick());
        }
    }

    @Override
    public void RefreshUI()
    {
        if (currentRotation != -1 && System.currentTimeMillis() > nextTimer)
        {
            currentRotation = RotationUtil.GetCurrentRotationIndex(shopName);
            nextTimer = shopData.get().getLong("Options.Rotation.NextTimer");
            nextTimer_new = nextTimer;
        }

        String nextTimerString;
        if (currentRotation == -1)
        {
            nextTimerString = "-";
        } else
        {
            if (period != period_new || nextTimer != nextTimer_new)
            {
                nextTimerString = sdf.format(nextTimer_new);
            } else
            {
                nextTimerString = sdf.format(nextTimer);
            }
        }

        timeLore = t(player, "ROTATION_EDITOR.NEXT_ROTATION") +
                "\n §7" + nextTimerString +
                "\n\n" +
                t(player, "ROTATION_EDITOR.CURRENT_TIME") +
                "\n §7" + sdf.format(System.currentTimeMillis());

        UpdateToggleButton();
        UpdateSettingButtons();
        UpdateRotationDataIcons();
    }

    private void UpdateToggleButton()
    {
        // 활성화 토글
        if (currentRotation != -1)
        {
            String lore = t(player, "ROTATION_EDITOR.CLICK_TO_DISABLE") + "\n\n" + timeLore;
            CreateButton(TOGGLE_ENABLE, Material.GREEN_STAINED_GLASS, t(player, "ROTATION_EDITOR.ENABLED"), lore);
        } else
        {
            String lore = t(player, "ROTATION_EDITOR.CLICK_TO_ENABLE") + "\n\n" + timeLore;
            CreateButton(TOGGLE_ENABLE, Material.RED_STAINED_GLASS, t(player, "ROTATION_EDITOR.DISABLED"), lore);
        }
    }

    private void UpdateSettingButtons()
    {
        boolean isDirty = period != period_new || nextTimer != nextTimer_new;
        String unsavedChangesLore = isDirty ? t(player, "ROTATION_EDITOR.UNSAVED_CHANGES") : "";

        inventory.setItem(PERIOD, null);
        String periodLore = t(player, "ROTATION_EDITOR.HOUR").replace("{0}", String.valueOf(period_new / 60 / 60 / 1000));
        periodLore += "\n\n" + timeLore;
        periodLore += "\n" + unsavedChangesLore;
        periodLore += "\n" + t(player, "ROTATION_EDITOR.PERIOD_LORE_V2");
        CreateButton(PERIOD, Material.CLOCK, t(player, "ROTATION_EDITOR.PERIOD"), periodLore);

        inventory.setItem(TIMER, null);
        String timerLore = "§f" + ((nextTimer_new - nextTimer) / 1000 / 60);
        timerLore += "\n\n" + timeLore;
        timerLore += "\n" + unsavedChangesLore;
        timerLore += "\n" + t(player, "ROTATION_EDITOR.TIMER_LORE_V2");
        CreateButton(TIMER, Material.CLOCK, t(player, "ROTATION_EDITOR.TIMER"), timerLore);

        inventory.setItem(APPLY_CHANGES, null);
        String applyLore = timeLore + "\n" + unsavedChangesLore;
        applyLore += "\n" + t(player, "ROTATION_EDITOR.APPLY_CHANGES_LORE");
        CreateButton(APPLY_CHANGES, Material.CLOCK, t(player, "ROTATION_EDITOR.APPLY_CHANGES"), applyLore);
    }

    private void UpdateRotationDataIcons()
    {
        for (int i = 0; i < 27; i++)
        {
            inventory.setItem(i, null);
        }

        CustomConfig[] rotationData = RotationUtil.rotationDataMap.get(shopName);
        if (rotationData == null)
            return;

        for (int i = 0; i < 7; i++)
        {
            String slotString = "§7[" + (i + 1) + "]";

            if (rotationData[i] != null)
            {
                String lore = "§f" + RotationUtil.rotationFolderName + "/" + rotationData[i].GetFileName() + "\n";

                if (i == currentRotation)
                {
                    lore += t(player, "ROTATION_EDITOR.CURRENTLY_IN_USE") + "\n\n";
                    lore += t(player, "ROTATION_EDITOR.OPEN") + "\n";
                    lore += t(player, "ROTATION_EDITOR.MOVE") + "\n";
                    lore += t(player, "ROTATION_EDITOR.REAPPLY") + "\n";
                } else
                {
                    lore += "\n" + t(player, "ROTATION_EDITOR.APPLY_ROTATION") + "\n";
                    lore += t(player, "ROTATION_EDITOR.MOVE") + "\n";
                }

                lore += t(player, "ROTATION_EDITOR.DELETE") + "\n\n";

                lore += "§7Options.title:\n §7" + rotationData[i].get().getString("Options.title", "(empty)") + "\n";
                lore += "§7Options.lore:\n §7" + rotationData[i].get().getString("Options.lore", "(empty)") + "\n";
                lore += "§7" + (rotationData[i].get().getKeys(false).size() - 1) + " data";

                CreateButton(i + DATA_0, Material.CHEST, slotString, lore);

                if (currentRotation == i)
                {
                    CreateButton(i + DATA_0 - 9, Material.GREEN_STAINED_GLASS_PANE, " ", "");
                    CreateButton(i + DATA_0 + 9, Material.GREEN_STAINED_GLASS_PANE, " ", "");
                }
            } else
            {
                String lore = t(player, "ROTATION_EDITOR.CREATE") + "\n" + t(player, "ROTATION_EDITOR.COPY_AS_NEW");
                CreateButton(i + DATA_0, Material.BLACK_STAINED_GLASS_PANE, slotString, lore);
            }
        }
    }

    private void OnClickDataButton(boolean isEmptySlot, int dataIndex, boolean isLeftClick, boolean isRightClick, boolean isShift)
    {
        if (isEmptySlot)
        {
            // 새 로테이션 파일 생성
            if (isLeftClick)
            {
                RotationUtil.AddNewRotationData(shopName, dataIndex, true);
                RefreshUI();
            }
            else if (isRightClick)
            {
                // 이동
                if(moveTarget != -1)
                {
                    RotationUtil.OnRotationFileSlotMoved(shopName, moveTarget, dataIndex);
                    if(moveTarget == currentRotation)
                    {
                        currentRotation = dataIndex;
                        shopData.get().set("Options.Rotation.Current", currentRotation);
                    }
                    moveTarget = -1;
                }
                // 현재 상점 복사하여 새로 만들기
                else
                {
                    RotationUtil.AddNewRotationData(shopName, dataIndex, false);
                }
                RefreshUI();
            }
        } else
        {
            if (isLeftClick)
            {
                // 로테이션 적용
                if (currentRotation != dataIndex || isShift)
                {
                    RotationUtil.ApplyRotation(shopName, currentRotation, dataIndex);
                    currentRotation = dataIndex;
                    RefreshUI();
                }
                // 로테이션 적용된 상태의 상점 UI를 연다
                else
                {
                    DynaShopAPI.openShopGui(player, shopName, 0);
                }
            } else if (isRightClick)
            {
                // 로테이션 데이터 삭제
                if (isShift)
                {
                    RotationUtil.DeleteRotationFile(shopName, dataIndex);
                    if (currentRotation == dataIndex)
                    {
                        currentRotation = RotationUtil.FindNextRotationIndex(shopName, currentRotation);
                        if (currentRotation == -1)
                        {
                            RotationUtil.DisableRotation(shopName);
                        } else
                        {
                            RotationUtil.ApplyRotation(shopName, -1, currentRotation);
                        }
                    }
                    RefreshUI();
                }
                // 이동
                else
                {
                    if(moveTarget == -1)
                    {
                        moveTarget = dataIndex;
                    }
                }
            }
        }
    }

    private void OnClickToggleEnable()
    {
        if (currentRotation == -1)
        {
            currentRotation = 0;
            shopData.get().set("Options.Rotation.Current", currentRotation);

            RotationUtil.ReadRotationYMLFiles(shopName);

            if (RotationUtil.GetRotationYmlFileCount(shopName) == 0)
            {
                RotationUtil.AddNewRotationData(shopName, 0, false);
                RotationUtil.AddNewRotationData(shopName, 1, false);
            }

            long tick = MathUtil.MilliSecondsToTick(period);
            RotationUtil.ApplyRotation(shopName, -1, 0);
            RotationUtil.StartRotationTask(shopName, tick, tick);
            nextTimer = System.currentTimeMillis() + period;
            nextTimer_new = nextTimer;
            period_new = period;

            shopData.get().set("Options.Rotation.Period", tick);
            shopData.get().set("Options.Rotation.NextTimer", nextTimer);
        } else
        {
            currentRotation = -1;
            RotationUtil.DisableRotation(shopName);
        }

        shopData.save();
        RefreshUI();
    }

    private void OnClickApply(boolean isLeftClick, boolean isRightClick)
    {
        if (isLeftClick)
        {
            if (period == period_new && nextTimer == nextTimer_new)
                return;

            period = period_new;
            nextTimer = nextTimer_new;
            shopData.get().set("Options.Rotation.Period", MathUtil.MilliSecondsToTick(period));
            shopData.get().set("Options.Rotation.NextTimer", nextTimer);

            long timeLeft = nextTimer - System.currentTimeMillis();
            RotationUtil.StartRotationTask(shopName, MathUtil.MilliSecondsToTick(timeLeft), MathUtil.MilliSecondsToTick(period));

            RefreshUI();
        } else if (isRightClick)
        {
            if (period == period_new && nextTimer == nextTimer_new)
                return;

            nextTimer_new = nextTimer;
            period_new = period;
            RefreshUI();
        }
    }
}
