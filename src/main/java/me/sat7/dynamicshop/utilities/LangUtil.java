package me.sat7.dynamicshop.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LangUtil
{
    public static CustomConfig ccLang;

    private LangUtil()
    {

    }

    public static void setupLangFile(String lang)
    {
        // 한국어
        {
            ccLang.setup("Lang_V3_ko-KR", null);

            ccLang.get().addDefault("START_PAGE.EDITOR_TITLE", "§3아이콘 편집");
            ccLang.get().addDefault("START_PAGE.EDIT_NAME", "§f이름 바꾸기");
            ccLang.get().addDefault("START_PAGE.EDIT_LORE", "§f설명 바꾸기");
            ccLang.get().addDefault("START_PAGE.EDIT_ICON", "§f아이콘 바꾸기");
            ccLang.get().addDefault("START_PAGE.EDIT_ACTION", "§f실행 명령어 바꾸기");
            ccLang.get().addDefault("START_PAGE.SHOP_SHORTCUT", "§f상점으로 가는 버튼 만들기");
            ccLang.get().addDefault("START_PAGE.CREATE_DECO", "§f장식 버튼 만들기");
            ccLang.get().addDefault("START_PAGE.ENTER_SHOP_NAME", "상점 이름을 입력하세요.");
            ccLang.get().addDefault("START_PAGE.DEFAULT_SHOP_LORE", "§f§n클릭: 상점으로 가기");
            ccLang.get().addDefault("START_PAGE.ITEM_MOVE_LORE", "§e우클릭: 이동");
            ccLang.get().addDefault("START_PAGE.ITEM_COPY_LORE", "§e우클릭: 복사");
            ccLang.get().addDefault("START_PAGE.ITEM_REMOVE_LORE", "§eShift 좌클릭: 삭제");
            ccLang.get().addDefault("START_PAGE.ITEM_EDIT_LORE", "§eShift우클릭: 편집");
            ccLang.get().addDefault("START_PAGE.REMOVE", "§f제거");
            ccLang.get().addDefault("START_PAGE.REMOVE_LORE", "§f이 버튼을 시작페이지에서 제거합니다.");
            ccLang.get().addDefault("START_PAGE.ENTER_NAME", "버튼의 새 이름을 입력하세요.");
            ccLang.get().addDefault("START_PAGE.ENTER_LORE", "버튼의 새 설명을 입력하세요.");
            ccLang.get().addDefault("START_PAGE.ENTER_ICON", "버튼의 아이콘으로 사용할 아이탬 이름을 입력하세요. (영문. 대소문자 구분없음)");
            ccLang.get().addDefault("START_PAGE.ENTER_ACTION", "명령어를 '/' 제외하고 입력하세요. 버튼을 눌렀을때 이 명령어가 실행됩니다.");
            ccLang.get().addDefault("START_PAGE.ENTER_COLOR", "장식 버튼의 색상을 입력하세요. (영문)");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST_TITLE", "§3상점 목록");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST.PAGE_TITLE", "§f{curPage}/{maxPage} 페이지");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST.PAGE_LORE", "§e좌클릭: 이전 페이지\n§e우클릭: 다음 페이지");

            ccLang.get().addDefault("COLOR_PICKER_TITLE", "§3색상 선택");

            ccLang.get().addDefault("SHOP.TRADE_LORE", "§f§n클릭: 거래");
            ccLang.get().addDefault("SHOP.BUY_PRICE", "§f구매: {num}");
            ccLang.get().addDefault("SHOP.SELL_PRICE", "§f판매: {num}");
            ccLang.get().addDefault("SHOP.STOCK", "§8재고: {num}");
            ccLang.get().addDefault("SHOP.STOCK_2", "§8재고: {stock}/{max_stock}");
            ccLang.get().addDefault("SHOP.INF_STOCK", "§8무한");
            ccLang.get().addDefault("SHOP.STATIC_PRICE", "§8[고정 가격]");
            ccLang.get().addDefault("SHOP.STACKS", "§8{num} 스택");
            ccLang.get().addDefault("SHOP.ITEM_MOVE_LORE", "§e우클릭: 이동");
            ccLang.get().addDefault("SHOP.ITEM_COPY_LORE", "§e우클릭: 복사");
            ccLang.get().addDefault("SHOP.ITEM_EDIT_LORE", "§eShift우클릭: 편집");
            ccLang.get().addDefault("SHOP.DECO_DELETE_LORE", "§eShift우클릭: 삭제");
            ccLang.get().addDefault("SHOP.PAGE_TITLE", "§f{curPage}/{maxPage} 페이지");
            ccLang.get().addDefault("SHOP.PAGE_LORE_V2", "§f§n좌클릭: 이전 페이지\n§f§n우클릭: 다음 페이지\n§7인벤토리에서 아이템을 클릭하면 \n§7그 아이템이 있는 페이지로 이동합니다.");
            ccLang.get().addDefault("SHOP.GO_TO_PAGE_EDITOR", "§eShift+우: 페이지 에디터");
            ccLang.get().addDefault("SHOP.PAGE_EDIT_LORE", "§eShift 좌클릭: 페이지 삽입\n§eShift 우클릭: 페이지 삭제");
            ccLang.get().addDefault("SHOP.ITEM_MOVE_SELECTED", "아이탬 선택됨. 비어있는 칸을 우클릭하면 이동합니다.");
            ccLang.get().addDefault("SHOP.PERMISSION", "§f퍼미션:");
            ccLang.get().addDefault("SHOP.PERMISSION_ITEM", "§7 - {permission}");
            ccLang.get().addDefault("SHOP.FLAGS", "§e플래그:");
            ccLang.get().addDefault("SHOP.FLAGS_ITEM", "§e - {flag}");
            ccLang.get().addDefault("SHOP.SHOP_BAL_INF", "§f상점 계좌 무제한");
            ccLang.get().addDefault("SHOP.SHOP_BAL", "§f상점 계좌 잔액");
            ccLang.get().addDefault("SHOP.SHOP_LOCATION", "§f상점 위치: x {x}, y {y}, z {z}");
            ccLang.get().addDefault("SHOP.SHOP_LOCATION_B", "§f상점 위치: ");
            ccLang.get().addDefault("SHOP.SHOP_INFO_DASH", "§7 - ");
            ccLang.get().addDefault("SHOP.DISABLED", "§c비활성§8|§f");
            ccLang.get().addDefault("SHOP.INCOMPLETE_DATA", "불완전한 데이터");
            ccLang.get().addDefault("SHOP.INCOMPLETE_DATA_Lore", "이 아이템은 어드민이 아닌\n유저에게는 보이지 않습니다.\nIndex: ");

            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3상점 설정");
            ccLang.get().addDefault("SHOP_SETTING.LOG_TOGGLE_LORE", "§e우클릭: 로그 뷰어");
            ccLang.get().addDefault("SHOP_SETTING.MAX_PAGE", "§f최대 페이지");
            ccLang.get().addDefault("SHOP_SETTING.MAX_PAGE_LORE", "§f상점의 최대 페이지를 설정합니다");
            ccLang.get().addDefault("SHOP_SETTING.L_R_SHIFT", "§e좌: -1 우: +1 Shift: x5");
            ccLang.get().addDefault("SHOP_SETTING.FLAG", "§f플래그");
            ccLang.get().addDefault("SHOP_SETTING.SHOP_SETTINGS_LORE", "§e우클릭: 상점 편집");
            ccLang.get().addDefault("SHOP_SETTING.SIGN_SHOP_LORE", "§f표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("SHOP_SETTING.LOCAL_SHOP_LORE", "§f실제 상점 위치를 방문해야 합니다.\n§f상점의 위치를 설정해야만 합니다.");
            ccLang.get().addDefault("SHOP_SETTING.DELIVERY_CHARGE_LORE", "§f배달비를 지불하고 localshop에서 원격으로 거래합니다.");
            ccLang.get().addDefault("SHOP_SETTING.JOB_POINT_LORE", "§fJobs 플러그인의 job point로 거래합니다.");
            ccLang.get().addDefault("SHOP_SETTING.SHOW_VALUE_CHANGE_LORE", "§f가격 변화량을 표시합니다.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_STOCK", "§f재고 수량 표시를 숨깁니다.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_PRICING_TYPE", "§f가격 유형 표기를 숨깁니다.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_SHOP_BALANCE", "§f상점 계좌 잔액을 숨깁니다.");
            ccLang.get().addDefault("SHOP_SETTING.SHOW_MAX_STOCK", "§f재고 상한을 표시합니다.");
            ccLang.get().addDefault("SHOP_SETTING.HIDDEN_IN_COMMAND", "§f명령어 자동완성시 이 상점을 표시하지 않습니다.");
            ccLang.get().addDefault("SHOP_SETTING.INTEGER_ONLY", "§f구매 가격이 올림 처리됩니다.\n판매 가격은 내림 처리됩니다.");
            ccLang.get().addDefault("SHOP_SETTING.PERMISSION", "§f퍼미션");
            ccLang.get().addDefault("SHOP_SETTING.STATE", "§f상태");
            ccLang.get().addDefault("SHOP_SETTING.STATE_ENABLE", "§a활성");
            ccLang.get().addDefault("SHOP_SETTING.STATE_DISABLE", "§c비활성");

            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3아이탬 셋팅");
            ccLang.get().addDefault("ITEM_SETTING.VALUE_BUY", "§f구매가치: ");
            ccLang.get().addDefault("ITEM_SETTING.VALUE_SELL", "§f판매가치: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE", "§f구매: ");
            ccLang.get().addDefault("ITEM_SETTING.SELL_PRICE", "§f판매: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE_MIN", "§f최소 가격: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE_MAX", "§f최대 가격: ");
            ccLang.get().addDefault("ITEM_SETTING.MEDIAN", "§f중앙값: ");
            ccLang.get().addDefault("ITEM_SETTING.STOCK", "§f재고: ");
            ccLang.get().addDefault("ITEM_SETTING.MAX_STOCK", "§f재고 상한: ");
            ccLang.get().addDefault("ITEM_SETTING.MAX_STOCK_LORE", "§f재고량이 이보다 많아지면\n§f더이상 상점에 판매할 수 없게됩니다.");
            ccLang.get().addDefault("ITEM_SETTING.INF_STOCK", "무한 재고");
            ccLang.get().addDefault("ITEM_SETTING.STATIC_PRICE", "고정 가격");
            ccLang.get().addDefault("ITEM_SETTING.UNLIMITED", "무제한");
            ccLang.get().addDefault("ITEM_SETTING.MEDIAN_HELP", "§f중앙값이 작을수록 가격이 급격이 변화합니다.");
            ccLang.get().addDefault("ITEM_SETTING.TAX_IGNORED", "판매세 설정이 무시됩니다.");
            ccLang.get().addDefault("ITEM_SETTING.RECOMMEND", "§f추천 값 적용");
            ccLang.get().addDefault("ITEM_SETTING.DONE", "§f완료");
            ccLang.get().addDefault("ITEM_SETTING.DONE_LORE", "§f완료!");
            ccLang.get().addDefault("ITEM_SETTING.ROUND_DOWN", "§f내림");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_MEDIAN", "§f중앙값에 맞춤");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_STOCK", "§f재고에 맞춤");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_VALUE", "§f가격에 맞춤");
            ccLang.get().addDefault("ITEM_SETTING.CLOSE", "§f닫기");
            ccLang.get().addDefault("ITEM_SETTING.CLOSE_LORE", "§f§n클릭: 닫기");
            ccLang.get().addDefault("ITEM_SETTING.REMOVE", "§c제거");
            ccLang.get().addDefault("ITEM_SETTING.REMOVE_LORE", "§f이 아이탬을 상점에서 제거합니다.");
            ccLang.get().addDefault("ITEM_SETTING.BUY", "§3§l구매: {num}");
            ccLang.get().addDefault("ITEM_SETTING.SELL", "§3§l판매: {num}");

            ccLang.get().addDefault("TRADE_TITLE", "§3아이탬 거래");
            ccLang.get().addDefault("TRADE.TOGGLE_SELLABLE", "§e클릭: 판매전용 토글");
            ccLang.get().addDefault("TRADE.TOGGLE_BUYABLE", "§e클릭: 구매전용 토글");
            ccLang.get().addDefault("TRADE.BUY_ONLY_LORE", "§f구매만 가능한 아이탬");
            ccLang.get().addDefault("TRADE.SELL_ONLY_LORE", "§f판매만 가능한 아이탬");
            ccLang.get().addDefault("TRADE.BALANCE", "§3내 잔액");
            ccLang.get().addDefault("TRADE.PRICE", "§f구매: {num}");
            ccLang.get().addDefault("TRADE.SELL_PRICE", "§f판매: {num}");
            ccLang.get().addDefault("TRADE.BUY", "§c구매");
            ccLang.get().addDefault("TRADE.SELL", "§2판매");
            ccLang.get().addDefault("TRADE.STOCK", "§8재고: ");
            ccLang.get().addDefault("TRADE.STACKS", "§8{num} 스택");
            ccLang.get().addDefault("TRADE.INF_STOCK", "§8무한 재고");
            ccLang.get().addDefault("TRADE.SHOP_BAL_INF", "§f상점 계좌 무제한");
            ccLang.get().addDefault("TRADE.SHOP_BAL", "§3상점 계좌 잔액 \n§f{num}");
            ccLang.get().addDefault("TRADE.CLICK_TO_BUY", "§c§n클릭: {amount}개 구매");
            ccLang.get().addDefault("TRADE.CLICK_TO_SELL", "§2§n클릭: {amount}개 판매");

            ccLang.get().addDefault("PAGE_EDITOR_TITLE", "§3페이지 편집");
            ccLang.get().addDefault("PAGE_EDITOR.PREV", "§f<<");
            ccLang.get().addDefault("PAGE_EDITOR.NEXT", "§f>>");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_SUCCESS", "§f페이지가 교체 되었습니다.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_FAIL", "§f페이지 교체 실패.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_SELECTED", "§f페이지 선택되었습니다. 서로 교체 할 다른 페이지를 우클릭 하세요.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_LORE", "§e좌클릭: 페이지 이동\n§e우클릭: 페이지 교체(스왑)\n§eShift+좌: 페이지 삽입\n§eShift+우: 페이지 삭제");
            ccLang.get().addDefault("PAGE_EDITOR.PRICE", "§f구매: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.SELL_PRICE", "§f판매: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.STOCK", "§8재고: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.STACKS", "§8{num} 스택");
            ccLang.get().addDefault("PAGE_EDITOR.STATIC_PRICE", "§8[고정 가격]");
            ccLang.get().addDefault("PAGE_EDITOR.EMPTY", "§8(비어있음)");

            ccLang.get().addDefault("LOG_VIEWER_TITLE", "§3로그 뷰어");
            ccLang.get().addDefault("LOG_VIEWER.DATE", "§f날짜: ");
            ccLang.get().addDefault("LOG_VIEWER.TIME", "§f시간: ");
            ccLang.get().addDefault("LOG_VIEWER.CURRENCY", "§f화폐 유형: ");
            ccLang.get().addDefault("LOG_VIEWER.PRICE", "§f가격: ");
            ccLang.get().addDefault("LOG_VIEWER.EXPAND", "§f펼치기");
            ccLang.get().addDefault("LOG_VIEWER.COLLAPSE", "§f접기");

            ccLang.get().addDefault("LOG.LOG", "§f로그");
            ccLang.get().addDefault("LOG.CLEAR", "§f로그 삭제됨");
            ccLang.get().addDefault("LOG.SAVE", "§f로그 저장됨");
            ccLang.get().addDefault("LOG.DELETE", "§4로그 삭제");

            ccLang.get().addDefault("STOCK_SIMULATOR_TITLE", "§3재고 시뮬레이터");
            ccLang.get().addDefault("STOCK_SIMULATOR.CHANGE_SAMPLE_LORE", "§e좌, 우클릭: 아이템 변경");
            ccLang.get().addDefault("STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE", "§e우클릭: 시뮬레이터");
            ccLang.get().addDefault("STOCK_SIMULATOR.RUN_TITLE", "§f실행");
            ccLang.get().addDefault("STOCK_SIMULATOR.RUN_LORE", "§e좌클릭: 시뮬레이션 실행\n§e우클릭: 설정값을 상점에 적용합니다\n§f아이템은 영향받지 않습니다.");
            ccLang.get().addDefault("STOCK_SIMULATOR.REAL_TIME", "§a(실제 시간)");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_S", "§a{0}초 후");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_M", "§a{0}분 후");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_H", "§a{0}시간 후");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_D", "§a{0}일 후");
            ccLang.get().addDefault("STOCK_SIMULATOR.L_R_SHIFT", "§e좌: -1 우: +1 Shift: x5");
            ccLang.get().addDefault("STOCK_SIMULATOR.PRICE", "§f구매: {num}");
            ccLang.get().addDefault("STOCK_SIMULATOR.MEDIAN", "§f중앙값: {num}");
            ccLang.get().addDefault("STOCK_SIMULATOR.STOCK", "§f재고: {num}");

            ccLang.get().addDefault("PALETTE_TITLE", "§3판매할 아이탬 선택");
            ccLang.get().addDefault("PALETTE.LORE", "§e좌클릭: 추가\n§e우클릭: 장식으로 추가");
            ccLang.get().addDefault("PALETTE.SEARCH", "§f찾기");
            ccLang.get().addDefault("PALETTE.ADD_ALL", "§f모두 추가");
            ccLang.get().addDefault("PALETTE.PAGE_TITLE", "§f{curPage}/{maxPage} 페이지");
            ccLang.get().addDefault("PALETTE.PAGE_LORE", "§f§n좌클릭: 이전 페이지\n§f§n우클릭: 다음 페이지");
            ccLang.get().addDefault("PALETTE.FILTER_APPLIED", "§f필터 적용됨 : ");
            ccLang.get().addDefault("PALETTE.FILTER_LORE", "§f좌클릭: 검색\n§f우클릭: 필터 초기화\n\n§7\"BLUE_WOOL\"을 찾으려는 경우:\n§7 b w\n§7 wool\n§7 blue wool");

            ccLang.get().addDefault("QUICK_SELL_TITLE", "§3빠른 판매");
            ccLang.get().addDefault("QUICK_SELL.GUIDE_TITLE", "§3§l빠른 판매 도움말");
            ccLang.get().addDefault("QUICK_SELL.GUIDE_LORE", "§a판매할 아이탬을 좌클릭 하세요.\n§a씨프트 좌클릭으로 같은 유형의 아이탬을 모두 팝니다.\n§a우클릭으로 해당 아이탬 상점으로 이동합니다.");

            ccLang.get().addDefault("ARROW.UP", "§a⬆");
            ccLang.get().addDefault("ARROW.DOWN", "§c⬇");
            ccLang.get().addDefault("ARROW.UP_2", "§c⬆");
            ccLang.get().addDefault("ARROW.DOWN_2", "§a⬇");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§f문 여는 시간 설정");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§f문 닫는 시간 설정");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§f영업시간");
            ccLang.get().addDefault("TIME.OPEN24", "24시간 오픈");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§f상점이 문을 닫았습니다. 개점: {time}시. 현재시간: {curTime}시");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "영업시간 설정");
            ccLang.get().addDefault("TIME.CUR", "§f현재 시간: {time}시");

            ccLang.get().addDefault("STOCK_STABILIZING.SS", "§f재고 안정화");
            ccLang.get().addDefault("STOCK_STABILIZING.L_R_SHIFT", "§e좌클릭: -0.1 우클릭: +0.1 Shift: x5");
            ccLang.get().addDefault("STOCK_STABILIZING.STRENGTH_LORE_A", "§f중앙값(median)의 n%");
            ccLang.get().addDefault("STOCK_STABILIZING.STRENGTH_LORE_B", "§f중앙값(median)과의 격차의 n%");

            ccLang.get().addDefault("FLUCTUATION.FLUCTUATION", "§f무작위 재고 변동");
            ccLang.get().addDefault("FLUCTUATION.INTERVAL", "§f변화 간격");
            ccLang.get().addDefault("FLUCTUATION.INTERVAL_LORE", "§f1h = 1000틱 = 현실시간 50초");
            ccLang.get().addDefault("FLUCTUATION.STRENGTH", "§f변화 강도");
            ccLang.get().addDefault("FLUCTUATION.STRENGTH_LORE", "§f중앙값(median)의 n%");

            ccLang.get().addDefault("TAX.SALES_TAX", "§f판매세");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "전역설정 사용 ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "별도 설정");

            ccLang.get().addDefault("MESSAGE.SEARCH_ITEM", "§f찾으려는 아이템의 이름을 입력하세요.");
            ccLang.get().addDefault("MESSAGE.SEARCH_CANCELED", "§f검색 취소됨.");
            ccLang.get().addDefault("MESSAGE.INPUT_CANCELED", "§f입력 취소됨.");
            ccLang.get().addDefault("MESSAGE.DELETE_CONFIRM", "§f정말로 페이지를 삭제할까요? 'delete' 를 입력하면 삭제합니다.");
            ccLang.get().addDefault("MESSAGE.CANT_DELETE_LAST_PAGE", "§f마지막 남은 페이지를 삭제할 수 없습니다.");
            ccLang.get().addDefault("MESSAGE.SHOP_BAL_LOW", "§f상점이 돈을 충분히 가지고 있지 않습니다.");
            ccLang.get().addDefault("MESSAGE.SHOP_CREATED", "§f상점 생성됨!");
            ccLang.get().addDefault("MESSAGE.SHOP_DELETED", "§f상점 제거됨!");
            ccLang.get().addDefault("MESSAGE.OUT_OF_STOCK", "§f재고 없음!");
            ccLang.get().addDefault("MESSAGE.BUY_SUCCESS", "§f{item} {amount}개를 {price}에 구매함. 잔액: {bal}");
            ccLang.get().addDefault("MESSAGE.SELL_SUCCESS", "§f{item} {amount}개를 {price}에 판매함. 잔액: {bal}");
            ccLang.get().addDefault("MESSAGE.BUY_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 구매함. 남은포인트: {bal}");
            ccLang.get().addDefault("MESSAGE.SELL_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 판매함. 남은포인트: {bal}");
            ccLang.get().addDefault("MESSAGE.QSELL_NA", "§f해당 아이탬을 취급하는 상점이 없습니다.");
            ccLang.get().addDefault("MESSAGE.DELIVERY_CHARGE", "§f배달비: {fee}");
            ccLang.get().addDefault("MESSAGE.DELIVERY_CHARGE_NA", "§f다른 월드로 배달할 수 없습니다.");
            ccLang.get().addDefault("MESSAGE.NOT_ENOUGH_MONEY", "§f돈이 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("MESSAGE.NOT_ENOUGH_POINT", "§f포인트가 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("MESSAGE.NO_ITEM_TO_SELL", "§f판매 할 아이탬이 없습니다.");
            ccLang.get().addDefault("MESSAGE.INVENTORY_FULL", "§4인벤토리에 빈 공간이 없습니다!");
            ccLang.get().addDefault("MESSAGE.IRREVERSIBLE", "§f이 행동은 되돌릴 수 없습니다!");
            ccLang.get().addDefault("MESSAGE.ITEM_ADDED", "아이탬 추가됨!");
            ccLang.get().addDefault("MESSAGE.ITEM_UPDATED", "아이탬 수정됨!");
            ccLang.get().addDefault("MESSAGE.ITEM_DELETED", "아이탬 제거됨!");
            ccLang.get().addDefault("MESSAGE.CHANGES_APPLIED", "변경사항 적용됨. 새로운 값: ");
            ccLang.get().addDefault("MESSAGE.CHANGES_APPLIED_2", "변경사항 적용됨");
            ccLang.get().addDefault("MESSAGE.RECOMMEND_APPLIED", "추천 값 적용됨. {playerNum}명 기준입니다. config파일에서 이 값을 바꿀 수 있습니다.");
            ccLang.get().addDefault("MESSAGE.TRANSFER_SUCCESS", "송금 완료");
            ccLang.get().addDefault("MESSAGE.PURCHASE_REJECTED", "상점에 이 아이템이 너무 많습니다. 지금은 팔 수 없습니다.");
            ccLang.get().addDefault("MESSAGE.CLICK_YOUR_ITEM_START_PAGE", "인벤토리의 아이템을 클릭하면 가장 좋은 조건의 상점으로 이동합니다.\n좌클릭: 구매   우클릭: 판매");
            ccLang.get().addDefault("MESSAGE.MOVE_TO_BEST_SHOP_BUY", "{item}을 가장 저렴하게 살 수 있는 상점으로 이동했습니다.");
            ccLang.get().addDefault("MESSAGE.MOVE_TO_BEST_SHOP_SELL", "{item}을 가장 비싸게 팔 수 있는 상점으로 이동했습니다.");
            ccLang.get().addDefault("MESSAGE.SHOP_IS_CLOSED_BY_ADMIN", "이 상점은 서버 관리자에 의해 닫혔습니다.");
            ccLang.get().addDefault("MESSAGE.SHOP_DISABLED", "이 상점은 비황성화된 상태입니다. 어드민이 아닌 유저는 접근할 수 없습니다. 상점 설정에서 활성화 할 수 있습니다.");

            ccLang.get().addDefault("HELP.TITLE", "§f도움말: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "상점을 엽니다.");
            ccLang.get().addDefault("HELP.CMD", "명령어 도움말 표시 토글.");
            ccLang.get().addDefault("HELP.CREATE_SHOP", "상점을 새로 만듭니다.");
            ccLang.get().addDefault("HELP.CREATE_SHOP_2", "퍼미션(나중에 바꿀 수 있습니다.)\n   true: dshop.user.shop.상점이름\n   false: 아무나 접근가능(기본값)\n   임의 입력: 해당 퍼미션 필요");
            ccLang.get().addDefault("HELP.DELETE_SHOP", "기존의 상점을 제거합니다.");
            ccLang.get().addDefault("HELP.SHOP_ADD_HAND", "손에 들고 있는 아이탬을 상점에 추가합니다.");
            ccLang.get().addDefault("HELP.SHOP_ADD_ITEM", "상점에 아이탬을 추가합니다.");
            ccLang.get().addDefault("HELP.SHOP_EDIT", "상점에 있는 아이탬을 수정합니다.");
            ccLang.get().addDefault("HELP.PRICE", "§7가격은 다음과 같이 계산됩니다: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == 고정가격     stock<0 == 무한재고");
            ccLang.get().addDefault("HELP.EDIT_ALL", "상점의 모든 아이탬을 한번에 수정합니다.");
            ccLang.get().addDefault("HELP.EDIT_ALL_2", "§c주의. 값이 유효한지는 확인하지 않음.");
            ccLang.get().addDefault("HELP.RELOAD", "플러그인을 재시작 합니다.");
            ccLang.get().addDefault("HELP.RELOADED", "플러그인 리로드됨!");
            ccLang.get().addDefault("HELP.USAGE", "사용법");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item}(은)는 이미 판매중임.\n   {info}\n   명령어를 입력하면 값이 수정됩니다.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item}의 현재 설정:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f인자를 0으로 입력하면 이 아이탬을 상점에서 §4제거§f합니다.");
            ccLang.get().addDefault("HELP.QSELL", "§f빠르게 아이탬을 판매합니다.");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "장기간 접속하지 않은 유저의 데이터를 삭제합니다.");
            ccLang.get().addDefault("HELP.ACCOUNT", "상점의 계좌 잔액을 설정합니다. -1 = 무제한");
            ccLang.get().addDefault("HELP.SET_TO_REC_ALL", "§e상점의 모든 아이템 설정값을 권장값으로 §c초기화§e합니다.");
            ccLang.get().addDefault("HELP.SHOP_ENABLE", "상점을 활성화 또는 비활성화 합니다.");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6플레이어 uuid를 찾을 수 없습니다. 상점 이용 불가능.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "상점에 해당 아이탬이 존재하지 않습니다.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "사용할 수 없는 아이탬 입니다.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§e권한이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "잘못된 명령어 사용법. 도움말을 확인하세요.");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "상점에 빈 공간이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "인자의 유형이 잘못 입력되었습니다.");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "인자값이 0보다 커야 합니다.");
            ccLang.get().addDefault("ERR.WRONG_ITEM_NAME", "유효하지 않은 아이탬 이름입니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "아이탬을 손에 들고 있어야 합니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§o아이탬을 손에 들고 있어야 합니다!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§f해당 상점을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "해당 이름을 가진 상점이 이미 존재합니다.");
            ccLang.get().addDefault("ERR.SIGN_SHOP_REMOTE_ACCESS", "해당 상점은 표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("ERR.LOCAL_SHOP_REMOTE_ACCESS", "해당 상점은 직접 방문해야만 사용할 수 있습니다.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "최대 가격은 최소 가격보다 커야합니다.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "기본 가격은 최소 가격과 최대 가격 사이의 값이어야 합니다.");
            ccLang.get().addDefault("ERR.NO_RECOMMEND_DATA", "Worth.yml 파일에 이 아이탬의 정보가 없습니다.");
            ccLang.get().addDefault("ERR.JOBS_REBORN_NOT_FOUND", "Jobs reborn 플러그인을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} 상점은 무한계좌 상점입니다.");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "두 상점이 서로 다른 통화를 사용합니다.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "해당 플레이어를 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "상점 둘 중 하나는 실제 계좌이어야 합니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "목표 상점은 실제 계좌를 가지고 있어야 합니다.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "계층 구조를 이루는것은 금지되어 있습니다. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE", "§eCreative mode 에서 이 명령어를 사용할 수 없습니다. 권한이 없습니다.");
            ccLang.get().addDefault("ERR.FILE_CREATE_FAIL", "§e파일 생성에 실패했습니다.");
            ccLang.get().addDefault("ERR.INVALID_TRANSACTION", "이 거래는 더이상 유효하지 않습니다. 문제가 반복되면 서버 관리자에게 문의하세요.");

            ccLang.get().addDefault("ON", "켜짐");
            ccLang.get().addDefault("OFF", "꺼짐");
            ccLang.get().addDefault("SET", "설정");
            ccLang.get().addDefault("UNSET", "설정해제");
            ccLang.get().addDefault("NULL(OPEN)", "없음 (모두에게 열려있음)");
            ccLang.get().addDefault("CUR_STATE", "현재상태");
            ccLang.get().addDefault("CLICK", "클릭");
            ccLang.get().addDefault("LMB", "좌클릭");
            ccLang.get().addDefault("RMB", "우클릭");
            ccLang.get().addDefault("CLOSE", "§f닫기");
            ccLang.get().addDefault("CLOSE_LORE", "§f§n클릭: 닫기");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        // 영어
        {
            ccLang.setup("Lang_V3_en-US", null);

            ccLang.get().addDefault("START_PAGE.EDITOR_TITLE", "§3Edit icon");
            ccLang.get().addDefault("START_PAGE.EDIT_NAME", "§fRename");
            ccLang.get().addDefault("START_PAGE.EDIT_LORE", "§fChange lore");
            ccLang.get().addDefault("START_PAGE.EDIT_ICON", "§fChange icon");
            ccLang.get().addDefault("START_PAGE.EDIT_ACTION", "§fChange command");
            ccLang.get().addDefault("START_PAGE.SHOP_SHORTCUT", "§fCreate shop button");
            ccLang.get().addDefault("START_PAGE.CREATE_DECO", "§fCreate decorative button");
            ccLang.get().addDefault("START_PAGE.ENTER_SHOP_NAME", "Please enter shop name");
            ccLang.get().addDefault("START_PAGE.DEFAULT_SHOP_LORE", "§f§nClick: go to shop");
            ccLang.get().addDefault("START_PAGE.ITEM_MOVE_LORE", "§eRMB: Move");
            ccLang.get().addDefault("START_PAGE.ITEM_COPY_LORE", "§eRMB: Copy");
            ccLang.get().addDefault("START_PAGE.ITEM_REMOVE_LORE", "§eShift LMB: Remove");
            ccLang.get().addDefault("START_PAGE.ITEM_EDIT_LORE", "§eShift RMB: Edit");
            ccLang.get().addDefault("START_PAGE.REMOVE", "§fRemove");
            ccLang.get().addDefault("START_PAGE.REMOVE_LORE", "§fRemove this button from the start page.");
            ccLang.get().addDefault("START_PAGE.ENTER_NAME", "Enter a new name for the button.");
            ccLang.get().addDefault("START_PAGE.ENTER_LORE", "Enter a new description for the button.");
            ccLang.get().addDefault("START_PAGE.ENTER_ICON", "Enter the name of the item to be used as the icon for the button. (English, case insensitive)");
            ccLang.get().addDefault("START_PAGE.ENTER_ACTION", "Enter the command without '/'. This command is executed when the button is pressed.");
            ccLang.get().addDefault("START_PAGE.ENTER_COLOR", "Enter a color for the decorative button. (English)");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST_TITLE", "§3Shop List");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST.PAGE_TITLE", "§f{curPage}/{maxPage} Page");
            ccLang.get().addDefault("START_PAGE.SHOP_LIST.PAGE_LORE", "§eLMB: Previous page\n§eRMB: Next page");

            ccLang.get().addDefault("COLOR_PICKER_TITLE", "§3Color Picker");

            ccLang.get().addDefault("SHOP.TRADE_LORE", "§f§nClick: Trade");
            ccLang.get().addDefault("SHOP.BUY_PRICE", "§fBuy: {num}");
            ccLang.get().addDefault("SHOP.SELL_PRICE", "§fSell: {num}");
            ccLang.get().addDefault("SHOP.STOCK", "§8Stock: {num}");
            ccLang.get().addDefault("SHOP.STOCK_2", "§8Stock: {stock}/{max_stock}");
            ccLang.get().addDefault("SHOP.INF_STOCK", "§8Infinite");
            ccLang.get().addDefault("SHOP.STATIC_PRICE", "§8[Fixed price]");
            ccLang.get().addDefault("SHOP.STACKS", "§8{num} Stacks");
            ccLang.get().addDefault("SHOP.ITEM_MOVE_LORE", "§eRMB: Move");
            ccLang.get().addDefault("SHOP.ITEM_COPY_LORE", "§eRMB: Copy");
            ccLang.get().addDefault("SHOP.ITEM_EDIT_LORE", "§eShiftRMB: Edit");
            ccLang.get().addDefault("SHOP.DECO_DELETE_LORE", "§eShiftRMB: Remove");
            ccLang.get().addDefault("SHOP.PAGE_TITLE", "§f{curPage}/{maxPage} Page");
            ccLang.get().addDefault("SHOP.PAGE_LORE_V2", "§f§nLMB: Previous page\n§f§nRMB: Next page\n§7Clicking on your item will\n§7take you to the page where\n§7that item is located.");
            ccLang.get().addDefault("SHOP.GO_TO_PAGE_EDITOR", "§eShift RMB: Page Editor");
            ccLang.get().addDefault("SHOP.PAGE_EDIT_LORE", "§eShift LMB: Insert page\n§eShift RMB: Delete page");
            ccLang.get().addDefault("SHOP.ITEM_MOVE_SELECTED", "Item selected. Right-click on an empty field to move it.");
            ccLang.get().addDefault("SHOP.PERMISSION", "§fPermission:");
            ccLang.get().addDefault("SHOP.PERMISSION_ITEM", "§7 - {permission}");
            ccLang.get().addDefault("SHOP.FLAGS", "§eFlag:");
            ccLang.get().addDefault("SHOP.FLAGS_ITEM", "§e - {flag}");
            ccLang.get().addDefault("SHOP.SHOP_BAL_INF", "§fUnlimited");
            ccLang.get().addDefault("SHOP.SHOP_BAL", "§fShop account balance");
            ccLang.get().addDefault("SHOP.SHOP_LOCATION", "§fShop location: x {x}, y {y}, z {z}");
            ccLang.get().addDefault("SHOP.SHOP_LOCATION_B", "§fShop location: ");
            ccLang.get().addDefault("SHOP.SHOP_INFO_DASH", "§7 - ");
            ccLang.get().addDefault("SHOP.DISABLED", "§cDisabled§8|§f");
            ccLang.get().addDefault("SHOP.INCOMPLETE_DATA", "INCOMPLETE DATA");
            ccLang.get().addDefault("SHOP.INCOMPLETE_DATA_Lore", "This item is not visible\nto non-op users.\nIndex: ");

            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3Shop Settings");
            ccLang.get().addDefault("SHOP_SETTING.LOG_TOGGLE_LORE", "§eRMB: Log Viewer");
            ccLang.get().addDefault("SHOP_SETTING.MAX_PAGE", "§fMax page");
            ccLang.get().addDefault("SHOP_SETTING.MAX_PAGE_LORE", "§fSets the maximum page for the shop.");
            ccLang.get().addDefault("SHOP_SETTING.L_R_SHIFT", "§eLMB: -1 RMB: +1 Shift: x5");
            ccLang.get().addDefault("SHOP_SETTING.FLAG", "§fFlag");
            ccLang.get().addDefault("SHOP_SETTING.SHOP_SETTINGS_LORE", "§eRMB: Shop Settings");
            ccLang.get().addDefault("SHOP_SETTING.SIGN_SHOP_LORE", "§fOnly accessible via sign.");
            ccLang.get().addDefault("SHOP_SETTING.LOCAL_SHOP_LORE", "§fMust visit actual store locations.");
            ccLang.get().addDefault("SHOP_SETTING.DELIVERY_CHARGE_LORE", "§fYou can pay for delivery without \n§fhaving to go to the shop location to transact.");
            ccLang.get().addDefault("SHOP_SETTING.JOB_POINT_LORE", "§fTrade with job points. \n§fRequires 'Jobs Reborn' plugin");
            ccLang.get().addDefault("SHOP_SETTING.SHOW_VALUE_CHANGE_LORE", "§fShows the amount of change in price.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_STOCK", "§fHide stock.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_PRICING_TYPE", "§fHide price type.");
            ccLang.get().addDefault("SHOP_SETTING.HIDE_SHOP_BALANCE", "§fHide shop account balance.");
            ccLang.get().addDefault("SHOP_SETTING.SHOW_MAX_STOCK", "§fShow max stock.");
            ccLang.get().addDefault("SHOP_SETTING.HIDDEN_IN_COMMAND", "§fDon't show this store\n§fin command autocomplete.");
            ccLang.get().addDefault("SHOP_SETTING.INTEGER_ONLY", "§fThe purchase price will be rounded up.\nThe sale price will be rounded down.");
            ccLang.get().addDefault("SHOP_SETTING.PERMISSION", "§fPermission");
            ccLang.get().addDefault("SHOP_SETTING.STATE", "§fState");
            ccLang.get().addDefault("SHOP_SETTING.STATE_ENABLE", "§aEnable");
            ccLang.get().addDefault("SHOP_SETTING.STATE_DISABLE", "§cDisable");

            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3Item Settings");
            ccLang.get().addDefault("ITEM_SETTING.VALUE_BUY", "§fPurchase value: ");
            ccLang.get().addDefault("ITEM_SETTING.VALUE_SELL", "§fSales value: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE", "§fBuy: ");
            ccLang.get().addDefault("ITEM_SETTING.SELL_PRICE", "§fSell: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE_MIN", "§fMinimum price: ");
            ccLang.get().addDefault("ITEM_SETTING.PRICE_MAX", "§fMaximum price: ");
            ccLang.get().addDefault("ITEM_SETTING.MEDIAN", "§fMedian: ");
            ccLang.get().addDefault("ITEM_SETTING.STOCK", "§fStock: ");
            ccLang.get().addDefault("ITEM_SETTING.MAX_STOCK", "§fMax stock: ");
            ccLang.get().addDefault("ITEM_SETTING.MAX_STOCK_LORE", "§fIf the stock exceeds this,\n§fthe shop will refuse to purchase.");
            ccLang.get().addDefault("ITEM_SETTING.INF_STOCK", "Infinite stock");
            ccLang.get().addDefault("ITEM_SETTING.STATIC_PRICE", "Fixed price");
            ccLang.get().addDefault("ITEM_SETTING.UNLIMITED", "Unlimited");
            ccLang.get().addDefault("ITEM_SETTING.MEDIAN_HELP", "§fThe smaller the median,\n§fthe steeper the price change.");
            ccLang.get().addDefault("ITEM_SETTING.TAX_IGNORED", "Sales tax settings are ignored.");
            ccLang.get().addDefault("ITEM_SETTING.RECOMMEND", "§fApply recommended values");
            ccLang.get().addDefault("ITEM_SETTING.DONE", "§fDone");
            ccLang.get().addDefault("ITEM_SETTING.DONE_LORE", "§fDone!");
            ccLang.get().addDefault("ITEM_SETTING.ROUND_DOWN", "§fRound down");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_MEDIAN", "§fSet to median");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_STOCK", "§fSet to stock");
            ccLang.get().addDefault("ITEM_SETTING.SET_TO_VALUE", "§fSet to value");
            ccLang.get().addDefault("ITEM_SETTING.CLOSE", "§fClose");
            ccLang.get().addDefault("ITEM_SETTING.CLOSE_LORE", "§f§nClick: Close");
            ccLang.get().addDefault("ITEM_SETTING.REMOVE", "§cRemove");
            ccLang.get().addDefault("ITEM_SETTING.REMOVE_LORE", "§fRemove this item from the shop.");
            ccLang.get().addDefault("ITEM_SETTING.BUY", "§3§lBuy: {num}");
            ccLang.get().addDefault("ITEM_SETTING.SELL", "§3§lSell: {num}");

            ccLang.get().addDefault("TRADE_TITLE", "§3Trade");
            ccLang.get().addDefault("TRADE.TOGGLE_SELLABLE", "§eClick: Sale only toggle");
            ccLang.get().addDefault("TRADE.TOGGLE_BUYABLE", "§eClick: Purchase Only Toggle");
            ccLang.get().addDefault("TRADE.BUY_ONLY_LORE", "§fThis item cannot be sold.");
            ccLang.get().addDefault("TRADE.SELL_ONLY_LORE", "§fThis item cannot be purchased.");
            ccLang.get().addDefault("TRADE.BALANCE", "§3My balance");
            ccLang.get().addDefault("TRADE.PRICE", "§fBuy: {num}");
            ccLang.get().addDefault("TRADE.SELL_PRICE", "§fSell: {num}");
            ccLang.get().addDefault("TRADE.BUY", "§cBuy");
            ccLang.get().addDefault("TRADE.SELL", "§2Sell");
            ccLang.get().addDefault("TRADE.STOCK", "§8Stock: ");
            ccLang.get().addDefault("TRADE.STACKS", "§8{num} Stacks");
            ccLang.get().addDefault("TRADE.INF_STOCK", "§8Infinite");
            ccLang.get().addDefault("TRADE.SHOP_BAL_INF", "§fUnlimited");
            ccLang.get().addDefault("TRADE.SHOP_BAL", "§3Shop account balance \n§f{num}");
            ccLang.get().addDefault("TRADE.CLICK_TO_BUY", "§c§nClick: Buy {amount}");
            ccLang.get().addDefault("TRADE.CLICK_TO_SELL", "§2§nClick: Sell {amount}");

            ccLang.get().addDefault("PAGE_EDITOR_TITLE", "§3Page Editor");
            ccLang.get().addDefault("PAGE_EDITOR.PREV", "§f<<");
            ccLang.get().addDefault("PAGE_EDITOR.NEXT", "§f>>");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_SUCCESS", "§fThe page has been replaced.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_FAIL", "§fPage replacement failed.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_SWAP_SELECTED", "§fPage has been selected. Right-click on the other pages to be replaced.");
            ccLang.get().addDefault("PAGE_EDITOR.PAGE_LORE", "§eLMB: Open page\n§eRMB: Swap\n§eShift LMB: Insert\n§eShift RMB: Delete");
            ccLang.get().addDefault("PAGE_EDITOR.PRICE", "§fBuy: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.SELL_PRICE", "§fSell: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.STOCK", "§8Stock: {num}");
            ccLang.get().addDefault("PAGE_EDITOR.STACKS", "§8{num} Stakcs");
            ccLang.get().addDefault("PAGE_EDITOR.STATIC_PRICE", "§8[Fixed price]");
            ccLang.get().addDefault("PAGE_EDITOR.EMPTY", "§8(empty)");

            ccLang.get().addDefault("LOG_VIEWER_TITLE", "§3Log Viewer");
            ccLang.get().addDefault("LOG_VIEWER.DATE", "§fDate: ");
            ccLang.get().addDefault("LOG_VIEWER.TIME", "§fTime: ");
            ccLang.get().addDefault("LOG_VIEWER.CURRENCY", "§fCurrency: ");
            ccLang.get().addDefault("LOG_VIEWER.PRICE", "§fPrice: ");
            ccLang.get().addDefault("LOG_VIEWER.EXPAND", "§fExpand");
            ccLang.get().addDefault("LOG_VIEWER.COLLAPSE", "§fCollapse");

            ccLang.get().addDefault("LOG.LOG", "§fLog");
            ccLang.get().addDefault("LOG.CLEAR", "§fLog deleted");
            ccLang.get().addDefault("LOG.SAVE", "§fLog saved");
            ccLang.get().addDefault("LOG.DELETE", "§4Delete log");

            ccLang.get().addDefault("STOCK_SIMULATOR_TITLE", "§3Stock Simulator");
            ccLang.get().addDefault("STOCK_SIMULATOR.CHANGE_SAMPLE_LORE", "§eLMB, RMB: Change Item");
            ccLang.get().addDefault("STOCK_SIMULATOR.SIMULATOR_BUTTON_LORE", "§eRMB: Simulator");
            ccLang.get().addDefault("STOCK_SIMULATOR.RUN_TITLE", "§fRun");
            ccLang.get().addDefault("STOCK_SIMULATOR.RUN_LORE", "§eLMB: Run simulation\n§eRMB: Apply the settings to the shop\n§fItems are not affected.");
            ccLang.get().addDefault("STOCK_SIMULATOR.REAL_TIME", "§a(real time)");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_S", "§aAfter {0} seconds");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_M", "§aAfter {0} minutes");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_H", "§aAfter {0} hours");
            ccLang.get().addDefault("STOCK_SIMULATOR.AFTER_D", "§aAfter {0} days");
            ccLang.get().addDefault("STOCK_SIMULATOR.L_R_SHIFT", "§eLMB: -1 RMB: +1 Shift: x5");
            ccLang.get().addDefault("STOCK_SIMULATOR.PRICE", "§fPurchase price: {num}");
            ccLang.get().addDefault("STOCK_SIMULATOR.MEDIAN", "§fMedian: {num}");
            ccLang.get().addDefault("STOCK_SIMULATOR.STOCK", "§fStock: {num}");

            ccLang.get().addDefault("PALETTE_TITLE", "§3Select item to sell");
            ccLang.get().addDefault("PALETTE.LORE", "§eLMB: Add\n§eRMB: Add as decoration");
            ccLang.get().addDefault("PALETTE.SEARCH", "§fSearch");
            ccLang.get().addDefault("PALETTE.ADD_ALL", "§fAdd all");
            ccLang.get().addDefault("PALETTE.PAGE_TITLE", "§f{curPage}/{maxPage} page");
            ccLang.get().addDefault("PALETTE.PAGE_LORE", "§f§nLMB: Prev\n§f§nRMB: Next");
            ccLang.get().addDefault("PALETTE.FILTER_APPLIED", "§fFilter Applied : ");
            ccLang.get().addDefault("PALETTE.FILTER_LORE", "§eLMB: Search\n§eRMB: Clear filter\n\n§7Example for finding \"BLUE_WOOL\":\n§7 b w\n§7 wool\n§7 blue wool");

            ccLang.get().addDefault("QUICK_SELL_TITLE", "§3Quick Sell");
            ccLang.get().addDefault("QUICK_SELL.GUIDE_TITLE", "§3§lQuick Sell Guide");
            ccLang.get().addDefault("QUICK_SELL.GUIDE_LORE", "§aLeft-click the item you want to sell.\n§aShift left click to sell all items of the same type.\n§aRight-click to go to the item shop.");

            ccLang.get().addDefault("ARROW.UP", "§a⬆");
            ccLang.get().addDefault("ARROW.DOWN", "§c⬇");
            ccLang.get().addDefault("ARROW.UP_2", "§c⬆");
            ccLang.get().addDefault("ARROW.DOWN_2", "§a⬇");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§fSet opening time");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§fSet closing time");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§fOpening hours");
            ccLang.get().addDefault("TIME.OPEN24", "Open 24 hours");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§fThe shop is closed. It opens at {time} o'clock. {curTime} o'clock now.");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "Set business hours");
            ccLang.get().addDefault("TIME.CUR", "§fCurrent time: {time}h");

            ccLang.get().addDefault("STOCK_STABILIZING.SS", "§fStock stabilization");
            ccLang.get().addDefault("STOCK_STABILIZING.L_R_SHIFT", "§eLMB: -0.1 RMB: +0.1 Shift: x5");
            ccLang.get().addDefault("STOCK_STABILIZING.STRENGTH_LORE_A", "§fn% of median");
            ccLang.get().addDefault("STOCK_STABILIZING.STRENGTH_LORE_B", "§fn% of the gap with median");

            ccLang.get().addDefault("FLUCTUATION.FLUCTUATION", "§fStock fluctuation");
            ccLang.get().addDefault("FLUCTUATION.INTERVAL", "§fInterval");
            ccLang.get().addDefault("FLUCTUATION.INTERVAL_LORE", "§f1h = 1000ticks = real time 50s");
            ccLang.get().addDefault("FLUCTUATION.STRENGTH", "§fStrength");
            ccLang.get().addDefault("FLUCTUATION.STRENGTH_LORE", "§fn% of median");

            ccLang.get().addDefault("TAX.SALES_TAX", "§fSales tax");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "Use global settings ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "Set separately");

            ccLang.get().addDefault("MESSAGE.SEARCH_ITEM", "§fEnter the name of the item you are looking for.");
            ccLang.get().addDefault("MESSAGE.SEARCH_CANCELED", "§fSearch Canceled.");
            ccLang.get().addDefault("MESSAGE.INPUT_CANCELED", "§fInput canceled.");
            ccLang.get().addDefault("MESSAGE.DELETE_CONFIRM", "§fAre you sure you want to delete the page? Enter 'delete' to delete.");
            ccLang.get().addDefault("MESSAGE.CANT_DELETE_LAST_PAGE", "§fThe last remaining page cannot be deleted.");
            ccLang.get().addDefault("MESSAGE.SHOP_BAL_LOW", "§fThe shop doesn't have enough money.");
            ccLang.get().addDefault("MESSAGE.SHOP_CREATED", "§fShop created!");
            ccLang.get().addDefault("MESSAGE.SHOP_DELETED", "§fShop deleted!");
            ccLang.get().addDefault("MESSAGE.OUT_OF_STOCK", "§fOut of stock!");
            ccLang.get().addDefault("MESSAGE.BUY_SUCCESS", "§fBought {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("MESSAGE.SELL_SUCCESS", "§fSold {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("MESSAGE.BUY_SUCCESS_JP", "§fBought {item} x{amount} for {price}points. Remaining points: {bal}");
            ccLang.get().addDefault("MESSAGE.SELL_SUCCESS_JP", "§fSold {item} x{amount} for {price}points. Remaining points: {bal}");
            ccLang.get().addDefault("MESSAGE.QSELL_NA", "§fThere is no shop that handles this item.");
            ccLang.get().addDefault("MESSAGE.DELIVERY_CHARGE", "§fDelivery fee: {fee}");
            ccLang.get().addDefault("MESSAGE.DELIVERY_CHARGE_NA", "§fIt cannot be delivered to another world.");
            ccLang.get().addDefault("MESSAGE.NOT_ENOUGH_MONEY", "§fNot enough money. balance: {bal}");
            ccLang.get().addDefault("MESSAGE.NOT_ENOUGH_POINT", "§fNot enough points. balance: {bal}");
            ccLang.get().addDefault("MESSAGE.NO_ITEM_TO_SELL", "§fThere are no items for sale.");
            ccLang.get().addDefault("MESSAGE.INVENTORY_FULL", "§4There are no empty spaces in your inventory!");
            ccLang.get().addDefault("MESSAGE.IRREVERSIBLE", "§fThis action is irreversible!");
            ccLang.get().addDefault("MESSAGE.ITEM_ADDED", "Item added!");
            ccLang.get().addDefault("MESSAGE.ITEM_UPDATED", "Item updated!");
            ccLang.get().addDefault("MESSAGE.ITEM_DELETED", "Item deleted!");
            ccLang.get().addDefault("MESSAGE.CHANGES_APPLIED", "Changes applied. New values:");
            ccLang.get().addDefault("MESSAGE.CHANGES_APPLIED_2", "Changes applied");
            ccLang.get().addDefault("MESSAGE.RECOMMEND_APPLIED", "Recommended value applied. It is based on {playerNum}players. You can change this value in the config file.");
            ccLang.get().addDefault("MESSAGE.TRANSFER_SUCCESS", "Remittance completed");
            ccLang.get().addDefault("MESSAGE.PURCHASE_REJECTED", "There are too many of these items in the shop. Can't sell it now.");
            ccLang.get().addDefault("MESSAGE.CLICK_YOUR_ITEM_START_PAGE", "Click on an item in your inventory to find the shop with the best deal.\nLMB: Buy   RMB: Sell");
            ccLang.get().addDefault("MESSAGE.MOVE_TO_BEST_SHOP_BUY", "Moved to the shop where you can buy {item} at the lowest price.");
            ccLang.get().addDefault("MESSAGE.MOVE_TO_BEST_SHOP_SELL", "Moved to the shop where you can sell {item} at the highest price.");
            ccLang.get().addDefault("MESSAGE.SHOP_IS_CLOSED_BY_ADMIN", "This shop is currently closed by the server administrator.");
            ccLang.get().addDefault("MESSAGE.SHOP_DISABLED", "This shop is currently disabled. Non-admin users cannot use it. You can enable it in the shop settings.");

            ccLang.get().addDefault("HELP.TITLE", "§fHelp: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "Open shop");
            ccLang.get().addDefault("HELP.CMD", "Toggle display of command help.");
            ccLang.get().addDefault("HELP.CREATE_SHOP", "Create a new shop.");
            ccLang.get().addDefault("HELP.CREATE_SHOP_2", "Permissions (can be changed later)\n   true: dshop.user.shop.shopName\n   false: Anyone can access (default)\n   Arbitrary value: Requires permission");
            ccLang.get().addDefault("HELP.DELETE_SHOP", "Remove existing stores.");
            ccLang.get().addDefault("HELP.SHOP_ADD_HAND", "Adds the item in hand to the shop.");
            ccLang.get().addDefault("HELP.SHOP_ADD_ITEM", "Add item to the shop.");
            ccLang.get().addDefault("HELP.SHOP_EDIT", "Edit item in the store.");
            ccLang.get().addDefault("HELP.PRICE", "§7Price calculation formula: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == Fixed price     stock<0 == Infinite stock");
            ccLang.get().addDefault("HELP.EDIT_ALL", "Modify all items in the shop at once.");
            ccLang.get().addDefault("HELP.EDIT_ALL_2", "§cCaution. Not checking if the value is valid.");
            ccLang.get().addDefault("HELP.RELOAD", "Reload the plugin.");
            ccLang.get().addDefault("HELP.RELOADED", "Plugin reloaded");
            ccLang.get().addDefault("HELP.USAGE", "Usage");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item} is already on sale.\n   {info}\n   Entering a command modifies the value.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item}'s current settings:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§fEntering an argument of 0 will §4remove§f this item from the store.");
            ccLang.get().addDefault("HELP.QSELL", "§fSell items quickly.");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "Delete long-term inactive user data");
            ccLang.get().addDefault("HELP.ACCOUNT", "Sets the account balance of the shop. -1 = unlimited");
            ccLang.get().addDefault("HELP.SET_TO_REC_ALL", "§cResets§e all item settings in the store to the recommended values.");
            ccLang.get().addDefault("HELP.SHOP_ENABLE", "Enables or disables the shop.");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6Player uuid not found. Shop unavailable.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "The item does not exist in the store.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "This is a prohibited item.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§eYou do not have permission.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "Incorrect command usage.");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "There is no empty space in the shop.");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "Invalid argument type");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "The argument value must be greater than 0.");
            ccLang.get().addDefault("ERR.WRONG_ITEM_NAME", "Invalid item name.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "You must hold the item in your hand.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§oYou must have the item in your hand!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§fThe shop could not be found.");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "A store with that name already exists.");
            ccLang.get().addDefault("ERR.SIGN_SHOP_REMOTE_ACCESS", "The shop is only accessible via sign.");
            ccLang.get().addDefault("ERR.LOCAL_SHOP_REMOTE_ACCESS", "The shop can only be used by visiting it in person.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "The maximum price must be greater than the minimum price.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "The base price must be between the minimum price and the maximum price.");
            ccLang.get().addDefault("ERR.NO_RECOMMEND_DATA", "There is no information for this item in the Worth.yml file.");
            ccLang.get().addDefault("ERR.JOBS_REBORN_NOT_FOUND", "Could not find 'Jobs reborn'.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} is an infinite account store.");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "The two stores use different currencies.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "The player could not be found.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "Either store must be a real account.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "The target store must have a real account.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "It is forbidden to build hierarchies. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE", "§eYou cannot use this command in Creative mode. You do not have permission.");
            ccLang.get().addDefault("ERR.FILE_CREATE_FAIL", "§eFile creation failed");
            ccLang.get().addDefault("ERR.INVALID_TRANSACTION", "This transaction is no longer valid. If this problem recurs, contact your server administrator");

            ccLang.get().addDefault("ON", "ON");
            ccLang.get().addDefault("OFF", "OFF");
            ccLang.get().addDefault("SET", "SET");
            ccLang.get().addDefault("UNSET", "UNSET");
            ccLang.get().addDefault("NULL(OPEN)", "None (open to all)");
            ccLang.get().addDefault("CUR_STATE", "Current Status");
            ccLang.get().addDefault("CLICK", "Click");
            ccLang.get().addDefault("LMB", "LMB");
            ccLang.get().addDefault("RMB", "RMB");
            ccLang.get().addDefault("CLOSE", "§fClose");
            ccLang.get().addDefault("CLOSE_LORE", "§f§nClick: Close");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        if (lang == null) lang = "en-US";

        if (!lang.equals("en-US") && !lang.equals("ko-KR"))
        {
            ConfigurationSection conf = ccLang.get().getConfigurationSection("");

            ccLang.setup("Lang_V3_" + lang, null);

            for (String s : conf.getKeys(true))
            {
                if (!ccLang.get().contains(s))
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "String Key " + s + " added");
                    ccLang.get().addDefault(s, conf.get(s));
                }
            }
        } else
        {
            ccLang.setup("Lang_V3_" + lang, null);
        }

        ccLang.get().options().copyDefaults(true);
        ccLang.save();

        ReloadNumberFormat();
    }

    public static final Pattern HEX_PATTERN = Pattern.compile("(#[A-Fa-f0-9]{6})");

    public static String t(Player player, String key)
    {
        return t(player, key, true);
    }

    public static String t(CommandSender sender, String key)
    {
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;

        return t(player, key, true);
    }

    public static String t(Player player, String key, boolean hexConvert)
    {
        String temp = ccLang.get().getString(key);
        if(temp == null || temp.isEmpty())
            return key;

        if (hexConvert && DynamicShop.plugin.getConfig().getBoolean("UI.UseHexColorCode"))
        {
            Matcher matcher = HEX_PATTERN.matcher(temp);
            while (matcher.find())
            {
                temp = temp.replace(matcher.group(), "" + ChatColor.of(matcher.group()));
            }
        }

        if(player != null && DynamicShop.isPapiExist && DynamicShop.plugin.getConfig().getBoolean("UI.UsePlaceholderAPI"))
            return PlaceholderAPI.setPlaceholders(player, temp);
        else
            return temp;
    }

    public static String TranslateHexColor(String message)
    {
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find())
        {
            message = message.replace(matcher.group(), "" + ChatColor.of(matcher.group()));
        }
        return message;
    }

    public static boolean sendMessageWithLocalizedItemName(Player player, String message, Material material) {
        if (material != null) {
            String matKey;
            try {
                matKey = DynamicShop.localeManager.queryMaterial(material, (short)0, null);
            } catch (Exception var8) {
                Bukkit.getLogger().severe("[LocaleLib] Unable to query Material: " + material.name());
                return false;
            }

            String[] splitByRegex = null;
            if(DynamicShop.plugin.getConfig().getBoolean("UI.UseHexColorCode"))
                splitByRegex = HEX_PATTERN.split(message);

            if(splitByRegex != null && splitByRegex.length > 1)
            {
                StringBuilder finalString;
                if(splitByRegex[0].contains("<item>"))
                {
                    String[] splitByItem = splitByRegex[0].split("<item>");
                    finalString = new StringBuilder(("{\"text\":\"" + splitByItem[0] + "\"},"));
                    finalString.append("{\"translate\":\"").append(matKey).append("\"},");
                    finalString.append("{\"text\":\"").append(splitByItem[1]).append("\"},");
                }
                else
                {
                    finalString = new StringBuilder("{\"text\":\"" + splitByRegex[0] + "\"},");
                }

                int idx = 0;

                Matcher matcher = HEX_PATTERN.matcher(message);

                while (matcher.find())
                {
                    if(splitByRegex[idx+1].contains("<item>"))
                    {
                        String[] splitByItem = splitByRegex[idx+1].split("<item>");
                        finalString.append("{\"text\":\"").append(splitByItem[0]).append("\", \"color\":\"").append(matcher.group()).append("\"},");
                        finalString.append("{\"translate\":\"").append(matKey).append("\", \"color\":\"").append(matcher.group()).append("\"},");
                        finalString.append("{\"text\":\"").append(splitByItem[1]).append("\", \"color\":\"").append(matcher.group()).append("\"}");
                    }
                    else
                    {
                        finalString.append("{\"text\":\"").append(splitByRegex[idx + 1]).append("\", \"color\":\"").append(matcher.group()).append("\"}");
                    }

                    idx++;
                    if(idx < splitByRegex.length - 1)
                        finalString.append(",");
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " [" + finalString + "]");
            }
            else
            {
                String replacement = "\",{\"translate\":\"" + matKey + "\"";

                String text = message.split("<item>")[0];
                if (text.contains("§")) {
                    String colorCode = org.bukkit.ChatColor.getLastColors(text).replace("§", "");
                    if (org.bukkit.ChatColor.getByChar(colorCode) != null) {
                        String colorName = org.bukkit.ChatColor.getByChar(colorCode).name();
                        replacement = replacement + ", \"color\":\"" + colorName.toLowerCase() + "\"";
                    }
                }
                replacement = replacement + "},\"";

                String msg = message.replace("<item>", replacement);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " [\"" + msg + "\"]");
            }

            return true;
        } else {
            return false;
        }
    }

    private static void ReloadNumberFormat()
    {
        intFormat = new DecimalFormat(DynamicShop.plugin.getConfig().getString("UI.IntFormat", "###,###"));
        doubleFormat = new DecimalFormat(DynamicShop.plugin.getConfig().getString("UI.DoubleFormat", "###,###.##"));
    }

    private static DecimalFormat intFormat;
    private static DecimalFormat doubleFormat;

    public static String n(int i)
    {
        return intFormat.format(i);
    }

    public static String n(double i)
    {
        return doubleFormat.format(i);
    }
}
