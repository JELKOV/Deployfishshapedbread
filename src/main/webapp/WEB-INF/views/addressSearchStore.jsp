<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<!-- css 변경을 위한 캐시 방지 -->
<meta http-equiv="Cache-Control"
	content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<!-- 파비콘 -->
<link rel="icon" href="${path}/resources/assets/images/logo.png"
	type="image/x-icon" />


<meta charset="UTF-8">
<title>가게 주소 검색</title>

<!-- css -->
<link rel="stylesheet" href="${path}/resources/assets/css/main.css">
<link rel="stylesheet" href="${path}/resources/assets/css/searchbar.css">
<link rel="stylesheet"
	href="${path}/resources/assets/css/store/searchvalue.css">

<!-- bootstrap -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">

<!-- bootstrap icon 사용 -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.1/font/bootstrap-icons.css">

<!-- 맵을 그리기 위한 script -->
<!-- appkey에 발급받은 APP KEY를 넣음 -->
<!-- 추가 기능 사용 시 &libraries=services 코드 추가(주소를 좌표로) -->
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${mapApiKey}&libraries=services"></script>
<!-- sweetAlert을 사용하기 위한 script -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

</head>
<body>
	<div id="page-wrapper">

		<custom:header />

		<div class="container">

			<div class="row">
				<!-- 지도 부분 -->
				<div class="col-12 col-md-8 nonePadding fullWidthHeight">
					<div class="fullMap" id="addressSearchMap"></div>
				</div>

				<!-- 주소 검색 창 -->
				<div class="col-12 col-md-4 mapSearchContainer">

					<div class="row">
						<!-- 검색 부분 -->
						<div class="addressSearch">
							<section id="adTitle">
								<!-- 제목 -->
								<h4>주소 검색</h4>
							</section>
							<section id="adSelect">
								<!-- 주소 select-->
								<select class="addresSelect" name="address1" id="city">
									<option selected disabled="disabled">특별/광역시</option>
								</select>
								<!-- 구 주소-->
								<select class="addresSelect" name="address2" id="district">
									<option selected disabled="disabled">군/구</option>
								</select>
							</section>
							<hr>
						</div>
					</div>

					<div class="row">
						<div class="col-12 text-center">
							<div class="scrollContainer">
								<div class="storeList" id="storeList"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br>
		</div>
		<custom:footer />
	</div>
</body>

<!-- js를 쓰므로 jquery 사용 -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- 외부 script 파일 사용 -->
<script src="${path}/resources/assets/js/store/storeList.js"></script>
<script src="${path}/resources/assets/js/store/copy.js"></script>
<script src="${path}/resources/assets/js/store/selectList.js"></script>
<script src="${path}/resources/assets/js/map/printMap.js"></script>
<script src="${path}/resources/assets/js/store/addressSearch.js"></script>
</html>