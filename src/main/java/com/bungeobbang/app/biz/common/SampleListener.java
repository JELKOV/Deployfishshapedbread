package com.bungeobbang.app.biz.common;

import com.bungeobbang.app.biz.crawler.Crawling;
import com.bungeobbang.app.biz.product.ProductDTO;
import com.bungeobbang.app.biz.product.ProductService;
import com.bungeobbang.app.biz.productCate.ProductCateDTO;
import com.bungeobbang.app.biz.productCate.ProductCateService;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;


@Component
@WebListener
@Slf4j
public class SampleListener implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCateService productCateService;

	private ProductDTO productDTO;

	//생성자
	public SampleListener() {
		this.productDTO = new ProductDTO();
		productDTO.setEndNum(10);//페이지네이션 용 값
	}

	//서버 시작시
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)  {
	//	log.warn("\u001B[32mListener log: 기본 데이터 insert문 실행 필수 / Starting to execute required initial data insert statements...\u001B[0m");
		log.warn("\u001B[32mListener log: fileName : sampleDataInsert.sql\u001B[0m");
		ArrayList<ProductDTO> dbDatas = productService.selectAll(productDTO);
		if(dbDatas == null || dbDatas.isEmpty()) {
			log.info("\u001B[32mListener log: Listener Product isEmpty\u001B[0m");
			
			//크롤링 도커 빌드시 제외
	        String enableCrawling = System.getenv("ENABLE_CRAWLING");
	        if ("false".equalsIgnoreCase(enableCrawling)) {
	            log.info("Crawling is disabled via environment variable.");
	            return;
	        }
			//만약 상품 DB가 비어있을 경우
			//크롤링한 데이터 받아오기
			ArrayList<ProductDTO> datas = Crawling.findProductInfo();
			//현재 있는 상품 카테고리 리스트 받아오기
			ProductCateDTO productCateDTO = new ProductCateDTO();
			ArrayList<ProductCateDTO> productCateList = productCateService.selectAll(productCateDTO);
			//랜덤값을 위한 랜덤 객체 선언
			Random rand = new Random();
			for(ProductDTO data : datas) {
				//크롤링한 데이터를 순회하며 카테고리 번호를 랜덤값으로 넣어
				data.setProductCategoryNum(productCateList.get(rand.nextInt(productCateList.size())).getProductCategoryNum());
				data.setCondition("CRAWLING_ONLY");
				//DB에 상품 추가
				productService.insert(data);
			}
		}
		log.info("\u001B[32mListener log: Listener End\u001B[0m");
	}
}