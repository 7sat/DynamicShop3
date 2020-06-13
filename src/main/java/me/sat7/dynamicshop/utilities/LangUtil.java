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

        // 영어
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

        if(lang == null) lang = "en-US";

        if(!lang.equals("en-US") && !lang.equals("ko-KR"))
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
