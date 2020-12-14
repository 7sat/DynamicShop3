package me.sat7.dynamicshop.utilities;

import org.bukkit.configuration.ConfigurationSection;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.files.CustomConfig;

public final class LangUtil {
    public static CustomConfig ccLang;

    private LangUtil() {

    }

    public static void setupLangFile(String lang)
    {
        // 한국어
        {
            ccLang.setup("Lang_v2_ko-KR",null);
            ccLang.get().addDefault("STARTPAGE.EDITOR_TITLE", "§3시작 화면 편집");
            ccLang.get().addDefault("STARTPAGE.EDIT_NAME", "이름 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_LORE", "설명 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_ICON", "아이콘 바꾸기");
            ccLang.get().addDefault("STARTPAGE.EDIT_ACTION", "실행 명령어 바꾸기");
            ccLang.get().addDefault("STARTPAGE.SHOP_SHORTCUT", "상점으로 가는 버튼 만들기");
            ccLang.get().addDefault("STARTPAGE.CREATE_DECO", "장식 버튼 만들기");
            ccLang.get().addDefault("STARTPAGE.ENTER_SHOPNAME", "상점 이름을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_NAME", "버튼의 새 이름을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_LORE", "버튼의 새 설명을 입력하세요.");
            ccLang.get().addDefault("STARTPAGE.ENTER_ICON", "버튼의 아이콘으로 사용할 아이탬 이름을 입력하세요. (영문. 대소문자 구분없음)");
            ccLang.get().addDefault("STARTPAGE.ENTER_ACTION", "명령어를 '/' 제외하고 입력하세요. 버튼을 눌렀을때 이 명령어가 실행됩니다.");
            ccLang.get().addDefault("STARTPAGE.ENTER_COLOR", "장식 버튼의 색상을 입력하세요. (영문)");
            ccLang.get().addDefault("STARTPAGE.DEFAULT_SHOP_LORE", "§f상점으로 가기");

            ccLang.get().addDefault("TRADE_TITLE", "§3아이탬 거래");
            ccLang.get().addDefault("PALETTE_TITLE", "§3판매할 아이탬 선택");
            ccLang.get().addDefault("PALETTE_LORE", "§e좌클릭: 이 아이탬을 상점에 등록");
            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3아이탬 셋팅");
            ccLang.get().addDefault("QUICKSELL_TITLE", "§3빠른 판매");
            ccLang.get().addDefault("TRADE_LORE", "§f클릭: 거래화면");
            ccLang.get().addDefault("BUY", "§c구매");
            ccLang.get().addDefault("BUYONLY_LORE", "§f구매만 가능한 아이탬");
            ccLang.get().addDefault("SELL", "§2판매");
            ccLang.get().addDefault("SELLONLY_LORE", "§f판매만 가능한 아이탬");
            ccLang.get().addDefault("VALUE_BUY", "§f구매가치: ");
            ccLang.get().addDefault("VALUE_SELL", "§f판매가치: ");
            ccLang.get().addDefault("PRICE", "§f구매: ");
            ccLang.get().addDefault("SELLPRICE", "§f판매: ");
            ccLang.get().addDefault("PRICE_MIN", "§f최소 가격: ");
            ccLang.get().addDefault("PRICE_MAX", "§f최대 가격: ");
            ccLang.get().addDefault("MEDIAN", "§f중앙값: ");
            ccLang.get().addDefault("MEDIAN_HELP", "§f중앙값이 작을수록 가격이 급격이 변화합니다.");
            ccLang.get().addDefault("STOCK", "§f재고: ");
            ccLang.get().addDefault("INFSTOCK", "무한 재고");
            ccLang.get().addDefault("STATICPRICE", "고정 가격");
            ccLang.get().addDefault("UNLIMITED", "무제한");
            ccLang.get().addDefault("TAXIGNORED", "판매세 설정이 무시됩니다.");
            ccLang.get().addDefault("TOGGLE_SELLABLE", "§e클릭: 판매전용 토글");
            ccLang.get().addDefault("TOGGLE_BUYABLE", "§e클릭: 구매전용 토글");
            ccLang.get().addDefault("BALANCE", "§3내 잔액");
            ccLang.get().addDefault("ITEM_MOVE_LORE", "§e우클릭: 이동");
            ccLang.get().addDefault("ITEM_COPY_LORE", "§e우클릭: 복사");
            ccLang.get().addDefault("ITEM_EDIT_LORE", "§eShift우클릭: 편집");
            ccLang.get().addDefault("DECO_CREATE_LORE", "§e우클릭: 장식 버튼으로 추가");
            ccLang.get().addDefault("DECO_DELETE_LORE", "§eShift우클릭: 삭제");
            ccLang.get().addDefault("RECOMMEND", "§f추천 값 적용");
            ccLang.get().addDefault("RECOMMEND_LORE", "§f가격, 중앙값, 재고를 자동으로 설정합니다.");
            ccLang.get().addDefault("DONE", "§f완료");
            ccLang.get().addDefault("DONE_LORE", "§f완료!");
            ccLang.get().addDefault("ROUNDDOWN", "§f내림");
            ccLang.get().addDefault("SETTOMEDIAN", "§f중앙값에 맞춤");
            ccLang.get().addDefault("SETTOSTOCK", "§f재고에 맞춤");
            ccLang.get().addDefault("SETTOVALUE", "§f가격에 맞춤");
            ccLang.get().addDefault("SAVE_CLOSE", "§f저장 후 닫기");
            ccLang.get().addDefault("CLOSE", "§f닫기");
            ccLang.get().addDefault("CLOSE_LORE", "§f이 창을 닫습니다.");
            ccLang.get().addDefault("REMOVE", "§f제거");
            ccLang.get().addDefault("REMOVE_LORE", "§f이 아이탬을 상점에서 제거합니다.");
            ccLang.get().addDefault("PAGE", "§f페이지");
            ccLang.get().addDefault("PAGE_LORE", "§f좌클릭: 이전페이지 / 우클릭: 다음페이지");
            ccLang.get().addDefault("PAGE_INSERT", "§eShift+좌: 페이지 삽입");
            ccLang.get().addDefault("PAGE_DELETE", "§eShift+우: 페이지 §c삭제");
            ccLang.get().addDefault("ITEM_MOVE_SELECTED", "아이탬 선택됨. 비어있는 칸을 우클릭하면 이동합니다.");
            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3상점 설정");
            ccLang.get().addDefault("SHOP_INFO", "§3상점 정보");
            ccLang.get().addDefault("PERMISSION", "§f퍼미션");
            ccLang.get().addDefault("CUR_STATE", "현재상태");
            ccLang.get().addDefault("CLICK", "클릭");
            ccLang.get().addDefault("MAXPAGE", "§f최대 페이지");
            ccLang.get().addDefault("MAXPAGE_LORE", "§f상점의 최대 페이지를 설정합니다");
            ccLang.get().addDefault("L_R_SHIFT", "§e좌: -1 우: +1 Shift: x5");
            ccLang.get().addDefault("FLAG", "§f플래그");
            ccLang.get().addDefault("RMB_EDIT", "§e우클릭: 편집");
            ccLang.get().addDefault("SIGNSHOP_LORE", "§f표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("LOCALSHOP_LORE", "§f실제 상점 위치를 방문해야 합니다.");
            ccLang.get().addDefault("LOCALSHOP_LORE2", "§f상점의 위치를 설정해야만 합니다.");
            ccLang.get().addDefault("DELIVERYCHARG_LORE", "§f배달비를 지불하고 localshop에서 원격으로 거래합니다.");
            ccLang.get().addDefault("JOBPOINT_LORE", "§fJobs 플러그인의 job point로 거래합니다.");
            ccLang.get().addDefault("SEARCH", "§f찾기");
            ccLang.get().addDefault("SEARCH_ITEM", "§f찾으려는 아이템의 이름을 입력하세요.");
            ccLang.get().addDefault("SEARCH_CANCELED", "§f검색 취소됨.");
            ccLang.get().addDefault("INPUT_CANCELED", "§f입력 취소됨.");
            ccLang.get().addDefault("ADDALL", "§f모두 추가");
            ccLang.get().addDefault("RUSURE", "§f정말로 페이지를 삭제할까요? 'delete' 를 입력하면 삭제합니다.");
            ccLang.get().addDefault("CANT_DELETE_LAST_PAGE", "§f마지막 남은 페이지를 삭제할 수 없습니다.");
            ccLang.get().addDefault("SHOP_BAL_INF", "§f상점 계좌 무제한");
            ccLang.get().addDefault("SHOP_BAL", "§f상점 계좌 잔액");
            ccLang.get().addDefault("SHOP_BAL_LOW", "§f상점이 돈을 충분히 가지고 있지 않습니다.");
            ccLang.get().addDefault("ON","켜짐");
            ccLang.get().addDefault("OFF","꺼짐");
            ccLang.get().addDefault("SET","설정");
            ccLang.get().addDefault("UNSET","설정해제");
            ccLang.get().addDefault("NULL(OPEN)","없음 (모두에게 열려있음)");

            ccLang.get().addDefault("LOG.LOG", "§f로그");
            ccLang.get().addDefault("LOG.CLEAR", "§f로그 삭제됨");
            ccLang.get().addDefault("LOG.SAVE", "§f로그 저장됨");
            ccLang.get().addDefault("LOG.DELETE", "§f로그 삭제");

            ccLang.get().addDefault("SHOP_CREATED", "§f상점 생성됨!");
            ccLang.get().addDefault("SHOP_DELETED", "§f상점 제거됨!");
            ccLang.get().addDefault("POSITION", "§f위치: ");
            ccLang.get().addDefault("SHOP_LIST", "§f상점 목록");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§f문 여는 시간 설정");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§f문 닫는 시간 설정");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§f영업시간");
            ccLang.get().addDefault("TIME.OPEN24", "24시간 오픈");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§f상점이 문을 닫았습니다. 개점: {time}시. 현재시간: {curTime}시");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "영업시간 설정");
            ccLang.get().addDefault("TIME.CUR", "§f현재 시간: {time}시");

            ccLang.get().addDefault("STOCKSTABILIZING.SS", "§f재고 안정화");
            ccLang.get().addDefault("STOCKSTABILIZING.L_R_SHIFT", "§e좌클릭: -0.1 우클릭: +0.1 Shift: x5");

            ccLang.get().addDefault("FLUC.FLUCTUATION", "§f무작위 재고 변동");
            ccLang.get().addDefault("FLUC.INTERVAL", "§f변화 간격");
            ccLang.get().addDefault("FLUC.STRENGTH", "§f변화 강도");

            ccLang.get().addDefault("TAX.SALESTAX", "§f판매세");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "전역설정 사용 ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "별도 설정");

            ccLang.get().addDefault("OUT_OF_STOCK", "§f재고 없음!");
            ccLang.get().addDefault("BUY_SUCCESS", "§f{item} {amount}개를 {price}에 구매함. 잔액: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS", "§f{item} {amount}개를 {price}에 판매함. 잔액: {bal}");
            ccLang.get().addDefault("BUY_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 구매함. 남은포인트: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS_JP", "§f{item} {amount}개를 {price}포인트에 판매함. 남은포인트: {bal}");
            ccLang.get().addDefault("QSELL_RESULT", "§f거래한 상점: ");
            ccLang.get().addDefault("QSELL_NA", "§f해당 아이탬을 취급하는 상점이 없습니다.");
            ccLang.get().addDefault("DELIVERYCHARGE", "§f배달비: {fee}");
            ccLang.get().addDefault("DELIVERYCHARGE_EXEMPTION", "§f배달비: {fee} ({fee2} 면제됨)");
            ccLang.get().addDefault("DELIVERYCHARGE_NA", "§f다른 월드로 배달할 수 없습니다.");
            ccLang.get().addDefault("NOT_ENOUGH_MONEY", "§f돈이 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("NOT_ENOUGH_POINT", "§f포인트가 부족합니다. 잔액: {bal}");
            ccLang.get().addDefault("NO_ITEM_TO_SELL", "§f판매 할 아이탬이 없습니다.");
            ccLang.get().addDefault("INVEN_FULL", "§4인벤토리에 빈 공간이 없습니다!");
            ccLang.get().addDefault("IRREVERSIBLE", "§f이 행동은 되돌릴 수 없습니다!");

            ccLang.get().addDefault("HELP.TITLE", "§f도움말: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "상점을 엽니다.");
            ccLang.get().addDefault("HELP.CMD", "명령어 도움말 표시 토글.");
            ccLang.get().addDefault("HELP.CREATESHOP", "상점을 새로 만듭니다.");
            ccLang.get().addDefault("HELP.DELETESHOP", "기존의 상점을 제거합니다.");
            ccLang.get().addDefault("HELP.SETTAX", "판매 세금을 설정합니다.");
            ccLang.get().addDefault("HELP.SETTAX_TEMP", "임시 판매 세 설정");
            ccLang.get().addDefault("HELP.SHOPADDHAND", "손에 들고 있는 아이탬을 상점에 추가합니다.");
            ccLang.get().addDefault("HELP.SHOPADDITEM", "상점에 아이탬을 추가합니다.");
            ccLang.get().addDefault("HELP.SHOPEDIT", "상점에 있는 아이탬을 수정합니다.");
            ccLang.get().addDefault("HELP.PRICE", "§7가격은 다음과 같이 계산됩니다: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == 고정가격     stock<0 == 무한재고");
            ccLang.get().addDefault("HELP.EDITALL", "상점의 모든 아이탬을 한번에 수정합니다.");
            ccLang.get().addDefault("HELP.EDITALL2", "§c주의. 값이 유효한지는 확인하지 않음.");
            ccLang.get().addDefault("HELP.RELOAD", "플러그인을 재시작 합니다.");
            ccLang.get().addDefault("HELP.RELOADED", "플러그인 리로드됨!");
            ccLang.get().addDefault("HELP.USAGE", "사용법");
            ccLang.get().addDefault("HELP.CREATESHOP2", "퍼미션(나중에 바꿀 수 있습니다.)\n   true: dshop.user.shop.상점이름\n   false: 아무나 접근가능(기본값)\n   임의 입력: 해당 퍼미션 필요");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item}(은)는 이미 판매중임.\n   {info}\n   명령어를 입력하면 값이 수정됩니다.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item}의 현재 설정:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f§o인자를 0으로 입력하면 이 아이탬을 상점에서 §4제거§f합니다.");
            ccLang.get().addDefault("HELP.QSELL", "§f빠르게 아이탬을 판매합니다.");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "장기간 접속하지 않은 유저의 데이터를 삭제합니다.");
            ccLang.get().addDefault("HELP.CONVERT", "다른 상점 플러그인의 정보를 변환합니다.");
            ccLang.get().addDefault("HELP.ACCOUNT", "상점의 계좌 잔액을 설정합니다. -1 = 무제한");

            ccLang.get().addDefault("ITEM_ADDED", "아이탬 추가됨!");
            ccLang.get().addDefault("ITEM_UPDATED", "아이탬 수정됨!");
            ccLang.get().addDefault("ITEM_DELETED", "아이탬 제거됨!");
            ccLang.get().addDefault("CHANGES_APPLIED", "변경사항 적용됨. 새로운 값: ");
            ccLang.get().addDefault("RECOMMAND_APPLIED", "추천 값 적용됨. {playerNum}명 기준입니다. config파일에서 이 값을 바꿀 수 있습니다.");
            ccLang.get().addDefault("TRANSFER_SUCCESS", "송금 완료");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6플레이어 uuid를 찾을 수 없습니다. 상점 이용 불가능.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "상점에 해당 아이탬이 존재하지 않습니다.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "사용할 수 없는 아이탬 입니다.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§e권한이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "잘못된 명령어 사용법. 도움말을 확인하세요.");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "상점에 빈 공간이 없습니다.");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "인자의 유형이 잘못 입력되었습니다.");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "인자값이 0보다 커야 합니다.");
            ccLang.get().addDefault("ERR.WRONG_ITEMNAME", "유효하지 않은 아이탬 이름입니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "아이탬을 손에 들고 있어야 합니다.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§o아이탬을 손에 들고 있어야 합니다!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§f해당 상점을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "해당 이름을 가진 상점이 이미 존재합니다.");
            ccLang.get().addDefault("ERR.SIGNSHOP_REMOTE_ACCESS", "해당 상점은 표지판을 통해서만 접근할 수 있습니다.");
            ccLang.get().addDefault("ERR.LOCALSHOP_REMOTE_ACCESS", "해당 상점은 직접 방문해야만 사용할 수 있습니다.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "최대 가격은 최소 가격보다 커야합니다.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "기본 가격은 최소 가격과 최대 가격 사이의 값이어야 합니다.");
            ccLang.get().addDefault("ERR.NO_RECOMMAND_DATA", "Worth.yml 파일에 이 아이탬의 정보가 없습니다. 추천값 사용 불가.");
            ccLang.get().addDefault("ERR.JOBSREBORN_NOT_FOUND", "Jobs reborn 플러그인을 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} 상점은 무한계좌 상점입니다.");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "두 상점이 서로 다른 통화를 사용합니다.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "해당 플레이어를 찾을 수 없습니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "상점 둘 중 하나는 실제 계좌이어야 합니다.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "목표 상점은 실제 계좌를 가지고 있어야 합니다.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "계층 구조를 이루는것은 금지되어 있습니다. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE","§eCreative mode 에서 이 명령어를 사용할 수 없습니다. 권한이 없습니다.");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        // 영어 English
        {
            ccLang.setup("Lang_v2_en-US",null);
            ccLang.get().addDefault("STARTPAGE.EDITOR_TITLE", "§3Start page editor");
            ccLang.get().addDefault("STARTPAGE.EDIT_NAME", "Change Name");
            ccLang.get().addDefault("STARTPAGE.EDIT_LORE", "Change Lore");
            ccLang.get().addDefault("STARTPAGE.EDIT_ICON", "Change Icon");
            ccLang.get().addDefault("STARTPAGE.EDIT_ACTION", "Change Action");
            ccLang.get().addDefault("STARTPAGE.SHOP_SHORTCUT", "Create shortcut button for shop");
            ccLang.get().addDefault("STARTPAGE.CREATE_DECO", "Create decorative button");
            ccLang.get().addDefault("STARTPAGE.ENTER_SHOPNAME", "Enter shop name.");
            ccLang.get().addDefault("STARTPAGE.ENTER_NAME", "Enter new name");
            ccLang.get().addDefault("STARTPAGE.ENTER_LORE", "Enter new lore");
            ccLang.get().addDefault("STARTPAGE.ENTER_ICON", "Enter new Icon (Minecraft material name. Case insensitive)");
            ccLang.get().addDefault("STARTPAGE.ENTER_ACTION", "Enter Command without '/'. This command will be execute when button pressed.");
            ccLang.get().addDefault("STARTPAGE.ENTER_COLOR", "Enter color. (ex.LIGHT_BLUE)");
            ccLang.get().addDefault("STARTPAGE.DEFAULT_SHOP_LORE", "§fGo to shop");

            ccLang.get().addDefault("TRADE_TITLE", "§3Tradeview");
            ccLang.get().addDefault("PALETTE_TITLE", "§3Item Palette");
            ccLang.get().addDefault("PALETTE_LORE", "§eLMB: Register this item on shop");
            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3Item Settings");
            ccLang.get().addDefault("QUICKSELL_TITLE", "§3Quick Sell");
            ccLang.get().addDefault("TRADE_LORE", "§fClick: Go to Tradeview");
            ccLang.get().addDefault("BUY", "§cBuy");
            ccLang.get().addDefault("BUYONLY_LORE", "§fThis item is Buy only");
            ccLang.get().addDefault("SELL", "§2Sell");
            ccLang.get().addDefault("SELLONLY_LORE", "§fThis item is Sell only");
            ccLang.get().addDefault("PRICE", "§fBuy: ");
            ccLang.get().addDefault("SELLPRICE", "§fSell: ");
            ccLang.get().addDefault("VALUE_BUY", "§fValue(Buy): ");
            ccLang.get().addDefault("VALUE_SELL", "§fValue(Sell): ");
            ccLang.get().addDefault("PRICE_MIN", "§fMin Price: ");
            ccLang.get().addDefault("PRICE_MAX", "§fMax Price: ");
            ccLang.get().addDefault("MEDIAN", "§fMedian: ");
            ccLang.get().addDefault("MEDIAN_HELP", "§fThe larger the median value, the more rapidly the price changes.");
            ccLang.get().addDefault("STOCK", "§fStock: ");
            ccLang.get().addDefault("INFSTOCK", "Infinite stock");
            ccLang.get().addDefault("STATICPRICE", "Static price");
            ccLang.get().addDefault("UNLIMITED", "Unlimited");
            ccLang.get().addDefault("TAXIGNORED", "Sales tax will be ignored.");
            ccLang.get().addDefault("TOGGLE_SELLABLE", "§eClick: Toggle Sellable");
            ccLang.get().addDefault("TOGGLE_BUYABLE", "§eClick: Toggle Buyable");
            ccLang.get().addDefault("BALANCE", "§3Balance");
            ccLang.get().addDefault("ITEM_MOVE_LORE", "§eRMB: Move");
            ccLang.get().addDefault("ITEM_COPY_LORE", "§eRMB: Copy");
            ccLang.get().addDefault("ITEM_EDIT_LORE", "§eShift+RMB: Edit");
            ccLang.get().addDefault("DECO_CREATE_LORE", "§eRMB: Add as decoration");
            ccLang.get().addDefault("DECO_DELETE_LORE", "§eShift + RMB: Delete");
            ccLang.get().addDefault("RECOMMEND", "§fUse recommended value");
            ccLang.get().addDefault("RECOMMEND_LORE", "§fAutomatically set values");
            ccLang.get().addDefault("DONE", "§fDone");
            ccLang.get().addDefault("DONE_LORE", "§fDone!");
            ccLang.get().addDefault("ROUNDDOWN", "§fRound down");
            ccLang.get().addDefault("SETTOMEDIAN", "§fSet to median");
            ccLang.get().addDefault("SETTOSTOCK", "§fSet to stock");
            ccLang.get().addDefault("SETTOVALUE", "§fSet to value");
            ccLang.get().addDefault("SAVE_CLOSE", "§fSave and close");
            ccLang.get().addDefault("CLOSE", "§fClose");
            ccLang.get().addDefault("CLOSE_LORE", "§fClose this window.");
            ccLang.get().addDefault("REMOVE", "§fRemove");
            ccLang.get().addDefault("REMOVE_LORE", "§fRemove this item from shop.");
            ccLang.get().addDefault("PAGE", "§fPage");
            ccLang.get().addDefault("PAGE_LORE", "§fLMB: Previous / RMB: Next");
            ccLang.get().addDefault("PAGE_INSERT", "§eShift+L: Insert page");
            ccLang.get().addDefault("PAGE_DELETE", "§eShift+R: §cDelete page");
            ccLang.get().addDefault("ITEM_MOVE_SELECTED", "Item selected. Right click on empty space.");
            ccLang.get().addDefault("SEARCH", "§fSearch");
            ccLang.get().addDefault("SEARCH_ITEM", "§fPlease enter the name of the item you are looking for.");
            ccLang.get().addDefault("SEARCH_CANCELED", "§fSearch canceled");
            ccLang.get().addDefault("INPUT_CANCELED", "§fInput canceled");
            ccLang.get().addDefault("ADDALL", "§fAdd all");
            ccLang.get().addDefault("RUSURE", "§fAre you sure? Type 'delete' to confirm.");
            ccLang.get().addDefault("CANT_DELETE_LAST_PAGE", "§fYou can't delete last page.");
            ccLang.get().addDefault("SHOP_BAL_INF", "§fUnlimited balance");
            ccLang.get().addDefault("SHOP_BAL", "§fShop balance");
            ccLang.get().addDefault("SHOP_BAL_LOW", "§fShop does not have enough money.");
            ccLang.get().addDefault("ON","On");
            ccLang.get().addDefault("OFF","Off");
            ccLang.get().addDefault("SET","Set");
            ccLang.get().addDefault("UNSET","Unset");
            ccLang.get().addDefault("NULL(OPEN)","null (Open for everyone)");

            ccLang.get().addDefault("LOG.LOG", "§fLog");
            ccLang.get().addDefault("LOG.CLEAR", "§fLog deleted");
            ccLang.get().addDefault("LOG.SAVE", "§fLog saved");
            ccLang.get().addDefault("LOG.DELETE", "§fDelete Log");

            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3Shop Settings");
            ccLang.get().addDefault("SHOP_INFO", "§3Shop Info");
            ccLang.get().addDefault("PERMISSION", "§fPermission");
            ccLang.get().addDefault("CUR_STATE", "Current");
            ccLang.get().addDefault("CLICK", "Click");
            ccLang.get().addDefault("MAXPAGE", "§fMax Page");
            ccLang.get().addDefault("MAXPAGE_LORE", "§fSet maximum number of pages");
            ccLang.get().addDefault("L_R_SHIFT", "§eLMB: -1 RMB: +1 Shift: x5");
            ccLang.get().addDefault("FLAG", "§fFlag");
            ccLang.get().addDefault("RMB_EDIT", "§eRMB: Edit");
            ccLang.get().addDefault("SIGNSHOP_LORE", "§fThis shop is only accessible from the sign.");
            ccLang.get().addDefault("LOCALSHOP_LORE", "§fPlayer must visit the actual location of the store.");
            ccLang.get().addDefault("LOCALSHOP_LORE2", "§fThis flag requires a position value to work.");
            ccLang.get().addDefault("DELIVERYCHARG_LORE", "§fPay delivery charge, Buy items from a distance.");
            ccLang.get().addDefault("JOBPOINT_LORE", "§fJobs Reborn point shop.");

            ccLang.get().addDefault("SHOP_CREATED", "§fShop Created!");
            ccLang.get().addDefault("SHOP_DELETED", "§fShop Deleted!");
            ccLang.get().addDefault("POSITION", "§fPosition: ");
            ccLang.get().addDefault("SHOP_LIST", "§fShop list");

            ccLang.get().addDefault("TIME.OPEN", "Open");
            ccLang.get().addDefault("TIME.CLOSE", "Close");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§fSet Open time");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§fSet Close time");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§fShop hours");
            ccLang.get().addDefault("TIME.OPEN24", "Open 24 Hours");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§fShop is closed. Open: {time}h. Current Time: {curTime}h");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "Set shop hours");
            ccLang.get().addDefault("TIME.CUR", "§fCurrent Time: {time}h");

            ccLang.get().addDefault("FLUC.FLUCTUATION", "Random Stock Fluctuation");
            ccLang.get().addDefault("FLUC.INTERVAL", "Interval");
            ccLang.get().addDefault("FLUC.STRENGTH", "Strength");

            ccLang.get().addDefault("STOCKSTABILIZING.SS", "§fStock Stabilizing");
            ccLang.get().addDefault("STOCKSTABILIZING.L_R_SHIFT", "§eLMB: -0.1 RMB: +0.1 Shift: x5");

            ccLang.get().addDefault("TAX.SALESTAX", "§fSales tax");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "Use global setting ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "Separate setting");

            ccLang.get().addDefault("OUT_OF_STOCK", "§fOut of stock!");
            ccLang.get().addDefault("BUY_SUCCESS", "§fBought {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS", "§fSold {item} x{amount} for {price}. Balance: {bal}");
            ccLang.get().addDefault("BUY_SUCCESS_JP", "§fBought {item} x{amount} for {price}points. Balance: {bal}");
            ccLang.get().addDefault("SELL_SUCCESS_JP", "§fSold {item} x{amount} for {price}points. Balance: {bal}");
            ccLang.get().addDefault("QSELL_RESULT", "§fTo: ");
            ccLang.get().addDefault("QSELL_NA", "§fThere are no shops to trade that item.");
            ccLang.get().addDefault("DELIVERYCHARGE", "§fDelivery charge");
            ccLang.get().addDefault("DELIVERYCHARGE_EXEMPTION", "§fDelivery charge: {fee} ({fee2} exempt)");
            ccLang.get().addDefault("DELIVERYCHARGE_NA", "§fCan't deliver to different world.");
            ccLang.get().addDefault("NOT_ENOUGH_MONEY", "§fNot enough money. Balance: {bal}");
            ccLang.get().addDefault("NOT_ENOUGH_POINT", "§fNot enough point. Balance: {bal}");
            ccLang.get().addDefault("NO_ITEM_TO_SELL", "§fNot enough item.");
            ccLang.get().addDefault("INVEN_FULL", "§4Inventory is full!");
            ccLang.get().addDefault("IRREVERSIBLE", "§fThis action is irreversible!");

            ccLang.get().addDefault("HELP.TITLE", "§fHelp: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "Open Shop GUI.");
            ccLang.get().addDefault("HELP.CMD", "Toggle Command Help.");
            ccLang.get().addDefault("HELP.CREATESHOP", "Create new shop.");
            ccLang.get().addDefault("HELP.DELETESHOP", "Delete exist shop.");
            ccLang.get().addDefault("HELP.SETTAX", "Set sale tax.");
            ccLang.get().addDefault("HELP.SETTAX_TEMP", "Set sales tax temporarily");
            ccLang.get().addDefault("HELP.SHOPADDHAND", "Add Item to shop.");
            ccLang.get().addDefault("HELP.SHOPADDITEM", "Add Item to shop.");
            ccLang.get().addDefault("HELP.SHOPEDIT", "Edit shop item.");
            ccLang.get().addDefault("HELP.PRICE", "§7Formula: median*value/stock");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7median<0 == static price     stock<0 == infinite stock");
            ccLang.get().addDefault("HELP.EDITALL", "Edit all shop items");
            ccLang.get().addDefault("HELP.EDITALL2", "§cWarning. There is no sanity check. Use at your own caution.");
            ccLang.get().addDefault("HELP.RELOAD", "Reload YML.");
            ccLang.get().addDefault("HELP.RELOADED", "Plugin reloaded");
            ccLang.get().addDefault("HELP.USAGE", "Usage");
            ccLang.get().addDefault("HELP.CREATESHOP2", "Permission(You can change this later.)\n   true: dshop.user.shop.shopname\n   false: no permission needed(Default)\n   user input: need that permission");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item} is already selling.\n   {info}\n   Values will be update.");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item} is now selling for:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f§oEnter 0 for value to §4Remove§f this item.");
            ccLang.get().addDefault("HELP.QSELL", "§fQuick Sell");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "Delete Old Inactive User data from User.yml.");
            ccLang.get().addDefault("HELP.CONVERT", "Convert data from other shop plugin");
            ccLang.get().addDefault("HELP.ACCOUNT", "Set shop account balance. -1 = Infinite");

            ccLang.get().addDefault("ITEM_ADDED", "Item Added!");
            ccLang.get().addDefault("ITEM_UPDATED", "Item Updated!");
            ccLang.get().addDefault("ITEM_DELETED", "Item Removed!");
            ccLang.get().addDefault("CHANGES_APPLIED", "Changes applied. New value: ");
            ccLang.get().addDefault("RECOMMAND_APPLIED", "Suggestion applied. Based on {playerNum}players. This value can be edited in config");
            ccLang.get().addDefault("TRANSFER_SUCCESS", "Transfer success.");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6Cant find your uuid from server. Shop Unavailable.");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "Item not exist.");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "Forbidden Item.");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§eNo permission.");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "Wrong usage");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "Shop is full");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "Wrong Argument type");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "Argument must be greater than 0");
            ccLang.get().addDefault("ERR.WRONG_ITEMNAME", "There's no such item.");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "You must be holding an item to sell.");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§oYou must be holding an item to sell!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§fShop not found");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "This name already exist.");
            ccLang.get().addDefault("ERR.SIGNSHOP_REMOTE_ACCESS", "You can't access sign shop remotely.");
            ccLang.get().addDefault("ERR.LOCALSHOP_REMOTE_ACCESS", "You can't access local shop remotely.");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "Max price must be greater than Min price.");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "Price must be between min and max");
            ccLang.get().addDefault("ERR.NO_RECOMMAND_DATA", "No data found in Worth.yml.");
            ccLang.get().addDefault("ERR.JOBSREBORN_NOT_FOUND", "'Jobs Reborn' not found.");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} has infinite balance");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "These shops have different currency.");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "Player not exist.");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "At least one of them must be an actual account.");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "Target shop must have actual account.");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "Nested structure is forbidden. (ex. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE","§eYou can not use this command in creative mode. No permission.");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        // 简体中文 Simplifed Chinese
        {
            ccLang.setup("Lang_v2_zh-CN",null);
            ccLang.get().addDefault("STARTPAGE.EDITOR_TITLE", "§3开始页面管理编辑");
            ccLang.get().addDefault("STARTPAGE.EDIT_NAME", "更改名称");
            ccLang.get().addDefault("STARTPAGE.EDIT_LORE", "更改简介");
            ccLang.get().addDefault("STARTPAGE.EDIT_ICON", "更改图标");
            ccLang.get().addDefault("STARTPAGE.EDIT_ACTION", "更改响应行为");
            ccLang.get().addDefault("STARTPAGE.SHOP_SHORTCUT", "为商店创建快捷打开按钮");
            ccLang.get().addDefault("STARTPAGE.CREATE_DECO", "创建装饰性按钮");
            ccLang.get().addDefault("STARTPAGE.ENTER_SHOPNAME", "请输入商店名称。");
            ccLang.get().addDefault("STARTPAGE.ENTER_NAME", "请输入新的名称");
            ccLang.get().addDefault("STARTPAGE.ENTER_LORE", "请输入新的简介");
            ccLang.get().addDefault("STARTPAGE.ENTER_ICON", "请输入新的物品图标名称 (Minecraft 材料名称。不区分大小写)");
            ccLang.get().addDefault("STARTPAGE.ENTER_ACTION", "请输入一个不带 '/' 的命令。该命令将在点击按钮时被执行。");
            ccLang.get().addDefault("STARTPAGE.ENTER_COLOR", "请输入一个颜色。(示例LIGHT_BLUE)");
            ccLang.get().addDefault("STARTPAGE.DEFAULT_SHOP_LORE", "§f前往商店页面");

            ccLang.get().addDefault("TRADE_TITLE", "§3贸易市场");
            ccLang.get().addDefault("PALETTE_TITLE", "§3物品浏览");
            ccLang.get().addDefault("PALETTE_LORE", "§e左击: 在商店中上架该物品");
            ccLang.get().addDefault("ITEM_SETTING_TITLE", "§3物品设置");
            ccLang.get().addDefault("QUICKSELL_TITLE", "§3一键出售");
            ccLang.get().addDefault("TRADE_LORE", "§f点击: 前往贸易市场");
            ccLang.get().addDefault("BUY", "§c购买");
            ccLang.get().addDefault("BUYONLY_LORE", "§f该物品被设为 只可购买");
            ccLang.get().addDefault("SELL", "§2Sell");
            ccLang.get().addDefault("SELLONLY_LORE", "§f该物品被设为 只可收购");
            ccLang.get().addDefault("PRICE", "§f购入: ");
            ccLang.get().addDefault("SELLPRICE", "§f售出: ");
            ccLang.get().addDefault("VALUE_BUY", "§f价值(买): ");
            ccLang.get().addDefault("VALUE_SELL", "§f价值(卖): ");
            ccLang.get().addDefault("PRICE_MIN", "§f最低价格: ");
            ccLang.get().addDefault("PRICE_MAX", "§f最高价格: ");
            ccLang.get().addDefault("MEDIAN", "§f中间值: ");
            ccLang.get().addDefault("MEDIAN_HELP", "§f中间值越大，商品价格波动更敏感");
            ccLang.get().addDefault("STOCK", "§f库存: ");
            ccLang.get().addDefault("INFSTOCK", "无限库存");
            ccLang.get().addDefault("STATICPRICE", "静态价格");
            ccLang.get().addDefault("UNLIMITED", "无限制");
            ccLang.get().addDefault("TAXIGNORED", "售出税将被忽略。");
            ccLang.get().addDefault("TOGGLE_SELLABLE", "§e点击: 切换是否可卖");
            ccLang.get().addDefault("TOGGLE_BUYABLE", "§e点击: 切换是否可买");
            ccLang.get().addDefault("BALANCE", "§3余额");
            ccLang.get().addDefault("ITEM_MOVE_LORE", "§e右击: 移动");
            ccLang.get().addDefault("ITEM_COPY_LORE", "§e右击: 复制");
            ccLang.get().addDefault("ITEM_EDIT_LORE", "§eShift+右击: 编辑");
            ccLang.get().addDefault("DECO_CREATE_LORE", "§e右击: 添加装饰性按钮");
            ccLang.get().addDefault("DECO_DELETE_LORE", "§eShift + 右击: 删除");
            ccLang.get().addDefault("RECOMMEND", "§f使用系统推荐的价值");
            ccLang.get().addDefault("RECOMMEND_LORE", "§f自动设置价值");
            ccLang.get().addDefault("DONE", "§f完成操作");
            ccLang.get().addDefault("DONE_LORE", "§f大功告成!");
            ccLang.get().addDefault("ROUNDDOWN", "§f下调");
            ccLang.get().addDefault("SETTOMEDIAN", "§f设为中间值");
            ccLang.get().addDefault("SETTOSTOCK", "§f设为默认库存量");
            ccLang.get().addDefault("SETTOVALUE", "§f设为价值");
            ccLang.get().addDefault("SAVE_CLOSE", "§f保存并关闭");
            ccLang.get().addDefault("CLOSE", "§f关闭");
            ccLang.get().addDefault("CLOSE_LORE", "§f关闭这个窗口。");
            ccLang.get().addDefault("REMOVE", "§f移除");
            ccLang.get().addDefault("REMOVE_LORE", "§f从商店中移除这个物品。");
            ccLang.get().addDefault("PAGE", "§f页码");
            ccLang.get().addDefault("PAGE_LORE", "§f左击: 上一页 / 右击: 下一页");
            ccLang.get().addDefault("PAGE_INSERT", "§eShift+左击: 插入页面");
            ccLang.get().addDefault("PAGE_DELETE", "§eShift+右击: §c删除页面");
            ccLang.get().addDefault("ITEM_MOVE_SELECTED", "已选择物品。在有空位的地方右击移动。");
            ccLang.get().addDefault("SEARCH", "§f搜索");
            ccLang.get().addDefault("SEARCH_ITEM", "§f请输入您正在寻找的物品的名字。");
            ccLang.get().addDefault("SEARCH_CANCELED", "§f已取消搜索");
            ccLang.get().addDefault("INPUT_CANCELED", "§f已取消数据键入");
            ccLang.get().addDefault("ADDALL", "§f添加全部");
            ccLang.get().addDefault("RUSURE", "§f您真的确定要那么做?请输入 'delete' 以二次确认。");
            ccLang.get().addDefault("CANT_DELETE_LAST_PAGE", "§f这是最后一页了，您不能删除。");
            ccLang.get().addDefault("SHOP_BAL_INF", "§f无限的货币");
            ccLang.get().addDefault("SHOP_BAL", "§f商店货币总额");
            ccLang.get().addDefault("SHOP_BAL_LOW", "§f商店没有足够的钱来维持营运了。");
            ccLang.get().addDefault("ON","开启");
            ccLang.get().addDefault("OFF","关闭");
            ccLang.get().addDefault("SET","设置");
            ccLang.get().addDefault("UNSET","撤销设置");
            ccLang.get().addDefault("NULL(OPEN)","无 (为每个人都打开)");

            ccLang.get().addDefault("LOG.LOG", "§f日志");
            ccLang.get().addDefault("LOG.CLEAR", "§f已删除日志");
            ccLang.get().addDefault("LOG.SAVE", "§f已保存日志");
            ccLang.get().addDefault("LOG.DELETE", "§f删除日志");

            ccLang.get().addDefault("SHOP_SETTING_TITLE", "§3商店设置");
            ccLang.get().addDefault("SHOP_INFO", "§3商店信息");
            ccLang.get().addDefault("PERMISSION", "§f所需权限");
            ccLang.get().addDefault("CUR_STATE", "当前状态");
            ccLang.get().addDefault("CLICK", "点击");
            ccLang.get().addDefault("MAXPAGE", "§f最大页数");
            ccLang.get().addDefault("MAXPAGE_LORE", "§f设置商店最大页数");
            ccLang.get().addDefault("L_R_SHIFT", "§e左击: -1 右击: +1 Shift: x5");
            ccLang.get().addDefault("FLAG", "§f配置节点");
            ccLang.get().addDefault("RMB_EDIT", "§e右击: 编辑");
            ccLang.get().addDefault("SIGNSHOP_LORE", "§f这家商店只能从告示牌处进入。");
            ccLang.get().addDefault("LOCALSHOP_LORE", "§f玩家必须位于商店所在的实际地点。");
            ccLang.get().addDefault("LOCALSHOP_LORE2", "§f这个配置节点需要一个位置才能生效。");
            ccLang.get().addDefault("DELIVERYCHARG_LORE", "§f当您从远处买东西，您需要支付货运费。");
            ccLang.get().addDefault("JOBPOINT_LORE", "§fJobs Reborn 点数商店。");

            ccLang.get().addDefault("SHOP_CREATED", "§f已创建商店!");
            ccLang.get().addDefault("SHOP_DELETED", "§f已删除商店!");
            ccLang.get().addDefault("POSITION", "§f商店所处位置: ");
            ccLang.get().addDefault("SHOP_LIST", "§f商店列表");

            ccLang.get().addDefault("TIME.OPEN", "开市");
            ccLang.get().addDefault("TIME.CLOSE", "闭市");
            ccLang.get().addDefault("TIME.OPEN_LORE", "§f设置开市时间");
            ccLang.get().addDefault("TIME.CLOSE_LORE", "§f设置闭市时间");
            ccLang.get().addDefault("TIME.SHOPHOURS", "§f商店营业时间");
            ccLang.get().addDefault("TIME.OPEN24", "24小时无休营业");
            ccLang.get().addDefault("TIME.SHOP_IS_CLOSED", "§f商店已关门。开市时间: {time}h。当前时间: {curTime}时");
            ccLang.get().addDefault("TIME.SET_SHOPHOURS", "设置商店营业时间");
            ccLang.get().addDefault("TIME.CUR", "§f当前时间: {time}时");

            ccLang.get().addDefault("FLUC.FLUCTUATION", "随机库存波动");
            ccLang.get().addDefault("FLUC.INTERVAL", "波动周期");
            ccLang.get().addDefault("FLUC.STRENGTH", "波动强度");

            ccLang.get().addDefault("STOCKSTABILIZING.SS", "§f库存保持稳定");
            ccLang.get().addDefault("STOCKSTABILIZING.L_R_SHIFT", "§e左击: -0.1 右击: +0.1 Shift: x5");

            ccLang.get().addDefault("TAX.SALESTAX", "§f销售税");
            ccLang.get().addDefault("TAX.USE_GLOBAL", "使用全局税率 ({tax}%)");
            ccLang.get().addDefault("TAX.USE_LOCAL", "单独设置税率");

            ccLang.get().addDefault("OUT_OF_STOCK", "§f库存告急!");
            ccLang.get().addDefault("BUY_SUCCESS", "§f以 ${price} 的价格购买了 {item} x{amount}。余额: ${bal}");
            ccLang.get().addDefault("SELL_SUCCESS", "§f以 ${price} 的价格出售了 {item} x{amount}。余额: ${bal}");
            ccLang.get().addDefault("BUY_SUCCESS_JP", "§f以 ${price} 的工作点数购买了 {item} x{amount}。 余额: ${bal}");
            ccLang.get().addDefault("SELL_SUCCESS_JP", "§f以 ${price} 的工作点数出售了 {item} x{amount}。 余额: ${bal}");
            ccLang.get().addDefault("QSELL_RESULT", "§f给予: ");
            ccLang.get().addDefault("QSELL_NA", "§f目前没有商店收购那件物品。");
            ccLang.get().addDefault("DELIVERYCHARGE", "§f货运费");
            ccLang.get().addDefault("DELIVERYCHARGE_EXEMPTION", "§f货运费: {fee} ({fee2} 免税)");
            ccLang.get().addDefault("DELIVERYCHARGE_NA", "§f无法配送至不同的世界。");
            ccLang.get().addDefault("NOT_ENOUGH_MONEY", "§f没有足够的钱。余额: {bal}");
            ccLang.get().addDefault("NOT_ENOUGH_POINT", "§f没有足够的点数。余额: {bal}");
            ccLang.get().addDefault("NO_ITEM_TO_SELL", "§f没有相应的物品可以售出。");
            ccLang.get().addDefault("INVEN_FULL", "§4Inventory is full!");
            ccLang.get().addDefault("IRREVERSIBLE", "§f这个操作将是不可逆的!");

            ccLang.get().addDefault("HELP.TITLE", "§f帮助: {command} --------------------");
            ccLang.get().addDefault("HELP.SHOP", "打开商店GUI。");
            ccLang.get().addDefault("HELP.CMD", "切换是否启用命令帮助。");
            ccLang.get().addDefault("HELP.CREATESHOP", "创建新的商店。");
            ccLang.get().addDefault("HELP.DELETESHOP", "删除已有商店。");
            ccLang.get().addDefault("HELP.SETTAX", "设置销售税");
            ccLang.get().addDefault("HELP.SETTAX_TEMP", "设置临时销售税");
            ccLang.get().addDefault("HELP.SHOPADDHAND", "添加手中物品至商店。");
            ccLang.get().addDefault("HELP.SHOPADDITEM", "添加物品至商店。");
            ccLang.get().addDefault("HELP.SHOPEDIT", "编辑商店上架商品。");
            ccLang.get().addDefault("HELP.PRICE", "§7价格计算公式: 中间值*价值/库存");
            ccLang.get().addDefault("HELP.INF_STATIC", "§7中间值<0 == 静态价格     库存<0 == 无限库存");
            ccLang.get().addDefault("HELP.EDITALL", "编辑所有商店物品。");
            ccLang.get().addDefault("HELP.EDITALL2", "§c警告。这个操作没有完整性检测机制，请小心使用。");
            ccLang.get().addDefault("HELP.RELOAD", "重载 YML.");
            ccLang.get().addDefault("HELP.RELOADED", "插件已重新加载");
            ccLang.get().addDefault("HELP.USAGE", "用法");
            ccLang.get().addDefault("HELP.CREATESHOP2", "权限(您可以稍后更改此项。)\n   true: dshop.user.shop.shopname\n   false: 不需要任何权限(默认)\n   用户输入: 需要所键入的权限");
            ccLang.get().addDefault("HELP.ITEM_ALREADY_EXIST", "§7§o{item} 已经在上架销售了。\n   {info}\n   价值将被更新。");
            ccLang.get().addDefault("HELP.ITEM_INFO", "§7§o{item} 正在出售:\n   {info}");
            ccLang.get().addDefault("HELP.REMOVE_ITEM", "§f§o请输入 0 来作为数据值以 §4移除§f 这个物品。");
            ccLang.get().addDefault("HELP.QSELL", "§f快速出售");
            ccLang.get().addDefault("HELP.DELETE_OLD_USER", "从 User.yml 中删除不活跃的用户数据。");
            ccLang.get().addDefault("HELP.CONVERT", "从其他插件转换数据。");
            ccLang.get().addDefault("HELP.ACCOUNT", "设置商店货币总额。 -1 = 无限");

            ccLang.get().addDefault("ITEM_ADDED", "已添加物品!");
            ccLang.get().addDefault("ITEM_UPDATED", "已更新物品!");
            ccLang.get().addDefault("ITEM_DELETED", "已移除物品!");
            ccLang.get().addDefault("CHANGES_APPLIED", "已应用更改。新的价值: ");
            ccLang.get().addDefault("RECOMMAND_APPLIED", "已应用系统建议价值。基于 {playerNum}名玩家。该值能在配置文件中被编辑。");
            ccLang.get().addDefault("TRANSFER_SUCCESS", "转移成功。");

            ccLang.get().addDefault("ERR.NO_USER_ID", "§6无法在服务器中找到您的uuid。商店对您来说无法使用。");
            ccLang.get().addDefault("ERR.ITEM_NOT_EXIST", "物品不存在。");
            ccLang.get().addDefault("ERR.ITEM_FORBIDDEN", "物品已被禁止上架。");
            ccLang.get().addDefault("ERR.NO_PERMISSION", "§e没有相应权限。");
            ccLang.get().addDefault("ERR.WRONG_USAGE", "错误的用法");
            ccLang.get().addDefault("ERR.NO_EMPTY_SLOT", "商店已满");
            ccLang.get().addDefault("ERR.WRONG_DATATYPE", "错误的参数类型。");
            ccLang.get().addDefault("ERR.VALUE_ZERO", "该参数必须大于 0");
            ccLang.get().addDefault("ERR.WRONG_ITEMNAME", "没有这样的物品。");
            ccLang.get().addDefault("ERR.HAND_EMPTY", "您必须手持代售物品才能进行出售操作。");
            ccLang.get().addDefault("ERR.HAND_EMPTY2", "§c§o您必须手持待售物品才能进行出售!");
            ccLang.get().addDefault("ERR.SHOP_NOT_FOUND", "§f未找到商店");
            ccLang.get().addDefault("ERR.SHOP_EXIST", "这个名称已然存在。");
            ccLang.get().addDefault("ERR.SIGNSHOP_REMOTE_ACCESS", "您不能远程打开一个告示牌商店。");
            ccLang.get().addDefault("ERR.LOCALSHOP_REMOTE_ACCESS", "您不能远程打开一个区位商店。");
            ccLang.get().addDefault("ERR.MAX_LOWER_THAN_MIN", "最大价格必须高于最低价格");
            ccLang.get().addDefault("ERR.DEFAULT_VALUE_OUT_OF_RANGE", "价格必须处于价值所规定的区间");
            ccLang.get().addDefault("ERR.NO_RECOMMAND_DATA", "在 Worth.yml 没有找到可用的数据。");
            ccLang.get().addDefault("ERR.JOBSREBORN_NOT_FOUND", "'Jobs Reborn' 未被找到。");
            ccLang.get().addDefault("ERR.SHOP_HAS_INF_BAL", "{shop} 拥有无限的货币。");
            ccLang.get().addDefault("ERR.SHOP_DIFF_CURRENCY", "这些商店使用着不同的货币。");
            ccLang.get().addDefault("ERR.PLAYER_NOT_EXIST", "玩家不存在。");
            ccLang.get().addDefault("ERR.SHOP_LINK_FAIL", "他们之中必须有一个实体账户。");
            ccLang.get().addDefault("ERR.SHOP_LINK_TARGET_ERR", "目标商店必须有一个实体账户。");
            ccLang.get().addDefault("ERR.NESTED_STRUCTURE", "镶套结构是不被允许的。(例如. aa-bb, bb-cc)");
            ccLang.get().addDefault("ERR.CREATIVE","§e由于没有权限，您无法在创造模式下使用这个命令。");

            ccLang.get().options().copyDefaults(true);
            ccLang.save();
        }

        if(lang == null) lang = "en-US";

        if(!lang.equals("en-US") && !lang.equals("ko-KR") && !lang.equals("zh-CN"))
        {
            ConfigurationSection conf = ccLang.get().getConfigurationSection("");

            ccLang.setup("Lang_v2_"+lang,null);

            for (String s:conf.getKeys(true))
            {
                if(!ccLang.get().contains(s))
                {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "String Key " + s + " added");
                    ccLang.get().addDefault(s,conf.get(s));
                }
            }
        }
        else
        {
            ccLang.setup("Lang_v2_"+lang,null);
        }

        ccLang.get().options().copyDefaults(true);
        ccLang.save();
    }
}
