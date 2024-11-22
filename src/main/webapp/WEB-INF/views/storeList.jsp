<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="customStore" tagdir="/WEB-INF/tags/store"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<%--검색 check 유지를 위한 값 세팅--%>
<c:set var="menu" value="${fn:join(paramValues.storeMenu,',')}"/>
<c:set var="payment" value="${fn:join(paramValues.storePayment,',')}"/>
<%--페이지네이션 검색 check 유지를 위한 값 세팅--%>
<c:set var="queryString" value="${pageContext.request.queryString}" />
<!DOCTYPE HTML>

<html>
<head>
<title>가게 목록</title>
<meta charset="utf-8" />
<!-- 파비콘 -->
<link rel="icon" href="${path}/resources/assets/images/logo.png"
	type="image/x-icon" />

<!-- css -->
<link rel="stylesheet" href="${path}/resources/assets/css/main.css">
<link rel="stylesheet" href="${path}/resources/assets/css/searchbar.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/store/searchvalue.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/pagination.css">

<!-- bootstrap -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">

<!-- 맵을 그리기 위한 script -->
<!-- appkey에 발급받은 APP KEY를 넣음 -->
<!-- 추가 기능 사용 시 &libraries=services 코드 추가(주소를 좌표로) -->
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${mapApiKey}&libraries=services"></script>

<!-- js를 쓰므로 jquery 사용 -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- 외부 script 파일 사용 -->
<script src="${path}/resources/assets/js/store/storeList.js"></script>
<script src="${path}/resources/assets/js/store/copy.js"></script>
<script src="${path}/resources/assets/js/map/imageMap.js"></script>
<!-- sweetAlert을 사용하기 위한 script -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- bootstrap icon 사용 -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.1/font/bootstrap-icons.css">


</head>
<body>
	<div id="page-wrapper">
		<script
			src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
			crossorigin="anonymous">
		</script>

		<custom:header />

		<div class="container">

			<!-- container01 : 1행 1열-->
			<div class="row">
				<div class="col-12">
					<!-- 검색창 -->
					<form action="loadListStore.do" method="GET">
						<div class="container">
							<!-- container02 : 1행-->
							<!-- 가게명 검색 -->
							<div class="row align-items-center row-gap">
								<!-- container02 : 1행 1열-->
								<div class="col-sm-2 col-md-2">
									<div class="category" id="categoryName">가게명 검색</div>
								</div>
								<!-- container02 : 1행 2열-->
								<div class="col-10 col-md-10">
									<custom:searchbar placeholder="가게 이름을 입력해 주세요." value="${keyword}"/>
								</div>
							</div>

							<!-- 필터 조건들 출력 부분 -->
							<div class="row align-items-center row-gap">
								<div class="col-sm-2 col-md-2">
									<!-- 필터 조건들을 2번째 열에 출력하기 위한 빈 공간 -->
								</div>
								<div class="col-10 col-md-10">
									<div id=tagBox></div>
									<hr>
								</div>
							</div>

							<!-- 가게 메뉴 -->
							<div class="row align-items-center row-gap">
								<div class="col-sm-2 col-md-2">
									<div class="category" id="categoryMenu">가게 메뉴</div>
								</div>
								<div class="col-10 col-md-10">
									<div class="row">
										<custom:categoryKeyword name="storeMenu" value="MENU_NORMAL" id="" role="팥/슈크림"
											checked="${fn:contains(menu, 'MENU_NORMAL') ? 'checked' : ''}">
											팥/슈크림<span> (${menuCnt.storeMenuNormalCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_VEG" id="" role="야채/김치/만두"
											checked="${fn:contains(menu, 'MENU_VEG') ? 'checked' : ''}">
											야채/김치/만두<span> (${menuCnt.storeMenuVegCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_MINI" id="" role="미니 붕어빵"
											checked="${fn:contains(menu, 'MENU_MINI') ? 'checked' : ''}">
											미니 붕어빵<span> (${menuCnt.storeMenuMiniCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_POTATO" id="" role="고구마"
											checked="${fn:contains(menu, 'MENU_POTATO') ? 'checked' : ''}">
											고구마<span> (${menuCnt.storeMenuPotatoCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_ICE" id="" role="아이스크림/초코"
											checked="${fn:contains(menu, 'MENU_ICE') ? 'checked' : ''}">
											아이스크림/초코<span> (${menuCnt.storeMenuIceCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_CHEESE" id="" role="치즈"
											checked="${fn:contains(menu, 'MENU_CHEESE') ? 'checked' : ''}">
											치즈<span> (${menuCnt.storeMenuCheeseCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_PASTRY" id="" role="페스츄리"
											checked="${fn:contains(menu, 'MENU_PASTRY') ? 'checked' : ''}">
											페스츄리<span> (${menuCnt.storeMenuPastryCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storeMenu" value="MENU_OTHER" id="" role="기타"
											checked="${fn:contains(menu, 'MENU_OTHER') ? 'checked' : ''}">
											기타<span> (${menuCnt.storeMenuOtherCnt})</span>
										</custom:categoryKeyword>
									</div>
								</div>
							</div>

							<!-- 결제방법 -->
							<div class="row align-items-center row-gap">
								<div class="col-sm-2 col-md-2">
									<div class="category" id="categoryPayment">결제 방법</div>
								</div>
								<div class="col-10 col-md-10">
									<div class="row">
										<custom:categoryKeyword name="storePayment" value="PAYMENT_CASHMONEY" id="" role="현금결제"
											checked="${fn:contains(payment, 'PAYMENT_CASHMONEY') ? 'checked' : ''}">
											현금결제<span>(${paymentCnt.storePaymentCashmoneyCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storePayment" value="PAYMENT_CARD" id="" role="카드결제"
											checked="${fn:contains(payment, 'PAYMENT_CARD') ? 'checked' : ''}">
											카드결제<span>(${paymentCnt.storePaymentCardCnt})</span>
										</custom:categoryKeyword>
										<custom:categoryKeyword name="storePayment" value="PAYMENT_ACCOUNT" id="" role="계좌이체"
											checked="${fn:contains(payment, 'PAYMENT_ACCOUNT') ? 'checked' : ''}">
											계좌이체<span>(${paymentCnt.storePaymentAccountCnt})</span>
										</custom:categoryKeyword>
									</div>
								</div>
							</div>

							<!-- 운영상태 -->
							<div class="row align-items-center row-gap">
								<div class="col-sm-2 col-md-2">
									<div class="category" id="categoryState">운영 상태</div>
								</div>
								<div class="col-10 col-md-10">
									<div class="row">
										<custom:categoryKeyword name="storeClosed" value="N" id="" role="영업중인 가게만 보기"
											checked="${storeClosed == 'N' ? 'checked' : ''}">
											영업중인 가게만 보기</custom:categoryKeyword>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
				<hr>
			</div>

			<!-- container01 : 2행 1열-->
			<div class="row align-items-center">
				<div class="searchValue">
				<!-- 검색 결과가 없다면 -->
				<c:if test="${empty storeList}">
					<span>검색 결과가 없습니다.</span>
				</c:if>
				<c:if test="${not empty storeList}">
					<!-- 지도 출력 부분 -->
					<div class="col-6 text-center nonePadding fullWidthHeight">
						<!-- 검색 결과 출력 부분-->
						<div class="map" id="map"></div>
					</div>
					<!-- 가게 리스트 출력 부분 -->
					<div class="col-6 text-center">
						<div class="storeList">
							<c:forEach var="store" items="${storeList}">
								<customStore:simpleStoreData store="${store}"/>
							</c:forEach>
						</div>
					</div>
				</c:if>
				</div>
			</div>
			<!-- container01 : 3행 1열 -->
			<div class="row ">
				<div class="col-12">
					<!-- 페이지네이션 -->
					<section id="pagination">
						<div class="pagination">
							<!-- 이전 페이지 버튼 -->
							<c:if test="${page > 1}">
								<%--url을 받아와 page가 있다면 변경하고 없다면 추가--%>
								<c:set var="queryWithNewPage" value="${fn:contains(queryString, 'page=') ?
								   fn:replace(queryString, 'page='+=page, 'page='+=page-1) :
								   queryString.concat('&page=').concat(page-1)}" />
								<a href="?${queryWithNewPage}" id="pagenationNextValue">&laquo; 이전</a>
							</c:if>

							<c:set var="startPage" value="${page - 5}" />
							<c:set var="endPage" value="${page + 4}" />

							<c:if test="${startPage < 1}">
								<c:set var="startPage" value="1" />
							</c:if>
							<c:if test="${endPage > totalPage}">
								<c:set var="endPage" value="${totalPage}" />
							</c:if>

							<c:forEach var="i" begin="${startPage}" end="${endPage}">
								<c:choose>
									<c:when test="${i == page}">
										<strong>${i}</strong>
									</c:when>
									<c:otherwise>
										<%--url을 받아와 page가 있다면 변경하고 없다면 추가, 받아올 url이 없는 경우에도 추가--%>
										<c:set var="queryWithNewPage" value="${empty queryString ? 'page='.concat(i) :
												(fn:contains(queryString, 'page=') ?
													fn:replace(queryString, 'page='+=page, 'page='+=i) :
													queryString.concat('&page=').concat(i))}" />
										<a href="?${queryWithNewPage}" class="pagenationValue">${i}</a>
									</c:otherwise>
								</c:choose>
							</c:forEach>

							<c:if test="${page < totalPage}">
								<%--url을 받아와 page가 있다면 변경하고 없다면 추가--%>
								<c:set var="queryWithNewPage" value="${fn:contains(queryString, 'page=') ?
								   fn:replace(queryString, 'page='+=page, 'page='+=page+1) :
								   queryString.concat('&page=').concat(page+1)}" />
								<a href="?${queryWithNewPage}" id="pagenationNextValue">다음 &raquo;</a>
							</c:if>
						</div>
					</section>
					<!-- 페이지네이션 종료 -->
				</div>
			</div>
		</div>

	</div>

	<!-- footer 커스텀 태그 -->
	<custom:footer />

</body>
</html>
