package com.fproject.app.biz.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.springframework.stereotype.Repository;

import com.fproject.app.biz.common.JDBCUtil;
import com.fproject.app.biz.filter.FilterSearchUtil;
import com.fproject.app.biz.filter.ProductFilter;


@Repository
public class ProductDAO{

	private final String INSERT = "INSERT INTO BB_PRODUCT(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_PROFILE_WAY, BOARD_NUM, PRODUCT_CATEGORY_NUM) "
            						+ "VALUES (?, ?, ?, ?, ?)";
	private final String INSERT_CRAWLING = "INSERT INTO BB_PRODUCT(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_PROFILE_WAY, PRODUCT_CATEGORY_NUM) "
									+ "VALUES (?, ?, ?, ?)";
    private final String UPDATE = "UPDATE BB_PRODUCT SET PRODUCT_NAME = ?, PRODUCT_PRICE = ?, PRODUCT_PROFILE_WAY = ?, BOARD_NUM = ?, PRODUCT_CATEGORY_NUM = ? WHERE PRODUCT_NUM = ?";
    private final String DELETE = "DELETE FROM BB_PRODUCT WHERE PRODUCT_NUM = ?";

    private final String SELECTALL = "SELECT RN, PRODUCT_NUM, PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_PROFILE_WAY, PRODUCT_CATEGORY_NAME, BOARD_TITLE "
            + "FROM (SELECT ROW_NUMBER() OVER(ORDER BY PRODUCT_NUM DESC) AS RN, PRODUCT_NUM, PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_PROFILE_WAY, PRODUCT_CATEGORY_NAME, BOARD_TITLE "
            + " FROM BB_VIEW_PRODUCT_JOIN WHERE 1=1 ";
    private final String SELECTALL_ENDPART = " ORDER BY RN) AS PAGED WHERE RN LIMIT ?, ?";


    private final String SELECTONE = "SELECT PRODUCT_NUM, PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_PROFILE_WAY, BOARD_NUM, PRODUCT_CATEGORY_NUM, PRODUCT_CATEGORY_NAME, BOARD_TITLE, BOARD_CONTENT "
            + "FROM BB_VIEW_PRODUCT_JOIN "
            + "WHERE PRODUCT_NUM = ?";
    
    private final String SELECTONE_CNT = "SELECT COUNT(*) AS CNT FROM BB_PRODUCT bp "
    		+ "LEFT JOIN BB_BOARD bb ON bp.BOARD_NUM = bb.BOARD_NUM WHERE 1=1 ";


    public boolean insert(ProductDTO productDTO) {
		System.out.println("log: Product insert start");
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			if(productDTO.getCondition().equals("CRAWLING_ONLY")) {
				System.out.println("log: Product insert condition : CRAWLING_ONLY");
				//오직 크롤링 insert용 (Controller에서 사용하지 않음 / Crawling insert (Listener)는 model에서 담당)
				pstmt = conn.prepareStatement(INSERT_CRAWLING);
				pstmt.setString(1, productDTO.getProductName()); 		//상품 이름
				pstmt.setInt(2, productDTO.getProductPrice()); 			//상품 가격
				pstmt.setString(3, productDTO.getProductProfileWay()); 	//썸네일
				pstmt.setInt(4, productDTO.getProductCateNum()); 		//상품 카테고리 번호
				//넘어온 값 확인 로그
				System.out.println("log: parameter getProductName : "+productDTO.getProductName());
				System.out.println("log: parameter getProductPrice : "+productDTO.getProductPrice());
				System.out.println("log: parameter getProductProfileWay : "+productDTO.getProductProfileWay());
				System.out.println("log: parameter getProductCateNum : "+productDTO.getProductCateNum());
			}
			else {				
				//새 상품작성
				pstmt = conn.prepareStatement(INSERT);
				pstmt.setString(1, productDTO.getProductName()); 		//상품 이름
				pstmt.setInt(2, productDTO.getProductPrice()); 			//상품 가격
				pstmt.setString(3, productDTO.getProductProfileWay()); 	//썸네일
				pstmt.setInt(4, productDTO.getBoardNum()); 				//상품설명게시글번호
				pstmt.setInt(5, productDTO.getProductCateNum()); 		//상품 카테고리 번호
				//넘어온 값 확인 로그
				System.out.println("log: parameter getProductName : "+productDTO.getProductName());
				System.out.println("log: parameter getProductPrice : "+productDTO.getProductPrice());
				System.out.println("log: parameter getProductProfileWay : "+productDTO.getProductProfileWay());
				System.out.println("log: parameter getBoardNum : "+productDTO.getBoardNum());
				System.out.println("log: parameter getProductCateNum : "+productDTO.getProductCateNum());
			}
			if(pstmt.executeUpdate() <= 0) { 
				//쿼리는 정상적으로 실행됐으나 실패
				System.err.println("log: Product insert execute fail");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("log: Product insert SQLException fail");
			return false;
		} catch (Exception e) {
			System.err.println("log: Product insert Exception fail");
			return false;
		} finally {
			//연결해제
			if(!JDBCUtil.disconnect(conn, pstmt)) {
				//연결해제 실패
				System.err.println("log: Product insert disconnect fail");
				return false;
			}
			System.out.println("log: Product insert end");
		}
		System.out.println("log: Product insert true");
		return true;
	}
	
	public boolean update(ProductDTO productDTO) {
		System.out.println("log: Product update start");
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			//상품 수정
			System.out.println("log: Product update : UPDATE");
			pstmt = conn.prepareStatement(UPDATE);
			pstmt.setString(1, productDTO.getProductName()); 		//상품 이름
			pstmt.setInt(2, productDTO.getProductPrice()); 			//상품 가격
			pstmt.setString(3, productDTO.getProductProfileWay()); 	//썸네일
			pstmt.setInt(4, productDTO.getBoardNum()); 				//상품설명게시글번호
			pstmt.setInt(5, productDTO.getProductCateNum()); 		//상품 카테고리 번호
			pstmt.setInt(6, productDTO.getProductNum()); 			//상품 번호
			//넘어온 값 확인 로그
			System.out.println("log: parameter getProductName : "+productDTO.getProductName());
			System.out.println("log: parameter getProductPrice : "+productDTO.getProductPrice());
			System.out.println("log: parameter getProductProfileWay : "+productDTO.getProductProfileWay());
			System.out.println("log: parameter getBoardNum : "+productDTO.getBoardNum());
			System.out.println("log: parameter getProductCateNum : "+productDTO.getProductCateNum());
			System.out.println("log: parameter getProductNum : "+productDTO.getProductNum());
			if(pstmt.executeUpdate() <= 0) { 
				//쿼리는 정상적으로 실행됐으나 실패
				System.err.println("log: Product update execute fail");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("log: Product update SQLException fail");
			return false;
		} catch (Exception e) {
			System.err.println("log: Product update Exception fail");
			return false;
		} finally {
			//연결해제
			if(!JDBCUtil.disconnect(conn, pstmt)) {
				//연결해제 실패
				System.err.println("log: Product update disconnect fail");
				return false;
			}
			System.out.println("log: Product update end");
		}
		System.out.println("log: Product update true");
		return true;
	}
	
	public boolean delete(ProductDTO productDTO) {
		System.out.println("log: Product delete start");
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(DELETE);
			pstmt.setInt(1, productDTO.getProductNum()); //상품 번호
			//넘어온 값 확인 로그
			System.out.println("log: parameter getProductNum : "+productDTO.getProductNum());
			if(pstmt.executeUpdate() <= 0) { 
				//쿼리는 정상적으로 실행됐으나 실패
				System.err.println("log: Product delete execute fail");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("log: Product delete SQLException fail");
			return false;
		} catch (Exception e) {
			System.err.println("log: Product delete Exception fail");
			return false;
		} finally {
			//연결해제
			if(!JDBCUtil.disconnect(conn, pstmt)) {
				//연결해제 실패
				System.err.println("log: Product delete disconnect fail");
				return false;
			}
			System.out.println("log: Product delete end");
		}
		System.out.println("log: Product delete true");
		return true;
	}
	
	public ArrayList<ProductDTO> selectAll(ProductDTO productDTO) {
		System.out.println("log: Product selectAll start");
		ArrayList<ProductDTO> datas = new ArrayList<>();
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		
		try {
			//상품리스트 (+필터검색)
			//필터검색 추가
			HashMap<String, String> filters = productDTO.getFilterList();//넘어온 MAP filter키워드
			ProductFilter filterUtil = new ProductFilter();
			pstmt = conn.prepareStatement(filterUtil.buildFilterQuery(SELECTALL,filters).append(" "+SELECTALL_ENDPART).toString());
			int placeholderNum = 1; //필터검색 선택한 것만 검색어를 넣기 위한 카운트
			placeholderNum = filterUtil.setFilterKeywords(pstmt,filters,placeholderNum); 		//필터 검색 검색어 
			if(placeholderNum < 0) {
				//만약 filterKeywordSetter 메서드에서 오류가 발생했다면 SQL예외처리
				throw new SQLException();
			}
			pstmt.setInt(placeholderNum++, productDTO.getStartNum());			//페이지네이션 용 시작번호
			pstmt.setInt(placeholderNum++, productDTO.getEndNum());				//페이지네이션 용 끝번호
			//넘어온 값 확인 로그
			System.out.println("log: parameter getStartNum : "+productDTO.getStartNum());
			System.out.println("log: parameter getEndNum : "+productDTO.getEndNum());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) { 
				ProductDTO data = new ProductDTO();
				data.setProductNum(rs.getInt("PRODUCT_NUM"));		//상품번호
				data.setProductName(rs.getString("PRODUCT_NAME"));	//상품이름
				data.setProductPrice(rs.getInt("PRODUCT_PRICE"));	//상품가격
				data.setProductProfileWay(rs.getString("PRODUCT_PROFILE_WAY"));	//썸네일
				data.setProductCateName(rs.getString("PRODUCT_CATEGORY_NAME"));	//상품카테고리명
				data.setBoardTitle(rs.getString("BOARD_TITLE"));	//상품 게시글 제목
				//반환된 객체 리스트에 추가
				datas.add(data); 
				System.out.print(" | result "+data.getProductNum());
			}
			rs.close();
			System.out.println("end");
		} catch (SQLException e) {
			System.err.println("log: Product selectAll SQLException fail");
			datas.clear();//잔여데이터 삭제
		} catch (Exception e) {
			System.err.println("log: Product selectAll Exception fail");
			datas.clear();//잔여데이터 삭제
		} finally {
			//연결해제
			if(!JDBCUtil.disconnect(conn, pstmt)) {
				//연결해제 실패
				System.err.println("log: Product selectAll disconnect fail");
				datas.clear();//잔여데이터 삭제
			}
			System.out.println("log: Product selectAll end");
		}
		System.out.println("log: Product selectAll return datas");
		return datas;
	}
	
	public ProductDTO selectOne(ProductDTO productDTO) {
		System.out.println("log: Product selectOne start");
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ProductDTO data = null;
		try {
			if(productDTO.getCondition().equals("MD_ONE")) {
				//상품 상세보기
				System.out.println("log: Product selectOne : MD_ONE");
				pstmt = conn.prepareStatement(SELECTONE);
				pstmt.setInt(1, productDTO.getProductNum()); 	//상품번호
				//넘어온 값 확인 로그
				System.out.println("log: parameter getProductNum : "+productDTO.getProductNum());
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) { 
					data = new ProductDTO();
					data.setProductNum(rs.getInt("PRODUCT_NUM"));				//상품번호
					data.setProductName(rs.getString("PRODUCT_NAME"));			//상품이름
					data.setProductPrice(rs.getInt("PRODUCT_PRICE"));			//상품가격
					data.setProductProfileWay(rs.getString("PRODUCT_PROFILE_WAY"));	//썸네일
					data.setProductCateName(rs.getString("PRODUCT_CATEGORY_NAME"));	//상품카테고리명
					data.setProductCateNum(rs.getInt("PRODUCT_CATEGORY_NUM"));	//상품카테고리번호
					data.setBoardNum(rs.getInt("BOARD_NUM"));					//상품 게시글 번호
					data.setBoardTitle(rs.getString("BOARD_TITLE"));			//상품 게시글 제목
					data.setBoardContent(rs.getString("BOARD_CONTENT"));		//상품 게시글 내용
					System.out.println("result exists");
				}
				rs.close();
			}
			else if(productDTO.getCondition().equals("FILTER_CNT")) {
				//검색 별 상품 개수(+필터검색)
				System.out.println("log: Product selectOne : FILTER_CNT");
				//필터검색 추가
				HashMap<String, String> filters = productDTO.getFilterList();//넘어온 MAP filter키워드
				ProductFilter filterUtil = new ProductFilter();
				pstmt = conn.prepareStatement(filterUtil.buildFilterQuery(SELECTONE_CNT,filters).toString());
				int placeholderNum = 1; //필터검색 선택한 것만 검색어를 넣기 위한 카운트
				placeholderNum = filterUtil.setFilterKeywords(pstmt,filters,placeholderNum); 		//필터 검색 검색어 
				if(placeholderNum < 0) {
					//만약 filterKeywordSetter 메서드에서 오류가 발생했다면 SQL예외처리
					throw new SQLException();
				}
				//넘어온 값 확인 로그
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) { 
					data = new ProductDTO();
					data.setCnt(rs.getInt("CNT")); 				//상품 수
					System.out.println("result exists");
				}
				rs.close();
			}
			else {
				//컨디션값 오류
				System.err.println("log: Product selectOne condition fail");
			}
			System.out.println("end");
		} catch (SQLException e) {
			System.err.println("log: Product selectOne SQLException fail");
			return null;
		} catch (Exception e) {
			System.err.println("log: Product selectOne Exception fail");
			return null;
		} finally {
			//연결해제
			if(!JDBCUtil.disconnect(conn, pstmt)) {
				//연결해제 실패
				System.err.println("log: Product selectOne disconnect fail");
				return null;
			}
			System.out.println("log: Product selectAll end");
		}
		System.out.println("log: Product selectAll return datas");
		return data;
	}
}
