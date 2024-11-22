package com.bungeobbang.app.view.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bungeobbang.app.biz.board.BoardDTO;
import com.bungeobbang.app.biz.board.BoardService;
import com.bungeobbang.app.biz.boardCate.BoardCateDTO;
import com.bungeobbang.app.biz.boardCate.BoardCateService;
import com.bungeobbang.app.biz.product.ProductDTO;
import com.bungeobbang.app.biz.product.ProductService;
import com.bungeobbang.app.biz.productCate.ProductCateService;
import com.bungeobbang.app.view.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller("main")
@Slf4j
public class MainController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardCateService boardCateService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCateService productCateService;

    //실패 기본값
    private String msg = "요청한 서비스를 찾을 수 없습니다.";
    private String path = "main.do";

    private final String COOKIE_NAME = "viewedProducts"; //쿠키 이름 설정
    private final String BOARD_LIST = "boardList"; //커뮤니티 카테고리명

    private final int RECOMM_CNT = 3; //추천 상품 데이터 개수

    //page
    private final String PAGE_MAIN = "main"; //views 하위 메인페이지
    private final String FAIL_URL = "failInfo2"; //실패페이지

    @Value("${map.api.key}")
    private String mapApiKey;
    
    //메인페이지 이동
    @RequestMapping("/main.do")
    public String main(HttpServletRequest request, HttpServletResponse response, Model model) {
        log.info("log: /main.do main - start");

        //메인파트는 변동을 고려해 데이터 종류 별로 정리

        //가게 검색은 비동기

        //DAO 호출하여 인기 게시물 TOP 3을 가져오기///////////////////////////////////////////////////////////////
        BoardDTO boardDTO = new BoardDTO(); //게시글 조회 객체
        boardDTO.setCondition("HOT_BOARD"); //인기글 조회 설정

        BoardCateDTO boardCateDTO = new BoardCateDTO(); //카테고리 번호 조회용 객체
        boardCateDTO.setBoardCategoryName(BOARD_LIST); //커뮤니티게시판으로 고정
        boardDTO.setBoardCategoryNum(boardCateService.selectOne(boardCateDTO).getBoardCategoryNum());
        ArrayList<BoardDTO> hotBoardList = boardService.selectAll(boardDTO); // DAO에서 인기 게시물 조회

        model.addAttribute("hotBoardList", hotBoardList); // 상위 3개 인기 게시물 리스트를 request에 전달
        //확인
        log.info("log: main - send hotBoardList [{}]", hotBoardList);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        //쿠키 조회 상품 : 추천상품, 최근에 본 상품 ///////////////////////////////////////////////////////////////
        ArrayList<ProductDTO> recommendedProduct = null; //추천 상품 목록을 담을 리스트
        ArrayList<ProductDTO> resentProduct = null; //쿠키 저장용

        HashMap<String, String> filterList; //필터 검색용
        List<String> viewedProductList; //상품 조회용
        Map<Integer, Integer> categoryCount; //상품 카테고리 계산용
        ArrayList<String> deleteList; //쿠키 삭제용
        String viewedProducts = null; //쿠키 상품 리스트

        ProductDTO productDTO = new ProductDTO(); //조회용 재사용 객체

        //쿠키 가져와서 디코딩 : 사용자가 최근에 조회한 상품 목록
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.cookieData(cookies, COOKIE_NAME);
        if (cookie != null) {//NPE 방지
            viewedProducts = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
            log.info("log: main - viewedProducts : [{}]", viewedProducts);
        }

        // 쿠키에서 가져온 상품 목록이 있다면 리스트에 저장
        if (viewedProducts != null && !viewedProducts.isEmpty()) {
            //","를 통해 데이터 구분
            viewedProductList = Arrays.asList(viewedProducts.split(","));
            log.info("log: main - viewedProductList : [{}]", viewedProductList);

            deleteList = new ArrayList<>(); //삭제용 리스트
            resentProduct = new ArrayList<>(); //추천 상품 목록
            categoryCount = new HashMap<>(); //카테고리 계산용

            // 쿠키에서 가져온 상품 ID를 기반으로 상품을 조회하고 카테고리별로 개수 카운팅
            for (String pNum : viewedProductList) {
                productDTO.setProductNum(Integer.parseInt(pNum));
                Optional<ProductDTO> product = productService.selectOne(productDTO); // 상품 정보 조회
                log.info("log: main - viewedProductList for product : [{}]", product);

                //존재하지 않는 상품일 경우
                if (product == null) {
                    log.error("log: main - deleteList.add : [{}]", pNum);
                    //추후 쿠키에서 제거 작업하기 위해 저장
                    deleteList.add(pNum);
                    continue;
                }
                //TODO 컴파일에러 방지 주석처리
                //존재하는 상품일 경우 해당 상품을 저장
                //resentProduct.add(product);
                log.info("log: main - resentProducts.add");

                // 카테고리 번호별로 상품 수 계산
                //int categoryNum = product.getProductCategoryNum();
                //log.info("log: main - categoryNum : [{}]", categoryNum);
                //categoryCount.put(categoryNum, categoryCount.getOrDefault(categoryNum, 0) + 1);
            }
            //없는 상품 쿠키에서 제거
            if(CookieUtil.cookieDataDelete(response, cookie, deleteList)){
                //쿠키 제거에 실패했더라도 상품리스트를 사용자가 볼 수 있도록 개발자에게 안내만 제공
                log.error("log: [ERROR] main - delete history Cookie fail!");
            };

            // 가장 많이 본 카테고리
            // categoryCount Map에 저장된 카테고리별 조회 횟수를 기준으로 가장 많이 조회된 카테고리를 찾는다.
            // Map.Entry<Integer, Integer>는 카테고리 번호(Integer)와 해당 카테고리의 조회 수(Integer)를 의미.
            // max() 메서드는 가장 큰 값을 가진 카테고리를 찾는다.
            Optional<Map.Entry<Integer, Integer>> mostViewedCategoryOpt = categoryCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue());

            // Optional이 비어있지 않으면 (가장 많이 본 카테고리가 존재할 경우)
            if (mostViewedCategoryOpt.isPresent()) {
                // 가장 많이 본 카테고리의 key 값을 가져옴 (카테고리 번호)
                int mostViewedCategory = mostViewedCategoryOpt.get().getKey();
                // 가장 많이 본 카테고리에 속하는 상품을 필터링하여 추천 상품으로 설정
                filterList = new HashMap<>(); //필터 검색용
                filterList.put("GET_MD_CATEGORY", "" + mostViewedCategory);  // 카테고리 필터 추가
                // ProductDTO 객체 생성 후 필터 설정
                productDTO.setFilterList(filterList);  // 필터를 설정하여 해당 카테고리의 상품만 검색
                // 데이터 범위 설정 (추천 상품 수를 제한)
                productDTO.setStartNum(1);
                productDTO.setEndNum(RECOMM_CNT);
                //확인
                log.info("log: main - mostViewedCategory : [{}]", mostViewedCategory);
                log.info("log: main - filter search filterList : [{}]", filterList);

                // 필터링된 상품 목록을 조회하여 추천 상품 리스트 생성
                recommendedProduct = productService.selectAll(productDTO); // 상품 목록 조회
            }
        }

        //데이터 전달
        model.addAttribute("viewProductList", resentProduct); // 최근에 본 상품
        model.addAttribute("recommProductList", recommendedProduct); // 추천 상품
        model.addAttribute("mapApiKey", mapApiKey);// mapApiKey를 view로 전달

        //확인
        log.info("log: main - send resentProducts : [{}]", resentProduct);
        log.info("log: main - send recommendedProducts : [{}]", recommendedProduct);
        log.info("log: /main.do main - end");
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        log.info("log: /main.do main - end");
        return PAGE_MAIN;
    }

    //실패 알랏 페이지
    @RequestMapping("/failInfo.do")
    public String failInfo(Model model) {
        //전달받은 실패 메세지를 가지고 응답
        model.addAttribute("msg", this.msg); //메세지
        model.addAttribute("path", this.path); //이후 이동 경로
        log.info("log: /failInfo.do failInfo fail msg");
        //스위트 알랏
        return FAIL_URL;
    }
}
