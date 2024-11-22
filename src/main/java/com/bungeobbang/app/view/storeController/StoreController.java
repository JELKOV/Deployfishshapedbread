package com.bungeobbang.app.view.storeController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bungeobbang.app.biz.board.BoardDTO;
import com.bungeobbang.app.biz.board.BoardService;
import com.bungeobbang.app.biz.store.StoreDTO;
import com.bungeobbang.app.biz.store.StoreService;
import com.bungeobbang.app.biz.storeMenu.StoreMenuDTO;
import com.bungeobbang.app.biz.storeMenu.StoreMenuService;
import com.bungeobbang.app.biz.storePayment.StorePaymentDTO;
import com.bungeobbang.app.biz.storePayment.StorePaymentService;
import com.bungeobbang.app.biz.storeWork.StoreWorkDTO;
import com.bungeobbang.app.biz.storeWork.StoreWorkService;
import com.bungeobbang.app.view.util.PaginationUtils;

import lombok.extern.slf4j.Slf4j;

@Controller("store")
@Slf4j
public class StoreController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreMenuService storeMenuService;
    @Autowired
    private StorePaymentService storePaymentService;
    @Autowired
    private StoreWorkService storeWorkService;
    @Autowired
    private BoardService boardService;

    private final String FAIL_PATH = "failInfo2";
    private final String FAIL_DO = "redirect:failInfo.do";


    private final String NO = "N";
    private final String YES = "Y";

    //페이지에 뜨는 데이터 개수
    private final int CONTENT_SIZE = 4;

    //page
    private final String PAGE_ADD_STORE = "adminStoreRegister"; //views 하위, 가게 등록
    private final String PAGE_REPORT_STORE = "userStoreReport"; //views 하위, 가게 제보
    private final String PAGE_INFO_STORE = "store"; //views 하위, 가게 상세
    private final String PAGE_LOAD_LIST_STORE = "storeList"; //views 하위, 가게 검색+리스트
    private final String PAGE_MAP_STORE = "addressSearchStore"; //views 하위 가게 주소검색

    //msg
    private final String MSG_STORE_ADD = "가게 등록에 성공했습니다.";
    
    //@Value("${map.api.key}")
    //private String mapApiKey;

    //가게 주소 검색 페이지 이동
    @RequestMapping(value = "/loadListStoreMap.do", method = RequestMethod.GET)
    public String loadListStoreMap(Model model){
        //전달할 데이터는 비동기 호출
    	// 환경 변수 직접 접근
    	String mapApiKey = System.getenv("MAP_API_KEY");
    	log.info("log: 환경 변수에서 가져온 mapApiKey : [{}]", mapApiKey);

    	model.addAttribute("mapApiKey", mapApiKey);
        log.info("log: /loadListStoreMap.do loadListStoreMap GET");
        return PAGE_MAP_STORE;
    }

    //가게 등록 페이지 이동
    @RequestMapping(value = "/addStore.do", method = RequestMethod.GET)
    public String addStore(String condition, Model model){
        log.info("log: /addStore.do addStore GET - start");
        log.info("log: addStore - param condition : [{}]", condition);
        model.addAttribute("condition", condition);
        //확인
        log.info("log: addStore - send condition : [{}]", condition);
        log.info("log: /addStore.do addStore GET - end");
        return condition;
    }

    //가게 등록 기능 수행
    @RequestMapping(value = "/addStore.do", method = RequestMethod.POST)
    public String addStore(StoreDTO storeDTO, StoreMenuDTO storeMenuDTO, StorePaymentDTO storePaymentDTO, BoardDTO boardDTO,
                           String[] workWeek, String[] workStartTime, String[] workEndTime, Model model) {
        log.info("log: /addStore.do addStore - start");
        log.info("log: addStore - param storeDTO : [{}]", storeDTO);
        log.info("log: addStore - param storeMenuDTO : [{}]", storeMenuDTO);
        log.info("log: addStore - param storePaymentDTO : [{}]", storePaymentDTO);
        log.info("log: addStore - param boardDTO : [{}]", boardDTO);
        log.info("log: addStore - param workWeek : [{}]", Arrays.toString(workWeek));
        log.info("log: addStore - param workStartTime : [{}]", Arrays.toString(workStartTime));
        log.info("log: addStore - param workEndTime : [{}]", Arrays.toString(workEndTime));

        List<StoreWorkDTO> workList = new ArrayList<>(); //영업정보 (여러 행) 데이터를 취합할 리스트
        //받아온 데이터 취합 저장
        for(int i=0; i<workWeek.length; i++){
            StoreWorkDTO workDTO = new StoreWorkDTO();
            workDTO.setStoreWorkWeek(workWeek[i]);
            workDTO.setStoreWorkOpen(workStartTime[i]);
            workDTO.setStoreWorkClose(workEndTime[i]);
            log.info("log: addStore - workList StoreWorkDTO{} : [{}]", i, workDTO);
            workList.add(workDTO); //리스트에 추가
        }
        log.info("log: addStore - workList (workInfoList create) : [{}]", workList);

        //가게 추가
        if(!this.storeService.insert(storeDTO)){
            //insert 실패 시
            log.error("log: store insert failed");
            return FAIL_DO;
        }

        //추가한 가게의 PK번호
        storeDTO.setCondition("STORE_NEW_SELECTONE");
        int storePK = storeService.selectOne(storeDTO).getMaxPK();
        log.info("log: store insert successful storePK: [{}]", storePK);

        //PK번호 전달 및 메뉴 추가
        storeMenuDTO.setStoreNum(storePK);
        if(!this.storeMenuService.insert(storeMenuDTO)){
            //insert 실패 시
            log.error("log: storeMenu insert failed");
            return FAIL_DO;
        }

        //PK번호 전달 및 결제 정보 추가
        storePaymentDTO.setStoreNum(storePK);
        if(!this.storePaymentService.insert(storePaymentDTO)){
            //insert 실패 시
            log.error("log: storePayment insert failed");
            return FAIL_DO;
        }

        //PK번호 전달 및 영업 정보 추가
        boolean flag = true;
        for(StoreWorkDTO s : workList){ //여러 줄의 데이터이므로 반복문
            s.setStoreNum(storePK);
            if(!this.storeWorkService.insert(s)){
                //insert 실패 시
                log.error("log: storeWork insert failed : [{}]", s);
                flag = false;
                break; //반복 중단
            }
        }
        if(!flag){ //insert 실패 시
            return FAIL_DO;
        }

//        //가게 설명
//        if(boardDTO != null){ //가게 설명이 있다면
//            log.info("log: addStore board is not null - boardDTO : [{}]", boardDTO);
//            boardDTO.setCondition("MARKET_INSERT"); //가게 설명 추가용 condition
//            boardDTO.setStoreNum(storePK); //FK설정
//            if(!boardService.insert(boardDTO)) {//DB에 추가
//                log.error("log: board insert failed");
//                return FAIL_DO;
//            }
//        }
        //등록 성공 시
        log.info("log: /addStore.do addStore - end");
        model.addAttribute("msg", MSG_STORE_ADD);
        model.addAttribute("path", "loadListStore.do");
        return FAIL_PATH;
    }

    //가게 상세 조회
    @RequestMapping("/infoStore.do")
    public String infoStore(StoreDTO storeDTO, StoreWorkDTO storeWorkDTO, Model model){
        log.info("log: /infoStore.do infoStore - start");
        log.info("log: infoStore - param storeDTO num : [{}]", storeDTO.getStoreNum());
        //영업정보 추가
        storeDTO.setCondition("INFO_STORE_SELECTONE");
        storeDTO = storeService.selectOne(storeDTO);
        storeDTO.setWorkList(storeWorkService.selectAll(storeWorkDTO));
    	// 환경 변수 직접 접근
    	String mapApiKey = System.getenv("MAP_API_KEY");
    	log.info("log: 환경 변수에서 가져온 mapApiKey : [{}]", mapApiKey);
        
        //데이터 전달
        model.addAttribute("storeInfo", storeDTO);
        model.addAttribute("mapApiKey", mapApiKey);  // mapApiKey를 모델에 추가
        //확인
        log.info("log: infoStore - send storeInfo : [{}]", storeDTO);
        log.info("log: infoStore - send mapApiKey : [{}]", mapApiKey);
        log.info("log: /infoStore.do infoStore - end");
        return PAGE_INFO_STORE;
    }

    //가게 검색 (동기, 전체) + 가게 검색 페이지 이동
    @RequestMapping("/loadListStore.do")
    public String loadListStore(Model model, StoreDTO storeDTO, StoreDTO storeCntDTO,
                                String[] storeMenu, String[] storePayment, String storeClosed, String keyword, Integer page){
        log.info("log: /loadListStore.do loadListStore - start");
        log.info("log: loadListStore - param storeDTO : [{}]", storeDTO);
        log.info("log: loadListStore - param storeCntDTO : [{}]", storeCntDTO);
        log.info("log: loadListStore - param storeMenu : [{}]", Arrays.toString(storeMenu));
        log.info("log: loadListStore - param storePayment : [{}]", Arrays.toString(storePayment));
        log.info("log: loadListStore - param storeClosed : [{}]", storeClosed);
        log.info("log: loadListStore - param keyword : [{}]", keyword);
        log.info("log: loadListStore - param page : [{}]", page);
        
    	// 환경 변수 직접 접근
    	String mapApiKey = System.getenv("MAP_API_KEY");
    	log.info("log: 환경 변수에서 가져온 mapApiKey : [{}]", mapApiKey);
        
        ArrayList<StoreDTO> storeList; //가게 데이터
        int totalSize; //가게 데이터 총 개수
        int totalPage; //총 페이지 수
        StoreMenuDTO menuCnt; //메뉴 검색 개수
        StorePaymentDTO paymentCnt; //결제 방식 검색 개수

        //페이지 세팅
        page = page == null? 1 : page; //페이지 정보가 없다면 첫 페이지 로드
        log.info("log: loadListStore - now page : [{}]", page);

        //검색 결과 개수 데이터 조회///////////////////////////////////////////////////////////
        //메뉴 방식 개수
        menuCnt = storeMenuService.selectOne(new StoreMenuDTO());
        //결제 방식 개수
        paymentCnt = storePaymentService.selectOne(new StorePaymentDTO());
        ///////////////////////////////////////////////////////////////////////////////

        //검색 세팅 //////////////////////////////////////////////////////////////////////
        HashMap<String, String> filterList = new HashMap<>(); //필터 검색용 리스트
        //메뉴 검색 세팅
        if(storeMenu != null){//NPE 방지
            for(String menu : storeMenu){
                log.info("log: loadListStore - add menu condition : [{}]", menu);
                filterList.put(menu, this.YES);
            }
        }
        //결제방식 검색 세팅
        if(storePayment != null){//NPE 방지
            for(String payment : storePayment){
                log.info("log: loadListStore - add payment condition : [{}]", payment);
                filterList.put(payment, this.YES);
            }
        }
        //폐점여부 조회
        if(storeClosed != null){
            filterList.put("STORE_CLOSED", storeClosed);
        }
        //가게명 검색
        if(keyword != null){
            //앞뒤 공백 제거
            keyword = keyword.trim();
            if(!keyword.isEmpty()){ //빈 값 검색 방지
                log.info("log: loadListStore - name search");
                filterList.put("NAME_LIKE", keyword);
            }
        }
        filterList.put("STORE_SECRET", this.NO); //비공개 검색 방지
        //검색조건 세팅
        storeDTO.setFilterList(filterList);
        storeCntDTO.setFilterList(filterList);
        //////////////////////////////////////////////////////////////////////////////////

        //설정된 검색이 있다면 해당 조건 추가 아니라면 전체 검색
        //페이지네이션 데이터
        storeCntDTO.setCondition("STORE_CNT_SELECTONE");
        totalSize = storeService.selectOne(storeDTO).getCnt();
        totalPage = PaginationUtils.calTotalPages(totalSize, CONTENT_SIZE);
        log.info("log: loadListStore - total size : [{}]", totalSize);
        log.info("log: loadListStore - total page : [{}]", totalPage);
        //startNum EndNum 세팅
        PaginationUtils.setPagination(page, CONTENT_SIZE, totalSize, storeDTO);
        //store data
        storeDTO.setCondition("SELECTALL_VIEW_FILTER");
        storeList = storeService.selectAll(storeDTO); //검색 데이터

        //데이터
        model.addAttribute("storeList", storeList);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("menuCnt", menuCnt);
        model.addAttribute("paymentCnt", paymentCnt);
        //검색 유지 데이터
        model.addAttribute("storeMenu", storeMenu);
        model.addAttribute("storePayment", storePayment);
        model.addAttribute("storeClosed", storeClosed);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("mapApiKey", mapApiKey);
        //확인
        log.info("log: loadListStore - send storeList : [{}]", storeList);
        log.info("log: loadListStore - send totalPage : [{}]", totalPage);
        log.info("log: loadListStore - send storeMenu : [{}]", Arrays.toString(storeMenu));
        log.info("log: loadListStore - send storePayment : [{}]", Arrays.toString(storePayment));
        log.info("log: loadListStore - send storeClosed : [{}]", storeClosed);
        log.info("log: loadListStore - send keyword : [{}]", keyword);
        log.info("log: loadListStore - send page : [{}]", page);
        log.info("log: loadListStore - send menuCnt : [{}]", menuCnt);
        log.info("log: loadListStore - send paymentCnt : [{}]", paymentCnt);
        log.info("log: loadListStore - send mapApiKey : [{}]", mapApiKey);
        log.info("log: /loadListStore.do loadListStore - end");
        return PAGE_LOAD_LIST_STORE;
    }
}
